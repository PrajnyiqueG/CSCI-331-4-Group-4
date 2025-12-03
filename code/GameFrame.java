package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * GameFrame is the main GUI window for the 2048 AI project.
 * It displays the game board, score, highest tile, AI timings, and provides controls for gameplay.
 */
public class GameFrame extends JFrame {

    /** The game logic board */
    private Board board;

    /** Panel to display the board visually */
    private BoardPanel boardPanel;

    /** Labels for displaying score, highest tile, AI timings */
    private JLabel scoreLabel;
    private JLabel highestTileLabel;
    private JLabel aiTimingLabel;
    private JLabel totalMinimaxTimeLabel;
    private JLabel totalAlphaBetaTimeLabel;

    /** Total time spent performing Minimax / Alpha-Beta moves */
    private long totalMinimaxTime = 0;
    private long totalAlphaBetaTime = 0;

    /** Minimum window dimensions */
    private final int MIN_WIDTH = 1000;
    private final int MIN_HEIGHT = 650;

    /**
     * Constructor: Initializes the game frame, sets up UI components and keyboard controls.
     */
    public GameFrame() {
        board = new Board();
        board.startNewGame();

        boardPanel = new BoardPanel(board);
        boardPanel.setPreferredSize(new Dimension(400, 400));

        // Initialize labels
        scoreLabel = createLabel("Score: 0", 24, true);
        highestTileLabel = createLabel("Highest Tile: 0", 20, true);
        aiTimingLabel = createLabel("Minimax: 0 ms | Alpha-Beta: 0 ms", 16, false);
        totalMinimaxTimeLabel = createLabel("Total Minimax Time: 0 ms", 16, false);
        totalAlphaBetaTimeLabel = createLabel("Total Alpha-Beta Time: 0 ms", 16, false);

        // Control panel with buttons
        JPanel controlPanel = createControlPanel();

        // Top panel to display labels
        JPanel topPanel = new JPanel(new GridLayout(5, 1));
        topPanel.add(scoreLabel);
        topPanel.add(highestTileLabel);
        topPanel.add(aiTimingLabel);
        topPanel.add(totalMinimaxTimeLabel);
        topPanel.add(totalAlphaBetaTimeLabel);

        // Frame layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setTitle("2048 AI Project");
        setSize(700, 750);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Keyboard controls
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        updateUI();
    }

    /**
     * Creates a JLabel with specified text, font size, and bold style.
     * @param text Text to display
     * @param fontSize Font size
     * @param bold Whether font is bold
     * @return Configured JLabel
     */
    private JLabel createLabel(String text, int fontSize, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(new Color(0x776E65));
        return label;
    }

    /**
     * Creates the control panel with buttons for new game and AI moves.
     * @return JPanel containing buttons
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            board.startNewGame();
            totalMinimaxTime = 0;
            totalAlphaBetaTime = 0;
            updateUI();
            requestFocusInWindow();
        });

        JButton minimaxButton = new JButton("AI Move (Minimax)");
        minimaxButton.addActionListener(e -> {
            long start = System.nanoTime();
            board.MiniMax();
            long end = System.nanoTime();
            totalMinimaxTime += (end - start);
            updateUI(end - start, -1);
            requestFocusInWindow();
        });

        JButton alphaBetaButton = new JButton("AI Move (Alpha-Beta)");
        alphaBetaButton.addActionListener(e -> {
            long start = System.nanoTime();
            board.ABprune();
            long end = System.nanoTime();
            totalAlphaBetaTime += (end - start);
            updateUI(-1, end - start);
            requestFocusInWindow();
        });

        JButton m10minimaxButton = new JButton("10 Moves (MiniMax)");
        m10minimaxButton.addActionListener(e -> {
            long start = System.nanoTime();
            board.m10MiniMax();
            long end = System.nanoTime();
            totalMinimaxTime += (end - start);
            updateUI(end - start, -1);
            requestFocusInWindow();
        });

        JButton m10alphaBetaButton = new JButton("10 Moves (AlphaBeta)");
        m10alphaBetaButton.addActionListener(e -> {
            long start = System.nanoTime();
            board.m10ABprune();
            long end = System.nanoTime();
            totalAlphaBetaTime += (end - start);
            updateUI(-1, end - start);
            requestFocusInWindow();
        });

        panel.add(newGameButton);
        panel.add(minimaxButton);
        panel.add(alphaBetaButton);
        panel.add(m10minimaxButton);
        panel.add(m10alphaBetaButton);

        return panel;
    }

    /**
     * Handles arrow/WASD key presses to move tiles.
     * @param keyCode Key pressed
     */
    private void handleKeyPress(int keyCode) {
        boolean moved = false;
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:    board.moveUp(); moved = true; break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:  board.moveLeft(); moved = true; break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:  board.moveDown(); moved = true; break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT: board.moveRight(); moved = true; break;
        }
        if (moved) updateUI();
    }

    /**
     * Updates UI labels and repaints the board.
     * No specific AI move time.
     */
    private void updateUI() {
        updateUI(-1, -1);
    }

    /**
     * Updates UI labels, including last AI move durations.
     * @param lastMinimax Duration of last Minimax move (-1 if none)
     * @param lastAlphaBeta Duration of last Alpha-Beta move (-1 if none)
     */
    private void updateUI(long lastMinimax, long lastAlphaBeta) {
        int score = 0;
        int highest = 0;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int val = board.getValueAt(r, c);
                score += val;
                if (val > highest) highest = val;
            }
        }
        scoreLabel.setText("Score: " + score);
        highestTileLabel.setText("Highest Tile: " + highest);

        if (lastMinimax >= 0) {
            aiTimingLabel.setText("Minimax: " + lastMinimax / 1_000_000 + " ms | Alpha-Beta: 0 ms");
        } else if (lastAlphaBeta >= 0) {
            aiTimingLabel.setText("Minimax: 0 ms | Alpha-Beta: " + lastAlphaBeta / 1_000_000 + " ms");
        } else {
            aiTimingLabel.setText("Minimax: 0 ms | Alpha-Beta: 0 ms");
        }

        totalMinimaxTimeLabel.setText("Total Minimax Time: " + totalMinimaxTime / 1_000_000 + " ms");
        totalAlphaBetaTimeLabel.setText("Total Alpha-Beta Time: " + totalAlphaBetaTime / 1_000_000 + " ms");

        boardPanel.repaint();
        checkGameOver();
    }

    /**
     * Checks if the game is over and displays a message dialog if so.
     */
    private void checkGameOver() {
        if (board.isGameOver()) {
            JOptionPane.showMessageDialog(this,
                    "Game Over!\nFinal Score: " + calculateScore() +
                    "\nHighest Tile: " + getHighestTile());
        }
    }

    /** Calculates the current score by summing all tile values */
    private int calculateScore() {
        int score = 0;
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++)
                score += board.getValueAt(r, c);
        return score;
    }

    /** Returns the highest tile currently on the board */
    private int getHighestTile() {
        int highest = 0;
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++)
                if (board.getValueAt(r, c) > highest)
                    highest = board.getValueAt(r, c);
        return highest;
    }

    /**
     * Main method: Launches the game GUI.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
