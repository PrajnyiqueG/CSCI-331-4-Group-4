package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameFrame extends JFrame {

    private Board board;
    private BoardPanel boardPanel;

    public GameFrame() {
        this.board = new Board();
        this.boardPanel = new BoardPanel(board);

        setTitle("2048 AI Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(boardPanel, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        board.startNewGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            board.startNewGame();
            boardPanel.repaint();
            requestFocusInWindow();
        });

        JButton minimaxButton = new JButton("AI Move (Minimax)");
        minimaxButton.addActionListener(e -> {
            board.MiniMax();
            boardPanel.repaint();
            checkGameOver();
            requestFocusInWindow();
        });

        JButton alphaBetaButton = new JButton("AI Move (Alpha-Beta)");
        alphaBetaButton.addActionListener(e -> {
            board.ABprune();
            boardPanel.repaint();
            checkGameOver();
            requestFocusInWindow();
        });

        panel.add(newGameButton);
        panel.add(minimaxButton);
        panel.add(alphaBetaButton);
        return panel;
    }

    private void handleKeyPress(int keyCode) {
        boolean moved = false;
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                board.moveUp();
                moved = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                board.moveLeft();
                moved = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                board.moveDown();
                moved = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                board.moveRight();
                moved = true;
                break;
            default:
                break;
        }

        if (!moved) {
            return;
        }

        boardPanel.repaint();

        checkGameOver();
    }

    private void checkGameOver() {
        if (board.isGameOver()) {
            JOptionPane.showMessageDialog(this, "Game Over");
        }
    }
}
