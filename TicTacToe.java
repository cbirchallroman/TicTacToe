import javax.swing.*; // needed for JFrame, and JButton
import java.awt.*; // needed for GridLayout, and for ActionListener

import java.awt.event.ActionEvent; // these two are needed to sniff mouse clicks
import java.awt.event.ActionListener;
import java.util.*; //needed for map

public class TicTacToe implements ActionListener {
    int altNum; // this number enables alternate choices between an 'x' and an 'o' for various clicks
    Board board;
    int size;
    JButton[][] buttons;
    HashMap<JButton, Board.Tile> tilesDict;
    HashMap<Board.Tile, JButton> buttonsDict;

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
        altNum++;

        System.out.println(board);

        checkGameOver();

    }

    public void actionPerformed(ActionEvent event) { // this has to be implemented
        
        JButton buttonClicked = (JButton) event.getSource(); // get source of clicks, an object will be returned, so casting is needed
        Board.Tile tile = tilesDict.get(buttonClicked);

        if(!tile.unclaimed())
            return;

        char claim = this.altNum % 2 == 0 ? board.player1 : board.player2;
        claimTile(tile, claim);

    }

    public void checkGameOver(){

        if (board.gameOver()) {// if the game has ended

            int playAgain = 10; // playAgain will store the user choice on whether a new game takes place

            if (board.winner != ' ') { // before reaching 9 moves, someone has won
                playAgain = JOptionPane.showConfirmDialog(null, board.winner + " wins! Do you want to play again?", board.winner + "won!", JOptionPane.YES_NO_OPTION);
            }
            else {// otherwise the game is a draw
                playAgain = JOptionPane.showConfirmDialog(null, " The game is a draw! Do you want to play again?", "Draw!", JOptionPane.YES_NO_OPTION); 
            }

            // the user determines if a new game is to take place
            if(playAgain == JOptionPane.YES_OPTION) {
                clearBoard(); // if yes, then the board is cleared for the new game
            }
            else {
                System.exit(0); // otherwise we exit
            }
                   
        }

    }

    public void clearBoard() {

        
        board = new Board(size);

        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++){
                buttons[i][j].setText(" "); // reset the buttons to empty
                buttons[i][j].setBackground(buttons[i][j].getForeground());
                tilesDict.put(buttons[i][j], board.getTile(i, j));
            }
        }


        altNum = 0; // reset altnum to zero
    }

    public TicTacToe(int size){

        this.size = size;
        board = new Board(size);
        altNum = 0;

        buttons = new JButton[size][size]; // create an array of size^2 buttons
        tilesDict = new HashMap<>();

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
                TicTacToe aGame = new TicTacToe(size);
                aGame = new TicTacToe(Integer.parseInt(gridSize));
                aGame.setButtons();
            }
          
        }
        
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "You entered irregular input. Goodbye!", "Alert", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }       
        
    
    }
}