package com.example.Piece;

import com.example.Game.GamePvP;

public class King extends Piece {

    public King(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePvP.WHITE){
            image = getImage("/Piece_image/w_king");
        } else {
            image = getImage("/Piece_image/b_king");
        }
    }
    
    public boolean canMove(int targetRow, int targetCol){
        if(isWithinBoard(targetRow, targetCol) && isSamePosition(targetRow, targetCol) == false){
            if(Math.abs(targetRow - preRow) <= 1 && Math.abs(targetCol - preCol) <= 1){
                if(isValidSquare(targetRow, targetCol)){
                    return true;
                }
            }
            // castling
            if(!isMoved){
                //left castling
                if(targetRow == preRow && targetCol == preCol - 2 && limitMovement_straight(targetRow, targetCol) && isValidSquare(targetRow, targetCol) && isValidSquare(targetRow, targetCol -1)){
                    for(Piece p: GamePvP.simPieces){
                        if(p instanceof Rook && p.color == color & p.row == preRow && p.isMoved == false && p.col == 1){
                            return false;
                        }
                        if(p instanceof Rook && p.color == color && p.isMoved == false && p.preRow == preRow && p.preCol == 0){
                            GamePvP.castlingP = p;
                            return true;
                        }
                    }
                }
                //right castling
                if(targetRow == preRow && targetCol == preCol + 2 && limitMovement_straight(targetRow, targetCol) && isValidSquare(targetRow, targetCol)){
                    for(Piece p : GamePvP.simPieces){
                        if(p instanceof Rook && p.color == color && p.isMoved == false && p.preRow == preRow && p.preCol == 7){
                            GamePvP.castlingP = p;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
