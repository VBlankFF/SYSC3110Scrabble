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

    /**
     * Have views do something to describe the AI's turn and word
     * @param playerName The name of the (AI) player
     * @param word The word that was played by the AI
     * @param points The amount of points the word played was worth
     * @param totalPoints The AI player's total score after playing the word
     */
    void handleAIPlay(String playerName, String word, int points, int totalPoints);

    /**
     * Have views do something to listing the tiles the AI swapped
     * @param playerName The name of the (AI) player
     * @param tiles The tiles the AI placed in the bag
     */
    void handleAISwap(String playerName, String tiles);

    /**
     * Have views do something to state that the AI has passed
     * @param playerName The name of the (AI) player
     */
    void handleAIPass(String playerName);
}
