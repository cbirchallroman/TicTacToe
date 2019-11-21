import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Board{

    class Tile{

        private int x;
        private int y;
        private ArrayList<Row> rows;
        private Board board;
        private char player;

        private int score;
        private int visits;



        public Tile(int x, int y, Board board){

            this.x = x;
            this.y = y;
            this.board = board;
            player = ' ';
            rows = new ArrayList<>();

        }

        public boolean unclaimed(){ return player == ' ';}

        public void claim(char player){

            if(!unclaimed())
                System.out.println("Can't set tile at " + x + " " + y);

            this.player = player;
            for(int i = rows.size() - 1; i >= 0; i--){

                rows.get(i).claimTile(this);

            }

        }
        public char getClaimer(){ return player; }
        public int getScore() { return score; }
        public int getVisits() { return visits; }

        public String toString(){

            return "[" + player + "]";

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
                char other = row.claimer;
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
        private ArrayList<Tile> tiles;
        private int score, absScore, target;
        public char claimer = ' ';

        public Row(Tile[] tiles, Board board){

            this.tiles = new ArrayList<>();
            Collections.addAll(this.tiles, tiles);
            this.board = board;
            score = 0; //negative if more O, positive if more X
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
            //for(Tile tile : tiles)
            //    s += "(" + tile.coordinates() + ") ";
            for(int i = 0; i < absScore; i++)
                s += claimer;

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

        void claimTile(Tile tile){

            char player = tile.player;

            //if the player claiming this tile is not the same player who claimed tiles in this row before,
            //  this row is disqualified
            if(claimer != ' ' && claimer != player){
                disqualify();
                return;
            }

            absScore++; //increase absolute score by 1
            this.claimer = player;

            //player1 decreases score, player2 increases
            if(player == board.player0)
                score--;
            else
                score++;

            //if this was a winning move, declare a winner
            if(won())
                board.declareWinner(player);

            board.updateRow(this);  //update row's standing among the other rows
            tiles.remove(tile);     //remove claimed tile from list of tiles in this row
            tile.leaveRow(this);    //and do the same from the tile's end

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
    public char player0;
    public char player1;
    public Tile[][] tiles;
    public ArrayList<Tile> unclaimed;
    public DoublePriorityQueue<Row> rows;

    public void setPlayers(char player0, char player1){

        this.player0 = player0;
        this.player1 = player1;

    }

    public Board(int size){

        this.size = size;
        area = size * size;
        tiles = new Tile[size][size];   //board of size 3 has 9 tiles
        unclaimed = new ArrayList<>(area);
        rows = new DoublePriorityQueue<>();   //board of size 3 has 8 winning combinations
        winner = ' ';

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

        System.out.println(i + " " + j + " " + tiles[i][j]);
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

        return s + rows.size() + " rows\t[ " + player0 + ": (" + getBestRow(player0) + ")\t " + player1 + ": (" + getBestRow(player1) + ") ]";

    }

    //declare the winner here
    void declareWinner(char winner){

        this.winner = winner;

    }

    void disqualifyRow(Row row){

        rows.remove(row);

    }

    Row getBestRow(char player){

        if(player == player0)
            return rows.peek();
        return rows.peekLast();

    }

    Row getWorstRow(char player){

        return player == player0 ? getBestRow(player1) : getBestRow(player0);

    }

    public int getTotalScore(){ return area - unclaimed.size(); }
    public void recordTile(Tile tile, char player){
        unclaimed.remove(tile);
        tile.claim(player);
    }
    public void updateRow(Row row){
        rows.update(row);
    }
    public boolean noMoreTiles(){ return unclaimed.isEmpty(); }
    public boolean winnable(){ return rows.size() > 0; }
    public boolean gameOver(){ return winner != ' ' || noMoreTiles(); } //game over if winner or no more tiles

}