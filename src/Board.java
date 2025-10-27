import java.util.ArrayList;
import java.util.List;

/**
 * Board class for Scrabble game is a 15x15 game board.
 * It handles tile placement, position, validation, and also baord management
 *
 * @author Emmanuel Konate 101322259
 * @version 1.0
 */

public class Board {

    //board size is 15x15 and there is a center area which is [7][7]
    private static final int BOARD_SIZE = 15;
    private static final int CENTER_ROW = 7;
    private static final int CENTER_COL = 7;

    private Tile[][] grid;
    private boolean firstWordPlaced;

    /**
     * Constructor initializing the empty 15x15 board.
     */
    public Board(){
        this.grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        this.firstWordPlaced = false;
    }

    /**
     * Checks if a postion is valid for tile placement.
     * it checks if position is available
     *
     * @param col  the column index from 0 to 14
     * @param row the row index from 0 to 14
     * @return true if position is valid and available, false if not.
     */
    public boolean validPosition(int row, int col){
        //checks the bounds

        if(row< 0 || row >=BOARD_SIZE || col < 0 || col >= BOARD_SIZE){
            return false;
        }

        //desired position is valid and is with bounds
        return true;
    }

    /**
     * Gets the tile at a specific positon.
     *
     * @param row the row index from 0 to 14
     * @param col the column index from 0 to 14
     * @return the Tile at position, or null if empty or invalid position
     */
    public Tile getPosition(int row, int col){
        if(!validPosition(row, col)){
            return null;
        }
        return grid[row][col];
    }

    /**
     * Places the tiles on the board for a word.
     * This is the main way to place words when playing
     *
     * @param rowLabel row letter from A to O
     * @param col column number from 1 to 15
     * @param direction either ACROSS or DOWN
     * @param word the word to place
     * @param player the player placing the word
     * @return true if words fits within bounds
     */
    public boolean placeTiles(String rowLabel, int col, String direction, String word, Player player){
        //Convert to the 0 indexed
        int row = rowLabel.charAt(0) - 'A';
        col = col - 1;

        //checks if within bounds
        if (!isWordInBounds(row, col, direction, word)){
            return false;
        }

        //first word must be at center square
        if(!(firstWordPlaced) && !coversCenter(row, col, direction, word)){
            System.out.println("First word must cover the center square!");
            return false;
        }

        //checks that the tiles are not overwriting current tiles
        if(!canPlaceTiles(row, col, direction, word)){
            System.out.println("Cannot overwrite exisiting tiles!");
            return false;
        }

        //check if word connects to existing tiles
        if(firstWordPlaced && !wordConnects(row, col, direction, word)){
            System.out.println("Word must connect to existing tiles!");
            return false;
        }

        // check if player has the tiles needed
        if (!playerHasTiles(player, row, col, direction, word)){
            System.out.println("Player doesn't have the required tiles!");
            return false;
        }

        //place the tiles on the board
        performPlacement(player, row, col, direction, word);
        firstWordPlaced = true;
        return true;
    }

    /**
     * Checks if word placement is within the bounds of the board.
     *
     * @param col starting column
     * @param row starting row
     * @param direction "ACROSS" or "DOWN"
     * @param word the word to place
     * @return true if word fits within the bounds
     */
    private boolean isWordInBounds(int row, int col, String direction, String word){
        if (direction.equalsIgnoreCase("ACROSS")){
    return validPosition(row, col) && col + word.length() <= BOARD_SIZE;
        } else if (direction.equalsIgnoreCase("DOWN")){
            return validPosition(row, col) && row + word.length() <= BOARD_SIZE;
        }
        return false;
    }

    /**
     * Checks if word covers the center square for the first word.
     *
     *@param row starting row
     *@param col starting column
     * @param direction is either ACROSS or DOWN
     * @param word the word
     * @return true if word covers the center square and false if not
     */
    private boolean coversCenter(int row, int col, String direction, String word){
        if (direction.equalsIgnoreCase("ACROSS")){
            return row == CENTER_ROW && col <= CENTER_COL && col + word.length() > CENTER_COL;
        } else if (direction.equalsIgnoreCase("DOWN")){
            return col == CENTER_COL && row <= CENTER_ROW && row + word.length() > CENTER_ROW;
        }
        return false;
    }

    /**
     * Checks if tiles can be put without placing over different tiles.
     * Allows to reuse existing tiles it they are the same.
     *
     * @param row starting row
     * @param col starting column
     * @param word the word to place
     * @param direction either ACROSS or DOWN
     * @return true if tiles can be placed
     */
    private boolean canPlaceTiles(int row, int col, String direction, String word){
        for (int i = 0; i < word.length(); i++){
            int r = direction.equalsIgnoreCase("ACROSS") ? row : row + i;
            int c = direction.equalsIgnoreCase("ACROSS") ? col + i : col;

            Tile existingTile = grid[r][c];

            //if the tile exists and is different letter, we can't place
            if (existingTile != null && existingTile.getCharacter() != word.charAt(i)){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if word connects to current tiles on board.
     * It is mandatory for all words after the first word.
     *
     * @param row starting row
     * @param col starting column
     * @param direction either ACROSS or DOWN
     * @param word the word
     * @return true if the word connects to current tiles
     */
    private boolean wordConnects(int row, int col, String direction, String word){
        for (int i = 0; i < word.length(); i++){
            int r = direction.equalsIgnoreCase("ACROSS") ? row : row + i;
            int c = direction.equalsIgnoreCase("ACROSS") ? col + i : col;

            // if the position uses a current tile it does connect
            if (grid[r][c] != null){
                return true;
            }

            //check the neighbouring positions
            if (hasNeighbourTile(r, c)){
                return true;
            }

        }
        return false;
    }

    /**
     * Checks if a position has a neighbour tile.
     *
     * @param row the row
     * @param col the column
     * @return true if any neighbouring position has a tile
     */
    private boolean hasNeighbourTile(int row, int col){
        //checks up, down, left and right
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions){
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (validPosition(newRow, newCol) && grid[newRow][newCol] != null){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if player has all tiles needed for the word.
     *
     * @param player the player
     * @param row the starting row
     * @param col the starting column
     * @param direction either ACROSS or DOWN
     * @param word the word
     * @return true if player has the required tiles
     */
    private boolean playerHasTiles(Player player, int row, int col, String direction, String word){
        List<Character> tilesNeeded = new ArrayList<>();

        //checks which tiles are actually needed and not already seen on the board
        for (int i = 0; i < word.length(); i++){
            int r = direction.equalsIgnoreCase("ACROSS") ? row : row + i;
            int c = direction.equalsIgnoreCase("ACROSS") ? col + i : col;

            //if the position is empty the player will have to check the tile

            if(grid[r][c] == null){
                tilesNeeded.add(word.charAt(i));
            }
        }
        //checks if player has all needed tiles
        List<Tile> playerTiles = new ArrayList<>(player.getTiles());

        for(char needed : tilesNeeded){
            boolean found = false;
            for (int i = 0; i < playerTiles.size(); i++){
                if (playerTiles.get(i).getCharacter() == needed){
                    playerTiles.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found){
                return false;
            }
        }
        return true;
    }

    /**
     * Actually places the tiles on the board.
     * It also removes the tiles from the player's hand and places them on board.
     *
     * @param player the player
     * @param row the starting row
     * @param col the staarting column
     * @param direction either ACROSS or DOWN
     * @param word the word
     */
    private void performPlacement(Player player, int row, int col, String direction, String word) {
        for (int i = 0; i < word.length(); i++) {
            int r = direction.equalsIgnoreCase("ACROSS") ? row : row + i;
            int c = direction.equalsIgnoreCase("ACROSS") ? col + i : col;

            //only places if the position is empty
            if (grid[r][c] == null) {
                char letter = word.charAt(i);

                Tile tile = player.removeTile(letter);
                if (tile != null) {
                    grid[r][c] = tile;
                }
            }
        }
    }

    /**
     * Clears the entire board
     *
     * @return true if the board has cleared succesfully
     */
    public boolean clearBoard(){
        for (int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                grid[i][j] = null;
            }
        }
        firstWordPlaced = false;
        return true;
    }

    /**
     * Checks if the board is empty
     *
     * @return true if no tiles are on the board and false if otherwise
     */
    public boolean isEmpty(){
        for(int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                if (grid[i][j] != null){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets all words at a precised position.
     */
    public void getWordsAtPosition(int row, int col){
        if (!validPosition(row, col)){
            System.out.println("Invalid Position");
            return;
        }

        //words on the horizontal
        String horizontal = getHorizontalWord(row, col);
        if(horizontal.length() > 1){
            System.out.println("Horizontal word: " + horizontal);
        }

        //words on the vertical
        String vertical = getVerticalWord(row, col);
        if(vertical.length() > 1){
            System.out.println("Vertical word: " + vertical);
        }
    }

    /**
     * gets a horizontal word at a precised position
     */
    private String getHorizontalWord(int row, int col){
        //find start of word
        int startCol = col;
        while (startCol > 0 && grid[row][startCol - 1] != null){
            startCol--;
        }

        //constructs the word
        StringBuilder word = new StringBuilder();
        for (int c = startCol; c < BOARD_SIZE && grid[row][c] != null; c++){
            word.append(grid[row][c].getCharacter());
        }
        return word.toString();
    }

    /**
     * gets a vertical word at a precised position
     */
    private String getVerticalWord(int row, int col){
        //find start of word
        int startRow = row;
        while (startRow > 0 && grid[startRow - 1][col] != null){
            startRow--;
        }

        //constructs the word
        StringBuilder word = new StringBuilder();
        for (int r = startRow; r < BOARD_SIZE && grid[r][col] != null; r++){
            word.append(grid[r][col].getCharacter());
        }
        return word.toString();
    }

    /**
     * Displays the board in text format
     * shows the 15x15 grid with all the placed tiles.
     */
    public void display(){
        System.out.println(("\n  " + " 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15"));
        for (int i = 0; i < BOARD_SIZE; i++){
            //print A to O
            System.out.print((char)('A' + i) + " ");

            //print each cell

            for (int j = 0; j < BOARD_SIZE; j++){
                if (grid[i][j] != null){
                    System.out.print("[" + grid[i][j].getCharacter()+ "]");
                } else {
                    //center symbol
                    if (i == CENTER_ROW && j == CENTER_COL){
                        System.out.print(" * ");
                    } else {
                        System.out.print(" . ");
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * Gets the board size.
     *
     * @return the size of the board
     */
    public int getBoardSize(){
        return BOARD_SIZE;
    }

    /**
     * Checks if the first word has been placed
     *
     * @return true if the first word was placed, false if not
     */
    public boolean isFirstWordPlaced(){
        return firstWordPlaced;
    }
}
