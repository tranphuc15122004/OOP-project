package Main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {
    public final static int MAX_ROW = 8;
    public final static int MAX_COL = 8;
    public static int SQUARE_SIZE = 100;
    public static int HALF_SQUARE_SIZE = 50;

    public void draw(Graphics2D g2d){
        for(int row = 0; row < MAX_ROW; row++){
            for(int col = 0; col < MAX_COL; col++){
                if((row + col) % 2 == 0){
                    g2d.setColor(new Color( 0x8B4513));
                } else {
                    g2d.setColor(new Color(0xCD853F));
                }
                g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}
