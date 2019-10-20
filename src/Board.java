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
            for(Row row : rows)
                row.updateScore(status);

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

        void reset(){

            status = Status.A;

        }

    }

    class Row{

        private Tile[] tiles;
        private int score, absScore, target;

        public Row(Tile[] tiles){

            this.tiles = tiles;
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

            return Math.abs(score) != absScore && absScore < target;

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

        void reset(){

            score = 0;
            absScore = 0;

        }

    }

    private int size;
    private int area;
    private int totalScore;
    private boolean winnable;
    public Tile[][] tiles;
    public Row[] rows;

    public Board(int size){

        this.size = size;
        area = size * size;
        totalScore = 0;
        winnable = true;
        tiles = new Tile[size][size];   //board of size 3 has 9 tiles
        rows = new Row[size * 2 + 2];   //board of size 3 has 8 winning combinations

        for(int i = 0; i < size; i ++){

            //make horizontal row; we'll deal with verticals and diagonals in a second
            Tile[] rowTiles = new Tile[size];

            for(int j = 0; j < size; j++){

                Tile tile = new Tile(i, j, this);
                tiles[i][j] = tile;
                rowTiles[j] = tile;

            }

            rows[i] = new Row(rowTiles);

        }

        //vertical rows
        for(int j = 0; j < size; j++){

            Tile[] columnTiles = new Tile[size];
            for(int i = 0; i < size; i++)
                columnTiles[i] = tiles[i][j];
            rows[size + j] = new Row(columnTiles);

        }

        //diagonal rows
        Tile[] diagonal_a = new Tile[size];
        Tile[] diagonal_b = new Tile[size];
        for(int k = 0; k < size; k++){
            int opposite = size - 1 - k;
            diagonal_a[k] = tiles[k][k];
            diagonal_b[k] = tiles[k][opposite];
        }

        rows[size + size] = new Row(diagonal_a);
        rows[size + size + 1] = new Row(diagonal_b);

    }

    public Tile getTile(int i, int j){

        return tiles[i][j];

    }

    //prints out every winning combination of tiles
    public void printRows(){

        for(Row row : rows)
            System.out.println(row);

    }

    //returns O if O wins
    //returns X if X wins
    //returns A if no winner
    public Status winner(){

        //this function also keeps track of whether the game is still winnable (regardless of empty spaces)
        boolean winnable = false;

        //if there is a winner on any of the rows, return that one
        for(Row row : rows){
            Status winner = row.winner();
            System.out.println(row + " " + winner.name() + " " + row.score);
            if(winner != Status.A)
                return winner;
            else if(row.winnable())
                winnable = true;
        }

        //winnable?
        this.winnable = winnable;

        return Status.A;

    }

    //

    //prints status of board
    public String toString(){

        String s = "";
        for(int i = 0; i < size; i++){

            for(int j = 0; j < size; j++)
                s += (tiles[i][j]).toString();
            s += "\n";

        }

        return s;

    }

    public void reset(){

        for(Row row : rows)
            row.reset();

        for(Tile[] row : tiles)
            for(Tile tile : row)
                tile.reset();

        totalScore = 0;
        winnable = true;

    }

    public int getTotalScore(){ return totalScore; }
    public void incrementTotalScore(){ totalScore++; }
    public boolean noMovesLeft(){ return totalScore == area; }

}