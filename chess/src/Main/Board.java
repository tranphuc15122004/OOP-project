package Main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = 50;

    public void draw(Graphics2D g2d){
        for (int i = 0; i < MAX_COL; i++) {
            for (int j = 0; j < MAX_ROW; j++) {
                if ((i + j) % 2 == 0) {
                    g2d.setColor(new Color(255, 206, 158));
                } else {
                    g2d.setColor(new Color(209, 139, 71));
                }
                g2d.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}
