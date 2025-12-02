import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

/**
 * JUnit tests for serialization/deserialization options.
 *
 * @author Esli Emmanuel Konate 101322259
 * @version 1.0
 */

public class SerializationTest {
    private ScrabbleModel model;
    private static final String TEST_GAME = "test_game.scrabble";

    @Before public void setUp(){
        model = new ScrabbleModel();
        String[] playerNames = {"Alice", "Bob"};
        model.initializeGame(2, playerNames);
    }

    @After
    public void tearDown(){
        //clean up the test file
        File testFile = new File("saved_games" + File.separator + "test_game.scrabble");
        if (testFile.exists()){
            testFile.delete();
        }
    }

    /**
     * Test save and load functionality
     */
    @Test
    public void testSaveAndLoad(){
        //save the game
        assertTrue("Game should save correctly", GameSerializer.saveGame(model, "test_game"));

        //load the game
        ScrabbleModel loadedModel = GameSerializer.loadGame("test_game");
        assertNotNull("Loaded model has to be null", loadedModel);

        //now we verify the game state
        assertEquals("Should have the same number of players", 2, loadedModel.getPlayerList().size());
        assertTrue("Game should still be playing", loadedModel.isPlaying());
    }

    /**
     * Test Player data was kept (score, tiles, names)
     */
    @Test
    public void testDataKept(){
        //change player state
       Player player = model.getCurrentPlayer();
       player.addScore(50);
       String ogName = player.getName();
       int ogTileCount = player.getTiles().size();

       //save and load
        GameSerializer.saveGame(model, "test_game");
        ScrabbleModel loadedModel = GameSerializer.loadGame("test_game");

        //now verify the player data
        Player loadedPlayer = loadedModel.getCurrentPlayer();
        assertEquals("Name should be kept", ogName, loadedPlayer.getName());
        assertEquals("Score should be the kept", 50, loadedPlayer.getScore());
        assertEquals("Tile count should be kept", ogTileCount, loadedPlayer.getTiles().size());
    }

    /**
     * Test Board state kept
     */
    @Test
    public void testBoardStateKept(){
        //set up tiles and place a word
        Player player = model.getCurrentPlayer();
        player.getTiles().clear();
        player.addTile(new Tile('T'));
        player.addTile(new Tile('E'));
        player.addTile(new Tile('S'));
        player.addTile(new Tile('T'));

        model.placeWord(7, 7, 1, "TEST");

        //save and load now
        GameSerializer.saveGame(model, "test_game");
        ScrabbleModel loadedModel = GameSerializer.loadGame("test_game");

        //check board state
        Board loadedBoard = loadedModel.getBoard();
        assertNotNull("First tile should be on board", loadedBoard.getPosition(7,7));
        assertEquals("First tile should be T", 'T', loadedBoard.getPosition(7,7).getCharacter());
        assertFalse("First turn flag should be false", loadedModel.isFirstTurn());
    }

    /**
     * Test Error, we load a non-existent file
     */
    @Test
    public void testLoadNonExistentFile(){
       ScrabbleModel result = GameSerializer.loadGame("non_existent_game");
       assertNull("Should return null for non-existent file", result);
    }

    /**
     * Test we have invalid inputs
     */
    @Test
    public void testInvalidInputHandling(){
        //test null and also empty names
        assertFalse("Should not save with null name", GameSerializer.saveGame(model, null));
        assertFalse("Should not save with empty name", GameSerializer.saveGame(model, ""));
        assertNull("Should not load with null name", GameSerializer.loadGame( null));


    }





}
