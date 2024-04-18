package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Main.Board;
import Main.GamePanel;

public class Piece {
    public BufferedImage img;
    public int x, y;
    public int row, col, preRow, preCol;
    public int color;
    public Piece affectedP;
    public boolean isMoved = false;

    public Piece(int color, int row , int col){
        this.color = color;
        this.row = row;
        this.col = col;
        this.x = getX(col);
        this.y = getY(row);
        this.preRow = row;
        this.preCol = col;
    }

    // Get the image of the piece from piece_image folder
    public BufferedImage getImage(String path){
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getResourceAsStream( path  + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return img ;
    }

    // Get the x,y coordinate of the piece by using its column as parameter
    public int getX(int col){
        return col * Board.SQUARE_SIZE;
    }
    public int getY(int row){
        return row * Board.SQUARE_SIZE;
    }
    // The parameter passed in here is the coordinate of the mouse
    public int getRow(int y){
        return (y) / Board.SQUARE_SIZE;
    }
    // The parameter passed in here is the coordinate of the mouse
    public int getCol(int x){
        return (x ) / Board.SQUARE_SIZE;
    }

    // Update the position of the piece if the move is valid
    public void updatePosition(){
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y) ;
        isMoved = true;
    }

    // Return the piece to the previous position if the move is invalid
    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
        
    }

    // Check if the piece can move to the target square
    // It is implemented in the subclasses
    public boolean canMove(int moveRow, int moveCol){
        return false;
    }

    // Check if the move is within the board
    public boolean isWithinBoard(int row, int col){
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    // Check if the piece is in the target square
    public boolean isSameSquare(int row, int col){
        return (this.preRow == row && this.preCol == col);
    }

    //Limit the movement of the piece in the straight line
    // The piece can't jump over opponent's piece or its ally's piece
    public boolean limitMovement_straight(int targetRow, int targetCol){

        // check left movement
        for(int c= preCol - 1; c >targetCol;c--){
            for(Piece p: GamePanel.pieces){
                if(p.row == targetRow && p.col == c){
                    affectedP = p;
                    return false;
                }
            }
        }

        // check right movement
        for(int c= preCol + 1; c <targetCol;c++){
            for(Piece p: GamePanel.pieces){
                if(p.row == targetRow && p.col == c){
                    affectedP = p;
                    return false ;
                }
            }
        }

        // check up movement
        for(int r= preRow - 1; r >targetRow;r--){
            for(Piece p: GamePanel.pieces){
                if(p.col == targetCol && p.row == r){
                    affectedP = p;
                    return false;
                }
            }
        }

        // check down movement
        for(int r= preRow + 1; r <targetRow;r++){
            for(Piece p: GamePanel.pieces){
                if(p.col == targetCol && p.row == r){
                    affectedP = p;
                    return false;
                }
            }
        }
        return true;
    }

    //Limit the movement of the piece in the diagonal line
    // The piece can't jump over opponent's piece or its ally's piece
    public boolean limitMovement_diagonal(int targetRow, int targetCol){

        for(int i = 1; i < Math.abs(this.preRow - targetRow); i++){
            // check down-right movement
            if(this.preRow < targetRow && this.preCol < targetCol){
                for(Piece p: GamePanel.pieces){
                    if(p.row == this.preRow + i && p.col == this.preCol + i){
                        affectedP = p;
                        return false;
                    }
                }
            }

            // check down-left movement
            if(this.preRow < targetRow && this.preCol > targetCol){
                for(Piece p: GamePanel.pieces){
                    if(p.row == this.preRow + i && p.col == this.preCol - i){
                        affectedP = p;
                        return false;
                    }
                }
            }

            // check up-right movement
            if(this.preRow > targetRow && this.preCol < targetCol){
                for(Piece p: GamePanel.pieces){
                    if(p.row == this.preRow - i && p.col == this.preCol + i){
                        affectedP = p;
                        return false;
                    }
                }
            }

            // check up-left movement
            if(this.preRow > targetRow && this.preCol > targetCol){
                for(Piece p: GamePanel.pieces){
                    if(p.row == this.preRow - i && p.col == this.preCol - i){
                        affectedP = p;
                        return false;
                    }
                }
            }
        }

        return true;
    }

    //Get the index of the piece in the array
    public int getPieceIndex(ArrayList<Piece> arr){
        for(int i = 0; i < arr.size(); i++){
            if(arr.get(i) == this){
                return i;
            }
        }
        return -1;
    }

    // Get the piece that is affected by the move
    public Piece getAffectedPiece( int targetRow, int targetCol){
        for(Piece p: GamePanel.simPieces){
            if(p.row == targetRow && p.col == targetCol && p != this){
                return p;
            }
        }
        return null;
    }

    // Check the target square is valid or not
    // It is valid if the square is empty or the square is occupied by the opponent
    public boolean isValidSquare(int targetRow, int targetCol){
        affectedP = getAffectedPiece(targetRow, targetCol);

        if(affectedP == null){
            return true;
        }else{
            if(affectedP.color != this.color){
                return true;
            }else affectedP = null;
        }

        return false;
    }

    // Draw the piece
    public void draw(Graphics2D g2d) {
        g2d.drawImage(img, x, y,Board.SQUARE_SIZE, Board.SQUARE_SIZE,null);
    }
}
