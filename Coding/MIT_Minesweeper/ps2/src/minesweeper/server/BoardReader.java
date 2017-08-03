package minesweeper.server;
import java.util.*;
import java.io.*;

public class BoardReader{
	
	private final BufferedReader in;
	private int colms;
	private int rows;
	private List<Boolean> bombs = new ArrayList<>();
	
	
	/**
	 * Make a BoardReader which is a class that can be used to read data 
	 * needed to construct a Board object, from an input stream accessed via BufferedReader
	 * 
	 * Requires that each line of the input stream is equivalent to
	 * the corresponding line in a board file that follows the 
	 * Minesweeper protocol 
	 * 
	 * @param in BufferedReader providing an input stream whose
	 * lines contain data needed to construct a board object
	 */
	public BoardReader(BufferedReader in){
		this.in = in;
	}
	
	/**
	 * Calls functions to read data from a file that has the required format
	 * @throws IOException
	 */
	public void read() throws IOException {
		readDims();
		readRows();
	}
	
	/**
	 * Reads the board dimensions from a file in the appropriate format
	 * into the fields colms and rows 
	 * @throws IOException
	 */
	private void readDims() throws IOException{
    	String firstLine = in.readLine();
    	String firstLineRegex = "\\d+ \\d+";
    	if(!(firstLine.matches(firstLineRegex))){
    		throw new RuntimeException();
    	}
		Scanner firstLineScanner =  new Scanner(firstLine);
		colms = firstLineScanner.nextInt();
		rows =  firstLineScanner.nextInt();
		firstLineScanner.close();
		if(!(colms >0 & rows >0)){
			throw new RuntimeException();
		}
		
	}
	
	/**
	 * Reads the bomb positions from a file in the appropriate format
	 * into the field bombs
	 * @throws IOException
	 */
	private void readRows() throws IOException{
		String rowLine;
		String rowLineRegex = String.format("((0|1) ){%d}(0|1)",colms-1);
		
		while((rowLine = in.readLine()) != null){
			if(!(rowLine.matches(rowLineRegex))){
				throw new RuntimeException();
			}
			Scanner rowScanner = new Scanner(rowLine);
			while(rowScanner.hasNext()){
				bombs.add(rowScanner.nextInt() > 0);
			}
			rowScanner.close();
		}
		
		if(bombs.size() != (colms*rows)){
			throw new RuntimeException();
		}
	}
	
	
	/**
	 * 
	 * @return the number of board columns, according to the file
	 */
	public int getColms(){
		return colms;
	}
	
	/**
	 * 
	 * @return the number of board columns, according to the file
	 */
	public int getRows(){
		return rows;
	}
	
	/**
	 * 
	 * @return a list whose i-th element indicates whether 
	 * position x,y in the board contains bomb
	 * where x = floor(i/colms) and y = i%colms
	 * 
	 */
	public List<Boolean> getBombs(){
		return new ArrayList<>(bombs);
	}
	
	
}
