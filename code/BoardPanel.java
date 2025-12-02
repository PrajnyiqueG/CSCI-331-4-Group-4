package code;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private static final int TILE_SIZE = 80;
    private static final int GAP = 10;

    private Board board;

    public BoardPanel(Board board) {
        this.board = board;
        int size = 4 * TILE_SIZE + 5 * GAP;
        setPreferredSize(new Dimension(size, size));
        setBackground(new Color(0xBBADA0));
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (board == null) {
            return;
        }

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int x = GAP + col * (TILE_SIZE + GAP);
                int y = GAP + row * (TILE_SIZE + GAP);

                int value = board.getValueAt(row, col);

                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                if (value != 0) {
                    g.setColor(Color.BLACK);
                    String text = String.valueOf(value);
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getAscent();
                    int textX = x + (TILE_SIZE - textWidth) / 2;
                    int textY = y + (TILE_SIZE + textHeight) / 2 - 2;
                    g.drawString(text, textX, textY);
                }
            }
        }
    }
}
