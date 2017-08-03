package minesweeper.server;

import minesweeper.*;


/**
 * Executes the MinesweeperProtocol for a single player 
 */
public class MinesweeperProtocol {
	//Thread safety
	//	This class is not threadsafe because it contains a mutable board,
    //  which can be mutated through the handleInput method
    //  Since Board  itself is threadsafe, it may only be mutated by one thread at a time
    //  However the mutator is a public method so by itself the class cannot guarantee
    //  that the single player abstraction  will be maintained
    //
    //Rep invariant 
    //  handleStart is only ever called once 
    //  Provided the specifications are followed, neither handleStart nor handleInput
    //  ever throw an exception
    //
    //Abstraction function
    //  Represents a handler for dealing with single player's commands
    //  according to the Minesweeper Protocol
    //
    //Safety from rep exposure
    // All fields are private and final
    // The debug field is an immutable boolean 
    // The board field is necessarily mutable but no references are leaked to it
    // by the object itself and it may only be mutated via handleInput
    // Data related to the board are returned as immutable Strings and booleans
    // within the immutable MinesweeperMessage class 

	private final Board board;
	private final boolean debug;
	private final static String HELP = "https://en.wikipedia.org/wiki/Minesweeper_(video_game)";
	private final static String BOOM = "BOOM!";
	private final static String WELCOME = "Welcome to Minesweeper. "
			+ "Players: %d including you. Board: %d columns by %d rows. Type 'help' for help.";

	private boolean started = false;
	
	/**
	 * 
	 * @param board instance of Board shared by all the players playing the game 
	 * @param debug boolean indicating the mode of play - specifically if debug is true,
	 * then the game will not terminate if the player uncovers a bomb
	 */
	public MinesweeperProtocol(Board board,boolean debug){
		this.board = board;
		this.debug = debug;
	}
	
	/**
	 * Adds a new player to board and returns a welcome message 
	 * Requires that hasStarted() returns false
	 * @return returns the welcome message formatted with the board dimensions and the number of players
	 */
	public String handleStart(){
	    if(!started){
	        started = true;
	        return String.format(WELCOME, board.addAndGetPlayers(),board.getWidth(),board.getLength());
	    }
	    //Must never get here
	    throw new UnsupportedOperationException();
	}
	
	
	/**
	 * @return true if handleStart has been called else false
	 */
	public boolean hasStarted(){
	    return this.started;
	}
	
	/**
	 * Processes the client input
	 * 
	 * make requested mutations on game state if applicable, 
	 * in accordance with the protocol
	 * then return appropriate message
	 * 
	 * if the message or game state resulting from execution of command
	 * demands termination of the client connection,
	 * modifies internal state so that toTerminate returns true 
	 * 
	 * @param input
	 * @return a string containing either a representation of the board
	 * or another message, according to the protocol 
	 */
	public MinesweeperMessage handleInput(String input) {

		String regex = "(look)|(dig \\d+ \\d+)|(flag \\d+ \\d+)|(deflag \\d+ \\d+)|(help)|(bye)";
		if(!input.matches(regex)) {
			//invalid input
		    return new MinesweeperMessage(HELP);
		}
		String[] tokens = input.split(" ");
		if(tokens[0].equals("look")) {
			// 'look' request
			return new MinesweeperMessage(board.toString());
		} else if(tokens[0].equals("help")) {
			// 'help' request
			return new MinesweeperMessage(HELP);
		} else if(tokens[0].equals("bye")) {
			// 'bye' request
			board.deletePlayer();
			return new MinesweeperMessage(true);
		} else {
			int x = Integer.parseInt(tokens[1]);
			int y = Integer.parseInt(tokens[2]);
			if(tokens[0].equals("dig")) {
				// 'dig x y' request
				MinesweeperMessage mm = debug?board.dig(x, y):board.digAndDelete(x, y);
				boolean bomb = mm.getIndicator();
				return new MinesweeperMessage(bomb?BOOM:mm.getMessageString(),
						debug?false:bomb);
			} else if(tokens[0].equals("flag")) {
				// 'flag x y' request
				return board.flag(x, y);
			} else if(tokens[0].equals("deflag")) {
				// 'deflag x y' request
				return board.deflag(x, y);
			}
		}
		//should never get here
		throw new UnsupportedOperationException();
	}

}
