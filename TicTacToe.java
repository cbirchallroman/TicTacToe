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

    public void claimTile(Board.Tile tile, char player){

        if(!tile.unclaimed())
            return;

        board.recordTile(tile, player);
        buttonsDict.get(tile).setText(player + "");
        buttonsDict.get(tile).setBackground(Color.lightGray);
        if(board.gameOver()){
            System.out.println(currentPlayer() + " wins!");
            endGame();
            return; //don't do another turn
        }
        nextTurn();

    }

    private void nextTurn(){


        currentTurn = currentTurn == 1 ? 0 : 1;

        System.out.println(board);
        System.out.println("Current player: " + currentPlayer());

    }

    public void actionPerformed(ActionEvent event) { // this has to be implemented
        
        JButton buttonClicked = (JButton) event.getSource(); // get source of clicks, an object will be returned, so casting is needed
        Board.Tile tile = tilesDict.get(buttonClicked);

        if(!tile.unclaimed())
            return;

        char claim = this.currentTurn % 2 == 0 ? board.player1 : board.player2;
        claimTile(tile, claim);

    }

    private void endGame(){

        boolean draw = board.winner != ' ';

        //different dialogue depending on whether the game was a draw or if there was a winner
        String message = draw ? "The game is a draw! Do you want to play again?" : board.winner + " wins! Do you want to play again?";
        String title = draw ? "Draw!" : board.winner + "won!";

        // playAgain will store the user choice on whether a new game takes place
        int playAgain = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION); 

        // the user determines if a new game is to take place
        if(playAgain == JOptionPane.YES_OPTION) {
            frame.dispose();    //close old frame
            beginGame(size);
        }
        else {
            System.exit(0); // otherwise we exit
        }

    }

    private TicTacToe(int size){

        this.size = size;
        board = new Board(size);
        currentTurn = 0;

        buttons = new JButton[size][size]; // create an array of size^2 buttons
        tilesDict = new HashMap<>();
        buttonsDict = new HashMap<>();

        players = new Player[2];
        players[0] = new Human('O');
        players[1] = new Human('X');

    }

    public String currentPlayer() { return players[currentTurn].toString(); }

    public static void beginGame(int size){

        game = new TicTacToe(size);
        game.setButtons();
        System.out.println("Current player: " + game.currentPlayer());

    }

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        
        try {
            
            String gridSize = JOptionPane.showInputDialog(frame, "Enter the size of the grid (3, 4 or 5 accepted): ");
            int size = Integer.parseInt(gridSize);
          
            if (size < 3 || 5 < size) {
                JOptionPane.showMessageDialog(frame, "The number you entered doesn't satisfy acceptable values. Goodbye!", "Alert", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
            
            else {
                beginGame(size);
            }
          
        }
        
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "You entered irregular input. Goodbye!", "Alert", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }       
        
    
    }

}

abstract class Player {

    private char marker;

    public abstract void doMove(Board state);
    public Player(char marker){

        this.marker = marker;

    }

    public String toString(){ return "Player " + marker; }

}

class Human extends Player {

    public Human(char marker) {
        super(marker);
    }

    @Override
    public void doMove(Board state){
    


    }

    public String toString() { return "Human " + super.toString(); }

}

class Computer extends Player {

    public Computer(char marker) {
        super(marker);
    }

    @Override
    public void doMove(Board state){



    }

    public String toString() { return "Computer " + super.toString(); }

}