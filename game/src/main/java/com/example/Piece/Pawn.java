package com.example.Piece;

import com.example.Game.GamePvP;

public class Pawn extends Piece{

    public Pawn(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePvP.WHITE){
            image = getImage("/Piece_image/w_pawn");
        } else {
            image = getImage("/Piece_image/b_pawn");
        }
    }
    
    public boolean canMove(int targetRow , int targetCol){
        if(isWithinBoard(targetRow, targetCol) && isSamePosition(targetRow, targetCol) == false){
            int moveDiff;
            if(color == GamePvP.WHITE){
                moveDiff = -1;
            } else {
                moveDiff = 1;
            }
            // 1 square move
            affectedP = getaffectedP(targetRow, targetCol);
            if(targetCol == preCol && targetRow == preRow + moveDiff && affectedP == null){
                return true;
            }
            // 2 square move
            if(targetCol == preCol && targetRow == preRow + moveDiff * 2 && affectedP == null && isMoved == false && limitMovement_straight(targetRow, targetCol)){
                _2squareMove = true;
                return true;
            }
            // Capture
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveDiff && affectedP != null && affectedP.color != color){
                return true;
            }
            //En passant
            if(Math.abs(targetCol - preCol)== 1 && targetRow == preRow + moveDiff){
                for(Piece p : GamePvP.simPieces){
                    if(p instanceof Pawn && p.color != color && p.row == preRow && p.col == targetCol){
                        if(p._2squareMove){
                            affectedP = p;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
