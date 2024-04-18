package piece;

import Main.GamePanel;

public class Queen extends Piece{

    public Queen(int color, int row, int col) {
        super(color, row, col);
        //TODO Auto-generated constructor stub
        if(color == GamePanel.WHITE){
            img = getImage("/piece_image/w_queen");
        }else{
            img = getImage("/piece_image/b_queen");
        }
    }

    public boolean canMove(int moveRow, int moveCol){
        if(isWithinBoard(moveRow, moveCol) && !isSameSquare(moveRow, moveCol)){
            if(isValidSquare(moveRow, moveCol)){
                if(this.preRow == moveRow || this.preCol == moveCol){
                    if(limitMovement_straight(moveRow, moveCol)){
                        return true;
                    }
                }
                if(Math.abs(this.preRow - moveRow) == Math.abs(this.preCol - moveCol)){
                    if(limitMovement_diagonal(moveRow, moveCol)){

                        return true;
                    }
                }
            }
        }
        return false;
    }

}
