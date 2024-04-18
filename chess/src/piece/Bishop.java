package piece;

import Main.GamePanel;

public class Bishop extends Piece{

    public Bishop(int color, int row, int col) {
        super(color, row, col);
        //TODO Auto-generated constructor stub
        if(color == GamePanel.WHITE){
            img = getImage("/piece_image/w_bishop");
        }else{
            img = getImage("/piece_image/b_bishop");
        }
    }

    public boolean canMove(int moveRow, int moveCol){
        if(isWithinBoard(moveRow, moveCol) && isSameSquare(moveRow, moveCol) == false){
            if(isValidSquare(moveRow, moveCol)){
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
