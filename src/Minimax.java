import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Martin on 22.12.2016.
 */
public class Minimax {
    private String[][] tabel;
    private int maxDepth;
    int r = 6;
    int v = 7;

    public int[] calculateMove(String[][] tabel) {
        this.tabel = tabel;
        tabel = copyGrid(tabel);
        for (String[] strings : tabel) {
            for (String string : strings) {
                System.out.print(string + " ");
            }
            System.out.println();
        }

        ArrayList<int[]> moves = getLegalMoves();

        return moves.get(new Random().nextInt(moves.size()));
    }

    private ArrayList<int[]> getLegalMoves (){
        ArrayList<int[]> moves = new ArrayList<>();
        for (int i = 5; i >= 0; i--){
            for (int j =0; j< 7; j++){
                if (tabel[i][j].equals("_") && (i==5 || tabel[i+1][j].equals("x") || tabel[i+1][j].equals("o")))
                    moves.add(new int[]{i,j});
            }
        }
        return moves;
    }

    //Function to copy grid.
    private String[][] copyGrid(String[][] tabel) {
        String[][] uusTabel = new String[6][7];
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                uusTabel[row][col] = tabel[row][col];
            }
        }
        return uusTabel;
    }
}
