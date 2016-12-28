import javafx.scene.input.MouseEvent;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Martin on 22.12.2016.
 */

public class Minimax {

    private final int WIN = 8;
    private final int BEST_CASE = 7;
    private final int NEXT_BEST = 6;
    private final int GOOD_CASE = 5;
    private final int MEHH_CASE = 4;
    private final int BADD_CASE = 3;
    private final int NEXT_WRST = 2;
    private final int WRST_CASE = 1;
    private final int LOSE = 0;

    public int[] calculateMove(String[][] tabel) {


        char[][] charTabel = copyGrid(tabel);

        int[] move = minimax('x', charTabel);

/*
        if (move[0] == -1) {
            System.out.println("random");
            ArrayList<int[]> moves = getLegalMoves(charTabel);
            return moves.get(new Random().nextInt(moves.size()));
        }
*/

        return move;
    }

    private ArrayList<int[]> getLegalMoves(char[][] tabel) {
        ArrayList<int[]> moves = new ArrayList<>();
        for (int i = 4; i >= 0; i--) {
            for (int j = 0; j < 7; j++) {
                if (tabel[i][j] == '_' && (i == 4 || tabel[i + 1][j] == 'x' || tabel[i + 1][j] == 'o'))
                    moves.add(new int[]{i, j});
            }
        }
        return moves;
    }

    //Function to copy grid.
    private char[][] copyGrid(String[][] tabel) {
        char[][] uusTabel = new char[5][7];
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {
                uusTabel[row][col] = tabel[row][col].charAt(0);
            }
        }
        return uusTabel;
    }

    private String rowsToString(char[][] tabel) {
        StringBuilder sb = new StringBuilder();

        for (char[] row : tabel) {
            for (char cell : row) {
                sb.append(cell);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String colsToString(char[][] tabel) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            for (char[] row : tabel) {
                sb.append(row[i]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String diag1ToString(char[][] tabel) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < 3; col++) {
            for (int i = 0; i < 5 ; i++) {
                sb.append(tabel[i][col + i]);
            }
            sb.append("\n");
        }

        sb.append(tabel[1][0]).append(tabel[2][1]).append(tabel[3][2]).append(tabel[4][3]).append("\n");
        sb.append(tabel[0][3]).append(tabel[1][4]).append(tabel[2][5]).append(tabel[3][6]).append("\n");

        return sb.toString();
    }

    private String diag2ToString(char[][] tabel) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < 3; col++) {
            for (int i = 0; i < 5 ; i++) {
                sb.append(tabel[4-i][col + i]);
            }
            sb.append("\n");
        }

        sb.append(tabel[3][0]).append(tabel[2][1]).append(tabel[1][2]).append(tabel[0][3]).append("\n");
        sb.append(tabel[4][3]).append(tabel[3][4]).append(tabel[2][5]).append(tabel[1][6]).append("\n");
        return sb.toString();
    }

    private int eval(char player, char[][] tabel) {
        String allPossibleWINs = rowsToString(tabel) +
                colsToString(tabel) +
                diag1ToString(tabel) +
                diag2ToString(tabel);

        int current_case = MEHH_CASE;
        
        if (allPossibleWINs.contains("oooo"))
            return player == 'x' ? LOSE : WIN;
        if (allPossibleWINs.contains("xxxx"))
            return player == 'x' ? WIN : LOSE;

        if (allPossibleWINs.contains("_xxx_"))
            return player == 'x' ? BEST_CASE : WRST_CASE;
        if (allPossibleWINs.contains("_ooo_"))
            return player == 'x' ? WRST_CASE : BEST_CASE;

        if (allPossibleWINs.contains("xxx_") || allPossibleWINs.contains("_xxx")
                || allPossibleWINs.contains("x_xx") || allPossibleWINs.contains("xx_x"))
            return player == 'x' ? NEXT_BEST : NEXT_WRST;

        if (allPossibleWINs.contains("ooo_") || allPossibleWINs.contains("_ooo")
                || allPossibleWINs.contains("o_oo") || allPossibleWINs.contains("oo_o"))
            return player == 'x' ? NEXT_WRST : NEXT_BEST;

        if (allPossibleWINs.contains("_oo_") || allPossibleWINs.contains("oo__")
                || allPossibleWINs.contains("__oo") || allPossibleWINs.contains("o__o")
                || allPossibleWINs.contains("_o_o") || allPossibleWINs.contains("o_o_"))
            return player == 'x' ? BADD_CASE : GOOD_CASE;

        if (allPossibleWINs.contains("_xx_") || allPossibleWINs.contains("xx__")
                || allPossibleWINs.contains("__xx") || allPossibleWINs.contains("x__x")
                || allPossibleWINs.contains("_x_x") || allPossibleWINs.contains("x_x_"))
            return player == 'x' ? GOOD_CASE : BADD_CASE;

        return current_case;
    }

    private ArrayList<Move> compilePossibilites(char player, char[][] tabel) {

        HashMap<Integer, ArrayList<int[]>> possibilities = new HashMap<>();
        for (int i = LOSE; i <= WIN; i++) {
            possibilities.put(i, new ArrayList<>());
        }

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {
                if (tabel[row][col] == '_') {
                    tabel[row][col] = player;
                    possibilities.get(eval(player, tabel)).add(0, new int[]{row, col});
                    tabel[row][col] = '_';
                }
            }
        }

        ArrayList<int[]> legalMoves = getLegalMoves(tabel);

        ArrayList<Move> possibilityList = new ArrayList<>();
        int size = 0;
        for (int i = WIN; i >= LOSE; i--) {
            for (int[] ints : possibilities.get(i)) {
                size++;
                if (containsCoord(legalMoves, ints))
                    possibilityList.add(new Move(i, ints));
            }
        }
        return possibilityList;

    }

    private boolean containsCoord(List<int[]> list, int[] arr) {
        for (int[] item : list) {
            if (Arrays.equals(item, arr))
                return true;
        }
        return false;
    }

    private void printMoves(ArrayList<int[]> moves) {
        for (int[] move : moves) {
            System.out.println(move[0] + ", " + move[1]);
        }
        System.out.println("----");
    }

    private int[] minimax(char player, char[][] tabel) {
        int[] move = {-1, -1};
        int op_value = WIN;
        for (Move possibility : compilePossibilites(player, tabel)) {
            int[] coord = possibility.move;
            tabel[coord[0]][coord[1]] = player;
            ArrayList<Move> temp = compilePossibilites(player == 'x' ? 'o' : 'x', tabel);
            Move op_move;
            if (temp.size() > 0)
                op_move = temp.get(0);
            else {
                System.out.println("WWWWWWWWWW");
                op_move = new Move(WIN, coord);
            }

            tabel[coord[0]][coord[1]] = '_';

            if (op_value > op_move.value || move[0] == -1) {
                move = coord;
                op_value = op_move.value;
            }

        }
        System.out.println(op_value);


        return move;
    }
}


class Move {
    int[] move;
    int value;

    Move(int value, int[] move) {
        this.move = move;
        this.value = value;
    }
}