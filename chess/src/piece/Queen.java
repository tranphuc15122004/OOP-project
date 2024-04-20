package piece;

import Main.GamePanel;

public class Queen extends Piece{

    public Queen(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePanel.WHITE){
            image = getImage("/piece_image/w_queen");
        } else {
            image = getImage("/piece_image/b_queen");
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
