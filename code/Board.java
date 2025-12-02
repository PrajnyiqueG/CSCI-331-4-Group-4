package code;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Board {
    private static final int BOARD_SIZE = 4;
    private int[][] board;
    private Random r;
    private static final int SEARCH_DEPTH = 7; // change to 8 if you want deeper search (slower)

    public Board() {
        this.r = new Random();
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        clearBoard();
    }

    public void play() {
        generateRandomTile();
        generateRandomTile();
        display();
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (!input.equals("Q")) {
            input = scanner.next().toUpperCase();
            switch (input) {
                case "W":
                    moveUp();
                    break;
                case "A":
                    moveLeft();
                    break;
                case "S":
                    moveDown();
                    break;
                case "D":
                    moveRight();
                    break;
                case "Q":
                    System.exit(0);
                    break;
                case "AI1":
                    // Plain Minimax (no pruning)
                    String move1 = MiniMax();
                    System.out.println("MiniMax chose: " + move1);
                    break;
                case "AI2":
                    // Minimax with alpha-beta pruning
                    String move2 = ABprune();
                    System.out.println("ABprune chose: " + move2);
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
            }
            display();

            System.out.println();
            System.out.println();

            if (hasWon()) {
                System.out.println("Congrats! You won!");
                scanner.close();
                break;
            }
            if (isGameOver()) {
                System.out.println("Game over! No moves left.");
                scanner.close();
                break;
            }
        }
    }

    private boolean hasWon() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == 2048) {
                    return true;
                }
            }
        }
        return false;
    }

    private void display() {
        System.out.println("-----------------------------");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.printf("| %-4d ", board[i][j]);
            }
            System.out.println("|");
            System.out.println("-----------------------------");
        }
    }

    public void moveUp() {
        if (moveGridInPlace(board, "W")) generateRandomTile();
    }

    public void moveLeft() {
        if (moveGridInPlace(board, "A")) generateRandomTile();
    }

    public void moveRight() {
        if (moveGridInPlace(board, "D")) generateRandomTile();
    }

    public void moveDown() {
        if (moveGridInPlace(board, "S")) generateRandomTile();
    }

    // Note: generateRandomTile still used only for real moves (not for simulation)
    private void generateRandomTile() {
        ArrayList<int[]> empty = emptyCellsGrid(board);
        if (empty.isEmpty()) {
            return;
        }
        int[] index = empty.get(r.nextInt(empty.size()));
        board[index[0]][index[1]] = (r.nextInt(2) + 1) * 2;
    }

    private ArrayList<int[]> emptyCells() {
        return emptyCellsGrid(this.board);
    }

    private ArrayList<int[]> emptyCellsGrid(int[][] g) {
        ArrayList<int[]> empty = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (g[i][j] == 0) {
                    empty.add(new int[]{i, j});
                }
            }
        }
        return empty;
    }

    public int getValueAt(int row, int col) {
        return board[row][col];
    }

    public boolean isGameOver() {
        return isGameOverGrid(board);
    }

    public void startNewGame() {
        clearBoard();
        generateRandomTile();
        generateRandomTile();
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
            }
        }
    }

    /**
     * ============  Minimax WITHOUT pruning  ============
     * Returns direction chosen (W/A/S/D). ALSO applies move to the real board.
     */
    public String MiniMax() {
        String[] moves = new String[]{"W", "A", "S", "D"};
        double bestValue = Double.NEGATIVE_INFINITY;
        String bestMove = null;

        for (String m : moves) {
            int[][] clone = cloneGrid(board);
            boolean moved = moveGridInPlace(clone, m); // simulate player move (no random tile)
            if (!moved) continue;
            // Now it's adversary turn (minimizer) at depth-1
            double value = minimax(clone, SEARCH_DEPTH - 1, false);
            if (value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
        }

        // Apply chosen move to real board (this will generate a random tile via moveX methods)
        if (bestMove != null) {
            switch (bestMove) {
                case "W":
                    moveUp();
                    break;
                case "A":
                    moveLeft();
                    break;
                case "S":
                    moveDown();
                    break;
                case "D":
                    moveRight();
                    break;
            }
        }
        return bestMove;
    }

    /**
     * Minimax recursion WITHOUT alpha-beta pruning.
     * maximizingPlayer == true => player's turn (choose move)
     * maximizingPlayer == false => adversary's turn (places tile 2 or 4 to minimize)
     */
    private double minimax(int[][] g, int depth, boolean maximizingPlayer) {
        if (depth == 0 || isGameOverGrid(g)) {
            return evaluateGrid(g);
        }

        if (maximizingPlayer) {
            double best = Double.NEGATIVE_INFINITY;
            String[] moves = new String[]{"W", "A", "S", "D"};
            for (String m : moves) {
                int[][] child = cloneGrid(g);
                boolean moved = moveGridInPlace(child, m);
                if (!moved) continue;
                double val = minimax(child, depth - 1, false);
                if (val > best) best = val;
            }
            // If no valid moves, return evaluation
            if (best == Double.NEGATIVE_INFINITY) return evaluateGrid(g);
            return best;
        } else {
            // adversary: place a 2 or 4 in any empty cell to minimize player's evaluation
            ArrayList<int[]> empty = emptyCellsGrid(g);
            if (empty.isEmpty()) {
                return evaluateGrid(g);
            }
            double best = Double.POSITIVE_INFINITY;
            for (int[] cell : empty) {
                int r = cell[0], c = cell[1];

                // place 2
                int[][] child2 = cloneGrid(g);
                child2[r][c] = 2;
                double val2 = minimax(child2, depth - 1, true);
                if (val2 < best) best = val2;

                // place 4
                int[][] child4 = cloneGrid(g);
                child4[r][c] = 4;
                double val4 = minimax(child4, depth - 1, true);
                if (val4 < best) best = val4;
            }
            return best;
        }
    }

    /**
     * ============  Minimax WITH alpha-beta pruning  ============
     * Returns direction chosen (W/A/S/D). ALSO applies move to the real board.
     */
    public String ABprune() {
        String[] moves = new String[]{"W", "A", "S", "D"};
        double bestValue = Double.NEGATIVE_INFINITY;
        String bestMove = null;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        for (String m : moves) {
            int[][] clone = cloneGrid(board);
            boolean moved = moveGridInPlace(clone, m);
            if (!moved) continue;
            double value = minimaxAlphaBeta(clone, SEARCH_DEPTH - 1, false, alpha, beta);
            if (value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
            if (value > alpha) alpha = value;
        }

        // Apply chosen move to real board
        if (bestMove != null) {
            switch (bestMove) {
                case "W":
                    moveUp();
                    break;
                case "A":
                    moveLeft();
                    break;
                case "S":
                    moveDown();
                    break;
                case "D":
                    moveRight();
                    break;
            }
        }
        return bestMove;
    }

    private double minimaxAlphaBeta(int[][] g, int depth, boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0 || isGameOverGrid(g)) {
            return evaluateGrid(g);
        }

        if (maximizingPlayer) {
            double value = Double.NEGATIVE_INFINITY;
            String[] moves = new String[]{"W", "A", "S", "D"};
            for (String m : moves) {
                int[][] child = cloneGrid(g);
                boolean moved = moveGridInPlace(child, m);
                if (!moved) continue;
                double val = minimaxAlphaBeta(child, depth - 1, false, alpha, beta);
                if (val > value) value = val;
                if (value > alpha) alpha = value;
                if (alpha >= beta) break; // beta cutoff
            }
            if (value == Double.NEGATIVE_INFINITY) return evaluateGrid(g);
            return value;
        } else {
            // minimizing player: adversary places tile to minimize
            ArrayList<int[]> empty = emptyCellsGrid(g);
            if (empty.isEmpty()) {
                return evaluateGrid(g);
            }
            double value = Double.POSITIVE_INFINITY;
            for (int[] cell : empty) {
                int rr = cell[0], cc = cell[1];

                // place 2
                int[][] child2 = cloneGrid(g);
                child2[rr][cc] = 2;
                double val2 = minimaxAlphaBeta(child2, depth - 1, true, alpha, beta);
                if (val2 < value) value = val2;
                if (value < beta) beta = value;
                if (alpha >= beta) return value; // alpha cutoff

                // place 4
                int[][] child4 = cloneGrid(g);
                child4[rr][cc] = 4;
                double val4 = minimaxAlphaBeta(child4, depth - 1, true, alpha, beta);
                if (val4 < value) value = val4;
                if (value < beta) beta = value;
                if (alpha >= beta) return value; // alpha cutoff
            }
            return value;
        }
    }
    
    private int[][] cloneGrid(int[][] g) {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(g[i], 0, copy[i], 0, BOARD_SIZE);
        }
        return copy;
    }

    private boolean moveGridInPlace(int[][] g, String dir) {
        boolean changed = false;
        switch (dir) {
            case "W": // up
                for (int col = 0; col < BOARD_SIZE; col++) {
                    for (int row = 0; row < BOARD_SIZE - 1; row++) {
                        if (g[row][col] == 0) {
                            for (int i = row + 1; i < BOARD_SIZE; i++) {
                                if (g[i][col] != 0) {
                                    g[row][col] = g[i][col];
                                    g[i][col] = 0;
                                    changed = true;
                                    break;
                                }
                            }
                        } else {
                            for (int i = row + 1; i < BOARD_SIZE; i++) {
                                if (g[i][col] != 0) {
                                    if (g[i][col] == g[row][col]) {
                                        g[row][col] *= 2;
                                        g[i][col] = 0;
                                        changed = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case "A": // left
                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int col = 0; col < BOARD_SIZE; col++) {
                        if (g[row][col] == 0) {
                            for (int i = col + 1; i < BOARD_SIZE; i++) {
                                if (g[row][i] != 0) {
                                    g[row][col] = g[row][i];
                                    g[row][i] = 0;
                                    changed = true;
                                    break;
                                }
                            }
                        } else {
                            for (int i = col + 1; i < BOARD_SIZE; i++) {
                                if (g[row][i] != 0) {
                                    if (g[row][i] == g[row][col]) {
                                        g[row][col] *= 2;
                                        g[row][i] = 0;
                                        changed = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case "D": // right
                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int col = BOARD_SIZE - 1; col >= 0; col--) {
                        if (g[row][col] == 0) {
                            for (int i = col - 1; i >= 0; i--) {
                                if (g[row][i] != 0) {
                                    g[row][col] = g[row][i];
                                    g[row][i] = 0;
                                    changed = true;
                                    break;
                                }
                            }
                        } else {
                            for (int i = col - 1; i >= 0; i--) {
                                if (g[row][i] != 0) {
                                    if (g[row][i] == g[row][col]) {
                                        g[row][col] *= 2;
                                        g[row][i] = 0;
                                        changed = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case "S": // down
                for (int col = 0; col < BOARD_SIZE; col++) {
                    for (int row = BOARD_SIZE - 1; row >= 0; row--) {
                        if (g[row][col] == 0) {
                            for (int i = row - 1; i >= 0; i--) {
                                if (g[i][col] != 0) {
                                    g[row][col] = g[i][col];
                                    g[i][col] = 0;
                                    changed = true;
                                    break;
                                }
                            }
                        } else {
                            for (int i = row - 1; i >= 0; i--) {
                                if (g[i][col] != 0) {
                                    if (g[i][col] == g[row][col]) {
                                        g[row][col] *= 2;
                                        g[i][col] = 0;
                                        changed = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            default:
                return false;
        }

        return changed;
    }

    private double evaluateGrid(int[][] g) {
        double sum = 0.0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                sum += g[i][j];
            }
        }
        return sum;
    }

    private boolean isGameOverGrid(int[][] g) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (g[i][j] == 0) return false;
            }
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE - 1; j++) {
                if (g[i][j] == g[i][j + 1]) return false;
            }
        }
        for (int j = 0; j < BOARD_SIZE; j++) {
            for (int i = 0; i < BOARD_SIZE - 1; i++) {
                if (g[i][j] == g[i + 1][j]) return false;
            }
        }
        return true;
    }
}
