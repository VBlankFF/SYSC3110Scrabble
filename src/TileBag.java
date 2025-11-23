import java.util.*;

/**
 * TileBag class for the Scrabble game. It manages all the tiles.
 * It contains 100 tiles with normal Scrabble distribution.
 * It also gives ways to draw random tiles and check if empty.
 *
 * @author Emmanuel Konate 101322259
 * @version 2.0
 */

public class TileBag {
    //fields and instances
    private List<Tile> tiles;
    private Random random;

    /**
     * Constructor starts the tile bag with 100 tiles.
     * Tiles are then shared/distributed according to Scrabble rules.
     */

    public TileBag(){
        this.tiles = new ArrayList<>();
        this.random = new Random();
        initializeTiles();

    }

    /**
     * Initializes the bag according to Scrabble rules of distribution.
     * There is a total of 100 tiles
     *
     * The distribution works like this :
     * 1 point : E x 12, Ax9, Ix9, Ox8, Nx6, Rx6, Tx6, Lx4, Sx4, Ux4,
     * 2 points :  Dx4,Gx3,
     * 3 points : Bx2, Cx2, Mx2, Px2
     * 4 points : Fx2, Hx2, Vx2, Wx2, Yx2
     * 5 points : Kx1
     * 8 points : Jx1, Xx1
     * 10 points : Qx1, Zx1
     * 0 points : Blank x2
     */

    private void initializeTiles(){
        //add tiles while respecting Scrabble repartition
        addTiles('E', 12); //1 point
        addTiles('A', 9); //1 point
        addTiles('I', 9); //1 point
        addTiles('O', 8); //1 point
        addTiles('N', 6); //1 point
        addTiles('R', 6); //1 point
        addTiles('T', 6); //1 point
        addTiles('L', 4); //1 point
        addTiles('S', 4); //1 point
        addTiles('U', 4); //1 point

        addTiles('D', 4); //2 points
        addTiles('G', 3); //2 points

        addTiles('B', 2); //3 points
        addTiles('C', 2); //3 points
        addTiles('M', 2); //3 points
        addTiles('P', 2); //3 points

        addTiles('F', 2); //4 points
        addTiles('H', 2); //4 points
        addTiles('V', 2); //4 points
        addTiles('W', 2); //4 points
        addTiles('Y', 2); //4 points

        addTiles('K', 1); //5 points

        addTiles('J', 1); //8 points
        addTiles('X', 1); //8 points

        addTiles('Q', 1); //10 points
        addTiles('Z', 1); //10 points
        addTiles(' ', 2);

        //we finish with shuffling the tiles so that they aren't in order
        Collections.shuffle(tiles);
    }

    /**
     * Adds multiple tiles of same letter.
     *
     * @param character the letter to add.
     * @param count the number of tiles to add.
     */
    private void addTiles(char character, int count){
        for (int i = 0; i < count; i++){
            tiles.add(new Tile(character));
        }
    }

    /**
     * Checks if the tile bag is empty.
     *
     * @return true if there are no tiles remaining and false if otherwise
     */
    public boolean isEmpty(){
        return tiles.isEmpty();
    }

    /**
     * Draws a random tile from the bag.
     * Removes and returns one tile from the bag.
     *
     * @return a random Tile, or null if hte bag is empty
     */
    public Tile getRandomTile(){
        if (isEmpty()){
            return null;
        }
        //removes and return the first tile from the bag; the bag has been shuffled
        return tiles.remove(0);
    }

    /**
     * Returns a tile back to the bag.
     * It is used when a player has switched tiles.
     *
     * @param tile the tile to return
     */
    public void returnTile(Tile tile){
        if (tile != null){
            tiles.add(tile);
            //we have to shuffle the bag again
            Collections.shuffle(tiles);
        }
    }

    /**
     * Gets the number of tiles remaining in the bag.
     * Used for Test purposes in TileBagTest
     * @return the number of tiles still remaining in the bag
     */
    public int getRemainingTiles(){
        return tiles.size();
    }

    /**
     * Displays the information about the tile bag
     * It shows the remaining tile count
     * Used for Test purposes in TileBagTest
     */
    public void displayInfo(){
        System.out.println("Tiles remaining in bag: " + getRemainingTiles());
    }

    /**
     * Gets a list of the tile list
     * This method is designed to help with test cases.
     * Used for Test purposes in TileBagTest
     * @return a list of the tiles
     */
    public List<Tile> getTiles(){
        return new ArrayList<>(tiles);
    }

    /**
     * Removes a random tile
     */
    public void removeRandomTile(){
        tiles.remove(0);
    }
}
