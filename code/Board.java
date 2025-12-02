package code;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Board {
    private static final int BOARD_SIZE = 4;
    private int[][] board;
    private Random r;

    public Board() {
        this.r = new Random();
        this.board = new int[4][4];
        clearBoard();
    }

    public void play() {
        generateRandomTile();
        generateRandomTile();
        display();
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        String input = "";
        while(!input.equals("Q")){
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
                case "AI1":
                    System.out.println("AI not implemented yet.");
                case "AI2":
                    System.out.println("AI not implemented yet.");
                default:
                    System.out.println("Invalid input. Please try again.");
            }
            display();
            
            System.out.println();
            System.out.println();
            
            if(hasWon()){
                System.out.println("Congrats! You won!");
                scanner.close();
                break;
            }
        }
    }

    private boolean hasWon() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if(board[row][col]==2048){
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
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE - 1; row++) {
                if (board[row][col] == 0) {
                    for (int i = row + 1; i < BOARD_SIZE; i++) {
                        if (board[i][col] != 0) {
                            board[row][col] = board[i][col];
                            board[i][col] = 0;
                            break;
                        }
                    }
                } else {
                    for (int i = row + 1; i < BOARD_SIZE; i++) {
                        if (board[i][col] != 0) {
                            if (board[i][col] == board[row][col]) {
                                board[row][col] *= 2;
                                board[i][col] = 0;
                            }
                            break;
                        }
                    }
                }
            }
        }
        generateRandomTile();
    }

    public void moveLeft(){
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if(board[row][col]==0){
                    for (int i = col+1; i < BOARD_SIZE; i++) {
                        if(board[row][i]!=0){
                            board[row][col]=board[row][i];
                            board[row][i]=0;
                            break;
                        }
                    }
                } else {
                    for (int i = col+1; i < BOARD_SIZE; i++) {
                        if(board[row][i]!=0){
                            if(board[row][i] == board[row][col]) {
                                board[row][col] *= 2;
                                board[row][i] = 0;
                            }
                            break;
                        }
                    }
                }
            }
        }
        generateRandomTile();
    }

    public void moveRight(){
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = BOARD_SIZE -1; col >= 0; col--) {
                if(board[row][col]==0){
                    for (int i = col-1; i >= 0 ; i--) {
                        if(board[row][i] != 0){
                            board[row][col] = board[row][i];
                            board[row][i] = 0;
                            break;
                        }
                    }
                } else {
                    for (int i = col-1; i >= 0 ; i--) {
                        if(board[row][i] != 0){
                            if(board[row][i]==board[row][col]){
                                board[row][col]*=2;
                                board[row][i]=0;
                            }
                            break;
                        }
                    }
                }
            }
        }
        generateRandomTile();
    }

    public void moveDown(){
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = BOARD_SIZE -1; row >= 0; row--) {
                if(board[row][col]==0){
                    for (int i = row-1; i >= 0; i--) {
                        if(board[i][col]!=0){
                            board[row][col] = board[i][col];
                            board[i][col]=0;
                            break;
                        }
                    }
                } else {
                    for (int i = row-1; i >= 0; i--) {
                        if(board[i][col]!=0){
                            if(board[i][col]==board[row][col]){
                                board[row][col] *=2;
                                board[i][col]=0;
                            }
                            break;
                        }
                    }
                }
            }
        }
        generateRandomTile();
    }

    private void generateRandomTile(){
        ArrayList<Integer[]> empty = emptyCells();
        if (empty.isEmpty()) {
            return;
        }
        Integer[] index = empty.get(r.nextInt(empty.size()));
        board[index[0]][index[1]] = (r.nextInt(2) + 1) * 2;
    }

    private ArrayList<Integer[]> emptyCells(){
        ArrayList<Integer[]> empty= new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(board[i][j]==0){
                    empty.add(new Integer[]{i,j});
                }
            }
        }
        return empty;
    }

    public int getValueAt(int row, int col) {
        return board[row][col];
    }

    public boolean isGameOver() {
        return emptyCells().isEmpty();
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
}
