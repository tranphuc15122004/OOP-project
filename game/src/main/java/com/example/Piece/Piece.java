package com.example.Piece;

import com.example.Game.Board;
import com.example.Game.GamePvP;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Piece {
    public Image image;
    public int x , y , row , col , preCol , preRow;
    public int color;
    public Piece affectedP; // the piece that is overridden by the current piece while moving in player's thinking phase
    public boolean isMoved , _2squareMove;

    public Piece(int color , int row , int col){
        this.color = color;
        this.row = row;
        this.col = col;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public Image getImage(String path){
        Image img = null;
        try {
            img = new Image(getClass().getResourceAsStream(path + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    public void draw(GraphicsContext gc){
        gc.setImageSmoothing(true);
        gc.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
    }

    public int getX(int col){
        return col * Board.SQUARE_SIZE;
    }
    public int getY(int row){
        return row * Board.SQUARE_SIZE;
    }
    public int getCol(int x){
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public int getRow(int y){
        return (y +Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }
    public Piece getaffectedP(int targetRow, int targetCol){
        for(Piece p: GamePvP.simPieces){
            if(p.row == targetRow && p.col == targetCol && p != this){
                return p;
            }
        }
        return null;
    }
    public int getIndex(){
        return GamePvP.simPieces.indexOf(this);
    }
    public void updatePosition(){
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        isMoved = true;
    }
    public void resetPosition(){
        x = getX(preCol);
        y = getY(preRow);
        col = preCol;
        row = preRow;
    }
    
    
    // Condition for the piece to move
    // it is overridden by every subclasses
    public boolean canMove(int targetRow , int targetCol){
        return false;
    }
    
    public boolean isWithinBoard(int targetRow , int targetCol){
        return targetRow >= 0 && targetRow < Board.MAX_ROW && targetCol >= 0 && targetCol < Board.MAX_COL;
    }
    
    // Check if the target square is occupied by a piece of the same color or the opposite color
    // TRUE if it is valid
    // FALSE if it is not valid
    public boolean isValidSquare(int targetRow, int targetCol){
        affectedP = getaffectedP(targetRow, targetCol);
        if(affectedP == null){
            return true;
        }else{
            if(affectedP.color != this.color){
                return true;
            }
            else affectedP = null;
        }
        return false;
    }
    
    // Check if the target square is the same as the current position
    // TRUE if it is the same
    // FALSE if it is not the same
    public boolean isSamePosition(int targetRow, int targetCol){
        return targetRow == preRow && targetCol == preCol;
    }

    // Limit the horizontal and vertical movement of the piece
    // The piece cannot move if there is a piece in the way
    // The affectedP is the nearest piece that is in the way
    public boolean limitMovement_straight(int targetRow, int targetCol){
        // check left movement
        for(int c= preCol - 1; c >targetCol;c--){
            for(Piece p: GamePvP.simPieces){
                if(p.row == targetRow && p.col == c){
                    affectedP = p;
                    return false;
                }
            }
        }
        // check right movement
        for(int c= preCol + 1; c <targetCol;c++){
            for(Piece p: GamePvP.simPieces){
                if(p.row == targetRow && p.col == c){
                    affectedP = p;
                    return false ;
                }
            }
        }
        // check up movement
        for(int r= preRow - 1; r >targetRow;r--){
            for(Piece p: GamePvP.simPieces){
                if(p.col == targetCol && p.row == r){
                    affectedP = p;
                    return false;
                }
            }
        }
        // check down movement
        for(int r= preRow + 1; r <targetRow;r++){
            for(Piece p: GamePvP.simPieces){
                if(p.col == targetCol && p.row == r){
                    affectedP = p;
                    return false;
                }
            }
        }
        return true;
    }

    // Limit the diagonal movement of the piece
    // The piece cannot move if there is a piece in the way
    // The affectedP is the nearest piece that is in the way
    public boolean limitMovement_diagonal(int targetRow, int targetCol){
        for(int i = 1; i < Math.abs(this.preRow - targetRow); i++){
            // check down-right movement
            if(this.preRow < targetRow && this.preCol < targetCol){
                for(Piece p: GamePvP.simPieces){
                    if(p.row == this.preRow + i && p.col == this.preCol + i){
                        affectedP = p;
                        return false;
                    }
                }
            }
            // check down-left movement
            if(this.preRow < targetRow && this.preCol > targetCol){
                for(Piece p: GamePvP.simPieces){
                    if(p.row == this.preRow + i && p.col == this.preCol - i){
                        affectedP = p;
                        return false;
                    }
                }
            }
            // check up-right movement
            if(this.preRow > targetRow && this.preCol < targetCol){
                for(Piece p: GamePvP.simPieces){
                    if(p.row == this.preRow - i && p.col == this.preCol + i){
                        affectedP = p;
                        return false;
                    }
                }
            }
            // check up-left movement
            if(this.preRow > targetRow && this.preCol > targetCol){
                for(Piece p: GamePvP.simPieces){
                    if(p.row == this.preRow - i && p.col == this.preCol - i){
                        affectedP = p;
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
