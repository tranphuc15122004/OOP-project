package piece;

import Main.GamePanel;

public class Knight extends Piece{

    public Knight(int color, int row, int col) {
        super(color, row, col);
        //TODO Auto-generated constructor stub
        if(color == GamePanel.WHITE){
            img = getImage("/piece_image/w_knight");
        }else{
            img = getImage("/piece_image/b_knight");
        }
    }

    public boolean canMove(int moveRow, int moveCol){
        if(isWithinBoard(moveRow, moveCol) && isSameSquare(moveRow, moveCol) == false){
            if(isValidSquare(moveRow, moveCol)){
                if(Math.abs(this.preRow - moveRow) == 2 && Math.abs(this.preCol - moveCol) == 1){
                    return true;
                }
                if(Math.abs(this.preRow - moveRow) == 1 && Math.abs(this.preCol - moveCol) == 2){
                    return true;
                }
            }
        }
        return false;
    }
    
}
