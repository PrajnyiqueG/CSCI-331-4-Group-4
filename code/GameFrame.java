package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameFrame extends JFrame {

    private Board board;
    private BoardPanel boardPanel;
    private JLabel scoreLabel;
    private JLabel aiTimingLabel;

    private final int MIN_WIDTH = 700;
    private final int MIN_HEIGHT = 650;

    public GameFrame() {
        board = new Board();
        board.startNewGame();

        boardPanel = new BoardPanel(board);
        boardPanel.setPreferredSize(new Dimension(400, 400));

        // Score label
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(0x776E65));

        // AI timing label
        aiTimingLabel = new JLabel("Minimax: 0 ms | Alpha-Beta: 0 ms", SwingConstants.CENTER);
        aiTimingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        aiTimingLabel.setForeground(new Color(0x776E65));

        // Control panel
        JPanel controlPanel = createControlPanel();

        // Top panel (score + AI timing)
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(scoreLabel);
        topPanel.add(aiTimingLabel);

        // Layout setup
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setTitle("2048 AI Project");
        setSize(700, 750);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Keyboard input
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

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            board.startNewGame();
            board.setMinimaxTime(0);    // reset timings
            board.setAlphaBetaTime(0);
            updateUI();
            requestFocusInWindow();
        });

        JButton minimaxButton = new JButton("AI Move (Minimax)");
        minimaxButton.addActionListener(e -> {
            long start = System.nanoTime();
            board.MiniMax();
            long end = System.nanoTime();
            board.setMinimaxTime(end - start); // update minimax time
            updateUI();
            requestFocusInWindow();
        });

        JButton alphaBetaButton = new JButton("AI Move (Alpha-Beta)");
        alphaBetaButton.addActionListener(e -> {
            long start = System.nanoTime();
            board.ABprune();
            long end = System.nanoTime();
            board.setAlphaBetaTime(end - start); // update alpha-beta time
            updateUI();
            requestFocusInWindow();
        });

        JButton m10minimaxButton = new JButton("m10MiniMax");
        m10minimaxButton.addActionListener(e -> {
            board.m10MiniMax();
            updateUI();
            requestFocusInWindow();
        });

        JButton m10alphaBetaButton = new JButton("m10AlphaBeta");
        m10alphaBetaButton.addActionListener(e -> {
            board.m10ABprune();
            updateUI();
            requestFocusInWindow();
        });

        panel.add(newGameButton);
        panel.add(minimaxButton);
        panel.add(alphaBetaButton);
        panel.add(m10minimaxButton);
        panel.add(m10alphaBetaButton);

        return panel;
    }

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

        if (moved) {
            updateUI();
        }
    }

    private void updateUI() {
        // Update score
        int score = 0;
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++)
                score += board.getValueAt(r, c);
        scoreLabel.setText("Score: " + score);

        // Update AI timing label
        long minimaxMs = board.getMinimaxTime() / 1_000_000;
        long alphaBetaMs = board.getAlphaBetaTime() / 1_000_000;
        aiTimingLabel.setText("Minimax: " + minimaxMs + " ms | Alpha-Beta: " + alphaBetaMs + " ms");

        boardPanel.repaint();

        checkGameOver();
    }

    private void checkGameOver() {
        if (board.isGameOver()) {
            JOptionPane.showMessageDialog(this, "Game Over! Final Score: " +
                    calculateScore());
        }
    }

    private int calculateScore() {
        int score = 0;
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++)
                score += board.getValueAt(r, c);
        return score;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
