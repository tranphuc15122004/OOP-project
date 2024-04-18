package piece;

import Main.GamePanel;

public class Rook extends Piece{

    public Rook(int color, int row, int col) {
        super(color, row, col);
        //TODO Auto-generated constructor stub
        if(color == GamePanel.WHITE){
            img = getImage("/piece_image/w_rook");
        }else{
            img = getImage("/piece_image/b_rook");
        }
    }
    
    public boolean canMove(int moveRow, int moveCol){
        if(isWithinBoard(moveRow, moveCol) && isSameSquare(moveRow, moveCol) == false){
            if(isValidSquare(moveRow, moveCol)){
                if(this.preRow == moveRow || this.preCol == moveCol){
                    if(limitMovement_straight(moveRow, moveCol)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
