public class MonteCarlo {

    private int limit;
    private Board state;
    private static MonteCarlo search;

    private MonteCarlo(int limit, Board state){

        this.limit = limit;
        this.state = state;

    }

    public static void LoadSearch(int limit, Board state){

        search = new MonteCarlo(limit, state);

    }

    

}