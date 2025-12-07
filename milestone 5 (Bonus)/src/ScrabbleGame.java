import java.util.*;

/**
 * A class that models a game of Scrabble.
 *
 * @version 1.0
 */

 public class ScrabbleGame {
    private List<Player> playerList;
    private Board gameBoard;
    private int currentPlayer;
    private boolean isPlaying;
    private Dictionary gameDictionary;
    private Scanner userInput;
    private TileBag bagOfTiles;
    private boolean isFirstTurn;
    private int scorelessTurns;
    public ScrabbleGame()
    {
        playerList = new ArrayList<>();
        gameBoard = new Board();
        currentPlayer = 0;
        isPlaying = false;
        userInput = new Scanner(System.in);
        bagOfTiles = new TileBag();
        gameDictionary = new Dictionary();
    }

    /**
     * The main loop for the game. Exits when the game ends and the user does not wish to play again.
     */
    public void play()
    {
        isPlaying = true;
        while(isPlaying) {
            // initialize game variables
            currentPlayer = 0;
            isFirstTurn = true;
            gameBoard.clearBoard();
            playerList = new ArrayList<>();
            scorelessTurns = 0;
            System.out.println("Welcome to the Scrabble Game!");
            int numberOfPlayers;
            while (true) {
                System.out.println("Enter number of players: ");
                numberOfPlayers = userInput.nextInt();
                if (numberOfPlayers < 2 || numberOfPlayers > 4) {
                    System.out.println("Number of players must be between 2 and 4.");
                } else {
                    break;
                }
            }
            // Initialize each player
            for (int i = 0; i < numberOfPlayers; i++)
            {
                System.out.print("What is player " + (i + 1) + "'s name? ");
                String playerName = userInput.next();
                playerList.add( new Player(0, playerName, i));
                while (playerList.get(i).getTiles().size() < 7 && !bagOfTiles.isEmpty())
                {
                    playerList.get(i).addTile(bagOfTiles.getRandomTile());
                }
            }
            displayBoard();
            // main game loop
            while (isPlaying) {
                System.out.println("It's " + playerList.get(currentPlayer).getName() + "'s turn.");
                if (scorelessTurns >= 6)
                {
                    System.out.println("6 or more turns have passed without scoring. End game? (y/n): ");
                    if (Objects.equals(userInput.next(), "y"))
                    {
                        isPlaying = false;
                        break;
                    }
                }
                System.out.println("Choose an option: 1 (See hand and score), 2 (Swap tiles), 3 (Play tiles), 4 (Pass turn)");
                int choice = userInput.nextInt();
                // Runs the code corresponding to the user's choice
                switch (choice) {
                    // Print the Player's current hand and score. Does not end the turn.
                    case 1:
                        String tilesString = "";
                        List<Tile> tiles = playerList.get(currentPlayer).getTiles();
                        for (int i = 0; i < playerList.get(currentPlayer).getTiles().size(); i++) {
                            tilesString += tiles.get(i).getCharacter();
                        }
                        System.out.println("Hand: " + tilesString + " Score: " + playerList.get(currentPlayer).getScore());
                        break;
                    // Attempts to swap tiles. Doesn't end turn if unsuccessful.
                    case 2:
                        if (bagOfTiles.getRemainingTiles() < 7)
                        {
                            System.out.println("There are not enough tiles remaining in the bag to swap tiles.");
                        }
                        System.out.println("Input tiles to swap: ");
                        String swapTiles = userInput.next();
                        List<Tile> playerTiles = playerList.get(currentPlayer).getTiles();
                        // Swapped Tiles are moved to here in case swap fails in the middle
                        List<Tile> removedTiles = new ArrayList<Tile>();
                        for (int i = 0; i < swapTiles.length(); i++) {
                            // Search for a matching tile
                            for (int j = 0; j < playerTiles.size(); j++) {
                                // If a tile was found, move it from the hand to the removed tiles list
                                if (playerTiles.get(j).getCharacter() == swapTiles.charAt(i)) {
                                    removedTiles.add(playerTiles.get(j));
                                    playerTiles.remove(j);
                                    break;
                                }
                                // If this was the last tile in the hand, the swap fails. return all tiles to hand
                                if (j == playerTiles.size() - 1) {
                                    System.out.println("Missing tile " + swapTiles.charAt(i));
                                    playerTiles.addAll(removedTiles);
                                    i = swapTiles.length();
                                    break;
                                }
                            }
                            // The swap has succeeded. Finalize and end Player's turn.
                            if (i == swapTiles.length() - 1) {
                                for (int swappedTiles = 0; swappedTiles < swapTiles.length(); swappedTiles++)
                                {
                                    bagOfTiles.returnTile(removedTiles.get(swappedTiles));
                                }
                                System.out.println("Tiles swapped!");
                                nextTurn();
                                scorelessTurns++;
                            }
                        }
                        break;
                    // Play word. Ends turn if successful.
                    case 3:
                        // Get the desired starting position
                        System.out.println("Input row of starting position (1-15): ");
                        int x = userInput.nextInt() - 1;
                        System.out.println("Input column of starting position (A-O): ");
                        int y = columnToNumber(userInput.next().charAt(0));
                        // Fail if the starting position is not in bounds
                        if (!gameBoard.validPosition(x, y)) {
                            System.out.println("Position out of bounds.\n");
                            break;
                        }
                        System.out.println("Enter word length (including existing tiles): ");
                        int wordLength = userInput.nextInt();
                        System.out.println("Enter direction (1 for right, 2 for down): ");
                        int direction = userInput.nextInt();
                        boolean isValid = false;
                        // Check if the entire word would be in the bounds of the board
                        switch (direction) {
                            case 1:
                                if (!gameBoard.validPosition(x + wordLength, y)) {
                                    System.out.println("Word goes out of bounds.\n");
                                    break;
                                } else {
                                    isValid = true;
                                }
                                break;
                            case 2:
                                if (!gameBoard.validPosition(x, y + wordLength)) {
                                    System.out.println("Word goes out of bounds.\n");
                                    break;
                                } else {
                                    isValid = true;
                                }
                                break;
                            default:
                                System.out.println("Invalid input. Try again.\n");
                                break;
                        }
                        if (!isValid) {
                            break;
                        }
                        // Get the existing letters and gaps where the word will be placed and print them.
                        String wordString = "";
                        switch (direction) {
                            case 1:
                                for (int i = x; i < wordLength + x; i++) {
                                    if (gameBoard.getPosition(i, y) == null) {
                                        wordString += "_";
                                    } else {
                                        wordString += gameBoard.getPosition(i, y).getCharacter();
                                    }
                                }
                                break;
                            case 2:
                                for (int i = y; i < wordLength + y; i++) {
                                    if (gameBoard.getPosition(x, i) == null) {
                                        wordString += "_";
                                    } else {
                                        wordString += gameBoard.getPosition(x, i).getCharacter();
                                    }
                                }
                                break;
                        }
                        System.out.println("Empty spots are " + wordString + "\n Enter the word you want to play: ");
                        String intendedWord = userInput.next();
                        List<Tile> handTiles = playerList.get(currentPlayer).getTiles();
                        // Tiles removed from hand are returned if placement fails in the middle
                        List<Tile> tempTiles = new ArrayList<Tile>();
                        // Verify player has the Tiles to play the word
                        for (int i = 0; i < intendedWord.length(); i++) {
                            // Only play a tile if none already exist in this position
                            if (wordString.charAt(i) == '_') {
                                for (int j = 0; j < handTiles.size(); j++) {
                                    if (handTiles.get(j).getCharacter() == intendedWord.charAt(i)) {
                                        tempTiles.add(handTiles.get(j));
                                        handTiles.remove(j);
                                        break;
                                    }
                                    // Reached end of hand without finding tile, so player doesn't have it
                                    if (j == handTiles.size() - 1) {
                                        System.out.println("Missing tile " + intendedWord.charAt(i));
                                        handTiles.addAll(tempTiles);
                                        i = handTiles.size();
                                        break;
                                    }
                                }
                            }
                            // Player has every tile
                            if (i == intendedWord.length() - 1) {
                                // Calculate the score of the play
                                int score = scoreCalculation(x, y, direction, intendedWord, tempTiles);
                                // If play is valid
                                if (score > 0) {
                                    // place the tiles (right)
                                    if (direction == 1) {
                                        int wordDistance = x;
                                        while (!tempTiles.isEmpty()) {
                                            if (gameBoard.getPosition(wordDistance, y) == null) {
                                                gameBoard.placeTile(wordDistance, y, tempTiles.getFirst());
                                                tempTiles.remove(tempTiles.getFirst());
                                            }
                                            wordDistance++;
                                        }
                                    }
                                    // place the tiles (down)
                                    else {
                                        int wordDistance = y;
                                        while (!tempTiles.isEmpty()) {
                                            if (gameBoard.getPosition(x, wordDistance) == null) {
                                                gameBoard.placeTile(x, wordDistance, tempTiles.getFirst());
                                                tempTiles.remove(tempTiles.getFirst());
                                            }
                                            wordDistance++;
                                        }
                                    }
                                    playerList.get(currentPlayer).addScore(score);
                                    System.out.println("Word played successfully.");
                                    isFirstTurn = false;
                                    nextTurn();
                                    scorelessTurns = 0;
                                }
                                else {
                                    // Play is invalid, so return tiles to Player's hand
                                    playerList.get(currentPlayer).getTiles().addAll(tempTiles);
                                }
                            }
                        }
                        break;
                    // Pass turn
                    case 4:
                        nextTurn();
                        scorelessTurns++;
                        break;
                    default:
                        System.out.println("Invalid input. Try again.\n");
                        break;
                }
            }
            end();
        }
    }

    /**
     * Refills the current player's hand, if possible, then switches the active Player to
     * the next Player. Prints the game's board.
     */
    public void nextTurn()
    {
        while (playerList.get(currentPlayer).getTiles().size() < 7 && !bagOfTiles.isEmpty())
        {
            playerList.get(currentPlayer).addTile(bagOfTiles.getRandomTile());
        }
        if (playerList.get(currentPlayer).getTiles().isEmpty())
        {
            isPlaying = false;
        }
        else if (currentPlayer == playerList.size() - 1)
        {
            currentPlayer = 0;
        }
        else
        {
            currentPlayer++;
        }
        displayBoard();
    }

    /**
     * Print the current board state to the screen. x is right, and y is down. _ marks empty spaces.
     */
    public void displayBoard()
    {
        for (int i = 0; i < gameBoard.getBoardSize(); i++)
        {
            for (int j = 0; j < gameBoard.getBoardSize(); j++)
            {
                Tile thisTile = gameBoard.getPosition(j, i);
                if (thisTile == null)
                {
                    System.out.print("_");
                }
                else
                {
                    System.out.print(thisTile.getCharacter());
                }
            }
            System.out.print("\n");
        }
    }

    /**
     * Ends the current game. Prints a message that the game has ended, the scores of each Player,
     * the winner (or tie), and asks the user if they want to play again. If yes, start a new game.
     */
    public void end()
    {
        // Add 2 points to the player who is at 0 tiles for every tile the remaining players have
        // (unless the game was ended due to scoreless turns)
        if (scorelessTurns == 0)
        {
            for (int i = 0; i < playerList.size(); i++)
            {
                Player curPlayer = playerList.get(i);
                curPlayer.addScore(curPlayer.getTiles().size() * 2);
            }
        }
        System.out.println("The game has ended.");
        displayScores();
        System.out.println("Play again? (y/n): ");
        isPlaying = userInput.next().charAt(0) == 'y';
    }

    /**
     * Display the name and score of every Player in the game. Prints the name of the
     * Player who has the most points if there is no tie. If there is a tie, print that
     * instead.
     */
    public void displayScores()
    {
        int highest = 0;
        int indexOfHighest = 0;
        boolean isTie = false;
        for (int i = 0; i < playerList.size(); i++)
        {
            System.out.println(playerList.get(i).getName() + " has " + playerList.get(i).getScore() + " points.");
            if (playerList.get(i).getScore() > highest)
            {
                highest = playerList.get(i).getScore();
                indexOfHighest = i;
                isTie = false;
            }
            else if (playerList.get(i).getScore() == highest)
            {
                isTie = true;
            }
        }
        if (isTie)
        {
            System.out.println("There's a tie!");
        }
        else
        {
            System.out.println("Player " + (indexOfHighest + 1) + " wins!");
        }
    }

    /**
     * Calculate the value of the play and checks if it is valid. If it isn't, inform
     * the user and return 0. If it is, return the score.
     *
     * @param x The starting x position of the play.
     * @param y The starting y position of the play.
     * @param direction The direction of the play (1 is right, 2 is down)
     * @param word The word played.
     * @param usedTiles The tiles used by the player as part of this play.
     * @return The score of the play, or 0 if the play is invalid.
     */
    public int scoreCalculation(int x, int y, int direction, String word, List<Tile> usedTiles)
    {
        // every tile involved in the play. will be summed up to
        Set<Tile> involvedTiles = new HashSet<Tile>(usedTiles);
        boolean connectedToExistingTile = false;
        boolean isInvalid = false;
        String checkWord = word;
        // is word vertical?
        if (direction == 2)
        {
            // check for letters above that are a part of the word (y down)
            for (int i = y - 1; gameBoard.validPosition(x, i); i--)
            {
                if (gameBoard.getPosition(x, i) == null)
                {
                    break;
                }
                checkWord = gameBoard.getPosition(x, i).getCharacter() + checkWord;
                involvedTiles.add(gameBoard.getPosition(i, y));
                connectedToExistingTile = true;
            }
            // same, but below
            for (int i = y + word.length(); gameBoard.validPosition(x, i); i++)
            {
                if (gameBoard.getPosition(x, i) == null)
                {
                    break;
                }
                checkWord += gameBoard.getPosition(x, i).getCharacter();
                involvedTiles.add(gameBoard.getPosition(x, i));
                connectedToExistingTile = true;
            }
        }
        // word is horizontal
        else
        {
            // check for letters to the left that are a part of the word
            for (int i = x - 1; gameBoard.validPosition(i, y); i--)
            {
                if (gameBoard.getPosition(i, y) == null)
                {
                    break;
                }
                checkWord = gameBoard.getPosition(i, y).getCharacter() + checkWord;
                involvedTiles.add(gameBoard.getPosition(i, y));
                connectedToExistingTile = true;
            }
            // same, but to the right
            for (int i = x + word.length(); gameBoard.validPosition(i, y); i++)
            {
                if (gameBoard.getPosition(i, y) == null)
                {
                    break;
                }
                checkWord += gameBoard.getPosition(i, y).getCharacter();
                involvedTiles.add(gameBoard.getPosition(i, y));
                connectedToExistingTile = true;
            }
        }
        // check if the word that was played (including connected tiles) is a word
        if (!gameDictionary.validWord(checkWord.toLowerCase()))
        {
            System.out.println(checkWord + " is not a word.");
            isInvalid = true;
        }
        if (direction == 1)
        {
            // check tiles above and below each played tile for words
            for (int i = x; i < word.length() + x; i++)
            {
                // form a word based on the tiles above and below, and check it
                checkWord = word.substring(i - x, i - x + 1);
                // add tiles above to the word
                for (int j = y - 1; gameBoard.validPosition(i, j); j--)
                {
                    if (gameBoard.getPosition(i, j) == null)
                    {
                        break;
                    }
                    checkWord = gameBoard.getPosition(i, j).getCharacter() + checkWord;
                    involvedTiles.add(gameBoard.getPosition(i, j));
                    connectedToExistingTile = true;
                }
                // add tiles below to the word
                for (int j = y + 1; gameBoard.validPosition(i, j); j++)
                {
                    if (gameBoard.getPosition(i, j) == null)
                    {
                        break;
                    }
                    checkWord += gameBoard.getPosition(i, j).getCharacter();
                    involvedTiles.add(gameBoard.getPosition(i, j));
                    connectedToExistingTile = true;
                }
                // check if the word is valid (if a word exists)
                if (!gameDictionary.validWord(checkWord) && checkWord.length() > 1)
                {
                    System.out.println(checkWord + " is not a word.");
                    isInvalid = true;
                }
            }
        }
        else
        {
            // check tiles to the left and right each played tile for words
            for (int i = y; i < word.length() + y; i++)
            {
                // form a word based on the tiles to the left and right, and check it
                checkWord = word.substring(i - y, i - y + 1);
                // add tiles from the left of this tile to the word
                for (int j = x - 1; gameBoard.validPosition(j, i); j--)
                {
                    if (gameBoard.getPosition(j, i) == null)
                    {
                        break;
                    }
                    checkWord = gameBoard.getPosition(j, i).getCharacter() + checkWord;
                    involvedTiles.add(gameBoard.getPosition(j, i));
                    connectedToExistingTile = true;
                }
                // add tiles from the right of this tile to the word
                for (int j = x + 1; gameBoard.validPosition(j, i); j++)
                {
                    if (gameBoard.getPosition(j, i) == null)
                    {
                        break;
                    }
                    checkWord += gameBoard.getPosition(j, i).getCharacter();
                    involvedTiles.add(gameBoard.getPosition(j, i));
                    connectedToExistingTile = true;
                }
                // check if word is valid (if a word exists)
                if (!gameDictionary.validWord(checkWord) && checkWord.length() > 1)
                {
                    System.out.println(checkWord + " is not a word.");
                    isInvalid = true;
                }
            }
        }
        /* a play is valid if:
        all the words it contains are real words
        it is connected to an existing tile (or the center space for the first move)
         */
        if ((!connectedToExistingTile && !IsFirstPlay(x, y, word.length(), direction)) || isInvalid)
        {
            // tell the player the play isn't connected to the board (if it is not)
            if (!connectedToExistingTile && !IsFirstPlay(x, y, word.length(), direction))
            {
                System.out.println("Word is not connected to an existing tile (or the center). ");
            }
            // reserved for invalid plays
            return 0;
        }
        // This should only ever happen if the first play is 1 letter, which is forbidden
        if (involvedTiles.size() == 1)
        {
            System.out.println("First play must be more than one tile.");
            return 0;
        }
        // add up the scores of every tile involved in the play. this is the play's score
        int score = 0;
        Tile[] scoringTiles = involvedTiles.toArray(new Tile[0]);
        for (int i = 0; i < involvedTiles.size(); i++)
        {
            score += scoringTiles[i].getValue();
        }
        return score;
    }

    /**
     * Return true if the play is the first play of the game, and touches the center tile.
     * Return false otherwise.
     *
     * @param x The starting x position of the word
     * @param y The starting y position of the word
     * @param length The length of the word played
     * @param direction The direction of the play (1 is right, 2 is down)
     * @return true if the play is the first play of the game, and touches the center tile, false otherwise.
     */
    public boolean IsFirstPlay(int x, int y, int length, int direction)
    {
        if (direction == 2)
        {
            int temp = x;
            x = y;
            y = temp;
        }
        if (y != 7 || !isFirstTurn)
        {
            return false;
        }
        return x <= 7 && x + length >= 7;
    }

    /**
     * Return the column label of a Scrabble game as its internal y index.
     * @param column The column label
     * @return A y value corresponding to the given label.
     */
    private int columnToNumber(char column)
    {
        return switch (column) {
            case 'A' -> 0;
            case 'B' -> 1;
            case 'C' -> 2;
            case 'D' -> 3;
            case 'E' -> 4;
            case 'F' -> 5;
            case 'G' -> 6;
            case 'H' -> 7;
            case 'I' -> 8;
            case 'J' -> 9;
            case 'K' -> 10;
            case 'L' -> 11;
            case 'M' -> 12;
            case 'N' -> 13;
            case 'O' -> 14;
            default -> -1;
        };
    }
}
