package piece;

import Main.GamePanel;

public class King extends Piece{

    public King(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePanel.WHITE){
            image = getImage("/piece_image/w_king");
        } else {
            image = getImage("/piece_image/b_king");
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
                if(targetRow == preRow && targetCol == preCol - 2 && limitMovement_straight(targetRow, targetCol)){
                    for(Piece p: GamePanel.simPieces){
                        if(p instanceof Rook && p.color == color & p.row == preRow && p.isMoved == false && p.col == 1){
                            return false;
                        }
                        if(p instanceof Rook && p.color == color && p.isMoved == false && p.preRow == preRow && p.preCol == 0){
                            GamePanel.castlingP = p;
                            return true;
                        }
                    }
                }
                //right castling
                if(targetRow == preRow && targetCol == preCol + 2 && limitMovement_straight(targetRow, targetCol)){
                    for(Piece p : GamePanel.simPieces){
                        if(p instanceof Rook && p.color == color && p.isMoved == false && p.preRow == preRow && p.preCol == 7){
                            GamePanel.castlingP = p;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
