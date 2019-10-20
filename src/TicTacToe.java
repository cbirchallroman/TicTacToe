import javax.swing.*; // needed for JFrame, and JButton
import java.awt.*; // needed for GridLayout, and for ActionListener

import java.awt.event.ActionEvent; // these two are needed to sniff mouse clicks
import java.awt.event.ActionListener;
import java.util.*; //needed for map

public class TicTacToe implements ActionListener {
    int altNum = 0; // this number enables alternate choices between an 'x' and an 'o' for various clicks
    Board board;
    int size = 3;
    int area = 9;
    JButton[][] buttons;
    HashMap<JButton, Board.Tile> tilesDict;
    String marker = " "; // initialize the marker, an 'x' or an 'o' to null

    public void setButtons() {

        JFrame frame = new JFrame("Tic Tac Toe"); // create a frame for the grid, with a label
        
        frame.setLayout(new GridLayout(size, size)); // make the grid be 3x3, a user choice can change this

        for(int i = 0; i < size; i ++){
            for(int j = 0; j < size; j++){

                buttons[i][j] = new JButton(); // point the buttons to button objects
                buttons[i][j].setFont(new Font(Font.DIALOG, Font.PLAIN, 60)); // set the font of the squares
                buttons[i][j].setText(" "); // initially the squares are set to an empty string
                buttons[i][j].addActionListener(this); // enables association of clicks to the class object, in our case TicTacToe
                frame.add(buttons[i][j]); // add the buttons to the frame

                tilesDict.put(buttons[i][j], board.getTile(i, j));

            }
        }

        frame.pack(); // this option seemed to speed up the gui display
        frame.setSize(600 * (size / 3), 600 * (size / 3));// set the frame size
        frame.setVisible(true); // make the frame visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if we close the frame, the program ends
    }
    public void actionPerformed(ActionEvent event) { // this has to be implemented
        
        JButton buttonClicked = (JButton) event.getSource(); // get source of clicks, an object will be returned, so casting is needed
        int playAgain = 10; // playAgain will store the user choice on whether a new game takes place
        Board.Tile tile = tilesDict.get(buttonClicked);

        if(!tile.Open())
            return;

        Status claim = this.altNum % 2 == 0 ? Status.X : Status.O;
        tile.Set(claim);
        buttonClicked.setText(claim.name());
        altNum++;

        System.out.println(board);

        if (gameEnds()) { // if the game has ended
              if (marker != null) { // before reaching 9 moves, someone has won
                  playAgain = JOptionPane.showConfirmDialog(null, marker + " wins! Do you want to play again?", marker + "won!", JOptionPane.YES_NO_OPTION); 
              } else {// otherwise the game is a draw
                playAgain = JOptionPane.showConfirmDialog(null, " The game is a draw! Do you want to play again?", "Draw!", JOptionPane.YES_NO_OPTION); 
              }

              // the user determines if a new game is to take place
                if(playAgain == JOptionPane.YES_OPTION) {
                    clearButtons(); // if yes, then the board is cleared for the new game
                } else {
                    System.exit(0); // otherwise we exit
                }
                   
        }

              
        
    }
    public void clearButtons() {

        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++){
                buttons[i][j].setText(" "); // reset the buttons to empty
            }
        }

        board.reset();

        altNum = 0; // reset altnum to zero
        marker = null;
    }
    public boolean gameEnds() {

        //if the board detects a winner
        Status winner = board.winner();
        if(winner != Status.A) {
            marker = winner.name();
            return true;
        }

        // if <area> moves have been reached the game ends in a draw
        if (board.noMovesLeft()) {
            marker = null;
            return true;
        }
        // otherwise the game is still afoot
        return false;
        
    }

    public TicTacToe(int size){

        this.size = size;
        area = size * size;
        board = new Board(size);
        altNum = 0;

        buttons = new JButton[size][size]; // create an array of size^2 buttons
        tilesDict = new HashMap<>();
        marker = null;

    }

    public static void main(String[] args) {
        TicTacToe aGame = new TicTacToe(3);
        aGame.setButtons();
    }
}