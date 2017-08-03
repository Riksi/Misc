package minesweeper;


public class BoardState {
	//This class is not thread-safe. 
	//However a new instance is
	//created each time it is used.
	//This occurs within the Board class
	//which is accessed in a thread-safe manner 
	//and it is entirely contained within a single thread
	//and is not reachable by any other thread
	
	private boolean bombFound = false;
	private String boardString;
	
	public void setBombFound(boolean bombFound){
		this.bombFound = bombFound;
	}
	
	public boolean getBombFound(){
		return bombFound;
	}
	
	public void setBoardString(String boardString){
		this.boardString = boardString;
	}
	
	public String getBoardString(){
		return boardString;
	}
}
