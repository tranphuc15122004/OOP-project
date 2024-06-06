package com.example.Game;

import java.util.ArrayList;

import com.example.Piece.Bishop;
import com.example.Piece.King;
import com.example.Piece.Knight;
import com.example.Piece.Pawn;
import com.example.Piece.Piece;
import com.example.Piece.Queen;
import com.example.Piece.Rook;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.util.Pair;

public class Rule {
    GraphicsContext gc;
    public static Board board = new Board();
    static Mouse game_mouse = new Mouse();
    Canvas c = new Canvas();
    final static int FPS = 100;

    //Background
    static Image background = new Image(Rule.class.getResourceAsStream("/background/background.png"));

    //StopWatch
    static Stopwatch w_stopwatch = new Stopwatch();
    static Stopwatch b_stopwatch = new Stopwatch();

    //Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static int currentColor = WHITE;

    //Pieces
    public static ArrayList<Piece> pieces = new ArrayList<Piece>(); // original pieces, it can be known as real pieces on the board
    public static ArrayList<Piece> simPieces = new ArrayList<Piece>(); // simulated pieces, it can be known as pieces in player's thinking phase
    static ArrayList<Piece> promoPieces = new ArrayList<Piece>(); // pieces for promotion
    public static Piece activeP; // activeP is the piece that player is holding, checkingP is the piece that is giving check
    static Piece checkingP;
    public static Piece castlingP; // the rook that is involved in castling
    public static ArrayList<Pair<Integer, Integer>> all_move = new ArrayList<>();// all possible moves of the active piece

    //Boolean
    boolean canMove; // if the piece can move to the target square
    boolean validSquare; // if the target square is valid
    static boolean isPromo; // if the player needs to promote the pawn
    public static boolean gameOver; // if the game is over
    static boolean stalemate; // if the game is in stalemate

    
    static void Timing(){
        if(gameOver || stalemate) return;
        if(currentColor == WHITE){
            w_stopwatch.setSeconds(w_stopwatch.getSeconds() + 1);
            if(w_stopwatch.getSeconds() == 60){
                w_stopwatch.setMinutes(w_stopwatch.getMinutes() + 1);
                w_stopwatch.setSeconds(0);
            }
        }
        else{
            b_stopwatch.setSeconds(b_stopwatch.getSeconds() + 1);
            if(b_stopwatch.getSeconds() == 60){
                b_stopwatch.setMinutes(b_stopwatch.getMinutes() + 1);
                b_stopwatch.setSeconds(0);
            }
        }
    }

    public static void initialize_color(boolean isWhite ){
        if (isWhite){
            currentColor = WHITE;
        }
        else{
            currentColor = BLACK;
        }
    }

    // chưa test
    static void setBackground(String path){
        try {
            background = new Image(Rule.class.getResourceAsStream("/background/" + path + ".png"));
        } catch (Exception e) {
            background = new Image(Rule.class.getResourceAsStream("/background/background.png"));
        }
    }

    static void changeTurn(){
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

    static void copyPieces( ArrayList<Piece> from, ArrayList<Piece> to){
        to.clear();
        for(int i = 0; i < from.size(); i++){
            to.add(from.get(i));
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
        /* if(activeP.canMove(king.row, king.col)){
            checkingP = activeP;
            return true;
        }else{
            checkingP = null;
            return false;
        } */
        for(Piece p: simPieces){
            if(p.color != king.color && p.canMove(king.row, king.col)){
                checkingP = p;
                return true;
            }
        }
        checkingP = null;
        return false;
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

    // for drawing the possible moves of the active piece
    static void getAllMove(){
        Piece p = activeP;
        all_move.clear();
        for(int row = 0; row < Board.MAX_ROW; row++){
            for(int col  = 0 ; col < Board.MAX_COL; col++){
                if(p.canMove(row, col)){
                    if(checkingP == null){
                        all_move.add(new Pair<Integer,Integer>(row, col));
                    }
                    else{
                        copyPieces(simPieces, pieces);
                        p.row =row;
                        p.col = col;
                        if(opponentCanCaptureKing() == false || (row == checkingP.row && col == checkingP.col)){
                            all_move.add(new Pair<Integer,Integer>(row, col));
                        }
                        copyPieces(pieces, simPieces);
                    }
                } 
            }
        }    
    }
}
