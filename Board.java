import java.util.ArrayList;

public class Board{

    class Tile{

        private int x;
        private int y;
        private ArrayList<Row> rows;
        private Board board;
        private char claimer;

        public Tile(int x, int y, Board board){

            this.x = x;
            this.y = y;
            this.board = board;
            claimer = ' ';
            rows = new ArrayList<>();

        }

        public boolean unclaimed(){ return claimer == ' ';}
        public void claim(char status){

            if(!unclaimed())
                System.out.println("Can't set tile at " + x + " " + y);

            this.claimer = status;
            board.recordTile(this);
            for(int i = rows.size() - 1; i >= 0; i--){

                Row row = rows.get(i);
                row.updateScore(status);

                //if this move is a winning move, declare the player the winner
                if(row.won())
                    board.declareWinner(this);
            }

        }

        public String coordinates(){

            return x + ", " + y;

        }

        public String toString(){

            return "[" + claimer + "]";

        }

        void joinRow(Row row){

            rows.add(row);

        }

        public void leaveRow(Row row){

            rows.remove(row);

        }

        //returns number of rows eliminated if given player claims this tile
        public int rowsCanEliminate(char player){

            int score = 0;

            for(Row row : rows){
                char other = row.winner();
                if(other != ' ')
                    continue;
                if(other != player)
                    score++;
            }

            return score;

        }

    }

    class Row implements Comparable<Row>{

        Board board;
        private Tile[] tiles;
        private int score, absScore, target;
        public char claimer = ' ';

        public Row(Tile[] tiles, Board board){

            this.tiles = tiles;
            this.board = board;
            score = 0; //negative if more X, positive if more O
            absScore = 0; //total number of tiles claimed
            target = tiles.length; //how many tiles of the same type necessary for win condition

            //the tiles in the row will know that they are a member of this row
            for(Tile tile : tiles)
                tile.joinRow(this);

        }

        //returns true if the score equals target; this is only possible if all tiles in row are claimed by the same player
        public boolean won(){ return absScore == target; }

        public String toString(){

            String s = "";
            for(Tile tile : tiles)
                s += "(" + tile.coordinates() + ") ";

            return s;

        }

        public int compareTo(Row other){

            return this.score - ((Row)other).score;

        }

        //returns the tile with the most possible rows for this player to eliminate
        public Tile tileWithMostDisqualifications(char player){

            Tile most = null;
            int score = 0;
            for(Tile tile : tiles){

                int localScore = tile.rowsCanEliminate(player);
                if(localScore > score){
                    most = tile;
                    score = localScore;
                }

            }
            return most;

        }

        void updateScore(char player){

            //if the player claiming this tile is not the same player who claimed tiles in this row before,
            //  this row is disqualified
            if(claimer != ' ' && claimer != player){
                disqualify();
                return;
            }

            absScore++; //increase absolute score by 1
            claimer = player;

            if(player == board.player1)
                score++;
            else
                score--;

        }

        public void disqualify(){

            for(Tile tile : tiles)
                tile.leaveRow(this);

            board.disqualifyRow(this);
        }

    }

    private int size;
    private int area;
    public char winner;
    public char player1;
    public char player2;
    public Tile[][] tiles;
    public ArrayList<Tile> unclaimed;
    public DoublePriorityQueue<Row> rows;

    public Board(int size){

        this.size = size;
        area = size * size;
        tiles = new Tile[size][size];   //board of size 3 has 9 tiles
        unclaimed = new ArrayList<>();
        rows = new DoublePriorityQueue<>();   //board of size 3 has 8 winning combinations
        winner = ' ';
        player1 = 'X';
        player2 = 'O';

        for(int i = 0; i < size; i ++){

            //make horizontal row; we'll deal with verticals and diagonals in a second
            Tile[] rowTiles = new Tile[size];

            for(int j = 0; j < size; j++){

                Tile tile = new Tile(i, j, this);
                tiles[i][j] = tile;
                unclaimed.add(tile);
                rowTiles[j] = tile;

            }

            rows.add(new Row(rowTiles, this));

        }

        //vertical rows
        for(int j = 0; j < size; j++){

            Tile[] columnTiles = new Tile[size];
            for(int i = 0; i < size; i++)
                columnTiles[i] = tiles[i][j];
            rows.add(new Row(columnTiles, this));

        }

        //diagonal rows
        Tile[] diagonal_a = new Tile[size];
        Tile[] diagonal_b = new Tile[size];
        for(int k = 0; k < size; k++){
            int opposite = size - 1 - k;
            diagonal_a[k] = tiles[k][k];
            diagonal_b[k] = tiles[k][opposite];
        }

        rows.add(new Row(diagonal_a, this));
        rows.add(new Row(diagonal_b, this));

    }

    public Tile getTile(int i, int j){

        return tiles[i][j];

    }

    //prints out every winning combination of tiles
    public void printRows(){

        for(Row row : rows)
            System.out.println(row);

    }

    //prints status of board
    public String toString(){

        String s = "";
        for(int i = 0; i < size; i++){

            for(int j = 0; j < size; j++)
                s += (tiles[i][j]).toString();
            s += "\n";

        }

        return s + " (" + rows.size() + " rows)";

    }

    //returns O if O wins
    //returns X if X wins
    //returns A if no winner
    void declareWinner(Tile tile){

        winner = tile.claimer;
        System.out.println("Player " + winner + " wins");

    }

    void disqualifyRow(Row row){

        rows.remove(row);

    }

    public int getTotalScore(){ return area - unclaimed.size(); }
    public void recordTile(Tile tile){
        totalScore++;
        unclaimed.remove(tile);
    }
    public boolean noMoreTiles(){ return unclaimed.isEmpty(); }
    public boolean winnable(){ return rows.size() > 0; }
    public boolean gameOver(){ return winner != ' ' || noMoreTiles(); } //game over if winner or no more tiles

}