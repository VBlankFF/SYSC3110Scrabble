import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class to test the Board class
 * Tests all Board methods for placement, validation and also to check the state.
 *
 * @author Emmanuel Konate 101322259
 * @version 1.0
 */
public class BoardTest {

    private Board board;
    private Player testPlayer;

    /**
     * This sets up before each test
     */
    @Before
    public void setUp(){
        board = new Board();
        testPlayer = new Player(0,"Test Player", 1);

        //Add some tiles to test player
        testPlayer.addTile(new Tile('H'));
        testPlayer.addTile(new Tile('E'));
        testPlayer.addTile(new Tile('L'));
        testPlayer.addTile(new Tile('L'));
        testPlayer.addTile(new Tile('O'));
        testPlayer.addTile(new Tile('W'));
        testPlayer.addTile(new Tile('D'));

    }

    /**
     * The first test will be to test Initialization of Board
     */
    @Test
    public void testBoardInitialization(){
        assertNotNull("Board should not be null", board);
        assertTrue("new Board should be empty", board.isEmpty());
        assertEquals("Board should be 15x15", 15, board.getBoardSize());
        assertFalse("First word should not be placed at start", board.isFirstWordPlaced());
    }

    /**
     * Now we have to check that the position is valid
     */
    @Test
    public void testValidPosition(){
        //checks for valid position
        assertTrue("Top Left should be valid", board.validPosition(0,0));
        assertTrue("Center should be valid", board.validPosition(7,7));
        assertTrue("Bottom right should be valid", board.validPosition(14,14));

        //checks for invalid positiion
        assertFalse("Negative row should not worl", board.validPosition(-1, 0));
        assertFalse("Negative column should not work", board.validPosition(0, -1));
        assertFalse("Row 15 should not work", board.validPosition(15, 0));
        assertFalse("Column 15 should not work", board.validPosition(0, 15));
    }

    /**
     * Get position on an empty board
     */
    @Test
    public void testGetPositionEmpty(){
        //checks for valid position
        assertNull("Empty position should return null", board.getPosition(7,7));
        assertNull("Empty position should return null", board.getPosition(0,0));
    }

    /**
     * First word must cover the center.
     */
    @Test
    public void testFirstWordCoversCenter(){
        boolean result = board.placeTiles("H", 8, "ACROSS", "HELLO", testPlayer);
        assertTrue("First word covering center should succeed", result);
        assertTrue("First word flag should not be set", board.isFirstWordPlaced());
    }

    /**
     * Place word in a Horizontal way.
     */
    @Test
    public void testPlaceWordHorizontal(){
        boolean result = board.placeTiles("H", 8, "ACROSS", "HELLO", testPlayer);
        assertTrue("Horizontal placement should checks", result);

        //verify the tiles have been placed
        assertNotNull("H should be at H8", board.getPosition(7, 7));
        assertNotNull("E should be placed", board.getPosition(7, 8));
        assertNotNull("H should be placed", board.getPosition(7, 11));

        assertFalse("Board should not be empty after the placement", board.isEmpty());
    }

    /**
     * Place word in a Vertical way.
     */
    @Test
    public void testPlaceWordVertical(){
        boolean result = board.placeTiles("H", 8, "DOWN", "HELLO", testPlayer);
        assertTrue("Vertical placement should succeed", result);

        //verify the tiles have been placed
        assertNotNull("H should be at H8", board.getPosition(7, 7));
        assertNotNull("E should be placed below", board.getPosition(8, 7));
        assertNotNull("O should be placed", board.getPosition(11, 7));
    }

    /**
     * Cannot write over tiles
     */
    @Test
    public void testCannotOverwriteTiles(){
        //first word
        board.placeTiles("H", 8, "ACROSS", "HELLO", testPlayer);

        //Create second player with different tiles
        Player player2 = new Player(0,"Player 2", 2);
        player2.addTile(new Tile('W'));
        player2.addTile(new Tile('O'));
        player2.addTile(new Tile('R'));
        player2.addTile(new Tile('L'));
        player2.addTile(new Tile('D'));

        // try to overwrite with different word at same position
        boolean result = board.placeTiles("H", 8, "ACROSS", "WORLD", player2);
        assertFalse("Should not be able to overwrite with different letters", result);
    }



    /**
     * Can reuse existing tiles.
     */
    @Test
    public void testWordReuseTiles(){
        //first word HELLO horizontally at H8
        board.placeTiles("H", 8, "ACROSS", "HELLO", testPlayer);

        //Create second player with different tiles
        Player player2 = new Player(0,"Player 2", 2);
        player2.addTile(new Tile('W'));
        player2.addTile(new Tile('A'));
        player2.addTile(new Tile('T'));
        player2.addTile(new Tile('C'));
        player2.addTile(new Tile('H'));

        // try to overwrite with different word at same position
        boolean result = board.placeTiles("H", 8, "DOWN", "WATCH", player2);
        assertTrue("Should be bale to reuse existing tile", result);
    }

    /**
     * Following word must connect to existing tiles
     */
    @Test
    public void testWordMustConnect(){
        //first word
        board.placeTiles("H", 8, "ACROSS", "HELLO", testPlayer);

        //Create player with disconnect word.
        Player player2 = new Player(0,"Player 2", 2);
        player2.addTile(new Tile('W'));
        player2.addTile(new Tile('O'));
        player2.addTile(new Tile('R'));
        player2.addTile(new Tile('D'));

        // try to place word that does not connect
        boolean result = board.placeTiles("A", 1, "ACROSS", "WORD", player2);
        assertFalse("Disconnected word should fail", result);
    }

    /**
     * Checks for placement out of bounds
     */
    @Test
    public void testOutOfBoundsPlacement(){
        //first word that will go off the board
        boolean result = board.placeTiles("H", 14, "ACROSS", "HELLO", testPlayer);
        assertFalse("Out of bounds placement should fail", result);
    }

    /**
     * Player must have the required tiles.
     */
    @Test
    public void testPlayerMustHaveTiles(){
        Player emptyPlayer = new Player(0,"Empty", 3);

        boolean result = board.placeTiles("H", 8, "ACROSS", "HELLO", emptyPlayer);
        assertFalse("Player without tiles should fail", result);
    }

    /**
     * Checks if we clear board correctly.
     */
    @Test
    public void testClearBoard(){
        //checks if we place word correctly
        board.placeTiles("H", 8, "ACROSS", "HELLO", testPlayer);
        assertFalse("Board should not be empty", board.isEmpty());

        //clear the board
        boolean cleared = board.clearBoard();
        assertTrue("Clear should return true", cleared);
        assertTrue("Board should be empty after clear", board.isEmpty());
        assertFalse("First word should be reset", board.isFirstWordPlaced());
    }

    /**
     * isEmpty method.
     */
    @Test
    public void testIsEmpty(){
        assertTrue("New board should be empty", board.isEmpty());

        board.placeTiles("H", 8, "ACROSS", "HELLO", testPlayer);
        assertFalse("Board with tiles should not be empty", board.isEmpty());
    }

    /**
     * test that getWordsAtPosition method works well.
     */
    @Test
    public void testgetWordsAtPosition(){
        board.placeTiles("H", 8, "ACROSS", "HELLO", testPlayer);
        //call the method if it doesn't crash the test passes

        board.getWordsAtPosition(7,7);

        //if we are able to be here, the method did not crash
        assertTrue("Method worked successfully", true);
    }


}
