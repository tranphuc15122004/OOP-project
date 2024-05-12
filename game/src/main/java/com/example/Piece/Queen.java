package com.example.Piece;

import com.example.Game.GamePvP;

public class Queen extends Piece{

    public Queen(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePvP.WHITE){
            image = getImage("/Piece_image/w_queen");
        } else {
            image = getImage("/Piece_image/b_queen");
        }
    }
    
    public boolean canMove(int targetRow , int targetCol){
        if(isWithinBoard(targetRow, targetCol) && isSamePosition(targetRow, targetCol) == false){
            // horizontal or vertical movement
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetRow, targetCol) && limitMovement_straight(targetRow, targetCol)){
                    return true;
                }
            }
            // diagonal movement
            if(Math.abs(targetRow - preRow) == Math.abs(targetCol - preCol)){
                if(isValidSquare(targetRow, targetCol) && limitMovement_diagonal(targetRow, targetCol)){
                    return true;
                }
            }
        }
        return false;
    }
}