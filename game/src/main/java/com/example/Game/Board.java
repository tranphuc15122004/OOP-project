package com.example.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Board {
    public final static int MAX_ROW = 8;
    public final static int MAX_COL = 8;
    public static int SQUARE_SIZE = 100;
    public static int HALF_SQUARE_SIZE = 50;

    public static void draw(GraphicsContext gc){
        for(int row = 0; row < MAX_ROW; row++){
            for(int col = 0; col < MAX_COL; col++){
                if((row + col) % 2 == 0){
                    gc.setFill(Color.web("#8B4513"));
                } else {
                    gc.setFill(Color.web("#CD853F"));
                }
                gc.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}
