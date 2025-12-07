/**
 * This is the main class to start the Scrabble game with MVC.
 * It sets up the model, view, and controller and links them.
 * @version 2.0
 */
public class Main {
    /**
     * Main method to start the app.
     * @param args which is the line of command arguments
     */
    public static void main(String[] args){
        //creation of model
        ScrabbleModel model = new ScrabbleModel();
        //creation of gui view
        ScrabbleGUI view = new ScrabbleGUI(model);
        //create controller
        ScrabbleController controller = new ScrabbleController(model, view);
        //connect view to the controller
        view.setController(controller);
        //now add the view to the model, essentially register them as viewers
        model.addView(view);
        //show the view
        view.setVisible(true);



    }
}
