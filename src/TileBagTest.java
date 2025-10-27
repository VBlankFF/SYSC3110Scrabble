import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Test class for TileBag.
 * Tests important methods and distribution of tiles.
 *
 * @author Emmanuel Konate 101322259
 * @version 1.0
 */
public class TileBagTest {
    private TileBag tileBag;

    /**
     * Set Up the tests before each test
     */
    @Before
    public void setUp(){
        tileBag = new TileBag();
    }

    /**
     * TileBag starts with 100 tiles
     */
    @Test
    public void testTileBagInitialization(){
        assertNotNull("TileBag should not be null", tileBag);
        assertFalse("New tile bag should not be empty", tileBag.isEmpty());
        assertEquals("Scrabble usually has 100 tiles", 100, tileBag.getRemainingTiles());
    }


    /**
     * Test drawing a tile from the bag.
     */
    @Test
    public void testGetRandomTile(){
        Tile tile = tileBag.getRandomTile();

        assertNotNull("Should draw a tile", tile);
        assertEquals("Should have 99 tiles after drawing one", 99, tileBag.getRemainingTiles());
    }


    /**
     * Test isEmpty works correctly
     */
    @Test
    public void testIsEmpty(){
        assertFalse("New bag should not be empty", tileBag.isEmpty());

        //draw all the tiles

        while (tileBag.getRemainingTiles() > 0){
            tileBag.getRandomTile();
        }
        assertTrue("Bag should be empty after drawing all", tileBag.isEmpty());
    }


    /**
     * Test Returning a tile to the bag
     */
    @Test
    public void testReturnTile(){
        Tile tile = tileBag.getRandomTile();
        assertEquals("Should have 99 tiles", 99, tileBag.getRemainingTiles());

        tileBag.returnTile(tile);
        assertEquals("Should have 100 tiles after returning", 100, tileBag.getRemainingTiles());
    }


    /**
     *  Test total tile count is exactly 100.
     */
    @Test
    public void testTotalTileCount(){
        int count = 0;

        while (!tileBag.isEmpty()){
            tileBag.getRandomTile();
            count++;
        }

        assertEquals("Must have exactly 100 tiles", 100, count);
    }


    /**
     * Test tile distribution is correct
     */
    @Test
    public void testTileDistribution(){
        Map<Character, Integer> distribution = new HashMap<Character, Integer>();

        //count all tiles
        while(!tileBag.isEmpty()){
            Tile tile = tileBag.getRandomTile();
            char letter = tile.getCharacter();

            if (distribution.containsKey(letter)){
                distribution.put(letter, distribution.get(letter) + 1);
            } else {
                distribution.put(letter, 1);
            }
        }

        //now verify distributions
        assertEquals("Should have 12 E tiles", 12, (int)distribution.get('E'));
        assertEquals("Should have 9 A tiles", 9, (int)distribution.get('A'));
        assertEquals("Should have 1 Q tile", 1, (int)distribution.get('Q'));
        assertEquals("Should have 1 Z tile", 1, (int)distribution.get('Z'));
    }

    /**
     *  Test getRemainingTiles tracks correctly.
     */
    @Test
    public void testGetRemainingTiles(){
        assertEquals("Should start with 100 tiles", 100, tileBag.getRemainingTiles());

        tileBag.getRandomTile();
        assertEquals("Should have 99 tiles after one draw", 99, tileBag.getRemainingTiles());

        tileBag.getRandomTile();
        tileBag.getRandomTile();
        tileBag.getRandomTile();
        assertEquals("Should have 96 tiles after four draws", 96, tileBag.getRemainingTiles());

    }

}
