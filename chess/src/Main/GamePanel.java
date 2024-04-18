package Main;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS  = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //COlor
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //PIECE
    // Back up list of pieces
    public static ArrayList<Piece> pieces = new ArrayList<Piece>();
    // simulate pieces. If any pieces are taken, they will be removed from this list
    // And the taken piece will be removed from the board
    public static ArrayList<Piece> simPieces = new ArrayList<Piece>();      
    Piece activeP;      // the piece that is being held by the mouse
    ArrayList<Piece> promotionP = new ArrayList<>();
    public static Piece castlingRook, checkingP;
    

    //Boolean
    Boolean canMove;
    Boolean validSquare;
    boolean promotion = false;
    boolean gameOver = false; 


    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        //setPieces();
        TestPawn();
        copyPieces(pieces, simPieces);
    }

    public void start() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void TestPawn(){
        pieces.add(new King(WHITE, 7, 0));
        pieces.add(new King(BLACK, 0, 3));
        for(int i = 0; i < 8; i++){
            pieces.add(new Pawn(WHITE, 6, i));
            pieces.add(new Pawn(BLACK, 1, i));
        }

    }

    public void setPieces(){
        for(int i = 0; i < 8; i++){
            pieces.add(new Pawn(WHITE, 6, i));
            pieces.add(new Pawn(BLACK, 1, i));

            if( i == 0 || i == 7){
                pieces.add(new Rook(WHITE, 7, i));
                pieces.add(new Rook(BLACK, 0, i));
            }
            else if( i == 1 || i == 6){
                pieces.add(new Knight(WHITE, 7, i));
                pieces.add(new Knight(BLACK, 0, i));
            }
            else if( i == 2 || i == 5){
                pieces.add(new Bishop(WHITE, 7, i));
                pieces.add(new Bishop(BLACK, 0, i));
            }
            else if( i == 3){
                pieces.add(new Queen(WHITE, 7, i));
                pieces.add(new Queen(BLACK, 0, i));
            }
            else if( i == 4){
                pieces.add(new King(WHITE, 7, i));
                pieces.add(new King(BLACK, 0, i));
            } 
        } 
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }


    @Override
    public void run() {

        double drawInterval = 1000000000 / FPS;
        double lastTime = System.nanoTime();
        double currentTime;
        double delta = 0;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {

        //Promotion
        if(promotion){
            if(mouse.pressed){
                for(Piece p: promotionP){
                    if(p.row == mouse.y / Board.SQUARE_SIZE && p.col == mouse.x / Board.SQUARE_SIZE){
                        if(p instanceof Queen){
                            simPieces.add(new Queen(activeP.color, activeP.row, activeP.col));
                        }
                        else if(p instanceof Rook){
                            simPieces.add(new Rook(activeP.color, activeP.row, activeP.col));
                        }
                        else if(p instanceof Bishop){
                            simPieces.add(new Bishop(activeP.color, activeP.row, activeP.col));
                        }
                        else if(p instanceof Knight){
                            simPieces.add(new Knight(activeP.color, activeP.row, activeP.col));
                        }
                        simPieces.remove(activeP);
                        copyPieces(simPieces, pieces);
                        promotion = false;
                        activeP = null;
                        changeTurn();
                    }
                }
            }
        }else if(!gameOver){
            // mouse is pressed
            if(mouse.pressed){
                // if no piece is held
                if(activeP == null){
                    for(Piece p : simPieces){
                        if( p.row == mouse.y / Board.SQUARE_SIZE && p.col == mouse.x / Board.SQUARE_SIZE && p.color == currentColor){
                            activeP = p;
                        }
                    }
                }
                // if piece is held
                else{
                    simulate();
                }
            }
            // mouse is released
            if(!mouse.pressed){
                if(activeP != null){

                    // MOVE is Confirmed

                    if(validSquare){
                        
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        
                        if(castlingRook != null){
                            castlingRook.updatePosition();
                        }
                        
                        if(canPromo()){
                            promotion = true;
                        }
                        else{
                            activeP = null;
                            changeTurn();
                        }

                        if(inCheck() && isCheckMate()){
                            gameOver = true;
                        }
                    }
                    else{
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }
    }

    //Change turn
    public void changeTurn(){
        currentColor = (currentColor == WHITE) ? BLACK : WHITE;
    }

    //Check castling
    // Move the rook to the correct position if castling is allowed
    void Castling(){
        if(castlingRook != null){
            if(castlingRook.col == 0){
                castlingRook.col = 3;
            }else{
                castlingRook.col = 5;
            }
            castlingRook.x = castlingRook.getX(castlingRook.col);
        }
    } 

    private boolean canPromo(){

        if(activeP instanceof Pawn){
            if((activeP.color == WHITE && activeP.row == 0) || (activeP.color == BLACK && activeP.row == 7)){
                promotionP.clear();
                promotionP.add(new Queen(activeP.color, 2,9));
                promotionP.add(new Rook(activeP.color, 3, 9));
                promotionP.add(new Bishop(activeP.color,4, 9));
                promotionP.add(new Knight(activeP.color, 5, 9));
                return true;
            }
        }

        return false;
    }

    // Check if the king's next move is illegal
    boolean isKingIllegalMove(Piece king){

        if(king instanceof King){
            for(Piece p: simPieces){
                if( p.canMove(king.row, king.col) && p.color != king.color && !(p instanceof King)){
                    return true;
                }
            }
        }
        return false;
    }

    boolean opponentCanCaptureKing(){
        Piece king = getKingPiece();
        for(Piece p: simPieces){
            if(p.color != king.color && p.canMove(king.row, king.col)){
                return true;
            }
        }
        return false;
    }

    private boolean inCheck (){
        Piece king = null;
        king = getKingPiece();

        for(Piece p: simPieces){
            if(p.color != king.col && p.canMove(king.preRow, king.preCol)){
                checkingP = p;
                return true;
            }
        }
        return false;
    }

    Piece getKingPiece(){
        Piece king = null;
        for(Piece p : simPieces){
            if(p instanceof King && p.color == currentColor){
                king = p;
            }
        }
        return king;
    }
    

    // Check if the king is in checkmate
    private boolean isCheckMate(){
        Piece king = getKingPiece();
        if(KingCanMove()){
            return false;
        }
        else{
            // The king cannot move to any square
            // Check if any ally piece can take the piece that is giving check
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
                if(checkingP.col < king.col){
                    for(int col = checkingP.col; col < king.col; col++){
                        for(Piece p: simPieces){
                            if(p.canMove(king.row, col) && p.color == king.color && p != king){
                                return false;
                            }
                        }
                    }
                }
                else{
                    for(int col = checkingP.col; col > king.col; col--){
                        for(Piece p: simPieces){
                            if(p.canMove(king.row, col) && p.color == king.color && p != king){
                                return false;
                            }
                        }
                    }
                }
            }else if(ColDiff == RowDiff){
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

            }else{
                // If the checking piece is a knight
                for(Piece p: simPieces){
                    if(p.canMove(checkingP.row, checkingP.col) && p.color == king.color && p != king){
                        return false;
                    }
                }
            }
        }

        return true;
    } 

    // check every possible move of the king
    private boolean KingCanMove(){
        Piece king = getKingPiece();
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(KingSafeMove(king,king.row + i, king.col + j) && !(i == 0 && j == 0)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean KingSafeMove(Piece king,int row, int col){
        boolean res = false;
        
        king.row = row;
        king.col = col;
        if(king.canMove(row, col)){
            if(king.affectedP != null){
                simPieces.remove(king.affectedP.getPieceIndex(simPieces));
            }
            if(!isKingIllegalMove(king)){
                res = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);
        return res;
    }

    // simulate the movement of the piece
    private void simulate() {
        canMove = false;
        validSquare = false;

        //copyPieces(pieces, simPieces);

        // Cancel castling if the king is moved
        if(castlingRook != null && activeP instanceof King){
            castlingRook.col = castlingRook.preCol;
            castlingRook.x = castlingRook.getX(castlingRook.col);
            castlingRook = null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.row = activeP.getRow(mouse.y);
        activeP.col = activeP.getCol(mouse.x); 

        // if the piece can move to the new position
        if(activeP.canMove(activeP.row, activeP.col)){
            canMove = true;
            
            // if the affected piece can be taken
            // remove the affected piece from the list of pieces
            if(activeP.affectedP != null){
                simPieces.remove(activeP.affectedP.getPieceIndex(simPieces));
            }
            Castling();

            if(!isKingIllegalMove(activeP) && !opponentCanCaptureKing()){
                validSquare = true;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // board 
        board.draw(g2d);
        //piece
        for(Piece p : pieces){
            p.draw(g2d);
        } 

        // active piece
        if(activeP != null){
            if(canMove){
                if(isKingIllegalMove(activeP) || opponentCanCaptureKing()){

                    // draw the square in gray with dark gray diagonal lines if the king's next move is illegal
                    g2d.setColor(Color.gray);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2d.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    for(int i =0; i< Board.SQUARE_SIZE ;i = i + 25){
                        g2d.setColor(Color.darkGray);
                        g2d.setStroke(new BasicStroke(5));
                        g2d.drawLine(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE +i , activeP.col * Board.SQUARE_SIZE +i, activeP.row * Board.SQUARE_SIZE );
                        g2d.drawLine(activeP.col * Board.SQUARE_SIZE + i, (activeP.row +1) * Board.SQUARE_SIZE , (activeP.col+1) * Board.SQUARE_SIZE , activeP.row * Board.SQUARE_SIZE +i );
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }else{

                    // draw the square in white with 70% transparency if the piece can move to the new position
                    g2d.setColor(Color.white);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2d.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
                
            }

            activeP.draw(g2d);
        }

        //TEXT MESSAGE
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Color.white);

        if(gameOver){
            g2d.setColor(Color.RED);
            g2d.drawString((currentColor == WHITE)? "Black wins": "While wins", 840, 400);
        }else{
            //Promoting
            if(promotion){
                g2d.drawString("Promote to:", 840, 150);
                for(Piece p: promotionP){
                    p.draw(g2d);
                }
            }else{
                // Player turn
                if(inCheck() || opponentCanCaptureKing()){
                    g2d.setColor(Color.red);
                    g2d.drawString("Your King", 850, 370);
                    g2d.drawString("is in check!", 840, 410);
                }
                g2d.setColor(Color.white);
                g2d.drawString((currentColor == WHITE) ? "White's turn" : "Black's turn", 840 , (currentColor == WHITE)? 550:250);
            }
        }
    }
}
