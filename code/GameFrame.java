package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameFrame extends JFrame {

    private Board board;
    private BoardPanel boardPanel;
    private JLabel scoreLabel;

    public GameFrame() {
        board = new Board();
        board.startNewGame();

        boardPanel = new BoardPanel(board);

        // Score label
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(0x776E65));

        // Control panel with buttons
        JPanel controlPanel = createControlPanel();

        // Layout setup
        setLayout(new BorderLayout());
        add(scoreLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setTitle("2048 AI Project");
        setSize(600, 700);
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
            updateUI();
            requestFocusInWindow();
        });

        JButton minimaxButton = new JButton("AI Move (Minimax)");
        minimaxButton.addActionListener(e -> {
            board.MiniMax();
            updateUI();
            requestFocusInWindow();
        });

        JButton alphaBetaButton = new JButton("AI Move (Alpha-Beta)");
        alphaBetaButton.addActionListener(e -> {
            board.ABprune();
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

        // Repaint board
        boardPanel.repaint();

        // Check game over
        if (board.isGameOver()) {
            JOptionPane.showMessageDialog(this, "Game Over! Final Score: " + score);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
