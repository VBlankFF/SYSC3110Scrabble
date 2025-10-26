import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ScrabbleGame {
    private List<Player> playerList;
    private Board gameBoard;
    private int currentPlayer;
    private boolean isPlaying;
    private ScrabbleDictionary gameDictionary;
    private Scanner userInput;
    private TileBag bagOfTiles;
    public ScrabbleGame()
    {
        playerList = new ArrayList<>();
        gameBoard = new Board();
        currentPlayer = 0;
        isPlaying = false;
        userInput = new Scanner(System.in);
        bagOfTiles = new TileBag();
        // unsure if dictionary is static or not
    }
    public void play()
    {
        isPlaying = true;
        while(isPlaying) {
            currentPlayer = 0;
            gameBoard.clearBoard();
            playerList = new ArrayList<>();
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
            for (int i = 0; i < numberOfPlayers; i++)
            {
                playerList.add(new Player(i));
            }
            while (isPlaying) {
                System.out.println("It's player " + (currentPlayer + 1) + "'s turn.");
                System.out.println("Choose an option: 1 (See hand and score), 2 (Swap tiles), 3 (Play tiles), 4 (Pass turn)\n");
                int choice = userInput.nextInt();
                switch (choice) {
                    case 1:
                        String tilesString = "";
                        List<Tile> tiles = playerList.get(currentPlayer).getTiles();
                        for (int i = 0; i < playerList.get(currentPlayer).getTiles(); i++) {
                            tilesString += tiles.get(i);
                        }
                        System.out.println("Hand: " + tilesString + " Score: " + playerList.get(currentPlayer).getScore());
                        break;
                    case 2:
                        System.out.println("Input tiles to swap: ");
                        String swapTiles = userInput.next();
                        List<Tiles> playerTiles = playerList.get(currentPlayer).getTiles();
                        List<Tiles> removedTiles = new List<Tiles>();
                        for (int i = 0; i < swapTiles.length(); i++) {
                            for (int j = 0; j < playerTiles.size(); j++) {
                                if (playerTiles.get(j).getCharacter() == swapTiles.charAt(i)) {
                                    removedTiles.add(playerTiles.get(j));
                                    playerTiles.remove(j);
                                    break;
                                }
                                if (j == playerTiles.size() - 1) {
                                    System.out.println("Missing tile " + swapTiles.charAt(i));
                                    playerTiles.addAll(removedTiles);
                                    i = swapTiles.length();
                                    break;
                                }
                            }
                            if (i == swapTiles.length() - 1) {
                                System.out.println("Tiles swapped!");
                                nextTurn();
                            }
                        }
                        break;
                    case 3:
                        System.out.println("Input x of starting position: ");
                        int x = userInput.nextInt();
                        System.out.println("Input y of starting position: ");
                        int y = userInput.nextInt();
                        if (!gameBoard.validPosition(x, y)) {
                            System.out.println("Position out of bounds.\n");
                            break;
                        }
                        System.out.println("Enter word length (including existing tiles): ");
                        int wordLength = userInput.nextInt();
                        System.out.println("Enter direction (1 for right, 2 for down): ");
                        int direction = userInput.nextInt();
                        boolean isValid = false;
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
                        String wordString = "";
                        switch (direction) {
                            case 1:
                                for (int i = x; i < wordLength + x; i++) {
                                    if (board.getPosition(i, y) == null) {
                                        wordString += "_";
                                    } else {
                                        wordString += board.getPosition(i, y).getCharacter();
                                    }
                                }
                                break;
                            case 2:
                                for (int i = y; i < wordLength + y; i++) {
                                    if (board.getPosition(x, i) == null) {
                                        wordString += "_";
                                    } else {
                                        wordString += board.getPosition(x, i).getCharacter();
                                    }
                                }
                                break;
                        }
                        System.out.println("Empty spots are " + wordString + "\n Enter the word you want to play: ");
                        String intendedWord = userInput.next();
                        List<Tile> handTiles = playerList.get(currentPlayer).getTiles();
                        List<Tile> tempTiles = new List<Tile>();
                        for (int i = 0; i < intendedWord.length(); i++) {
                            if (wordString.charAt(i) != '_') {
                                continue;
                            }
                            for (int j = 0; j < handTiles.size(); j++) {
                                if (handTiles.get(j).getCharacter() == intendedWord.charAt(i)) {
                                    tempTiles.add(handTiles.get(j));
                                    handTiles.remove(j);
                                    break;
                                }
                                if (j == handTiles.size() - 1) {
                                    System.out.println("Missing tile " + intendedWord.charAt(i));
                                    handTiles.addAll(tempTiles);
                                    i = handTiles.length();
                                    break;
                                }
                            }
                            if (i == handTiles.length() - 1) {
                                int score = scoreCalculation(x, y, direction, intendedWord, tempTiles);
                                if (score > 0) {
                                    if (direction == 1) {
                                        int wordDistance = x;
                                        while (!tempTiles.isEmpty()) {
                                            if (gameBoard.getPosition(wordDistance, y) == null) {
                                                gameBoard.placeTile(wordDistance, y, tempTiles.getFirst());
                                                tempTiles.remove(tempTiles.getFirst());
                                            }
                                            wordDistance++;
                                        }
                                    } else {
                                        int wordDistance = y;
                                        while (!tempTiles.isEmpty()) {
                                            if (gameBoard.getPosition(x, wordDistance) == null) {
                                                gameBoard.placeTile(x, wordDistance, tempTiles.getFirst());
                                                tempTiles.remove(tempTiles.getFirst());
                                            }
                                            wordDistance++;
                                        }
                                    }
                                    playerList.get(currentPlayer).setScore(score + playerList.getScore(currentPlayer));
                                    System.out.println("Word played successfully.");
                                    nextTurn();
                                }
                            }
                        }
                        break;
                    case 4:
                        nextTurn();
                        break;
                }
            }
            end();
        }
    }
    public void nextTurn()
    {
        displayBoard();
        while (playerList.get(currentPlayer).getTiles().size() < 7 || !bagOfTiles.isEmpty())
        {
            playerList.get(currentPlayer).addTile(bagOfTiles.getRandomTile());
        }
        if (playerList.get(currentPlayer).getTiles().size() == 0)
        {
            isPlaying = false;
        }
        if (currentPlayer == playerList.size() - 1)
        {
            currentPlayer = 0;
        }
        else
        {
            currentPlayer++;
        }
    }
    public void end()
    {
        for (int i = 0; i < playerList.size(); i++)
        {
            if (i == currentPlayer)
            {
                continue;
            }
            playerList.get(currentPlayer).addScore(playerList.get(currentPlayer).getScore + playerList.get(i).getTiles().size() * 2);
        }
        System.out.println("The game has ended.\n");
        displayScores();
        System.out.println("Play again? (y/n): ");
        isPlaying = userInput.next().charAt(0) == 'y';
    }
    public void displayScores()
    {
        int highest = 0;
        int indexOfHighest = 0;
        for (int i = 0; i < playerList.size(); i++)
        {
            System.out.println("Player " + (i + 1) + " has " + playerList.get(i).getScore() + " points.\n");
            if (playerList.get(i).getScore() > highest)
            {
                highest = playerList.get(i).getScore();
                indexOfHighest = i;
            }
        }
        System.out.println("Player " + (indexOfHighest + 1) + " wins!\n");
    }
    public void displayBoard()
    {
        for (int i = 0; gameBoard.validPosition(0, i); i++) {
            String rowString = "";
            for (int j = 0; gameBoard.validPosition(j, 0); j++) {
                rowString += gameBoard.getTile(j, i);
            }
            System.out.println(rowString);
        }
    }
    // A score of 0 is reserved for invalid plays
    public int scoreCalculation(int x, int y, int direction, String word, List<Tile> usedTiles)
    {
        Set<Tile> involvedTiles = new Set<Tile>();
        involvedTiles.addAll(usedTiles);
        boolean connectedToExistingTile = false;
        boolean isInvalid = false;
        if (direction == 2)
        {
            int temp = x;
            x = y;
            y = temp;
        }
        String checkWord = word;
        // check for letters to the left that are a part of the word
        for (int i = x; gameBoard.validPosition(i, y); i--)
        {
            if (gameBoard.getPosition(i, y) == null)
            {
                break;
            }
            checkWord = gameBoard.getPosition(i, y) + checkWord;
            involvedTiles.add(gameBoard.getPosition(i, y));
            connectedToExistingTile = true;
        }
        for (int i = x + word.length(); gameBoard.validPosition(i, y); i++)
        {
            if (gameBoard.getPosition(i, y) == null)
            {
                break;
            }
            checkWord += gameBoard.getPosition(i, y);
            involvedTiles.add(gameBoard.getPosition(i, y));
            connectedToExistingTile = true;
        }
        if (!gameDictionary.validWord(checkWord))
        {
            System.out.println(checkWord + " is not a word.");
            isInvalid = true;
        }
        for (int i = x; i < word.length() + x; i++)
        {
            checkWord = word.substring(i, i + 1);
            for (int j = y; gameBoard.validPosition(i, j); j--)
            {
                if (gameBoard.getPosition(i, j) == null)
                {
                    break;
                }
                checkWord = gameBoard.getPosition(i, j) + checkWord;
                involvedTiles.add(gameBoard.getPosition(i, j));
                connectedToExistingTile = true;
            }
            for (int j = y + word.length(); gameBoard.validPosition(i, j); j++)
            {
                if (gameBoard.getPosition(i, j) == null)
                {
                    break;
                }
                checkWord += gameBoard.getPosition(i, j);
                involvedTiles.add(gameBoard.getPosition(i, j));
                connectedToExistingTile = true;
            }
            if (!gameDictionary.validWord(checkWord) && checkWord.length() > 1)
            {
                System.out.println(checkWord + " is not a word.");
                isInvalid = true;
            }
        }
        if (!connectedToExistingTile || isInvalid)
        {
            return 0;
        }
        int score = 0;
        Tile[] scoringTiles = involvedTiles.toArray();
        for (int i = 0; i < involvedTiles.size(); i++)
        {
            score += scoringTiles[i].getValue();
        }
        return score;
    }
}
