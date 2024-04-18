package piece;

import Main.GamePanel;

public class Pawn extends Piece{
    public boolean is2StepMove = false;

    public Pawn(int color, int row, int col) {
        super(color, row, col);
        //TODO Auto-generated constructor stub
        if(color == GamePanel.WHITE){
            img = getImage("/piece_image/w_pawn");
        }else{
            img = getImage("/piece_image/b_pawn");
        }
    }

    public boolean canMove(int moveRow, int moveCol){
        if(isWithinBoard(moveRow, moveCol) && !isSameSquare(moveRow, moveCol) && isValidSquare(moveRow, moveCol)){
            int rowDiff = (this.color == GamePanel.WHITE) ? -1 : 1;
            affectedP = getAffectedPiece(moveRow, moveCol);
            // Move forward
            if(this.preCol == moveCol && this.preRow + rowDiff == moveRow && affectedP == null){
                return true;
            }
            // Move 2 steps forward
            if(this.preCol == moveCol && this.preRow + 2*rowDiff == moveRow && isMoved == false && limitMovement_straight(moveRow, moveCol) && affectedP == null){
                is2StepMove = true;
                return true;
            }
            // Capture
            if(Math.abs(this.preCol - moveCol) == 1 && this.preRow + rowDiff == moveRow && affectedP != null && affectedP.color != this.color){
                return true;
            }
            

            // En passant
            if(Math.abs(this.preCol - moveCol) == 1 && this.preRow + rowDiff == moveRow ){
                for(Piece p: GamePanel.simPieces){
                    if(p.row == this.preRow && p.col == moveCol && p.color != this.color && p instanceof Pawn && ((Pawn)p).is2StepMove == true){
                        affectedP = p;
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    
}
