import java.util.ArrayList;
import java.util.List;

/**
 * GameHistory is a class that keeps track of the changes to the game state that happened in a given turn.
 * @author Amber Skinner
 */
public class GameHistory {
    public int playerId;
    public int scoreGained;
    public List<Tile> playedTiles;
    // Tiles drawn from the bag
    public List<Tile> gainedTiles;
    public List<Tile> swappedTiles;
    public List<int[]> newTilePositions;
    public GameHistory()
    {
        playerId = 0;
        scoreGained = 0;
        playedTiles = new ArrayList<>();
        gainedTiles = new ArrayList<>();
        swappedTiles = new ArrayList<>();
        newTilePositions = new ArrayList<>();
    }
    public GameHistory(int playerId, int scoreGained, List<Tile> playedTiles, List<Tile> gainedTiles, List<int[]> newTilePositions) {
        this.playerId = playerId;
        this.scoreGained = scoreGained;
        this.playedTiles = playedTiles;
        this.gainedTiles = gainedTiles;
        this.newTilePositions = newTilePositions;
    }
}
