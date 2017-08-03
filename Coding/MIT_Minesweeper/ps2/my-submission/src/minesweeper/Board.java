/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;
import java.util.*;


/**
 * Board represents a rectangular Minesweeper board of dimensions width x height
 */
public class Board {
    private final int width;
    private final int length;
    private static final int DEFAULT_SIZE = 10;
    private final List<Square> board = new ArrayList<>();
    private int players = 0;
    
    //Rep invariant:
    //  width and length are > 0
    //  players >= 0 
    //  a square returned by getSquare(x,y)
    //  must have a number of neighbours as follows:
    //  - 3 if x = 0 or width-1 and y = 0 or length-1
    //  - 5 if x = 0 or width-1 or y = 0 or length-1
    //  - 8, otherwise 
    // 
    //  the bomb counts of each square is
    //  the sum of the number of its 
    //  neighbours which contain bombs - I think this is 
    //  something that is tested by the addNeighbour
    //  method and should not be regarded as a rep invariant
    //
    //Abstraction function:
    //  represents a minesweeper board 
    //  of dimensions width x length 
    //
    //Safety from rep exposure
    //  All fields are private
    //  width and length are immutable
    //  board contains mutable Squares
    //  but these created within
    //  and only accessible via the
    //  board object as no references 
    //  are leaked to them.
    //  Also after construction
    //  the references to the Squares,
    //  cannot be modified as there are
    //  no mutator methods that 
    //  modify the list itself 
    //  rather than its elements 
    //
    //Thread safety argument
    //  The class itself is mutable. However all the
    //  accesses to mutators are guarded by Board's lock
    //  
    //  The mutators that modify the Squares call and return
    //  the result of the Board's toString observer so that 
    //  the result of the observer is available to the thread
    //  before any further mutation by other threads
    //
    //  The utility functions addAndGetPlayer and digAndDelete
    //  also offer similar functionality. The composite
    //  digAndDelete function enables the atomic execution
    //  of the dig command in addition to the deletion of a player
    //  should a bomb be uncovered. 
    //
    //  width and length are final and immutable
    //  so getWidth and getLength are not guarded 
    //  by Board's lock 
    // 
    //  The Squares in the board list are mutable 
    //  but all accesses to them happen 
    //  within Board methods which are guarded by 
    //  Board's lock
    //
    //  The Square class is not itself threadsafe
    //  but the Squares in the list are initialised
    //  from within the Board and are never exposed
    //  to a client. Since they are only accessed
    //  via methods guarded by Board's lock, they 
    //  are not reachable from any other thread
    //  than the one which owns Board's lock 
    //
    //  The private methods are not guarded by the
    //  locks but they are only accessed from within
    //  the guarded methods so they are not 
    //  accessible to any thread than the one
    //  which owns the lock
    
    /**
     * Clients may synchronize with each other using the Board object itself 
     */
    public Board(){
            this.width = DEFAULT_SIZE;
            this.length = DEFAULT_SIZE;
            this.addSquares();
            this.addNeighbours();
            checkRep();
    
    }
    
    /**
     * Makes a deep copy of an unplayed Board object
     * @param other a Board object which must be compatible with
     * the start state of a game specifically
     * all of its squares must be in the UNTOUCHED state
     */
    public Board(Board other){
        this.width = other.getWidth();
        this.length = other.getLength();
        List<Boolean> bombs = new ArrayList<>();
        for(Square square:other.board){
            assert square.getState() == SquareState.UNTOUCHED;
            bombs.add(square.hasBomb());
        }
        this.addSquares(bombs);
        this.addNeighbours();
        checkRep();
    }
    
    /**
     * Constructs a Board object with specified width and length
     * and randomly places bombs across its squares
     * @param width desired width of board must be > 0
     * @param length desired length of board must be > 0
     */
    public Board(int width, int length){
            assert width > 0;
            assert length > 0;
            this.width = width;
            this.length = length;
            this.addSquares();
            this.addNeighbours();
            checkRep();
        
    }

    
    /**
     * Constructs a Board object with specified width and length
     * and squares with bomb statuses according to list of bomb statuses for each square
     * with list elements mapped to grid positions in row major order 
     * @param width desired width of board must be > 0
     * @param length desired length of board must be > 0
     * @param bombs list of bomb status for each square must contain width*length elements
     */
    public Board(int width, int length, List<Boolean> bombs){
        assert width >0;
        assert length >0;
        assert bombs.size() == width*length;
        
        this.width = width;
        this.length = length;
        this.addSquares(bombs);
        this.addNeighbours();
        checkRep();
    }
    
    private void addSquares(){
            Random random = new Random();
            double sum = 0;
            for(int j = 0; j < this.length; j++){
                for(int i = 0; i < this.width; i++){
                    double num = random.nextDouble();
                    this.board.add(new Square(num < 0.25,i,j));
                    sum = sum + ((num < 0.25)?1:0);
                    
                }
            }
        
    }
    
    private void addSquares(List<Boolean> bombs){

            for(int j = 0; j < this.length; j++){
                for(int i = 0; i < this.width; i++){
                    boolean bomb = bombs.get(this.getSquareIndex(i,j));
                    this.board.add(new Square(bomb,i,j));
                }
            }
        
    }
    
    private void addNeighbours(){

            for(Square square: board){
                int x = square.getX();
                int y = square.getY();
                {

                    for(int di = -1; di < 2; di++){
                        for(int dj = -1; dj < 2; dj++){
                            int ii = x + di;
                            int jj = y + dj; 
                            if(!((ii==x) & (jj==y)) & withinBounds(ii,jj)) 
                                {square.addNeighbour(this.getSquare(ii, jj));
                                
                                }
                        }
                    }
                }
            }
        
    }
    
    private void checkRep(){
        assert this.width > 0;
        assert this.length > 0;
        assert this.players >= 0;
        for(Square square: board){
            
            int x = square.getX();
            int y = square.getY();
            
            
            int endX = this.width-1;
            int endY = this.length-1;
            boolean atRowEnd = (x==0)||(x==endX);
            boolean atColmEnd = (y==0)||(y==endY);
            //Corner
            
            if(atColmEnd & atRowEnd){
                if(this.getWidth()==1&this.getLength()==1) assert square.getNeighbours().size()==0;
                else if(this.getWidth()==1||this.getLength()==1) assert square.getNeighbours().size()==1;
                else assert square.getNeighbours().size()==3;
            }
            //Edge
            else if(atColmEnd || atRowEnd){
                if(this.getWidth()==1||this.getLength()==1) assert square.getNeighbours().size()==2;
                else assert square.getNeighbours().size()==5;
            }
            //Middle
            else{
                assert square.getNeighbours().size()==8;
            }
        }
    }
    
    
    /**
     * Adds a new player to the game
     */
    public void addPlayer(){
        synchronized(this){
            this.players++;
            checkRep();
        }
    }
    
    /**
     * 
     * @return number of players playing the game
     */
    public int getNumPlayers(){
        synchronized(this){
            return this.players;
        }
    }
    
    
    /**
     * A compound method
     * that updates the number of players and returns the updated number
     * of players playing the game
     * @return the number of players in the game
     */
    public int addAndGetPlayers(){
        synchronized(this){
            this.addPlayer();
            return this.getNumPlayers();
        }
    }
    
    /**
     * Requires that players > 0
     * Deletes a player from the game
     */
    public void deletePlayer(){
        synchronized(this){
            this.players--;
            checkRep();
        }
    }
    
    
    /**
     * A compound method that calls the dig method with the 
     * effect that if the board is modified
     * and a bomb is found, the number of players is reduced by one
     * since the player who dug that square can no longer play
     * @param x - the x-coordinate of the square to be modified
     * @param y - the y-coordinate of the square to be modified
     * @return BoardState representing result of modification
     */
    public MinesweeperMessage digAndDelete(int x, int y){
        synchronized(this){
            MinesweeperMessage mm = this.dig(x, y);
            if(mm.getIndicator()){
                this.deletePlayer();
            }
            return mm;
        }
    }
    
    
    
    /**
     * 
     * @return integer representing width of board
     */
    public int getWidth(){
        return this.width;
    }
    
    /**
     * 
     * @return integer representing length of board
     */
    public int getLength(){
        return this.length;
    }
    

    /**
     * Deals with a DIG request, with the effect that 
     * if x and y represent a valid board position, the square at that position
     * is modified in accordance with the protocol, otherwise has no effect
     * @param x - the x-coordinate of the square to be modified
     * @param y - the y-coordinate of the square to be modified
     * @return BoardState representing result of modification
     */
    public MinesweeperMessage dig(int x, int y){
        synchronized(this){
            return modify(x,y,BoardModification.DIG);
        }
    }
    
    /**
     * Executes the FLAG part of the Minesweeper protocol with the effect that
     * the square with coordinates x and y is modified in accordance with the protocol
     * if x and y represent a valid board position, otherwise has no effect
     * @param x - the x-coordinate of the square to be modified
     * @param y - the y-coordinate of the square to be modified
     * @return BoardState representing result of modification
     */
    public MinesweeperMessage flag(int x, int y){
        synchronized (this) {
            return modify(x,y,BoardModification.FLAG);
        }
    }
    
    /**
     * Executes the DEFLAG part of the Minesweeper protocol with the effect that
     * the square with coordinates x and y is modified in accordance with the protocol
     * if x and y represent a valid board position, otherwise has no effect
     * @param x - the x-coordinate of the square to be modified
     * @param y - the y-coordinate of the square to be modified
     * @return BoardState representing result of modification
     */
    public MinesweeperMessage deflag(int x, int y){
        synchronized (this) {
            return modify(x,y,BoardModification.DEFLAG);
        }
    }
    
    
    /**
     * Executes the modification specified by the enum mod with the effect that
     * the square with coordinates x and y is modified in accordance with the protocol
     * if x and y represent a valid board position, otherwise has no effect on the grid,
     * returns a MinesweeperMessage with a string representation of the board a
     * and indication of whether a bomb was uncovered as a result of executing
     * the command
     * @param x - the x-coordinate of the square to be modified
     * @param y - the y-coordinate of the square to be modified
     * @param mod - the type of modification to apply 
     * @return a MinesweeperMessage containing the string representation of the board
     * and indicating if a bomb was found
     */
    public MinesweeperMessage modify(int x, int y, BoardModification mod){
        synchronized (this) {
            if (this.withinBounds(x, y)) {
                
                Square square=this.getSquare(x, y);
                
                switch(mod){
                    case DIG: {
                        if(square.canDig()){
                            boolean bombFound = square.hasBomb();
                            square.dig(); 
                            return new MinesweeperMessage(this.toString(),bombFound);
                        }
                        return new MinesweeperMessage(this.toString());
                    }
                    case FLAG: {
                        square.flag();
                        return new MinesweeperMessage(this.toString());
                    }
                    case DEFLAG: {
                        square.deflag();
                        return new MinesweeperMessage(this.toString());
                    }
                //default needed?
                }
            }
            return new MinesweeperMessage(this.toString());
        }

    }
    
    
    /**
     * 
     * @param x - the x-coordinate of the square to be modified, must be >= 0 and < width
     * @param y - the y-coordinate of the square to be modified, must be >= 0 and < length
     * @return square with the coordinates x and y
     */
    private Square getSquare(int x, int y){
        assert (x >= 0) & (x < this.width);
        assert (y >= 0) & (y < this.length);
        return this.board.get(this.getSquareIndex(x, y));
    }
    
    
    /**
     * Determines if a pair of coordinates is represented
     * by the board 
     * @param x - represents an x-coordinate 
     * @param y - represents a y-coordinate 
     * @return true if the x and y correspond to an
     * index in the board list, otherwise false 
     */
    private boolean withinBounds(int x, int y){
        return (x >= 0) & (x < this.width) & (y >= 0) & (y < this.length);
    }
    
    /**
     * 
     * @param x - the x-coordinate of the square to be modified, must be >= 0 and < width
     * @param y - the y-coordinate of the square to be modified, must be >= 0 and < length
     * @return the index of the board list which contains the square with coordinates x and y
     */ 
    private int getSquareIndex(int x, int y){
        return this.width*y + x;
    }
    
    /**
     * @return a string representation of the present state of the board
     * in accordance with the protocol
     */
    @Override
    public String toString(){
        synchronized (this) {
            String boardString = "";
            for(Square square: board){
                boardString+=square.toString() 
                        + ((((square.getX()+1) %  width)>0) ? " " : "\n");
            }
            return boardString;
        }
    }
    
}
