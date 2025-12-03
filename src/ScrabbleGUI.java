import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Swing GUI implementation of ScrabbleView.
 * It shows the game state with Java Swing items.
 * It updates automatically when notified by the model.
 *
 * @author Esli Emmanuel Konate, Amber Skinner, Joseph Dereje, Aymen Zebentout
 * @version 2.0
 */

public class ScrabbleGUI extends JFrame implements ScrabbleView{
    private ScrabbleModel model;
    private ScrabbleController controller;

    //these are the GUI components
    private JPanel boardPanel;
    private JButton[][] boardButtons;
    private JLabel currentPlayerLabel;
    private JLabel scoreLabel;
    private JLabel tilesLabel;
    private JPanel handPanel;
    private JButton[] handButtons;
    private JButton placeWordButton;
    private JButton swapTilesButton;
    private JButton passTurnButton;
    private JLabel statusLabel;
    private JLabel tilesRemainingLabel;

    private static final int BOARD_SIZE = 15;
    private static final int CELL_SIZE = 40;

    /**
     * Constructor for ScrabbleGUI
     * @param model the game model to display
     */
    public ScrabbleGUI(ScrabbleModel model){
        this.model = model;
        setTitle("Scrabble Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        currentPlayerLabel = new JLabel("Current Player: ", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        tilesRemainingLabel = new JLabel("Tiles in bag: 100", SwingConstants.CENTER);
        tilesRemainingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        topPanel.add(currentPlayerLabel);
        topPanel.add(scoreLabel);
        topPanel.add(tilesRemainingLabel);

        //center panel now
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBackground(Color.LIGHT_GRAY);
        //these are the column labels from A to O at the top
        JPanel columnLabels = new JPanel(new GridLayout(1, BOARD_SIZE + 1));
        //this will be empty
        columnLabels.add(new JLabel(""));
        for (int i = 0; i < BOARD_SIZE; i++){
            JLabel colLabel = new JLabel(String.valueOf((char)('A' + i)), SwingConstants.CENTER);
            colLabel.setFont(new Font("Arial", Font.BOLD, 12));
            columnLabels.add(colLabel);
        }
        boardContainer.add(columnLabels, BorderLayout.NORTH);

        //now the row labels on the left of the board
        JPanel boardWithRowLabels = new JPanel(new BorderLayout());
        //row labels
        JPanel rowLabels = new JPanel(new GridLayout(BOARD_SIZE, 1));
        for (int i = 0; i < BOARD_SIZE; i++){
            JLabel rowLabel = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            rowLabels.setPreferredSize(new Dimension(30, CELL_SIZE));
            rowLabels.add(rowLabel);
        }
        boardWithRowLabels.add(rowLabels, BorderLayout.WEST);
        //the board
        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 1, 1));
        boardPanel.setBackground(Color.DARK_GRAY);
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        boardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                JButton btn = new JButton("");
                btn.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                btn.setMargin(new Insets(0, 0, 0, 0));
                //now color the center square because it is special
                if (i == 7 && j == 7){
                    //it will be pink
                    btn.setBackground(Color.PINK);
                } else {
                    btn.setBackground(Color.WHITE);
                }
                //set the backgtound color based on the type of premium square
                Board.PremiumSquare premium = model.getBoard().getPremiumSquare(i, j);
                switch (premium){
                    case TRIPLE_WORD:
                        btn.setBackground(Color.red);
                        break;
                    case DOUBLE_WORD:
                        btn.setBackground(Color.pink);
                        break;
                    case TRIPLE_LETTER:
                        //use rgb to get specific color
                        btn.setBackground(new Color(0, 0, 139));
                        break;
                    case DOUBLE_LETTER:
                        //use rgb to get specific color
                        btn.setBackground(new Color(173, 216, 230));
                        break;
                    default:
                        btn.setBackground(Color.white);
                        break;
                }
                btn.setFocusPainted(false);
                boardButtons[i][j] = btn;
                boardPanel.add(btn);
            }
        }
        boardWithRowLabels.add(boardPanel, BorderLayout.CENTER);
        boardContainer.add(boardWithRowLabels, BorderLayout.CENTER);

        //now the bottom panel this where we see the player hand and the controls
        JPanel bottomPanel = new JPanel(new BorderLayout(5,5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //the hand panel
        JPanel handContainer = new JPanel(new BorderLayout());
        tilesLabel = new JLabel("Your tiles:", SwingConstants.CENTER);
        tilesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        handContainer.add(tilesLabel, BorderLayout.NORTH);

        handPanel = new JPanel(new GridLayout(1, 7, 5, 5));
        handButtons = new JButton[7];
        for (int i = 0; i < 7; i++){
            JButton btn = new JButton("");
            btn.setPreferredSize(new Dimension(50, 60));
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setBackground(Color.ORANGE);
            btn.setEnabled(false);
            handButtons[i] = btn;
            handPanel.add(btn);
        }
        handContainer.add(handPanel, BorderLayout.CENTER);

       //this is the control panel
       JPanel controlPanel = new JPanel(new GridLayout(1,3,10,0));
       placeWordButton = new JButton("Place Word");
       placeWordButton.setFont(new Font("Arial", Font.BOLD, 14));
       placeWordButton.setEnabled(false);
       swapTilesButton = new JButton("Swap Tiles");
       swapTilesButton.setFont(new Font("Arial", Font.BOLD, 14));
       swapTilesButton.setEnabled(false);
       passTurnButton = new JButton("Pass Turn");
       passTurnButton.setFont(new Font("Arial", Font.BOLD, 14));
       passTurnButton.setEnabled(false);
       controlPanel.add(placeWordButton);
       controlPanel.add(swapTilesButton);
       controlPanel.add(passTurnButton);
       //status label
        statusLabel = new JLabel("Click  New Game to start", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLUE);
        bottomPanel.add(handContainer, BorderLayout.NORTH);
        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        //add all panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(boardContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        //this is the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e-> showNewGameDialog());
        JMenu selectBoardMenu = new JMenu("Select Board");
        JMenuItem standardBoard = new JMenuItem("Standard");
        standardBoard.addActionListener(e-> setBoardselection(0));
        selectBoardMenu.add(standardBoard);
        JMenuItem premiumAllDouble = new JMenuItem("Premium All Double");
        premiumAllDouble.addActionListener(e-> setBoardselection(1));
        selectBoardMenu.add(premiumAllDouble);
        JMenuItem premiumAllTriple = new JMenuItem("Premium All Triple");
        premiumAllTriple.addActionListener(e-> setBoardselection(2));
        selectBoardMenu.add(premiumAllTriple);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e->System.exit(0));
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        saveGameItem.addActionListener(e -> {
            if (this.model.isPlaying()){
                String filename = JOptionPane.showInputDialog("Enter filename:");
                if (filename != null && !filename.trim().isEmpty()){
                    if (GameSerializer.saveGame(this.model, filename)){
                        showWorked("Game saved!");
                    } else {
                        showFailed("Failed to save game.");
                    }
                }
            }
        });

        JMenuItem loadGameItem = new JMenuItem("Load Game");
        loadGameItem.addActionListener(e-> {
            String filename = JOptionPane.showInputDialog("Enter filename:");
            if (filename != null && !filename.trim().isEmpty()){
                ScrabbleModel loaded = GameSerializer.loadGame(filename);
                if (loaded != null){
                    this.model = loaded;
                    this.model.addView(this);
                    update();
                    showWorked("Game loaded!");
                } else {
                    showFailed("Failed to load the game.");
                }
            }
        });

        gameMenu.add(newGameItem);
        gameMenu.add(selectBoardMenu);
        gameMenu.addSeparator();
        gameMenu.add(saveGameItem);
        gameMenu.add(loadGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        pack();
        setLocationRelativeTo(null);
        setResizable(true);
        setSize(500,500);

    }

    /**
     * Sets the controller for the view
     * @param controller the controller to handle the user actions
     */
    public void setController(ScrabbleController controller){
        this.controller = controller;
        attachListeners();
    }

    /**
     * This attaches the listeners to buttons.
     */
    private void attachListeners(){
        if (controller == null){
            return;
        }

        //now set the action commands
        placeWordButton.setActionCommand("PLACE_WORD");
        swapTilesButton.setActionCommand("SWAP_TILES");
        passTurnButton.setActionCommand("PASS_TURN");
        //now we add the controller as listener
        placeWordButton.addActionListener(controller);
        swapTilesButton.addActionListener(controller);
        passTurnButton.addActionListener(controller);
    }

    /**
     * This shows all the dialog messages to start a new game
     */
    private void showNewGameDialog(){
        String[] options = {"2 Players", "3 Players", "4 Players"};
        int choice = JOptionPane.showOptionDialog(this, "Select the number of players:",
                "New Game", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice >= 0){
            int numPlayers = choice + 2;
            String[] playerNames = new String[numPlayers];

            for (int i = 0; i < numPlayers; i++){
                String name = JOptionPane.showInputDialog(this, "Enter name for Player " +
                        (i + 1) + " (Leave empty for AI):", "Player " + (i + 1));
                if (name == null || name.trim().isEmpty()){
                    name = "AI (player " + (i + 1) + ")";
                }
                playerNames[i] = name.trim();
            }

            if (controller != null){
                controller.startGame(numPlayers, playerNames);
            }
        }
    }

    /**
     * This will update the view depending on the state of the model
     * It is called by the model when the state has changed.
     */
    @Override
    public void update(){
        //update information about the board
        Board board = model.getBoard();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Tile tile = board.getPosition(row, col);
                if (tile != null) {
                    char displayChar = tile.isBlank() ? tile.getRepresentedLetter() : tile.getCharacter();
                    boardButtons[row][col].setText(String.valueOf(displayChar));
                    boardButtons[row][col].setBackground(Color.YELLOW);
                } else {
                    boardButtons[row][col].setText("");
                    Board.PremiumSquare premium = board.getPremiumSquare(row, col);
                    switch (premium) {
                        case TRIPLE_WORD:
                            boardButtons[row][col].setBackground(Color.red);
                            break;
                        case DOUBLE_WORD:
                            boardButtons[row][col].setBackground(Color.pink);
                            break;
                        case TRIPLE_LETTER:
                            //use rgb to get specific color
                            boardButtons[row][col].setBackground(new Color(0, 0, 139));
                            break;
                        case DOUBLE_LETTER:
                            //use rgb to get specific color
                            boardButtons[row][col].setBackground(new Color(173, 216, 230));
                            break;
                        default:
                            boardButtons[row][col].setBackground(Color.white);
                            break;
                    }
                }
            }
        }

            //information about player is updated
            if (!model.isPlaying() || model.getPlayerList().isEmpty()) {
                currentPlayerLabel.setText("The Game has not started yet bud!");
                scoreLabel.setText("Score: 0");
                tilesRemainingLabel.setText("Tiles in bag: " + model.getTilesRemaining());
                for (int i = 0; i < 7; i++) {
                    handButtons[i].setText("");
                }
                return;
            }
            Player currentPlayer = model.getCurrentPlayer();
            currentPlayerLabel.setText("Current Player: " + currentPlayer.getName());
            scoreLabel.setText("Score: " + currentPlayer.getScore());
            tilesRemainingLabel.setText("Tiles in bag: " + model.getTilesRemaining());

            //hand gets updated
            List<Tile> tiles = currentPlayer.getTiles();
            for (int i = 0; i < 7; i++) {
                if (i < tiles.size()) {
                    Tile tile = tiles.get(i);
                    if (tile.isBlank()) {
                        handButtons[i].setText("BLANK(0)");
                    } else {
                        handButtons[i].setText(tile.getCharacter() + "(" + tile.getValue() + ")");
                    }
                } else {
                    handButtons[i].setText("");
                }
            }

        //update the buttons
        boolean playing = model.isPlaying();
        placeWordButton.setEnabled(playing);
        swapTilesButton.setEnabled(playing && model.getTilesRemaining() >= 7);
        passTurnButton.setEnabled(playing);
        //update the status
        if (!model.isPlaying()){
            if (model.getPlayerList().isEmpty()){
                statusLabel.setText("Click Game then 'New Game' for a new game to start");
                statusLabel.setForeground(Color.BLUE);
            } else {
                gameOver();
            }
        } else {
            statusLabel.setText("It is " + model.getCurrentPlayer().getName() + "'s turn");
            statusLabel.setForeground(Color.BLACK);
        }
    }

    /**
     * This shows information when the game is over
     */
    private void gameOver(){
        List<Player> players = model.getPlayerList();
        int highestScore = 0;
        Player winner = null;
        boolean tie = false;

        StringBuilder scores = new StringBuilder("Final Scores:\n");
        for (Player p : players){
            scores.append(p.getName()).append(": ").append(p.getScore()).append("\n");
            if (p.getScore() > highestScore){
                highestScore = p.getScore();
                winner = p;
                tie = false;
            } else if (p.getScore() == highestScore){
                tie = true;
            }
        }

        String message = tie? "It is a tie!!!" : winner.getName() + " wins!!!";
        JOptionPane.showMessageDialog(this, scores.toString() + "\n" + message, "Game Over :(",
                JOptionPane.INFORMATION_MESSAGE);

        statusLabel.setText("Game Over: " + message);
        statusLabel.setForeground(Color.RED);
    }

    /**
     * Shows an error message when something fails
     * @param message the error message to be shown
     */
    public void showFailed(String message){
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a success message everything works correctly
     * @param message the success message to be shown
     */
    public void showWorked(String message){
        statusLabel.setText(message);
        statusLabel.setForeground(Color.GREEN);
    }

    public void showInfoDialog(String message){
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays a dialogue box describing the AI's turn and word
     * @param playerName The name of the (AI) player
     * @param word The word that was played by the AI
     * @param points The amount of points the word played was worth
     * @param totalPoints The AI player's total score after playing the word
     */
    public void handleAIPlay(String playerName, String word, int points, int totalPoints){
        if (!model.isPlaying()){ return; }
        showInfoDialog(playerName + " played " + word + " for " + points + " points. Point total is now " + totalPoints + ".");
    }

    /**
     * Displays a dialogue box listing the tiles the AI swapped
     * @param playerName The name of the (AI) player
     * @param tiles The tiles the AI placed in the bag
     */
    public void handleAISwap(String playerName, String tiles){
        if (!model.isPlaying()){ return; }
        showInfoDialog(playerName + " swapped, placing " + tiles + " in the bag.");
    }

    /**
     * Displays a dialogue box stating that the AI has passed
     * @param playerName The name of the (AI) player
     */
    public void handleAIPass(String playerName)
    {
        if (!model.isPlaying()){ return; }
        showInfoDialog(playerName + " passed.");
    }

    public void setBoardselection(int boardtype){
        model.getBoard().setBoardselection(boardtype);
        model.notifyViews();
    }

}
