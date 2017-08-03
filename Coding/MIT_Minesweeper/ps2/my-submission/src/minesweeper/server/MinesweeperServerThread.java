package minesweeper.server;

//TODO
//Make an interface for MinesweeperMessage which
//sometimes has a message string and sometimes has an indicator and sometimes both
//so you call a hasMessageString and hasIndicator first 

import java.net.*;

import minesweeper.MinesweeperMessage;

import java.io.*;

public class MinesweeperServerThread implements Runnable{
	private final Socket socket; 
	private final MinesweeperProtocol mp;
	
	/**
	 * @param socket which should be open
	 * @param mp an instance of MinesweeperProtocol which must be initialised with 
	 * a Board object shared by all the players playing the game  
	 */
	public MinesweeperServerThread(Socket socket, MinesweeperProtocol mp){
	    assert !socket.isClosed();
		this.socket = socket;
		this.mp = mp; 
		
	}
	
	/**
	 * Handles connection from a single client by reading from input stream in
	 * and writing to the output stream out 
	 * @param out output stream which should be open
	 * @param in input stream  which should be open
	 * @throws IOException
	 */
    public void handleConnection(BufferedReader in, PrintWriter out){
        // Method has been designed so that it can be tested without using a real socket 
        try {
        	
        	for (String line = in.readLine(); line != null; line = in.readLine()){
        		MinesweeperMessage output = mp.handleInput(line);
        		if(output != null){
	        		String messageString = output.getMessageString();
	        		
	        		if(messageString != "") {
	        			String[] mslines = messageString.split("\n");
	        			for(String msline:mslines){
	        				out.println(msline);
	        			}
	        			
	        		}
	        		
	        		if(output.getIndicator()){
	        			socket.close();
	        			break;
	        		}
        		}
        	}
        } catch (IOException e){
			e.printStackTrace();
		}
    }
	
	/**
	 * Handles the requests made by the client
	 */
	public void run(){
		try(
				PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(this.socket.getInputStream()));
			)
		{
			out.println(mp.handleStart());
			this.handleConnection(in, out);
			
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

}
