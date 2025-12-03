package code;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private Board board;
    private static final int GRID_SIZE = 4;

    public BoardPanel(Board board) {
        this.board = board;
        setBackground(new Color(0xBBADA0));
    }

    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (board == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int gap = Math.max(5, panelWidth / 80);
        int tileSize = (Math.min(panelWidth, panelHeight) - (GRID_SIZE + 1) * gap) / GRID_SIZE;

        int boardWidth = GRID_SIZE * tileSize + (GRID_SIZE + 1) * gap;
        int boardHeight = GRID_SIZE * tileSize + (GRID_SIZE + 1) * gap;

        int xOffset = (panelWidth - boardWidth) / 2;
        int yOffset = (panelHeight - boardHeight) / 2;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = xOffset + gap + col * (tileSize + gap);
                int y = yOffset + gap + row * (tileSize + gap);
                int value = board.getValueAt(row, col);

                g2.setColor(getTileColor(value));
                g2.fillRoundRect(x, y, tileSize, tileSize, 20, 20);

                if (value != 0) {
                    g2.setColor(value <= 4 ? new Color(0x776E65) : Color.WHITE);
                    int fontSize = Math.max(tileSize / 3, 18);
                    g2.setFont(new Font("Arial", Font.BOLD, fontSize));

                    String text = String.valueOf(value);
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getAscent();

                    int textX = x + (tileSize - textWidth) / 2;
                    int textY = y + (tileSize + textHeight) / 2 - 2;
                    g2.drawString(text, textX, textY);
                }
            }
        }
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 0: return new Color(0xCDC1B4);
            case 2: return new Color(0xEEE4DA);
            case 4: return new Color(0xEDE0C8);
            case 8: return new Color(0xF2B179);
            case 16: return new Color(0xF59563);
            case 32: return new Color(0xF67C5F);
            case 64: return new Color(0xF65E3B);
            case 128: return new Color(0xEDCF72);
            case 256: return new Color(0xEDCC61);
            case 512: return new Color(0xEDC850);
            case 1024: return new Color(0xEDC53F);
            case 2048: return new Color(0xEDC22E);
            default: return new Color(0x3C3A32);
        }
    }
}
