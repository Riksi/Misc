package minesweeper;


/**
 * MinesweeperMessage is container class for passing a combination of string and boolean messages
 */
public class MinesweeperMessage {
	//This class is thread-safe because it is immutable
	//- message and indicator are final
	//- both fields point to immutable String and boolean types
	
	private final boolean indicator;
	private final String message;
	private static final boolean FALSE = false;
	private static final String EMPTY = "";
	
	public MinesweeperMessage(String message){
		this.indicator=FALSE;
		this.message=message;
	}
	
	public MinesweeperMessage(boolean indicator){
		this.indicator=indicator;
		this.message=EMPTY;
	}
	
	public MinesweeperMessage(String message,boolean indicator){
		this.indicator=indicator;
		this.message=message;
	}
	
	public boolean getIndicator(){
		return indicator;
	}
	
	public String getMessageString(){
		return message;
	}
}
