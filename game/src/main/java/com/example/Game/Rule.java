package com.example.Game;

import com.example.Piece.Bishop;
import com.example.Piece.King;
import com.example.Piece.Knight;
import com.example.Piece.Pawn;
import com.example.Piece.Piece;
import com.example.Piece.Queen;
import com.example.Piece.Rook;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;

public class Rule extends GamePvP{
    public Rule(GraphicsContext gc1, Mouse mouse , Canvas canvas) {
        super(gc1, mouse, canvas);
    }

    private static void changeTurn(){
        currentColor = (currentColor == WHITE) ? BLACK : WHITE;
        for(Piece p: simPieces){
            if( currentColor == WHITE && p.color == WHITE){
                p._2squareMove = false;
            }
            if( currentColor == BLACK && p.color == BLACK){
                p._2squareMove = false;
            }
        }
    }

    // Change the position of the castling rook
    static void castling(){
        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col = 3;
            }
            if(castlingP.col == 7){
                castlingP.col = 5;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    // Check if the pawn is in the promotion zone
    static boolean checkPromo(){
        for(Piece p: simPieces){
            if(p instanceof Pawn && (p.row == 0 || p.row == 7)){
                promoPieces.clear();
                promoPieces.add(new Queen(currentColor , 2 , 9));
                promoPieces.add(new Rook(currentColor , 3 , 9));
                promoPieces.add(new Bishop(currentColor , 4 , 9));
                promoPieces.add(new Knight(currentColor , 5 , 9));
                return true;
            }
        }
        return false;
    }

    static void promoting(){
        if(game_mouse.isPressed()){
            for(Piece p: promoPieces){
                if(p.row == game_mouse.y / Board.SQUARE_SIZE && p.col == game_mouse.x / Board.SQUARE_SIZE){
                    if(p instanceof Queen){
                        simPieces.add(new Queen(activeP.color, activeP.row, activeP.col));
                    }else if(p instanceof Rook){
                        simPieces.add(new Rook(activeP.color, activeP.row, activeP.col));
                    }else if(p instanceof Bishop){
                        simPieces.add(new Bishop(activeP.color, activeP.row, activeP.col));
                    }else if(p instanceof Knight){
                        simPieces.add(new Knight(activeP.color, activeP.row, activeP.col));
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    isPromo = false;
                    changeTurn();
                }
            }
        }
    } 

    // Check if the king's next move can be attacked by the opponent piece or not
    static boolean isKingIllegalMove (Piece king){
        if(king instanceof King){
            for(Piece p: simPieces){
                if(p != king && p.color != king.color && p.canMove(king.row, king.col)){
                    return true;
                }
            }
        }
        return false;
    }

    // Check if the king is in check and assign the checking piece to checkingP
    static boolean isKingInCheck(){
        Piece king = getKingPiece(true);
        if(activeP.canMove(king.row, king.col)){
            checkingP = activeP;
            return true;
        }else{
            checkingP = null;
            return false;
        }
    }

    // Check if the opponent can capture the king
    static boolean opponentCanCaptureKing(){
        Piece king = getKingPiece(false);
        for(Piece p: simPieces){
            if(p.color != king.color && p.canMove(king.row, king.col)){
                return true;
            }
        }
        return false;
    }

    static boolean isCheckmate(){
        Piece king = getKingPiece(true);

        // The first situation is that the king can move to a square that is not attacked by the opponent
        if(kingCanMove(king)){
            return false;
        }
        // The second situation is that the king cannot move to any square
        // Check if any ally piece can take the piece that is giving check or block the path
        else{
            int ColDiff = Math.abs(king.col - checkingP.col);
            int RowDiff = Math.abs(king.row - checkingP.row);

            if(ColDiff ==0){
                // check up 
                if(checkingP.row < king.row){
                    for( int row = checkingP.row ; row < king.row; row++){
                        for(Piece p: simPieces){
                            if(p.canMove(row, king.col) && p.color == king.color && p != king){
                                return false;
                            }
                        }
                    }
                }
                // check down
                else{
                    for( int row = checkingP.row ; row > king.row; row--){
                        for(Piece p: simPieces){
                            if(p.canMove(row, king.col) && p.color == king.color && p != king){
                                return false;
                            }
                        }
                    }
                }
            }else if(RowDiff == 0){
                // check left
                if(checkingP.col < king.col){
                    for(int col = checkingP.col; col < king.col; col++){
                        for(Piece p: simPieces){
                            if(p.canMove(king.row, col) && p.color == king.color && p != king){
                                return false;
                            }
                        }
                    }
                }
                // check right
                else{
                    for(int col = checkingP.col; col > king.col; col--){
                        for(Piece p: simPieces){
                            if(p.canMove(king.row, col) && p.color == king.color && p != king){
                                return false;
                            }
                        }
                    }
                }
            }
            // Check the diagonal path
            else if(ColDiff == RowDiff){
                if(checkingP.row < king.row){
                    // up left
                    if(checkingP.col < king.col){
                        for(int col =checkingP.col, row = checkingP.row; col < king.col; col++, row++){
                            for(Piece p: simPieces){
                                if(p.canMove(row, col) && p.color == king.color && p != king){
                                    return false;
                                }
                            }
                        }
                    }
                    // up right
                    else{
                        for(int col =checkingP.col, row = checkingP.row; col > king.col; col--, row++){
                            for(Piece p: simPieces){
                                if(p.canMove(row, col) && p.color == king.color && p != king){
                                    return false;
                                }
                            }
                        }
                    }
                }
                else{
                    // down left
                    if(checkingP.col < king.col){
                        for(int col =checkingP.col, row = checkingP.row; col < king.col; col++, row--){
                            for(Piece p: simPieces){
                                if(p.canMove(row, col) && p.color == king.color && p != king){
                                    return false;
                                }
                            }
                        }
                    }
                    // down right
                    else{
                        for(int col =checkingP.col, row = checkingP.row; col > king.col; col--, row--){
                            for(Piece p: simPieces){
                                if(p.canMove(row, col) && p.color == king.color && p != king){
                                    return false;
                                }
                            }
                        }   
                    }
                }
            }
            // If the checking piece is a knight
            else{
                for(Piece p: simPieces){
                    if(p.canMove(checkingP.row, checkingP.col) && p.color == king.color && p != king){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Check 8 directions around the king to find out that king can move to any square that is not attacked by the opponent
    private static boolean kingCanMove(Piece king){
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(isValidMove(king,king.row + i, king.col + j) && !(i == 0 && j == 0)){
                    return true;
                }
            }
        }
        return false;
    }

    // Check if the king can move to the target square that is not attacked by the opponent
    private static boolean isValidMove(Piece king, int row, int col){
        boolean res = false;
        king.row = row;
        king.col = col;
        if(king.canMove(row, col)){
            if(king.affectedP != null){
                simPieces.remove(king.affectedP.getIndex());
            }
            if(isKingIllegalMove(king) == false){
                res = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);
        return res;
    }

    // Get the king piece of the current player or the opponent
    // The parameter 'opponent' is used to determine the king piece of the opponent
    private static Piece getKingPiece( boolean opponent){
        Piece king = null;
        for(Piece p: simPieces){
            if(opponent){
                if(p instanceof King && p.color != currentColor){
                    king = p;
                }
            }else{
                if(p instanceof King && p.color == currentColor){
                    king = p;
                }
            }
        }
        return king;
    }

    static boolean isStalemate(){
        int count = 0;
        for(Piece p : simPieces){
            if(p.color != currentColor) count ++;
        }
        if (count == 1) {
            if(!kingCanMove(getKingPiece(true))){
                return true;
            }
        }
        return false;
    }

    static void getAllMove(){
        Piece p = GamePvP.activeP;
        GamePvP.all_move.clear();
        for(int row = 0; row < Board.MAX_ROW; row++){
            for(int col  = 0 ; col < Board.MAX_COL; col++){
                if(p.canMove(row, col)){
                    if(GamePvP.checkingP == null){
                        GamePvP.all_move.add(new Pair<Integer, Integer>(row, col));
                    }
                    else{
                        GamePvP.copyPieces(GamePvP.simPieces, GamePvP.pieces);
                        p.row = row;
                        p.col = col;
                        if(opponentCanCaptureKing() == false || (row == GamePvP.checkingP.row && col == GamePvP.checkingP.col)){
                            GamePvP.all_move.add(new Pair<Integer,Integer>(row, col));
                        }
                        GamePvP.copyPieces(GamePvP.pieces, GamePvP.simPieces);
                    }
                } 
            }
        }    
    }
}
