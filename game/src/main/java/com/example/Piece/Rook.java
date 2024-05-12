package com.example.Piece;

import com.example.Game.GamePvP;

public class Rook extends Piece {

    public Rook(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePvP.WHITE){
            image = getImage("/Piece_image/w_rook");
        } else {
            image = getImage("/Piece_image/b_rook");
        }
    }
    
    public boolean canMove(int targetRow , int targetCol){
        if(isWithinBoard(targetRow, targetCol) && isSamePosition(targetRow, targetCol) == false){
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetRow, targetCol) && limitMovement_straight(targetRow, targetCol)){
                    return true;
                }
            }
        }
        return false;
    }
}
