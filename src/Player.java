import java.util.*;

/**
 * Player class for Scrabble game.
 * Represents a player with their tiles, score, and index.
 * @author Aymen Zebentout
 * @version 1.0
 * @since 10/26/2025
 */

public class Player {
    private int score;
    private String name;
    private int index;
    private List<Tile> tiles;

    /**
     * Constructor
     * @param score player's score
     * @param name player's name
     * @param index player's turn
     */
    public Player(int score, String name, int index) {
        this.score = score;
        this.name = name;
        this.index = index;
        this.tiles = new ArrayList<Tile>();
    }

    /**
     * Adds a tile to the player's hand with maximum of 7 tiles
     * @param tile tile to add
     */
    public void addTile(Tile tile){
        if (tiles.size()< 7 && tile != null){
            this.tiles.add(tile);
        }
    }

    /**
     * Removes a tile from player's hand when it is place on the board
     * @param character tile to remove
     */
    public Tile removeTile(char character){
        for (int i = 0; i < tiles.size(); i++){
            if (tiles.get(i).getCharacter()==character){
                return tiles.remove(i);
            }
        }
        return null;
    }

    /**
     * Adds points to player's score
     * @param points the points to add
     */
    public void addScore(int points){
        this.score += points;
    }

    /**
     * Gets player's score
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets player's tiles
     * @return list of the tiles in hand
     */
    public List<Tile> getTiles() {
        return tiles;
    }

    /**
     * Gets player's tiles as a string. Will be used to display the player's tiles
     * @return string of the tiles
     */
    public String getTilesAsString(){
        StringBuilder sb = new StringBuilder();
        for (Tile tile: tiles){
            sb.append(tile.getCharacter()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Gets player's turn
     * @return the player's turn (index)
     */
    public int getIndex(){
        return index;
    }

    /**
     * Gets player's name
     * @return the name
     */
    public String getName() {
        return name;
    }

}
