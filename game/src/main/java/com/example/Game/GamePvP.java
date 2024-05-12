package com.example.Game;

import java.util.ArrayList;

import com.example.Main;
import com.example.Piece.Bishop;
import com.example.Piece.King;
import com.example.Piece.Knight;
import com.example.Piece.Pawn;
import com.example.Piece.Piece;
import com.example.Piece.Queen;
import com.example.Piece.Rook;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;

public class GamePvP {
    GraphicsContext gc;
    Board board = new Board();
    static Mouse game_mouse = new Mouse();
    Canvas c = new Canvas();
    final int FPS = 60;

    //StopWatch
    Stopwatch w_stopwatch = new Stopwatch();
    Stopwatch b_stopwatch = new Stopwatch();

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
    static ArrayList<Pair<Integer, Integer>> all_move = new ArrayList<>();// all possible moves of the active piece

    //Boolean
    boolean canMove; // if the piece can move to the target square
    boolean validSquare; // if the target square is valid
    static boolean isPromo; // if the player needs to promote the pawn
    public static boolean gameOver; // if the game is over
    boolean stalemate; // if the game is in stalemate


    public GamePvP(GraphicsContext gc1 , Mouse mouse, Canvas canvas){
        gc = gc1;
        c = canvas;
        setPieces();
        game_mouse = mouse;
        copyPieces(pieces, simPieces);
    }

    public void setPieces(){
        for(int i = 0; i < 8; i++){
            pieces.add(new Pawn(BLACK, 1, i));
            pieces.add(new Pawn(WHITE, 6, i));
            if(i == 0 || i == 7){
                pieces.add(new Rook(BLACK, 0, i));
                pieces.add(new Rook(WHITE, 7, i));
            } else if(i == 1 || i == 6){
                pieces.add(new Knight(BLACK, 0, i));
                pieces.add(new Knight(WHITE, 7, i));
            } else if(i == 2 || i == 5){
                pieces.add(new Bishop(BLACK, 0, i));
                pieces.add(new Bishop(WHITE, 7, i));
            } else if(i == 3){
                pieces.add(new Queen(BLACK, 0, i));
                pieces.add(new Queen(WHITE, 7, i));
            }  else if(i == 4){
                pieces.add(new King(BLACK, 0, i));
                pieces.add(new King(WHITE, 7, i));
            }
        } 
    }

    static void copyPieces( ArrayList<Piece> from, ArrayList<Piece> to){
        to.clear();
        for(int i = 0; i < from.size(); i++){
            to.add(from.get(i));
        }
    }

    public void gameloop(){
        new AnimationTimer() {
            double drawInterval = 1000000000 / FPS;
            double delta = 0;
            long LastTime = System.nanoTime();
            int count = 0;

            @Override
            public void handle(long now) {
                delta += (now - LastTime) / drawInterval;
                LastTime = now;
                if(delta >= 1){
                    update();
                    gc.clearRect(0, 0, c.getWidth(), c.getHeight());
                    render();
                    delta--;
                    count++;
                }
                if(count == FPS){
                    Timing();
                    count = 0;
                }
            }
        }.start();
    }

    private void Timing(){
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

    private void update(){
        if(isPromo){
            Rule.promoting();
        }else if(!gameOver && !stalemate){
            if(game_mouse.isPressed()){

                if(activeP == null){
                    for(Piece p: simPieces){
                        if( p.color == currentColor && p.row == game_mouse.y / Board.SQUARE_SIZE && p.col == game_mouse.x / Board.SQUARE_SIZE){
                            activeP = p;
                        }
                    }
                    if(activeP != null) Rule.getAllMove();
                }
                else{
                    // if player holding a piece
                    simulate();
                }
            }
            if(!game_mouse.isPressed()){
                //if player release the piece
                if(activeP != null){
                    if(activeP.isSamePosition(activeP.row, activeP.col)){
                        activeP.resetPosition();                
                    }
                    else{
                        // Move is confirmed
                        if(validSquare){
                            copyPieces(simPieces, pieces);
                            activeP.updatePosition();
                            // Check end game condition
                            if(Rule.isKingInCheck() && Rule.isCheckmate()){
                                    gameOver = true;
                            }
                            else if(Rule.isKingInCheck() && Rule.isStalemate()){
                                stalemate = true;
                            }
                            else{
                                // normal move
                                if(castlingP != null){
                                    castlingP.updatePosition();
                                }
                                if(Rule.checkPromo()){
                                    isPromo = true;
                                }else{
                                    activeP = null;
                                    changeTurn();
                                }
                            }
                        }
                        else{
                            // Move is not completed
                            copyPieces(pieces, simPieces);
                            activeP.resetPosition();
                            activeP = null;
                        }
                    }
                }
            }
        }
    }

    private void changeTurn() {
        currentColor = (currentColor == WHITE) ? BLACK : WHITE;
        for(Piece p: simPieces){
            if( currentColor == WHITE && p.color == WHITE){
                p._2squareMove = false;
            }
            if( currentColor == BLACK && p.color == BLACK){
                p._2squareMove = false;
            }
        }
        throw new UnsupportedOperationException("Unimplemented method 'changeTurn'");
    }

    private void simulate(){
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        // reset the position of the castling rook
        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        activeP.x = game_mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = game_mouse.y - Board.HALF_SQUARE_SIZE;
        
        if(game_mouse.x <= Board.HALF_SQUARE_SIZE ){
            activeP.x = 0;
            if(game_mouse.y <= Board.HALF_SQUARE_SIZE) activeP.y = 0;
            else if(game_mouse.y >= Board.SQUARE_SIZE * Board.MAX_ROW - Board.HALF_SQUARE_SIZE) activeP.y = Board.SQUARE_SIZE * Board.MAX_ROW - Board.SQUARE_SIZE;
        }else if(game_mouse.x >= Board.SQUARE_SIZE * Board.MAX_COL - Board.HALF_SQUARE_SIZE){
            activeP.x = Board.SQUARE_SIZE * Board.MAX_COL - Board.SQUARE_SIZE;
            if(game_mouse.y <= Board.HALF_SQUARE_SIZE) activeP.y = 0;
            else if(game_mouse.y >= Board.SQUARE_SIZE * Board.MAX_ROW - Board.HALF_SQUARE_SIZE) activeP.y = Board.SQUARE_SIZE * Board.MAX_ROW - Board.SQUARE_SIZE;
        }else if (game_mouse.y <= Board.HALF_SQUARE_SIZE ){
            activeP.y = 0;
            if(game_mouse.x <= Board.HALF_SQUARE_SIZE) activeP.x = 0;
            else if(game_mouse.x >= Board.SQUARE_SIZE * Board.MAX_COL - Board.HALF_SQUARE_SIZE) activeP.x = Board.SQUARE_SIZE * Board.MAX_COL - Board.SQUARE_SIZE;
        }else if(game_mouse.y >= Board.SQUARE_SIZE * Board.MAX_ROW - Board.HALF_SQUARE_SIZE ){
            activeP.y = Board.SQUARE_SIZE * Board.MAX_ROW - Board.SQUARE_SIZE;
            if(game_mouse.x <= Board.HALF_SQUARE_SIZE) activeP.x = 0;
            else if(game_mouse.x >= Board.SQUARE_SIZE * Board.MAX_COL - Board.HALF_SQUARE_SIZE) activeP.x = Board.SQUARE_SIZE * Board.MAX_COL - Board.SQUARE_SIZE;
        }

        activeP.row = activeP.getRow(activeP.y);
        activeP.col = activeP.getCol(activeP.x);

        // Check if the piece can move to the target square
        if(activeP.canMove(activeP.row, activeP.col)){
            canMove = true;
            
            // if player is holding a piece and moving through other pieces, they will be removed from the simPieces
            // That the reason why we need to reset the simPieces after any loop
            if(activeP.affectedP != null){
                simPieces.remove(activeP.affectedP.getIndex());
            }
            Rule.castling();
            // if player's king is not in check after the opponent's move, the move is valid
            if(!Rule.isKingIllegalMove(activeP) && Rule.opponentCanCaptureKing() == false){
                validSquare = true;
            }
        }
    }

    private void render(){
        //Draw Board
        Board.draw(gc);

        //Draw pieces
        for(Piece p : pieces){
            p.draw(gc);
        }
        
        //Active Piece
        if(activeP != null){
            gc.setGlobalAlpha(0.3); // Set opacity to 50%
            gc.setFill(Color.WHITE);
            gc.fillRect(activeP.preCol * Board.SQUARE_SIZE, activeP.preRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
            gc.setGlobalAlpha(0.7);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(Board.SQUARE_SIZE / 20);
            int arcSize = 20;
            gc.strokeRoundRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE, arcSize , arcSize);
            
            if(!all_move.isEmpty()){
                for(Pair<Integer, Integer> pos: all_move){
                    int row = (int) pos.getKey();
                    int col = (int) pos.getValue();
                    boolean hit = false;
                    gc.setFill(Color.BLACK);
                    gc.setGlobalAlpha(0.3);
                    for(Piece p: pieces){
                        if(p.row == row && p.col == col && p.color != activeP.color){
                            hit = true;
                        }
                    }
                    if(hit){
                        int temp = Board.HALF_SQUARE_SIZE / 15;
                        gc.setStroke(Color.BLACK);
                        gc.setLineWidth(Board.SQUARE_SIZE / 12);
                        gc.strokeOval(col * Board.SQUARE_SIZE + temp, row * Board.SQUARE_SIZE+temp, Board.SQUARE_SIZE- 2*temp, Board.SQUARE_SIZE-2*temp);
                    }else gc.fillOval(col * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE/5 *3, row * Board.SQUARE_SIZE + Board.HALF_SQUARE_SIZE / 5 *3, Board.SQUARE_SIZE/10 *4, Board.SQUARE_SIZE/10 *4);;
                }
            }
            gc.setGlobalAlpha(1);
            activeP.draw(gc);
        }

        //MESSAGE
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gc.setFontSmoothingType(FontSmoothingType .LCD);

        if(gameOver){
            gc.setLineWidth(200);
            gc.setStroke(Color.WHITE);
            gc.setGlobalAlpha(0.5);
            gc.strokeLine(0, Main.HEIGHT/2, Main.WIDTH, Main.HEIGHT/2);
            gc.setGlobalAlpha(1);
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 100));
            if(currentColor == BLACK){
                gc.fillText("BLACK WINS", 200, Main.HEIGHT/2 +35);
            }else{
                gc.fillText("WHITE WINS", 200, Main.HEIGHT/2 +35);
            }
        }
        else if(stalemate){
            gc.setLineWidth(200);
            gc.setStroke(Color.WHITE);
            gc.setGlobalAlpha(0.5);
            gc.strokeLine(0, Main.HEIGHT/2, Main.WIDTH, Main.HEIGHT/2);
            gc.setGlobalAlpha(1);
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 100));
            gc.fillText("STALEMATE", 200, Main.HEIGHT/2 +35);
        }
        else{
            if(isPromo){
                gc.fillText("Promote to: ", 850, 180);
                for(Piece p: promoPieces){
                    p.draw(gc);
                }
            }
            // NORMAL
            else{
                if(currentColor == WHITE){
                    gc.fillText("WHITE TURN", 850, 650);
                    if( checkingP != null && checkingP.color == BLACK){
                        gc.setFill(Color.RED);
                        gc.fillText("The King", 880, 380);
                        gc.fillText("is in Check", 870, 420);
                    }
                } else {
                    gc.fillText("BLACK TURN", 850, 150);
                    if( checkingP != null && checkingP.color == WHITE){
                        gc.setFill(Color.RED);
                        gc.fillText("The King", 880, 380);
                        gc.fillText("is in Check", 870, 420);
                    }
                }
            }
        }

        //Stopwatch
        gc.setFill(Color.WHITE);
        gc.setGlobalAlpha(0.5);
        gc.fillRect(800, 0, 300, 100);
        gc.fillRect(800, 700, 300, 100);
        gc.setGlobalAlpha(1);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        gc.fillText(w_stopwatch.getTime(), 900, 760);
        gc.fillText(b_stopwatch.getTime(), 900, 60);
    }
    
}
