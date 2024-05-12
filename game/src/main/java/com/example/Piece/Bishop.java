package com.example.Piece;

import com.example.Game.GamePvP;

public class Bishop extends Piece{

    public Bishop(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePvP.WHITE){
            image = getImage("/Piece_image/w_bishop");
        } else {
            image = getImage("/Piece_image/b_bishop");
        }
    }
    
    public boolean canMove(int targetRow , int targetCol){
        if(isWithinBoard(targetRow, targetCol) && isSamePosition(targetRow, targetCol) == false){
            if(Math.abs(targetRow - preRow) == Math.abs(targetCol - preCol)){
                if(isValidSquare(targetRow, targetCol) && limitMovement_diagonal(targetRow, targetCol)){
                    return true;
                }
            }
        }
        return false;
    }
}
