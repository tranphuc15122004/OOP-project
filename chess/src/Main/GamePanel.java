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
    Board board = new Board();
    final int FPS = 60;
    Thread gameThread;
    Mouse mouse = new Mouse();

    //Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //Pieces
    public static ArrayList<Piece> pieces = new ArrayList<Piece>(); // original pieces, it can be known as real pieces on the board
    public static ArrayList<Piece> simPieces = new ArrayList<Piece>(); // simulated pieces, it can be known as pieces in player's thinking phase
    static ArrayList<Piece> promoPieces = new ArrayList<Piece>(); // pieces for promotion
    Piece activeP, checkingP; // activeP is the piece that player is holding, checkingP is the piece that is giving check
    public static Piece castlingP; // the rook that is involved in castling

    //Boolean
    boolean canMove; // if the piece can move to the target square
    boolean validSquare; // if the target square is valid
    boolean isPromo; // if the player needs to promote the pawn
    boolean gameOver; // if the game is over
    boolean stalemate; // if the game is in stalemate

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Set the pieces on the board
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

    private void copyPieces( ArrayList<Piece> from, ArrayList<Piece> to){
        to.clear();
        for(int i = 0; i < from.size(); i++){
            to.add(from.get(i));
        }
    }


    private void changeTurn(){
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
    private void castling(){
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
    private boolean checkPromo(){
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

    private void promoting(){
        if(mouse.pressed){
            for(Piece p: promoPieces){
                if(p.row == mouse.y / Board.SQUARE_SIZE && p.col == mouse.x / Board.SQUARE_SIZE){
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
    private boolean isKingIllegalMove (Piece king){
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
    private boolean isKingInCheck(){
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
    private boolean opponentCanCaptureKing(){
        Piece king = getKingPiece(false);
        for(Piece p: simPieces){
            if(p.color != king.color && p.canMove(king.row, king.col)){
                return true;
            }
        }
        return false;
    }

    private boolean isCheckmate(){
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
    private boolean kingCanMove(Piece king){
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
    private boolean isValidMove(Piece king, int row, int col){
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
    private Piece getKingPiece( boolean opponent){
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

    private boolean isStalemate(){
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

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long LastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - LastTime) / drawInterval;
            LastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    private void update() {
        if(isPromo){
            promoting();
        }else if(!gameOver && !stalemate){
            if(mouse.pressed){
                if(activeP == null){
                    for(Piece p: simPieces){
                        if( p.color == currentColor && p.row == mouse.y / Board.SQUARE_SIZE && p.col == mouse.x / Board.SQUARE_SIZE){
                            activeP = p;
                        }
                    }
                }
                else{
                    // if player holding a piece
                    simulate();
                }
            }
            if(!mouse.pressed){
                //if player release the piece
                if(activeP != null){
                    // Move is confirmed
                    if(validSquare){
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();

                        // Check end game condition
                        if(isKingInCheck() && isCheckmate()){
                            gameOver = true;
                        }
                        else if(isKingInCheck() && isStalemate()){
                            stalemate = true;
                        }
                        else{
                            // normal move
                            if(castlingP != null){
                                castlingP.updatePosition();
                            }
                            if(checkPromo()){
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

    // Simulate the move of the piece in the player's thinking phase
    private void simulate() {
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        // reset the position of the castling rook
        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.row = activeP.getRow(mouse.y);
        activeP.col = activeP.getCol(mouse.x);

        // Check if the piece can move to the target square
        if(activeP.canMove(activeP.row, activeP.col)){
            canMove = true;
            
            // if player is holding a piece and moving through other pieces, they will be removed from the simPieces
            // That the reason why we need to reset the simPieces after any loop
            if(activeP.affectedP != null){
                simPieces.remove(activeP.affectedP.getIndex());
            }
            castling();
            // if player's king is not in check after the opponent's move, the move is valid
            if(!isKingIllegalMove(activeP) && opponentCanCaptureKing() == false){
                validSquare = true;
            }
        }
    }

    // representation of the repaint() method
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        //Board
        board.draw(g2d);

        //Pieces
        for(Piece p: simPieces){
            p.draw(g2d);
        }

        //Active Piece
        if(activeP != null){
            if(canMove){
                if(isKingIllegalMove(activeP) || opponentCanCaptureKing()){
                    // draw the square in gray with dark gray diagonal lines if the king's next move is illegal
                    g2d.setColor(Color.gray);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    for(int i =0; i< Board.SQUARE_SIZE ;i = i + 25){
                        g2d.setColor(Color.darkGray);
                        g2d.setStroke(new BasicStroke(5));
                        g2d.drawLine(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE +i , activeP.col * Board.SQUARE_SIZE +i, activeP.row * Board.SQUARE_SIZE );
                        g2d.drawLine(activeP.col * Board.SQUARE_SIZE + i, (activeP.row +1) * Board.SQUARE_SIZE , (activeP.col+1) * Board.SQUARE_SIZE , activeP.row * Board.SQUARE_SIZE +i );
                    }
                    int arcSize = 15;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2d.drawRoundRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE, arcSize, arcSize);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
                // draw the blur white square if the move is valid
                else{
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(5));
                    int arcSize = 15; 
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2d.drawRoundRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE, arcSize, arcSize);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                }
            }
            
            activeP.draw(g2d);
        }

        //King in check
        // draw red square around the king if the king is in check
        if(checkingP != null){
            Piece king = getKingPiece(false);
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(5));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            int arcSize = 15;
            g2d.drawRoundRect(king.preCol * Board.SQUARE_SIZE, king.preRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE, arcSize, arcSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            king.draw(g2d);
        }

        //MESSAGE
        g2d.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));

        // GAME OVER
        if(gameOver){
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setStroke(new BasicStroke(200));
            g2d.drawLine(0, HEIGHT/2, WIDTH, HEIGHT/2);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            g2d.setFont(new Font("Arial", Font.BOLD, 100));
            g2d.setColor(Color.darkGray);
            if(currentColor == BLACK){
                g2d.drawString("BLACK WINS", 200, HEIGHT/2 +35);
            }else{
                g2d.drawString("WHITE WINS", 200, HEIGHT/2 +35);
            }
        }
        // STALEMATE
        else if(stalemate){
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setStroke(new BasicStroke(200));
            g2d.drawLine(0, HEIGHT/2, WIDTH, HEIGHT/2);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            g2d.setFont(new Font("Arial", Font.BOLD, 100));
            g2d.setColor(Color.darkGray);
            g2d.drawString("STALEMATE", 200, HEIGHT/2 +35);
        }
        else{
            // PROMOTE
            if(isPromo){
                g2d.drawString("Promote to: ", 850, 180);
                for(Piece p: promoPieces){
                    p.draw(g2d);
                }
            }
            // NORMAL
            else{
                if(currentColor == WHITE){
                    g2d.drawString("WHITE TURN", 850, 550);
                    if( checkingP != null && checkingP.color == BLACK){
                        g2d.setColor(Color.RED);
                        g2d.drawString("The King", 880, 380);
                        g2d.drawString("is in Check", 870, 420);
                    }
                } else {
                    g2d.drawString("BLACK TURN", 850, 250);
                    if( checkingP != null && checkingP.color == WHITE){
                        g2d.setColor(Color.RED);
                        g2d.drawString("The King", 850, 380);
                        g2d.drawString("is in Check", 850, 420);
                    }
                }
            }
        }
    }
}
