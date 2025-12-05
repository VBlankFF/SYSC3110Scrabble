import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

/**
 * Model class for the Scrabble game with MVC pattern.
 * It contains the logic and game state.
 * It notifies the views when the model changes with Observer pattern.
 *
 * @author Emmanuel Konate, Amber Skinner, Joseph Dereje, Aymen Zebentout
 * @version 3.0
 */

public class ScrabbleModel implements Serializable{
    private static final long serialVersionUID = 1L;

    private transient List<ScrabbleView> views;
    private List<Player> playerList;
    private Board gameBoard;
    private int currentPlayer;
    private boolean isPlaying;
    private transient Dictionary gameDictionary;
    private TileBag bagOfTiles;
    private boolean isFirstTurn;
    private int scorelessTurns;
    private List<GameHistory> history;
    private int pointInHistory;


    /**
     * This is a constructor for ScrabbleModel
     */

    public ScrabbleModel(){
        views = new ArrayList<>();
        playerList = new ArrayList<>();
        gameBoard = new Board();
        currentPlayer = 0;
        isPlaying = false;
        bagOfTiles = new TileBag();
        gameDictionary = new Dictionary();
        isFirstTurn = true;
        scorelessTurns = 0;
    }


    /**
     * Adds a view for notification when model changes
     * @param view the view to add.
     */
    public void addView(ScrabbleView view){
        views.add(view);
    }

    /**
     * Removes a view for notification when model changes
     * @param view the view to remove.
     */
    public void removeView(ScrabbleView view){
        views.remove(view);
    }

    /**
     * Notifies all the views to update
     */
    public void notifyViews(){
        for(ScrabbleView view : views){
            view.update();
        }
    }

    /**
     * Starts a new game with the given number of players
     * @param numPlayers the number of players between 2 and 4
     * @param playerNames array of player names
     * @return true if initialization works, false if otherwise
     */
    public boolean initializeGame(int numPlayers, String[] playerNames){
        if (numPlayers < 2 || numPlayers > 4){
            return false;
        }

        //reset the game state
        currentPlayer = 0;
        isFirstTurn = true;
        gameBoard.clearBoard();
        playerList = new ArrayList<>();
        scorelessTurns = 0;
        bagOfTiles = new TileBag();
        history = new ArrayList<>();
        pointInHistory = 0;

        //starts the players
        for(int i = 0; i < numPlayers; i++){
            if (playerNames[i].startsWith("AI"))
            {
                playerList.add (new AIPlayer(0, playerNames[i], i));
            }
            else
            {
                playerList.add(new Player(0, playerNames[i], i));
            }

            while (playerList.get(i).getTiles().size() < 7 && !bagOfTiles.isEmpty()){
                playerList.get(i).addTile(bagOfTiles.getRandomTile());
            }
        }
        isPlaying = true;
        notifyViews();
        return true;
    }

    /**
     * Tries to place a word on the board
     * @param row row from 0 to 14
     * @param col colum from 0 to 14
     * @param direction 1 from horizontal which is the right and 2 for vertical which is 2
     * @param word the word to place
     * @param actuallyPlace if the word should actually be placed on the board (otherwise it's just checking the score)
     * @return the score if valid is >0 or 0 if not
     */
    public int placeWord(int row, int col, int direction, String word, boolean actuallyPlace){
        if(!isPlaying || word == null || word.isEmpty()){
            return 0;
        }
        GameHistory hist = new GameHistory();

        //get the current player
        Player player = playerList.get(currentPlayer);

        //now we validate the position
        if (!gameBoard.validPosition(row, col)){
            return 0;
        }

        //check if the word can fit on the board
        if (direction == 1 && col + word.length() > gameBoard.getBoardSize()){
            return 0;
        }
        if (direction == 2 && row + word.length() > gameBoard.getBoardSize()){
            return 0;
        }

        //get tiles at the positions
        List<Tile> usedTiles = new ArrayList<>();
        List<int[]> newTilePositions = new ArrayList<>();

        for (int i = 0; i < word.length(); i++){
            int r = (direction == 1) ? row : row + i;
            int c = (direction == 1) ? col + i : col;

            Tile existTile = gameBoard.getPosition(r, c);

            if(existTile == null){
                //we need to use a tile from hand
                char needed = Character.toUpperCase((word.charAt(i)));
                boolean found = false;

                for (Tile t : player.getTiles()){
                    if (!usedTiles.contains(t) && (t.getCharacter() == needed || t.isBlank())){
                        usedTiles.add(t);
                        newTilePositions.add(new int[] {r, c});
                        found = true;
                        break;
                    }
                }
                if (!found){
                    //the player does not have tile
                        return  0;
                }
            } else {
                //now we can handle blank tiles on the board
                char existChar = existTile.isBlank() ? existTile.getRepresentedLetter() : existTile.getCharacter();
                //the position has a tile already
                if(existChar != Character.toUpperCase(word.charAt(i))){
                    return 0;
                }
            }
        }
        // the player has to play at least one tile from their hand for the play to be valid
        if (usedTiles.isEmpty()) { return 0;}
        //calculate the score and validation of word
        int score = scoreCalculation(row, col, direction, word, usedTiles, newTilePositions);

        hist.newTilePositions = newTilePositions;

        if (!actuallyPlace){ return score; }

        if(score > 0){
            //put the tiles on the board
            int tileIndex = 0;
            for (int i = 0; i < word.length(); i++){
                int r = (direction == 1) ? row : row + i;
                int c = (direction == 1) ? col + i : col;

                if (gameBoard.getPosition(r, c) == null){
                    Tile tile = usedTiles.get(tileIndex);
                    //if it is a blank tile, we set what letter it will represent
                    if (tile.isBlank()){
                        tile.setRepresentedLetter(word.charAt(i));
                    }
                    gameBoard.placeTile(r, c, tile);
                    player.getTiles().remove(tile);
                    hist.playedTiles.add(tile);
                    tileIndex++;
                }
            }
            //now we update the score
            player.addScore(score);
            hist.scoreGained = score;
            hist.playerId = currentPlayer;
            isFirstTurn = false;
            scorelessTurns = 0;

            //now we refill the player's hand
            while (player.getTiles().size() < 7 && !bagOfTiles.isEmpty()){
                Tile drawnTile = bagOfTiles.getRandomTile();
                player.addTile(drawnTile);
                hist.gainedTiles.add(drawnTile);
            }
            //check if the game has to end, like the player is out of tiles
            addGameHistory(hist);

            if (player.getTiles().isEmpty()){
                endGame();
            } else {
                nextPlayer();
            }
            notifyViews();
        }

        return score;
    }

    public int placeWord(int row, int col, int direction, String word)
    {
        return placeWord(row, col, direction, word, true);
    }

    /**
     * Removes every GameHistory that is past the current point in history, then adds hist to the
     * end of GameHistory and moves the point in history forward 1 state.
     * @param hist the GameHistory to add to the end of history.
     */
    public void addGameHistory(GameHistory hist)
    {
        while(pointInHistory < history.size() - 1)
        {
            history.removeLast();
        }
        history.add(hist);
        pointInHistory++;
    }

    /**
     * Calculate the score for a placement of word
     * @param row the row
     * @param col the column
     * @param direction 1 for right and 2 for down
     * @param word the word that is being placed
     * @param usedTiles tiles used from the hand
     * @return score if valid and 0 if invalid
     */
    private int scoreCalculation(int row, int col, int direction, String word, List<Tile> usedTiles, List<int[]>
                                 newTilePositions){
        Set<Tile> tilesInvolved = new HashSet<>(usedTiles);
        boolean connectedToCurrentTile = false;
        String checkWord = word.toUpperCase();

        //check if word adds from current tiles in main direction
        //vertical
        if (direction == 2){
            //check tiles above
            for (int i = row - 1; i >= 0; i--){
                Tile t = gameBoard.getPosition(i, col);
                if (t == null){
                    break;
                }
                char characterT = t.isBlank() ? t.getRepresentedLetter() : t.getCharacter();
                checkWord = characterT + checkWord;
                tilesInvolved.add(t);
                connectedToCurrentTile = true;
            }
            //check tiles under
            for (int i = row + word.length(); i < gameBoard.getBoardSize(); i++){
                Tile t = gameBoard.getPosition(i, col);
                if (t == null){
                    break;
                }
                char characterT = t.isBlank() ? t.getRepresentedLetter() : t.getCharacter();
                checkWord += characterT;
                tilesInvolved.add(t);
                connectedToCurrentTile = true;
            }
            // horizontal
        } else {
            //check tiles to the left
            for (int i = col - 1; i >= 0; i--){
                Tile t = gameBoard.getPosition(row, i);
                if(t == null){
                    break;
                }
                char characterT = t.isBlank() ? t.getRepresentedLetter() : t.getCharacter();
                checkWord = characterT + checkWord;
                tilesInvolved.add(t);
                connectedToCurrentTile = true;
            }
            //check tiles on the right
            for (int i = col + word.length(); i < gameBoard.getBoardSize(); i++){
                Tile t = gameBoard.getPosition(row, i);
                if (t == null){
                    break;
                }
                char characterT = t.isBlank() ? t.getRepresentedLetter() : t.getCharacter();
                checkWord += characterT;
                tilesInvolved.add(t);
                connectedToCurrentTile = true;
            }
        }

        //validate the word
        if (!gameDictionary.validWord(checkWord.toLowerCase())){
            return 0;
        }

        Set<String> newPosSet = new HashSet<>();
        for (int[] pos : newTilePositions){
            newPosSet.add(pos[0] + "," + pos[1]);
        }
        int score = 0;
        int multiplyWord = 1;

        //check words in perpendicular
        //horizontal, so we check words above and under each tile
        if (direction == 1){
            for (int i = 0; i < word.length(); i++){
                int c = col + i;
                String perpenWord = String.valueOf(word.charAt(i)).toUpperCase();

                //check above
                for (int r = row - 1; r >= 0; r--){
                    Tile t = gameBoard.getPosition(r, c);
                    if (t == null){
                        break;
                    }
                    char characterT = t.isBlank() ? t.getRepresentedLetter() : t.getCharacter();
                    perpenWord = characterT + perpenWord;
                    tilesInvolved.add(t);
                    connectedToCurrentTile = true;
                }

                //check under
                for (int r = row + 1; r < gameBoard.getBoardSize(); r++){
                    Tile t = gameBoard.getPosition(r, c);
                    if (t == null){
                        break;
                    }
                    char characterT = t.isBlank() ? t.getRepresentedLetter() : t.getCharacter();
                    perpenWord += characterT;
                    tilesInvolved.add(t);
                    connectedToCurrentTile = true;
                }
                if (perpenWord.length() > 1 ){
                    if (!gameDictionary.validWord(perpenWord.toLowerCase())){
                        return 0;
                    }

                    int perpenScore = 0;
                    int perpenMultiplier = 1;

                    int startR = row;
                    while (startR > 0 && gameBoard.getPosition(startR - 1, c) != null){
                        startR--;
                    }
                    for (int r = startR; r < gameBoard.getBoardSize() && gameBoard.getPosition(r, c) != null; r++){
                        Tile t = gameBoard.getPosition(r, c);
                        int tileVal = t.getValue();
                        int letterMult = 1;

                        if (newPosSet.contains(r + "," + c)){
                            Board.PremiumSquare prem = gameBoard.getPremiumSquare(r, c);
                            if (prem == Board.PremiumSquare.DOUBLE_LETTER){
                                letterMult = 2;
                            } else if (prem == Board.PremiumSquare.TRIPLE_LETTER){
                                letterMult = 3;
                            } else if (prem == Board.PremiumSquare.DOUBLE_WORD){
                                perpenMultiplier *= 2;
                            } else if (prem == Board.PremiumSquare.TRIPLE_WORD){
                                perpenMultiplier *= 3;
                            }
                        }
                        perpenScore += tileVal * letterMult;
                    }
                    score += perpenScore * perpenMultiplier;
                }
            }
            //vertical check words to the left and right of each tile
        } else {
            for (int i = 0; i < word.length(); i++){
                int r = row + i;
                String perpenWord = String.valueOf(word.charAt(i)).toUpperCase();

                //check the left
                for (int c = col - 1; c >= 0; c--){
                    Tile t = gameBoard.getPosition(r, c);
                    if (t == null){
                        break;
                    }
                    char characterT = t.isBlank() ? t.getRepresentedLetter() : t.getCharacter();
                    perpenWord = characterT + perpenWord;                    tilesInvolved.add(t);
                    connectedToCurrentTile = true;
                }
                //check the right
                for (int c = col + 1; c < gameBoard.getBoardSize(); c++){
                    Tile t = gameBoard.getPosition(r, c);
                    if (t == null){
                        break;
                    }
                    char characterT = t.isBlank() ? t.getRepresentedLetter() : t.getCharacter();
                    perpenWord += characterT;
                    tilesInvolved.add(t);
                    connectedToCurrentTile = true;
                }
                if (perpenWord.length() > 1 ){
                    if (!gameDictionary.validWord(perpenWord.toLowerCase())){
                        return 0;
                    }

                    int perpenScore = 0;
                    int perpenMultiplier = 1;

                    int startC = col;
                    while (startC > 0 && gameBoard.getPosition(r, startC - 1) != null){
                        startC--;
                    }
                    for (int c = startC; c < gameBoard.getBoardSize() && gameBoard.getPosition(r, c) != null; c++){
                        Tile t = gameBoard.getPosition(r, c);
                        int tileVal = t.getValue();
                        int letterMult = 1;

                        if (newPosSet.contains(r + "," + c)){
                            Board.PremiumSquare prem = gameBoard.getPremiumSquare(r, c);
                            if (prem == Board.PremiumSquare.DOUBLE_LETTER){
                                letterMult = 2;
                            } else if (prem == Board.PremiumSquare.TRIPLE_LETTER){
                                letterMult = 3;
                            } else if (prem == Board.PremiumSquare.DOUBLE_WORD){
                                perpenMultiplier *= 2;
                            } else if (prem == Board.PremiumSquare.TRIPLE_WORD){
                                perpenMultiplier *= 3;
                            }
                        }
                        perpenScore += tileVal * letterMult;
                    }
                    score += perpenScore * perpenMultiplier;
                }
            }
        }
        //check if the first player touches the center
        if (isFirstTurn){
            boolean touchCenter = false;
            if (direction == 1){
                touchCenter = (row == 7 && col <= 7 && col + word.length() > 7);
            } else {
                touchCenter = (col == 7 && row <= 7 && row + word.length() > 7);
            }
            if (!touchCenter){
                return 0;
            }
            if (tilesInvolved.size() <= 1){
                //the first word has to be more than one tile
                return 0;
            }
        } else {
            //if it is not the first turn then it must be linked to a current tile
            if (!connectedToCurrentTile){
                return 0;
            }
        }


        for (int i = 0; i < word.length(); i++){
            int r = (direction == 1) ? row : row + i;
            int c = (direction == 1) ? col + i : col ;

            Tile tile = gameBoard.getPosition(r, c);
            boolean isNewTile = newPosSet.contains(r + "," + c);

            int tileVal = 0;
            if (tile != null){
                tileVal = tile.getValue();
            } else {
                //tile is not placed yet so we get from usedTiles
                for (int j = 0; j < newTilePositions.size(); j++){
                    if (newTilePositions.get(j)[0] == r && newTilePositions.get(j)[1] == c){
                        tileVal = usedTiles.get(j).getValue();
                        break;
                    }
                }
            }
            int letterMultiply = 1;
            // now we put premium squares only for new tiles
            if (isNewTile){
                Board.PremiumSquare premium = gameBoard.getPremiumSquare(r, c);
                switch (premium){
                    case DOUBLE_LETTER:
                        letterMultiply = 2;
                        break;
                    case TRIPLE_LETTER:
                        letterMultiply = 3;
                        break;
                    case DOUBLE_WORD:
                        multiplyWord *= 2;
                        break;
                    case TRIPLE_WORD:
                        multiplyWord *= 3;
                        break;
                }
            }

            score += tileVal * letterMultiply;
        }



        score *= multiplyWord;
        return score;
    }

    /**
     * It swaps the tiles from current player's hand.
     * @param swappingTiles string of tile characters to swap
     * @return true if swap worked and false if otherwise.
     */
    public boolean swapTiles(String swappingTiles)
    {
        if(!isPlaying || bagOfTiles.getRemainingTiles() < 7){
            return false;
        }

        Player player = playerList.get(currentPlayer);
        List<Tile> playerTiles = player.getTiles();
        List<Tile> removedTiles = new ArrayList<>();
        GameHistory hist =  new GameHistory();
        hist.playerId = currentPlayer;

        //remove tiles from the hand
        for (char c : swappingTiles.toUpperCase().toCharArray()){
            boolean found = false;
            for (int i = 0; i < playerTiles.size(); i++){
                if (playerTiles.get(i).getCharacter() == c){
                    removedTiles.add(playerTiles.remove(i));
                    found = true;
                    break;
                }
            }
            if (!found){
                //bring back the tiles that failed
                playerTiles.addAll(removedTiles);
                return false;
            }
        }
        //return the tiles to the bag and bring new ones
        hist.swappedTiles = removedTiles;
        for (Tile t : removedTiles){
            bagOfTiles.returnTile(t);
        }

        for (int i = 0; i < removedTiles.size(); i++){
            if (!bagOfTiles.isEmpty()){
                Tile drawnTile = bagOfTiles.getRandomTile();
                player.addTile(drawnTile);
                hist.gainedTiles.add(drawnTile);
            }
        }
        addGameHistory(hist);
        scorelessTurns++;
        if (scorelessTurns >= 6){
            endGame();
        } else {
            nextPlayer();
            notifyViews();
        }
        return true;
    }

    /**
     * Passes the current player's turn
     */
    public void passTurn(){
        scorelessTurns++;
        GameHistory hist =  new GameHistory();
        hist.playerId = currentPlayer;
        addGameHistory(hist);
        if (scorelessTurns >= 6){
            endGame();
        } else {
            nextPlayer();
        }
        notifyViews();
    }

    /**
     * Returns true if undoing is possible, false otherwise.
     * @return true if undoing is possible, false otherwise
     */
    public boolean canUndo()
    {
        return pointInHistory > 0;
    }

    /**
     * Undoes the changes stored in the current GameHistory. This tries to maintain randomization (keep the tiles
     * drawn the same if the same play is done). Swapping may rerandomize drawn tiles.
     */
    public void undo()
    {
        GameHistory undoPoint =  history.get(pointInHistory - 1);
        currentPlayer = undoPoint.playerId;
        Player player = playerList.get(currentPlayer);
        // Remove the newly played tiles from the board
        for (int[] positions : undoPoint.newTilePositions)
        {
            gameBoard.removeTile(positions[0], positions[1]);
        }
        // Get the tiles added to the player's hand and place them back in the bag
        // This is done back to front, since that's the opposite way they are added to hand
        for (int tileIndex = undoPoint.gainedTiles.size() - 1; tileIndex >= 0; tileIndex--)
        {
            Tile tile = undoPoint.gainedTiles.get(tileIndex);
            // If this is the same tile as the one in the player's hand, remove it from their hand
            for (int i = player.tiles.size() - 1; i >= 0; i--)
            {
                if (player.tiles.get(i).equals(tile)){
                    player.tiles.remove(i);
                    // add it back to the front of the bag, where tiles are drawn from
                    bagOfTiles.addTileToStart(tile);
                    break;
                }
            }
        }
        // Remove tiles added to the bag after a swap, and add them back to the player's hand.
        // This may not keep randomization consistent.
        for (Tile tile : undoPoint.swappedTiles)
        {
            bagOfTiles.removeTile(tile);
            player.addTile(tile);
        }
        // Return the tiles the player played to their hand
        for (Tile tile : undoPoint.playedTiles)
        {
            if (tile.isBlank())
            {
                tile.setRepresentedLetter(' ');
            }
            player.addTile(tile);
        }
        // Remove the score the player gained
        player.addScore(-undoPoint.scoreGained);
        pointInHistory--;
        notifyViews();
        // AI players immediately replay the turn if we let them play, so undo past their turn
        if (playerList.get(currentPlayer) instanceof AIPlayer)
        {
            undo();
        }
    }

    /**
     * Returns true if redoing is possible, false otherwise.
     * @return true if redoing is possible, false otherwise
     */
    public boolean canRedo()
    {
        return pointInHistory < history.size();
    }

    /**
     * Redoes the next play in the history ("un-undoes it"). This likely breaks randomization on swaps.
     */
    public void redo()
    {
        GameHistory redoPoint =  history.get(pointInHistory);
        currentPlayer = redoPoint.playerId;
        Player player = playerList.get(currentPlayer);
        // Add the played tiles back onto the board
        for (int i = 0; i < redoPoint.newTilePositions.size(); i++)
        {
            int[] positions = redoPoint.newTilePositions.get(i);
            gameBoard.placeTile(positions[0], positions[1], redoPoint.playedTiles.get(i));
        }
        // Remove the swapped tiles from the player's hand and add them back to the bag.
        // This may not keep randomization consistent.
        for (Tile tile : redoPoint.swappedTiles)
        {
            player.removeTile(tile.getCharacter());
            bagOfTiles.returnTile(tile);
        }
        // Remove the tiles the player played from their hand
        for (Tile tile : redoPoint.playedTiles)
        {
            player.removeTile(tile.getCharacter());
        }
        // Readd the tiles the player drew to their hand and remove them from the bag
        for (Tile tile : redoPoint.gainedTiles)
        {
            player.addTile(tile);
            bagOfTiles.removeTile(tile);
        }

        // Readd the score the player gained
        player.addScore(redoPoint.scoreGained);
        pointInHistory++;
        currentPlayer++;
        if (currentPlayer == playerList.size())
        {
            currentPlayer = 0;
        }
        notifyViews();
        // AI players would ruin the redo stack if we let them play, so redo over their turn
        if (playerList.get(currentPlayer) instanceof AIPlayer)
        {
            redo();
        }
    }

    /**
     * Moves to the next player
     */
    private void nextPlayer(){
        currentPlayer = (currentPlayer + 1) % playerList.size();
    }

    public boolean CheckAITurn(){
        if (playerList.get(currentPlayer) instanceof AIPlayer && isPlaying())
        {
            doAITurn();
            return true;
        }
        return false;
    }
    /**
     * Makes the current AI player play their turn, then produces a dialogue box describing their action.
     */
    private void doAITurn()
    {
        notifyViews();
        AIPlayer ai =  (AIPlayer) playerList.get(currentPlayer);
        AIPlayer.AIPlay highestPlay = new AIPlayer.AIPlay(0, 0, 0, "a");
        for (AIPlayer.AIPlay play : ai.GetPossibleMoves(this))
        {
            play.wordScore = placeWord(play.xPos, play.yPos, play.direction, play.word, false);
            if (play.wordScore > highestPlay.wordScore){
                highestPlay = play;
            }
        }
        if (highestPlay.wordScore > 0)
        {
            placeWord(highestPlay.xPos, highestPlay.yPos, highestPlay.direction, highestPlay.word, true);
            for (ScrabbleView v : views)
            {
                v.handleAIPlay(ai.getName(), highestPlay.word, highestPlay.wordScore, ai.getScore() + highestPlay.wordScore);
            }
            return;
        }
        String aiTiles = ai.getTilesAsString().toUpperCase().replaceAll(" ", "");
        if (swapTiles(aiTiles))
        {
            for (ScrabbleView v : views)
            {
                v.handleAISwap(ai.getName(), aiTiles);
            }
            return;
        }
        passTurn();
        for (ScrabbleView v : views)
        {
            v.handleAIPass(ai.getName());
        }
        notifyViews();
    }

    /**
     * End the current game
     */
    private void endGame(){
        isPlaying = false;
        //give points for the tiles remaining
        if (scorelessTurns == 0){
            for (Player p : playerList){
                p.addScore(p.getTiles().size() * 2);
            }
        }
        notifyViews();
    }

    /**
     * Get the list of players
     */
    public List<Player> getPlayerList(){
        return playerList;
    }

    /**
     * Get the current player
     */
    public Player getCurrentPlayer(){
        return playerList.get(currentPlayer);
    }

    /**
     * Get the index of the current player.
     */
    public int getCurrentPlayerIndex(){
        return currentPlayer;
    }

    /**
     *  Get the board of the game.
     */
    public Board getBoard(){
        return gameBoard;
    }

    /**
     * Get the playing state.
     */
    public boolean isPlaying(){
        return isPlaying;
    }

    /**
     * Get the scoreless turns
     */
    public int getScorelessTurns(){
        return scorelessTurns;
    }

    /**
     * Get the amount of tiles remaining
     */
    public int getTilesRemaining(){
        return bagOfTiles.getRemainingTiles();
    }
    /**
     * Returns if it is the first turn or not
     */
    public boolean isFirstTurn(){
        return isFirstTurn;
    }

    public void removeTile(){ bagOfTiles.removeRandomTile();}

    public Dictionary getDictionary(){ return gameDictionary; }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
        in.defaultReadObject();
        views = new ArrayList<>(); //here we reset the views
        gameDictionary = new Dictionary(); // here we recreate a dictionary
    }


}
