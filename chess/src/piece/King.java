package piece;

import Main.GamePanel;

public class King extends Piece{

    public King(int color, int row, int col) {
        super(color, row, col);
        //TODO Auto-generated constructor stub
        if(color == GamePanel.WHITE){
            img = getImage("/piece_image/w_king");
        }else{
            img = getImage("/piece_image/b_king");
        }
    }

    public boolean canMove(int moveRow, int moveCol){
        if(isWithinBoard(moveRow, moveCol) && isSameSquare(moveRow, moveCol) == false){
            if(Math.abs(this.preRow - moveRow) <= 1 && Math.abs(this.preCol - moveCol) <= 1){
                if(isValidSquare(moveRow, moveCol)) return true;
            }

            //Castling
            if(!isMoved){
                // Left
                if(moveRow == this.preRow && moveCol == this.preCol - 2 && GamePanel.castlingRook == null && limitMovement_straight(moveRow, moveCol)){
                    for(Piece p: GamePanel.simPieces){
                        if(p.row == this.preRow && p.col == this.preCol - 3){
                            return false;
                        }

                        if(p.color == this.color && p.row == this.preRow && p.col == this.preCol - 4 && p.isMoved == false){
                            GamePanel.castlingRook = p;
                            return true;
                        }
                    }
                }

                //Right
                if(moveRow == this.preRow && moveCol == this.preCol +2 && GamePanel.castlingRook == null && limitMovement_straight(moveRow, moveCol)){
                    for(Piece p: GamePanel.simPieces){
                        if(p.color == this.color && p.row == this.preRow && p.col == this.preCol + 3 && p.isMoved == false){
                            GamePanel.castlingRook = p;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
