import java.util.*;

public class player {
    private int score;
    private String name;
    private int index;
    private List<Object> tiles;
    private Tilebag bag;
    public player(int score, String name, int index) {
        this.score = score;
        this.name = name;
        this.index = index;
        this.tiles = new ArrayList<Object>();
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addTile(Tile tile){
        this.tiles.add(bag.getRamdomTile());
    }

    private void removeTile(Tile tile){
        this.tiles.remove(tile);
    }

    public int getScore() {
        return score;
    }

    public List<Object> getTiles() {
        return tiles;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
