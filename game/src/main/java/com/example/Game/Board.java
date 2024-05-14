package com.example.Game;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class Board {
    static ArrayList<Pair<String, String>> color_list= new ArrayList<Pair<String, String>>();
    public final static int MAX_ROW = 8;
    public final static int MAX_COL = 8;
    public static int SQUARE_SIZE = 100;
    public static int HALF_SQUARE_SIZE = 50;
    private static int index = 0;

    public Board(){
        color_list.add(new Pair<String, String>("#EBECD0", "#739552"));
        color_list.add(new Pair<String, String>("#8B4513", "#CD853F"));
    }

    public void setColor_list(String color1, String color2){
        color_list.add(new Pair<String, String>(color1, color2));
    }

    public void set_BoardColor(int i){
        index = i;
    }

    public static void draw(GraphicsContext gc){
        for(int row = 0; row < MAX_ROW; row++){
            for(int col = 0; col < MAX_COL; col++){
                if((row + col) % 2 == 0){
                    gc.setFill(Color.web(color_list.get(index).getKey()));
                } else {
                    gc.setFill(Color.web(color_list.get(index).getValue()));
                }
                gc.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}
