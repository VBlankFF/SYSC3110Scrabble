/**
 * Abstract View interface for the Scrabble game in MVC pattern.
 * It defines the contract that all view implementations have to follow.
 * Views are notified by the model when a state changes as the Observer Pattern.
 *
 * @author Esli Emmanuel Konate, Joseph Dereje, Aymen Zebentout, Amber Skinner
 * @version 2.0
 */

public interface ScrabbleView {

    /**
     * Updates the view so that it shows the current model state.
     * It is called by the model when a state changes.
     */
    void update();

    public void handleAIPlay(String playerName, String word, int points, int totalPoints);

    public void handleAISwap(String playerName, String tiles);

    public void handleAIPass(String playerName);
}
