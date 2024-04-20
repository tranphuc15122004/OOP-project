package piece;

import Main.GamePanel;

public class Bishop extends Piece{

    public Bishop(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePanel.WHITE){
            image = getImage("/piece_image/w_bishop");
        } else {
            image = getImage("/piece_image/b_bishop");
        }
    }
    
    public boolean canMove(int targetRow , int targetCol){
        if(isWithinBoard(targetRow, targetCol) && isSamePosition(targetRow, targetCol) == false){
            if(Math.abs(targetRow - preRow) == Math.abs(targetCol - preCol)){
                if(isValidSquare(targetRow, targetCol) && limitMovement_diagonal(targetRow, targetCol)){
                    return true;
                }
            }
        }
        return false;
    }
}
