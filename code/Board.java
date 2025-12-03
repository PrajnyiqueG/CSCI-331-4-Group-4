package code;

import java.util.ArrayList;
import java.util.Random;

/**
 * Board class represents the 2048 game board and contains
 * the game logic, including movement, tile generation, and
 * AI decision-making using Minimax and Alpha-Beta pruning.
 */
public class Board {

    /** Constant size of the 4x4 board */
    private static final int BOARD_SIZE = 4;

    /** 2D array representing the board state */
    private int[][] board;

    /** Random number generator for generating new tiles */
    private Random r;

    /** Depth of search for Minimax and Alpha-Beta algorithms */
    private static final int SEARCH_DEPTH = 7;

    /** Stores execution time for 10 Minimax moves (in nanoseconds) */
    private long minimaxTime = 0;

    /** Stores execution time for 10 Alpha-Beta moves (in nanoseconds) */
    private long alphaBetaTime = 0;

    /**
     * Constructor: Initializes the board and random generator,
     * and clears the board.
     */
    public Board() {
        this.r = new Random();
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        clearBoard();
    }

    /**
     * Moves the board up and generates a new tile if any tile moved.
     */
    public void moveUp() {
        if (moveGridInPlace(board, "W")) generateRandomTile();
    }

    /**
     * Moves the board left and generates a new tile if any tile moved.
     */
    public void moveLeft() {
        if (moveGridInPlace(board, "A")) generateRandomTile();
    }

    /**
     * Moves the board right and generates a new tile if any tile moved.
     */
    public void moveRight() {
        if (moveGridInPlace(board, "D")) generateRandomTile();
    }

    /**
     * Moves the board down and generates a new tile if any tile moved.
     */
    public void moveDown() {
        if (moveGridInPlace(board, "S")) generateRandomTile();
    }

    /**
     * Generates a new tile (2 or 4) at a random empty cell.
     */
    private void generateRandomTile() {
        ArrayList<int[]> empty = emptyCellsGrid(board);
        if (empty.isEmpty()) return;
        int[] index = empty.get(r.nextInt(empty.size()));
        board[index[0]][index[1]] = (r.nextInt(2) + 1) * 2;
    }

    /**
     * Returns a list of empty cells on the given grid.
     * @param g Grid to search
     * @return ArrayList of int[] containing {row, col} of empty cells
     */
    private ArrayList<int[]> emptyCellsGrid(int[][] g) {
        ArrayList<int[]> empty = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (g[i][j] == 0) empty.add(new int[]{i, j});
            }
        }
        return empty;
    }

    /**
     * Returns the value of the cell at a specific position.
     */
    public int getValueAt(int row, int col) {
        return board[row][col];
    }

    /**
     * Checks if the game is over (no moves left).
     */
    public boolean isGameOver() {
        return isGameOverGrid(board);
    }

    /**
     * Starts a new game by clearing the board and generating two tiles.
     */
    public void startNewGame() {
        clearBoard();
        generateRandomTile();
        generateRandomTile();
    }

    /**
     * Clears all cells on the board (sets to 0).
     */
    private void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
            }
        }
    }

    // ===================== AI: Minimax without pruning =====================

    /**
     * Chooses the best move using Minimax (without pruning) and applies it to the board.
     * @return Direction chosen ("W", "A", "S", "D")
     */
    public String MiniMax() {
        String[] moves = new String[]{"W", "A", "S", "D"};
        double bestValue = Double.NEGATIVE_INFINITY;
        String bestMove = null;

        for (String m : moves) {
            int[][] clone = cloneGrid(board);
            boolean moved = moveGridInPlace(clone, m);
            if (!moved) continue;
            double value = minimax(clone, SEARCH_DEPTH - 1, false);
            if (value > bestValue) {
                bestValue = value;
                bestMove = m;
            }
        }

        if (bestMove != null) {
            switch (bestMove) {
                case "W": moveUp(); break;
                case "A": moveLeft(); break;
                case "S": moveDown(); break;
                case "D": moveRight(); break;
            }
        }
        return bestMove;
    }

    /**
     * Recursive Minimax function without alpha-beta pruning.
     * @param g Current grid
     * @param depth Remaining search depth
     * @param maximizingPlayer True if it's the player's turn
     * @return Heuristic value of the grid
     */
    private double minimax(int[][] g, int depth, boolean maximizingPlayer) {
        if (depth == 0 || isGameOverGrid(g)) return evaluateGrid(g);

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
            return best == Double.NEGATIVE_INFINITY ? evaluateGrid(g) : best;
        } else {
            ArrayList<int[]> empty = emptyCellsGrid(g);
            if (empty.isEmpty()) return evaluateGrid(g);

            double best = Double.POSITIVE_INFINITY;
            for (int[] cell : empty) {
                int r = cell[0], c = cell[1];
                int[][] child2 = cloneGrid(g); child2[r][c] = 2;
                best = Math.min(best, minimax(child2, depth - 1, true));
                int[][] child4 = cloneGrid(g); child4[r][c] = 4;
                best = Math.min(best, minimax(child4, depth - 1, true));
            }
            return best;
        }
    }

    // ===================== AI: Minimax with alpha-beta pruning =====================

    /**
     * Chooses the best move using Minimax with alpha-beta pruning and applies it.
     * @return Direction chosen ("W", "A", "S", "D")
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
            alpha = Math.max(alpha, value);
        }

        if (bestMove != null) {
            switch (bestMove) {
                case "W": moveUp(); break;
                case "A": moveLeft(); break;
                case "S": moveDown(); break;
                case "D": moveRight(); break;
            }
        }
        return bestMove;
    }

    /**
     * Recursive Minimax function with alpha-beta pruning.
     * @param g Current grid
     * @param depth Remaining search depth
     * @param maximizingPlayer True if player's turn
     * @param alpha Alpha value for pruning
     * @param beta Beta value for pruning
     * @return Heuristic value of the grid
     */
    private double minimaxAlphaBeta(int[][] g, int depth, boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0 || isGameOverGrid(g)) return evaluateGrid(g);

        if (maximizingPlayer) {
            double value = Double.NEGATIVE_INFINITY;
            String[] moves = new String[]{"W", "A", "S", "D"};
            for (String m : moves) {
                int[][] child = cloneGrid(g);
                boolean moved = moveGridInPlace(child, m);
                if (!moved) continue;
                value = Math.max(value, minimaxAlphaBeta(child, depth - 1, false, alpha, beta));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // beta cutoff
            }
            return value == Double.NEGATIVE_INFINITY ? evaluateGrid(g) : value;
        } else {
            ArrayList<int[]> empty = emptyCellsGrid(g);
            if (empty.isEmpty()) return evaluateGrid(g);

            double value = Double.POSITIVE_INFINITY;
            for (int[] cell : empty) {
                int rr = cell[0], cc = cell[1];
                int[][] child2 = cloneGrid(g); child2[rr][cc] = 2;
                value = Math.min(value, minimaxAlphaBeta(child2, depth - 1, true, alpha, beta));
                beta = Math.min(beta, value);
                if (alpha >= beta) return value;
                int[][] child4 = cloneGrid(g); child4[rr][cc] = 4;
                value = Math.min(value, minimaxAlphaBeta(child4, depth - 1, true, alpha, beta));
                beta = Math.min(beta, value);
                if (alpha >= beta) return value;
            }
            return value;
        }
    }

    /**
     * Returns a deep copy of a grid.
     */
    private int[][] cloneGrid(int[][] g) {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(g[i], 0, copy[i], 0, BOARD_SIZE);
        }
        return copy;
    }

    /**
     * Moves tiles in-place in the given direction.
     * @param g Grid to move
     * @param dir Direction ("W","A","S","D")
     * @return True if any tile moved or merged
     */
    private boolean moveGridInPlace(int[][] g, String dir) {
        boolean changed = false;
        // movement logic for each direction
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
            default: return false;
        }
        return changed;
    }

    /**
     * Heuristic evaluation of the grid. Currently sum of all tiles.
     */
    public double evaluateGrid(int[][] g) {
        double sum = 0.0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                sum += g[i][j];
            }
        }
        return sum;
    }

    /**
     * Checks if a given grid has no moves left.
     */
    private boolean isGameOverGrid(int[][] g) {
        // check for empty cells
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                if (g[i][j] == 0) return false;

        // check for horizontal merges
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE - 1; j++)
                if (g[i][j] == g[i][j + 1]) return false;

        // check for vertical merges
        for (int j = 0; j < BOARD_SIZE; j++)
            for (int i = 0; i < BOARD_SIZE - 1; i++)
                if (g[i][j] == g[i + 1][j]) return false;

        return true;
    }

    /**
     * Returns the highest tile currently on the board.
     */
    public int getHighestTile() {
        int max = 0;
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                if (board[i][j] > max) max = board[i][j];
        return max;
    }

    /**
     * Performs 10 moves using Minimax and measures total time.
     */
    public void m10MiniMax() {
        long start = System.nanoTime();
        for (int i = 0; i < 10; i++) MiniMax();
        long end = System.nanoTime();
        minimaxTime = (end - start);
    }

    /**
     * Performs 10 moves using Alpha-Beta and measures total time.
     */
    public void m10ABprune() {
        long start = System.nanoTime();
        for (int i = 0; i < 10; i++) ABprune();
        long end = System.nanoTime();
        alphaBetaTime = (end - start);
    }

    // Getter and setter methods for timing
    public long getMinimaxTime() { return minimaxTime; }
    public long getAlphaBetaTime() { return alphaBetaTime; }
    public void setMinimaxTime(long time) { this.minimaxTime = time; }
    public void setAlphaBetaTime(long time) { this.alphaBetaTime = time; }
}
