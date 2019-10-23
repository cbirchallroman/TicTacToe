import java.util.ArrayList;

//A is default
//O is player
//X is computer
enum Status{O, X, A}

public class Board{

    class Tile{

        private int x;
        private int y;
        private Tile[] tiles;
        private ArrayList<Row> rows;
        private Board board;
        private Status status;

        public Tile(int x, int y, Board board){

            this.x = x;
            this.y = y;
            this.board = board;
            status = Status.A;
            rows = new ArrayList<>();

        }

        public void SetNeighbors(Tile[] tiles){

            this.tiles = tiles;

        }

        public boolean Open(){ return status == Status.A;}
        public void Set(Status status){

            if(!Open())
                System.out.println("Can't set tile at " + x + " " + y);

            this.status = status;
            board.incrementTotalScore();
            for(int i = rows.size() - 1; i >= 0; i--){

                Row row = rows.get(i);
                row.updateScore(status);

                //if this move is a winning move, declare the player the winner
                if(row.winner() == status)
                    board.declareWinner(this);

                //else if this row is no longer winnable, make it remove itself from its tiles and the board
                else if(!row.winnable())
                    row.leave();
            }

        }

        public String coordinates(){

            return x + ", " + y;

        }

        public String toString(){

            return "[" + (status == Status.A ? ' ' : status) + "]";

        }

        void JoinRow(Row row){

            rows.add(row);

        }

        public void leaveRow(Row row){

            rows.remove(row);

        }

    }

    class Row{

        Board board;
        private Tile[] tiles;
        private int score, absScore, target;

        public Row(Tile[] tiles, Board board){

            this.tiles = tiles;
            this.board = board;
            score = 0; //negative if more X, positive if more O
            absScore = 0; //total number of tiles claimed
            target = tiles.length; //how many tiles of the same type necessary for win condition

            //the tiles in the row will know that they are a member of this row
            for(Tile tile : tiles)
                tile.JoinRow(this);

        }

        //whether this tile is still winnable
        //  if Math.abs(score) != absScore, this means that there are tiles of different types
        //  therefore it is not possible to win on this row anymore
        //  EX. a tile with two Xs and one O has a score of -1 and an abs. score of 3
        public boolean winnable(){

            return Math.abs(score) == absScore;

        }

        //if a player has won a row, the magnitude of the score must equal the target
        //  eg. if score is -3 or 3 and target is 3, return true

        //  if the score is negative, X wins
        //  else if score is positive, O wins
        //  if row isn't won at all, return A as the default
        public Status winner(){

            //only proceed if conditions are met
            if(Math.abs(score) != target)
                return Status.A;

            return score > 0 ? Status.O : Status.X;

        }

        public String toString(){

            String s = "";
            for(Tile tile : tiles)
                s += "(" + tile.coordinates() + ") ";

            return s;

        }

        void updateScore(Status player){

            absScore++; //increase absolute score by 1
            switch (player){

                case O: //O increases score
                    score++;
                    break;
                case X: //X decreases score
                    score--;
                    break;

            }

        }

        public void leave(){

            for(Tile tile : tiles)
                tile.leaveRow(this);

            board.disqualifyRow(this);
        }

    }

    private int size;
    private int area;
    private int totalScore;
    private boolean winnable;
    public Status winner;
    public Tile[][] tiles;
    public ArrayList<Row> rows;

    public Board(int size){

        this.size = size;
        area = size * size;
        totalScore = 0;
        winnable = true;
        tiles = new Tile[size][size];   //board of size 3 has 9 tiles
        rows = new ArrayList<>(size * 2 + 2);   //board of size 3 has 8 winning combinations
        winner = Status.A;

        for(int i = 0; i < size; i ++){

            //make horizontal row; we'll deal with verticals and diagonals in a second
            Tile[] rowTiles = new Tile[size];

            for(int j = 0; j < size; j++){

                Tile tile = new Tile(i, j, this);
                tiles[i][j] = tile;
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

        winner = tile.status;
        System.out.println(winner.name() + " wins");

    }

    void disqualifyRow(Row row){

        rows.remove(row);

    }

    public int getTotalScore(){ return totalScore; }
    public void incrementTotalScore(){ totalScore++; }
    public boolean noMovesLeft(){ return totalScore == area; }

}