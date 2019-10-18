import javax.swing.*; // needed for JFrame, and JButton
import java.awt.*; // needed for GridLayout, and for ActionListener

import java.awt.event.ActionEvent;// these two are needed to sniff mouse clicks
import java.awt.event.ActionListener;

public class TicTacToe implements ActionListener {
    int altNum = 0; // this number enables alternate choices between an 'x' and an 'o' for various clicks
    Board board;
    int size = 3;
    int area = 9;
    JButton[] buttons;
    String marker = " "; // initialize the marker, an 'x' or an 'o' to null

    public void setButtons() {

        JFrame frame = new JFrame("Tic Tac Toe"); // create a frame for the grid, with a label
        
        frame.setLayout(new GridLayout(size, size)); // make the grid be 3x3, a user choice can change this
        
        for (int i = 0; i < area; i++) {
            buttons[i] = new JButton(); // point the buttons to button objects
            buttons[i].setFont(new Font(Font.DIALOG, Font.PLAIN, 60)); // set the font of the squares
            buttons[i].setText(" "); // initially the squares are set to an empty string
            buttons[i].addActionListener(this); // enables association of clicks to the class object, in our case TicTacToe
            frame.add(buttons[i]); // add the buttons to the frame
        }

        frame.pack(); // this option seemed to speed up the gui display
        frame.setSize(600 * (size / 3), 600 * (size / 3));// set the frame size
        frame.setVisible(true); // make the frame visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if we close the frame, the program ends
    }
    public void actionPerformed(ActionEvent event) { // this has to be implemented
        
        JButton buttonClicked = (JButton) event.getSource(); // get source of clicks, an object will be returned, so casting is needed
        int playAgain = 10; // playAgain will store the user choice on whether a new game takes place

        if (this.altNum % 2 == 0 && buttonClicked.getText().equals(" ") && !gameEnds()) { 
            buttonClicked.setText("X"); // the empty string option above is needed, if clicking on an already
            altNum++; // clicked square is to cause nothing
        } else {
            if (buttonClicked.getText().equals(" ") && !gameEnds()) {
                buttonClicked.setText("O");
                altNum++;
            } 
        }

        if (gameEnds()) { // if the game has ended
              if (altNum < area) { // before reaching 9 moves, someone has won
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
        for (int i = 0; i < area; i++) {
            buttons[i].setText(" "); // reset the buttons to empty
        }
        altNum = 0; // reset altnum to zero
    }
    public boolean gameEnds() {

        
        for (int i = 0; i <= 6; i += 3) { // horizontal winning combinations
            if (adjacents(i, i+1) && adjacents(i+1, i+2)) {
                marker = buttons[i].getText();
                return true;
            }
        }
        for (int i = 0; i <= 2; i++) { // vertical winning combinations
            if (adjacents(i, i+3) && adjacents(i+3, i + 6)) {
                marker = buttons[i].getText();
                return true;
            }
        }
        // diagonal winning combinations
        if (adjacents(0, 4) && adjacents(4, 8)) {
            marker = buttons[0].getText();
            return true;
        }
        if (adjacents(2, 4) && adjacents(4, 6)) {
            marker = buttons[2].getText();
            return true;
        }
        // if <area> moves have been reached the game ends in a draw
        if (altNum == area) {
            return true;
        }
        // otherwise the game is still afoot
        return false;
        
    }
    // adjacents because of the constraint equals imposes for string checking, we can only check two args
    public boolean adjacents(int i, int j) {
        // we want to compare squares with text, that have already been clicked
        if (buttons[i].getText().equals(buttons[j].getText()) && !buttons[i].getText().equals(" ")) {
            return true;
        } else {
            return false;
        }
    }

    public TicTacToe(int size){

        this.size = size;
        area = size * size;
        board = new Board(size);

        buttons = new JButton[area]; // create an array of size^2 buttons

    }

    public static void main(String[] args) {
        TicTacToe aGame = new TicTacToe(3);
        aGame.setButtons();
    }
}
