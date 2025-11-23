import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;

/**
 * JUnit test class for ScrabbleModel.
 * Tests game logic, word placement, scoring, and game state management.
 * 
 * @author Emmanuel Konate, Aymen Zebentout, Joseph Dereje, Amber Skinner
 * @version 2.0
 */
public class ScrabbleModelTest {
    
    private ScrabbleModel model;
    private String[] playerNames;
    
    /**
     * Set up test fixture - runs before each test
     */
    @Before
    public void setUp() {
        model = new ScrabbleModel();
        playerNames = new String[]{"Alice", "Bob"};
    }
    
    /**
     * Clean up after each test
     */
    @After
    public void tearDown() {
        model = null;
        playerNames = null;
    }
    
    // ========== GAME INITIALIZATION TESTS ==========
    
    @Test
    public void testInitializeGameValidPlayers() {
        boolean result = model.initializeGame(2, playerNames);
        assertTrue("Game should initialize with 2 players", result);
        assertTrue("Game should be playing", model.isPlaying());
        assertEquals("Should have 2 players", 2, model.getPlayerList().size());
    }
    
    @Test
    public void testInitializeGameThreePlayers() {
        String[] threeNames = {"Alice", "Bob", "Charlie"};
        boolean result = model.initializeGame(3, threeNames);
        assertTrue("Game should initialize with 3 players", result);
        assertEquals("Should have 3 players", 3, model.getPlayerList().size());
    }
    
    @Test
    public void testInitializeGameFourPlayers() {
        String[] fourNames = {"Alice", "Bob", "Charlie", "Diana"};
        boolean result = model.initializeGame(4, fourNames);
        assertTrue("Game should initialize with 4 players", result);
        assertEquals("Should have 4 players", 4, model.getPlayerList().size());
    }
    
    @Test
    public void testInitializeGameInvalidPlayerCount() {
        String[] oneName = {"Alice"};
        boolean result = model.initializeGame(1, oneName);
        assertFalse("Game should not initialize with 1 player", result);
    }
    
    @Test
    public void testInitializeGameTooManyPlayers() {
        String[] fiveNames = {"A", "B", "C", "D", "E"};
        boolean result = model.initializeGame(5, fiveNames);
        assertFalse("Game should not initialize with 5 players", result);
    }
    
    @Test
    public void testPlayerStartsWith7Tiles() {
        model.initializeGame(2, playerNames);
        Player player = model.getCurrentPlayer();
        assertEquals("Player should start with 7 tiles", 7, player.getTiles().size());
    }
    
    @Test
    public void testFirstTurnIsPlayerOne() {
        model.initializeGame(2, playerNames);
        assertEquals("First turn should be player index 0", 0, model.getCurrentPlayerIndex());
        assertEquals("First player should be Alice", "Alice", model.getCurrentPlayer().getName());
    }
    
    // ========== WORD PLACEMENT TESTS ==========
    
    @Test
    public void testPlaceWordNotPlaying() {
        int score = model.placeWord(7, 7, 1, "HELLO");
        assertEquals("Cannot place word when not playing", 0, score);
    }
    
    @Test
    public void testPlaceWordNullWord() {
        model.initializeGame(2, playerNames);
        int score = model.placeWord(7, 7, 1, null);
        assertEquals("Cannot place null word", 0, score);
    }
    
    @Test
    public void testPlaceWordEmptyWord() {
        model.initializeGame(2, playerNames);
        int score = model.placeWord(7, 7, 1, "");
        assertEquals("Cannot place empty word", 0, score);
    }
    
    @Test
    public void testPlaceWordInvalidPosition() {
        model.initializeGame(2, playerNames);
        int score = model.placeWord(20, 20, 1, "HELLO");
        assertEquals("Cannot place word at invalid position", 0, score);
    }
    
    @Test
    public void testPlaceWordOutOfBoundsHorizontal() {
        model.initializeGame(2, playerNames);
        // Try to place at column 14 going right - would go off board
        int score = model.placeWord(7, 14, 1, "HELLO");
        assertEquals("Word should not fit horizontally", 0, score);
    }
    
    @Test
    public void testPlaceWordOutOfBoundsVertical() {
        model.initializeGame(2, playerNames);
        // Try to place at row 14 going down - would go off board
        int score = model.placeWord(14, 7, 2, "HELLO");
        assertEquals("Word should not fit vertically", 0, score);
    }

    /**
     * Stuck here
     */
    @Test
    public void testPlaceWordLetterDouble(){
        model.initializeGame(2, playerNames);
        model.
        assertEquals("Word should not fit vertically", 12, score);
    }
    
    // ========== TILE SWAPPING TESTS ==========
    
    @Test
    public void testSwapTilesNotPlaying() {
        boolean result = model.swapTiles("ABC");
        assertFalse("Cannot swap tiles when not playing", result);
    }
    
    @Test
    public void testSwapTilesNotEnoughInBag() {
        model.initializeGame(2, playerNames);
        // Drain the bag to less than 7 tiles
        while (model.getTilesRemaining() >= 7) {
            model.removeTile();
        }
        boolean result = model.swapTiles("ABC");
        assertFalse("Cannot swap when bag has less than 7 tiles", result);
    }
    
    @Test
    public void testPassTurn() {
        model.initializeGame(2, playerNames);
        int initialPlayer = model.getCurrentPlayerIndex();
        model.passTurn();
        int newPlayer = model.getCurrentPlayerIndex();
        assertNotEquals("Turn should pass to next player", initialPlayer, newPlayer);
    }
    
    @Test
    public void testPassTurnMultipleTimes() {
        model.initializeGame(3, new String[]{"A", "B", "C"});
        assertEquals("Should start at player 0", 0, model.getCurrentPlayerIndex());
        model.passTurn();
        assertEquals("Should be player 1", 1, model.getCurrentPlayerIndex());
        model.passTurn();
        assertEquals("Should be player 2", 2, model.getCurrentPlayerIndex());
        model.passTurn();
        assertEquals("Should wrap to player 0", 0, model.getCurrentPlayerIndex());
    }
    
    @Test
    public void testGameEndsAfterSixScorelessTurns() {
        model.initializeGame(2, playerNames);
        assertTrue("Game should be playing initially", model.isPlaying());
        
        // Pass 6 times
        for (int i = 0; i < 6; i++) {
            model.passTurn();
        }
        
        assertFalse("Game should end after 6 consecutive passes", model.isPlaying());
    }
    
    // ========== GAME STATE TESTS ==========
    
    @Test
    public void testGetBoardReturnsBoard() {
        model.initializeGame(2, playerNames);
        Board board = model.getBoard();
        assertNotNull("Should return a board", board);
        assertEquals("Board should be 15x15", 15, board.getBoardSize());
    }
    
    @Test
    public void testGetCurrentPlayerReturnsCorrectPlayer() {
        model.initializeGame(2, playerNames);
        Player player = model.getCurrentPlayer();
        assertNotNull("Should return current player", player);
        assertEquals("Should be first player", "Alice", player.getName());
    }
    
    @Test
    public void testGetPlayerListReturnsAllPlayers() {
        model.initializeGame(3, new String[]{"A", "B", "C"});
        List<Player> players = model.getPlayerList();
        assertEquals("Should have 3 players", 3, players.size());
    }
    
    @Test
    public void testIsPlayingInitiallyFalse() {
        assertFalse("Game should not be playing initially", model.isPlaying());
    }
    
    @Test
    public void testIsPlayingAfterInitialize() {
        model.initializeGame(2, playerNames);
        assertTrue("Game should be playing after initialization", model.isPlaying());
    }
    
    @Test
    public void testGetTilesRemainingInitialCount() {
        model.initializeGame(2, playerNames);
        // Started with 100, each player got 7, so 100 - 14 = 86
        assertEquals("Should have 86 tiles remaining", 86, model.getTilesRemaining());
    }
    
    @Test
    public void testIsFirstTurnInitially() {
        model.initializeGame(2, playerNames);
        assertTrue("Should be first turn initially", model.isFirstTurn());
    }
    
    @Test
    public void testScorelessTurnsInitially() {
        model.initializeGame(2, playerNames);
        assertEquals("Should have 0 scoreless turns initially", 0, model.getScorelessTurns());
    }
    
    @Test
    public void testScorelessTurnsIncrement() {
        model.initializeGame(2, playerNames);
        model.passTurn();
        assertEquals("Should have 1 scoreless turn", 1, model.getScorelessTurns());
        model.passTurn();
        assertEquals("Should have 2 scoreless turns", 2, model.getScorelessTurns());
    }
    
    // ========== OBSERVER PATTERN TESTS ==========
    
    @Test
    public void testAddViewDoesNotCrash() {
        MockView mockView = new MockView();
        model.addView(mockView);
        // If this doesn't crash, test passes
        assertTrue("Adding view should not crash", true);
    }
    
    @Test
    public void testRemoveViewDoesNotCrash() {
        MockView mockView = new MockView();
        model.addView(mockView);
        model.removeView(mockView);
        assertTrue("Removing view should not crash", true);
    }
    
    @Test
    public void testNotifyViewsCallsUpdate() {
        MockView mockView = new MockView();
        model.addView(mockView);
        model.initializeGame(2, playerNames);
        // initializeGame calls notifyViews()
        assertTrue("View should have been updated", mockView.wasUpdated);
    }
    
    // ========== HELPER CLASSES ==========
    
    /**
     * Mock view for testing Observer pattern
     */
    private static class MockView implements ScrabbleView {
        public boolean wasUpdated = false;
        
        @Override
        public void update() {
            wasUpdated = true;
        }
    }
}
