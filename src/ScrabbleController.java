import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller class for the Scrabble game with MVC pattern.
 * It implements ActionListener to handle button clicks with Event Model.
 * Middle point between the view and model.
 * @author Esli Emmanuel Konate, Ayment Zebentout, Joseph Dereje, Amber Skinner
 * @version 2.0
 */

public class ScrabbleController implements ActionListener{
    private ScrabbleModel model;
    //this will allow to interact with gui
    private ScrabbleGUI view;

    /**
     * Constructor for ScrabbleController
     * @param model the game model
     * @param view the GUI view
     */
    public ScrabbleController(ScrabbleModel model, ScrabbleGUI view){
        this.model = model;
        this.view = view;
    }

    /**
     * This handles all the button click events from the view.
     * It uses action commands to figure out which button was clicked
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();

        switch (command){
            case "PLACE_WORD":
                handlePlaceWord();
                break;
            case "SWAP_TILES":
                handleSwapTiles();
                break;

            case "PASS_TURN":
                handlePassTurn();
                break;

            case "UNDO":
                handleUndo();
                break;

            case "REDO":
                handleRedo();
                break;

            default:
                System.err.println("This is an unknown action command: " + command);
                break;
        }
        // Check and do AI turns until it is no longer an AI's turn
        while (model.CheckAITurn());
    }

    private void handleUndo() {
        model.undo();
    }

    private void handleRedo() {
        model.redo();
    }

    /**
     * Now we start a new game with the given number of players
     * @param numPlayers number of players from 2 to 4
     * @param playerNames is the array of player names
     */
    public void startGame(int numPlayers, String[] playerNames){
        if (model.initializeGame(numPlayers, playerNames)){
            view.showWorked("Game started! " + model.getCurrentPlayer().getName() + "'s turn.");
            // Check and do AI turns until it is no longer an AI's turn
            while (model.CheckAITurn());
        } else {
            view.showFailed("Failed to start game. Try again! budday :)");
        }
    }

    /**
     * Takes care of placing words
     */
    private void handlePlaceWord(){
        if(!model.isPlaying()){
            view.showFailed("Game is currently not in progress :(");
            return;
        }

        //get where to place the words from the user
        String rowStr = JOptionPane.showInputDialog(view, "Enter row number from 1 to 15:",
                "Place Word", JOptionPane.QUESTION_MESSAGE);
        if (rowStr == null){
            //the case where the student cancels
            return;
        }
        String colStr = JOptionPane.showInputDialog(view, "Enter column letter from A to O:",
                "Place Word", JOptionPane.QUESTION_MESSAGE);
        if (colStr == null){
            return;
        }
        String[] directionChoices = {"Horizontal (Right)", "Vertical (Down)"};
        int directionChoice = JOptionPane.showOptionDialog(view, "Select direction",
                "Place Word", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                directionChoices, directionChoices[0]);

        if (directionChoice < 0){
            return;
        }

        String word = JOptionPane.showInputDialog(view, "Enter the word: ",
                "Place Word", JOptionPane.QUESTION_MESSAGE);

        if (word == null || word.trim().isEmpty()){
            view.showFailed("Please enter a valid word that works. ");
            return;
        }

        //now we parse the inputs
        //I made use of error handling here
        try {
            //it is converted in 0 indexed
            int row = Integer.parseInt(rowStr.trim()) -1;
            int col = parseColumn(colStr.trim().toUpperCase());
            //1 for horizontal and 2 for vertical
            int direction = directionChoice + 1;

            if (col < 0){
                view.showFailed("This column does not work. Please enter between A to O");
                return;
            }
            if (row < 0 || row >= 15){
                view.showFailed("The row must be between 1 and 15.");
                return;
            }

            //tries to place a word
            int score = model.placeWord(row, col, direction, word.trim().toUpperCase());

            if (score > 0){
                view.showWorked("Word is placed! Yayyyy!!!! Scored " + score + " points.");
            } else {
                view.showFailed("Invalid word placement. Make sure you have the required tiles. " +
                        "That your word is in dictionary, that your word connects to current tiles. If it is the first " +
                        "word, it has to cover the center square.");
            }
        } catch (NumberFormatException e){
            view.showFailed("The row number is invalid. Please enter a number between 1 and 15.");
        }
    }

    /**
     * It handles the swapping of tiles
     */
    private void handleSwapTiles(){
        if (!model.isPlaying()){
            view.showFailed("Game is not in progress.");
            return;
        }

        if (model.getTilesRemaining() < 7){
            view.showFailed("Not enough tiles in bag to swap, we need at least 7.");
            return;
        }

        String tiles = JOptionPane.showInputDialog(view, "Enter tiles to swap: " +
                model.getCurrentPlayer().getTilesAsString(), "Swap Tiles", JOptionPane.QUESTION_MESSAGE);

        if (tiles == null){
            return;
        }

        if (tiles.trim().isEmpty()){
            view.showFailed("Please enter tiles to swap. ");
            return;
        }

        if (model.swapTiles(tiles.trim())){
            view.showWorked("Tiles swapped correctly!");
        } else {
            view.showFailed("Failed to swap tiles. Make sure you have all the specific tiles.");
        }
    }

    /**
     * This handles pass turn action
     */
    private void handlePassTurn(){
        if (!model.isPlaying()){
            view.showFailed("Game not in progress.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(view, "Are you sure you want to pass your " +
                "turn?", "Pass Turn", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION){
            model.passTurn();
            view.showWorked("Turn has passed.");
        }
    }

    /**
     * This parses a column of letter from A to O to a column from 0 to 14
     * @param colStr the column letter
     * @return the column index or -1 if it does not work.
     */
    private int parseColumn(String colStr){
        if (colStr == null || colStr.length() != 1 ){
            return -1;
        }

        char col = colStr.charAt(0);
        if (col >= 'A' && col <= 'O'){
            return col - 'A';
        }

        return -1;
    }


}
