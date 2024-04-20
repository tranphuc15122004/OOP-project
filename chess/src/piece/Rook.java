package piece;

import Main.GamePanel;

public class Rook extends Piece{

    public Rook(int color, int row, int col) {
        super(color, row, col);
        if(color == GamePanel.WHITE){
            image = getImage("/piece_image/w_rook");
        } else {
            image = getImage("/piece_image/b_rook");
        }
    }
    
    public boolean canMove(int targetRow , int targetCol){
        if(isWithinBoard(targetRow, targetCol) && isSamePosition(targetRow, targetCol) == false){
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetRow, targetCol) && limitMovement_straight(targetRow, targetCol)){
                    return true;
                }
            }
        }
        return false;
    }
}
