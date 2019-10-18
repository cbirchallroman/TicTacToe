import java.util.ArrayList;

//A is default
//O is player
//X is computer
enum Status{O, X, A}

class Tile{

    private int x;
    private int y;
    private Tile[] tiles;
    private ArrayList<Row> rows;
    private Status status;

    public Tile(int x, int y){

        this.x = x;
        this.y = y;
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
        for(Row row : rows)
            row.UpdateScore(status);

    }

    public String coordinates(){

        return x + ", " + y;

    }

    public String toString(){

        return "[" + (status == Status.A ? ' ' : status) + "]";

    }

    public void JoinRow(Row row){

        rows.add(row);

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

        return Math.abs(score) != absScore;

    }

    //if a player has won a row, the row must be winnable (ie. only one type of tile present)
    //  and it must have number of claimed tiles equal to the total number of tiles

    //  if the score is negative, X wins
    //  else if score is positive, O wins
    //  if row isn't won at all, return A as the default
    public Status winner(){

        //only proceed if conditions are met
        if(!(winnable() && absScore == target))
            return Status.A;

        return score > 0 ? Status.O : Status.X;

    }

    public String toString(){

        String s = "";
        for(Tile tile : tiles)
            s += "(" + tile.coordinates() + ") ";

        return s;

    }

    void UpdateScore(Status player){

        absScore++;
        switch (player){

            case O:
                score++;
                break;
            case X:
                score--;
                break;

        }

    }

}

public class Board{

    private int size;
    public Tile[][] tiles;
    public Row[] rows;

    public Board(int size){

        this.size = size;
        tiles = new Tile[size][size];   //board of size 3 has 9 tiles
        rows = new Row[size * 2 + 2];   //board of size 3 has 8 winning combinations

        for(int i = 0; i < size; i ++){

            //make horizontal row; we'll deal with verticals and diagonals in a second
            Tile[] rowTiles = new Tile[size];

            for(int j = 0; j < size; j++){

                Tile tile = new Tile(i, j);
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
            diagonal_b[k] = tiles[opposite][opposite];
        }

        rows[size + size] = new Row(diagonal_a);
        rows[size + size + 1] = new Row(diagonal_b);

    }

    //prints out every winning combination of tiles
    public void printRows(){

        for(Row row : rows)
            System.out.println(row);

    }

    //returns O if O wins
    //returns X if X wins
    //returns A if no winner
    public Status Winner(){

        return Status.A;

    }

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

}