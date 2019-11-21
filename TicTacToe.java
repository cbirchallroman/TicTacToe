import javax.swing.*; // needed for JFrame, and JButton

import java.awt.*; // needed for GridLayout, and for ActionListener

import java.awt.event.ActionEvent; // these two are needed to sniff mouse clicks
import java.awt.event.ActionListener;
import java.util.*; //needed for map

public class TicTacToe implements ActionListener {

    public static TicTacToe game;

    private int currentTurn; // this number enables alternate choices between an 'x' and an 'o' for various clicks
    private Board board;
    private JFrame frame;
    private int size;
    private boolean pve, first;
    private Player[] players;
    private JButton[][] buttons;
    private HashMap<JButton, Board.Tile> tilesDict;
    private HashMap<Board.Tile, JButton> buttonsDict;

    public void setButtons() {

        frame = new JFrame("Tic Tac Toe"); // create a frame for the grid, with a label
        
        frame.setLayout(new GridLayout(size, size)); // make the grid be 3x3, a user choice can change this


        for(int i = 0; i < size; i ++){
            for(int j = 0; j < size; j++){

                buttons[i][j] = new JButton(); // point the buttons to button objects
                buttons[i][j].setFont(new Font(Font.DIALOG, Font.PLAIN, 60)); // set the font of the squares
                buttons[i][j].setText(" "); // initially the squares are set to an empty string
                buttons[i][j].addActionListener(this); // enables association of clicks to the class object, in our case TicTacToe
                frame.add(buttons[i][j]); // add the buttons to the frame

                tilesDict.put(buttons[i][j], board.getTile(i, j));
                buttonsDict.put(board.getTile(i, j), buttons[i][j]);

            }
        }

        frame.pack(); // this option seemed to speed up the gui display
        frame.setSize(600 * (size / 3), 600 * (size / 3));// set the frame size
        frame.setVisible(true); // make the frame visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if we close the frame, the program ends
    }

    public void claimTile(Board.Tile tile, Player player){

        //if tile is claimed already, don't proceed
        if(!tile.unclaimed())
            return;

        //get marker of claimant
        char marker = player.getMarker();

        //record claim in board structure
        board.recordTile(tile, marker);

        //change appearance of tile
        buttonsDict.get(tile).setText(marker + "");
        buttonsDict.get(tile).setBackground(Color.lightGray);

        //if game is over, end game and don't do another turn
        if(board.gameOver()){
            endGame();
            return; 
        }

        //proceed to next turn
        nextTurn();

    }

    private void nextTurn(){

        //increment
        currentTurn = currentTurn == 1 ? 0 : 1;

        //print board status
        System.out.println(board);

        //if current player is computer, process its turn
        Player player = currentPlayer();
        if(!player.getManual())
            computerTurn(player);
        else
            System.out.println("Waiting for " + player + "'s turn...");

    }

    private void computerTurn(Player player){

        System.out.println(player + " is doing their turn...");

    }

    public void actionPerformed(ActionEvent event) { // this has to be implemented
        
        JButton buttonClicked = (JButton) event.getSource(); // get source of clicks, an object will be returned, so casting is needed
        Board.Tile tile = tilesDict.get(buttonClicked);
        Player player = currentPlayer();

        //if the tile has been claimed or the current player does not use manual input, do not proceed
        if(!tile.unclaimed() || !player.getManual())
            return;
        
        claimTile(tile, player);

    }

    private void endGame(){

        boolean draw = board.winner == ' ';

        //different dialogue depending on whether the game was a draw or if there was a winner
        String message = draw ? "The game is a draw! Do you want to play again?" : board.winner + " wins! Do you want to play again?";
        String title = draw ? "Draw!" : board.winner + "won!";

        // playAgain will store the user choice on whether a new game takes place
        int playAgain = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION); 

        // the user determines if a new game is to take place
        if(playAgain == JOptionPane.YES_OPTION) {
            frame.dispose();    //close old frame
            userConfig();
        }
        else {
            System.exit(0); // otherwise we exit
        }

    }

    public static void main(String[] args) {

        userConfig();
        
    }

    public static void userConfig(){

        JFrame frame = new JFrame();
        
        try {
            
            //ask what size of board to use
            int size = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the size of the grid (3, 4 or 5 accepted): "));
            if(size < 3 || 5 < size) {
                JOptionPane.showMessageDialog(frame, "The number you entered doesn't satisfy acceptable values. Goodbye!", "Alert", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
                return;
            }

            //ask whether to play against computer or not
            char opt = JOptionPane.showInputDialog(frame, "Do you want to play against the computer? (y/n): ").charAt(0);
            boolean pve = opt == 'y' || opt == 'Y';

            //only show option to move first if playing against the computer
            boolean first = false;
            if(pve){
                opt = JOptionPane.showInputDialog(frame, "Do you want to move first? (y/n): ").charAt(0);
                first = opt == 'y' || opt == 'Y';
            }
            
            //begin game with these parameters
            beginGame(size, pve, first);
          
        }
        
        //if Integer.parseInt() fails
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "You entered irregular input. Goodbye!", "Alert", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
        
        //if String.getChar() fails
        catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(frame, "You didn't write any input. Goodbye!", "Alert", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }

    }

    public static void beginGame(int size, boolean pve, boolean first){

        game = new TicTacToe(size, pve, first);
        game.setButtons();

    }

    private TicTacToe(int size, boolean pve, boolean first){

        this.size = size;
        this.pve = pve;
        this.first = first;
        board = new Board(size);
        currentTurn = 1;    //begin at 1 so we can call nextTurn() and have Turn 0

        buttons = new JButton[size][size]; // create an array of size^2 buttons
        tilesDict = new HashMap<>();
        buttonsDict = new HashMap<>();

        instantiatePlayers();

        nextTurn();

    }

    private void instantiatePlayers(){

        //create array of 2 players
        players = new Player[2];

        //assume that O and X will be used as markers
        char marker0 = 'O';
        char marker1 = 'X';

        //first player is computer if pve == true and first == false
        players[0] = new Player(marker0, !(pve == true && first == false), this);

        //second player is computer if pve == true and first == true
        players[1] = new Player(marker1, !(pve == true && first == true), this);
        
        //send markers to board
        board.setPlayers(marker0, marker1);

    }

    public Player currentPlayer() { return players[currentTurn]; }

    class Player {
    
        protected TicTacToe game;
        protected char marker;
        protected boolean manual;
    
        public char getMarker() { return marker; }
        public boolean getManual() { return manual; }
        public String toString(){ return "Player " + marker; }
    
        public Player(char marker, boolean manual, TicTacToe game){
    
            this.marker = marker;   //'X' or 'O' or whatever
            this.manual = manual;   //whether this takes human input or computer input
            this.game = game;
    
        }
    
    }

}