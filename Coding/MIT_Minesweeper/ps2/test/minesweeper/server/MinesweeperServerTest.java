/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import minesweeper.Board;
import minesweeper.MinesweeperMessage;

/**
 * TODO
 */
public class MinesweeperServerTest {
    
    String helpMessage = "https://en.wikipedia.org/wiki/Minesweeper_(video_game)\n";
    String boom = "BOOM!\n";
    

    public void testBoardReader(String urlString,int colms,int rows, List<Boolean> bombs) {
        URL url = ClassLoader.getSystemClassLoader().getResource(urlString);
        try(BufferedReader in = new BufferedReader(
                new FileReader(new File(url.toURI()).getAbsolutePath())
                );
                )
        {
            
            BoardReader br = new BoardReader(in);
            br.read();
            assertEquals(colms,br.getColms());
            assertEquals(rows,br.getRows());
            assertEquals(bombs,br.getBombs());

        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        catch (URISyntaxException urise){
            urise.printStackTrace();
        }
        
    }
    
    
    @Test
    public void testBoardReaderSeveral(){
        //Square, long and wide boards
        
        Boolean[] bombsSq =     
                  {true,false,false,
                   false,true,false,
                   false,false,true};   
        testBoardReader("board_square",3,3,
                Arrays.asList(bombsSq));
        
        Boolean[] bombsLg =     
                   {true,false,
                   false,true,
                   false,false,};   
        testBoardReader("board_long",2,3,
                Arrays.asList(bombsLg));
        
        Boolean[] bombsWd =     
                   {true,false,false,
                   false,true,false};   
        testBoardReader("board_wide",3,2,
                Arrays.asList(bombsWd));
    }
    
    @Test
    public void testHandleConnection(){
        Boolean[] bombs = {true,true,false,false,
                           false,true,false,false,
                           false,false,false,false,
                           false,false,false,true,};
        List<Boolean> bombStatuses = Arrays.asList(bombs);
        
        Board b = new Board(4,4,bombStatuses);
        b.addPlayer();
        MinesweeperProtocol mp = new MinesweeperProtocol(b,true);
        MinesweeperServerThread mst = new MinesweeperServerThread(new Socket(),mp);
        String[] commands =  {"look\n","flag 2 3\n","deflag 0 3\n","dig 0 1\n","dig 0 0\n"};
        
        for(String command:commands){
        try(
                    ByteArrayInputStream inBytes = new ByteArrayInputStream(command.getBytes());
                    ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(inBytes));
                    // write characters to temporary storage
                    PrintWriter out = new PrintWriter(outBytes, true);
                ){
                mst.handleConnection(in, out);
                assertEquals("Empty input", null,in.readLine());
                // check that it wrote the expected output
                String result = (command == commands[commands.length-1])?boom:b.toString();
                assertEquals("Expected output",result,outBytes.toString());//
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
        

        
        try(
                ByteArrayInputStream inBytes = new ByteArrayInputStream("help\n".getBytes());
                ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inBytes));
                // write characters to temporary storage
                PrintWriter out = new PrintWriter(outBytes, true);
            ){
            mst.handleConnection(in, out);
            assertEquals("Empty input", null,in.readLine());
            // check that it wrote the expected output
            assertEquals("Expected output",helpMessage,outBytes.toString());
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        
        try(
                ByteArrayInputStream inBytes = new ByteArrayInputStream("dig 1 \n".getBytes());
                ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inBytes));
                // write characters to temporary storage
                PrintWriter out = new PrintWriter(outBytes, true);
            ){
            mst.handleConnection(in, out);
            assertEquals("Empty input", null,in.readLine());
            // check that it wrote the expected output
            assertEquals("Expected help for invalid input",
                    helpMessage,outBytes.toString());
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        
        b.addPlayer();
        
        try(
                ByteArrayInputStream inBytes = new ByteArrayInputStream("look\nbye".getBytes());
                ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inBytes));
                // write characters to temporary storage
                PrintWriter out = new PrintWriter(outBytes, true);
            ){
            mst.handleConnection(in, out);
            assertEquals("Empty input", null,in.readLine());
            // check that it wrote the expected output
            assertEquals("Expected output is BOARD for look and nothing for bye",
                    b.toString(),outBytes.toString());
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        
        b.addPlayer();
        
        try(
                ByteArrayInputStream inBytes = new ByteArrayInputStream("bye".getBytes());
                ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inBytes));
                // write characters to temporary storage
                PrintWriter out = new PrintWriter(outBytes, true);
            ){
            mst.handleConnection(in, out);
            assertEquals("Empty input", null,in.readLine());
            // check that it wrote the expected output
            assertEquals("No output for just bye",
                    0,outBytes.size());
            
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        
    }
    
    @Test
    public void testMSPDigFlag(){
        
        //If square is flagged can't be dug whether or not there is a bomb in it
        
        Boolean[] bombs = {true,false,false,
                            false,true,false,
                             false,false,true,};
        List<Boolean> bombStatuses = Arrays.asList(bombs);
        
        Board b = new Board(3,3,bombStatuses);
        MinesweeperProtocol mp = new MinesweeperProtocol(b,true);
        
        MinesweeperMessage ms1 = mp.handleInput("flag 1 0");
        assertEquals("- F -\n"
                +    "- - -\n"
                +    "- - -\n",ms1.getMessageString());
        assertEquals(false,ms1.getIndicator());
        
        MinesweeperMessage ms2 = mp.handleInput("flag 1 1");
        assertEquals("- F -\n"
                +    "- F -\n"
                +    "- - -\n",ms2.getMessageString());
        assertEquals(false,ms1.getIndicator());
        
        MinesweeperMessage ms3 = mp.handleInput("dig 1 0");
        assertEquals("- F -\n"
                +    "- F -\n"
                +    "- - -\n",ms3.getMessageString());
        assertEquals(false,ms1.getIndicator());
        
        MinesweeperMessage ms4 = mp.handleInput("dig 1 1");
        assertEquals("- F -\n"
                +    "- F -\n"
                +    "- - -\n",ms4.getMessageString());
        assertEquals(false,ms1.getIndicator());
        
    }
    
    @Test
    public void testMSPStart(){
        Boolean[] bombs = {true,true,false,false,
                false,true,false,false,
                false,false,false,false,
                false,false,false,true,};
        List<Boolean> bombStatuses = Arrays.asList(bombs);
        
        Board b = new Board(4,4,bombStatuses);
        
        MinesweeperProtocol mp = new MinesweeperProtocol(b,true);

        assertEquals(false,mp.hasStarted());
        String s1 = mp.handleStart();
        assertEquals("Welcome to Minesweeper. "
            + "Players: 1 including you. Board: 4 columns by 4 rows. Type 'help' for help.",s1);
        assertEquals(true,mp.hasStarted());
        
    }
    
    @Test
    public void testMinesweeperServerProtocol(){
        String boom = this.boom.substring(0, this.boom.length()-1);
        String helpMessage = this.helpMessage.substring(0, this.helpMessage.length()-1);
        //One or both coordinates invalid - no change
        //Valid coordinates, invalid command (dig flagged, flag flagged, flag dug, deflag dug, deflag untouched) - no change
        //Valid coordinates, valid command (dig untouched with and without bomb, dig untouched neighbours with and without bombs, 
        //flag untouched, deflag flagged) 
        //Get indicator should return true for bye command with debug = true
        //Get indicator should return true for bye command and dig with bomb with debug = false
        //Number of players in board should have decreased when debug is on and bomb is uncovered
        
        
        //DEBUG TRUE
        Boolean[] bombs = {true,true,false,false,
                           false,true,false,false,
                           false,false,false,false,
                           false,false,false,true,};
        List<Boolean> bombStatuses = Arrays.asList(bombs);
        
        Board b = new Board(4,4,bombStatuses);
        
        b.addPlayer();
        
        
        MinesweeperProtocol mp = new MinesweeperProtocol(b,true);

        
        MinesweeperMessage ms1 = mp.handleInput("look");
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",ms1.getMessageString());
        assertEquals(false,ms1.getIndicator());
        
        
        //Invalid coordinates
        
        MinesweeperMessage ms2 = mp.handleInput("dig 1 9");
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",ms2.getMessageString());
        assertEquals(false,ms2.getIndicator());
        
        MinesweeperMessage ms3 = mp.handleInput("dig 9 1");
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",ms3.getMessageString());
        assertEquals(false,ms3.getIndicator());
        
        MinesweeperMessage ms4 = mp.handleInput("dig 9 9");
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",ms4.getMessageString());
        assertEquals(false,ms4.getIndicator());
        
        
        //Valid coordinates, valid command
        
        //dig untouched with bomb, neighbours with bombs
        assertEquals("Players before dig should be 1",1,b.getNumPlayers());
        MinesweeperMessage ms5 = mp.handleInput("dig 1 0");
        assertEquals(boom,ms5.getMessageString());
        assertEquals(false,ms5.getIndicator());
        assertEquals("Players after dig with bomb should be still 1 for debug",1,b.getNumPlayers());
        
        //dig untouched with bomb, neighbours with bombs change in neighbour bombs
        assertEquals("Players before dig should be 1",1,b.getNumPlayers());
        MinesweeperMessage ms6 = mp.handleInput("dig 0 0");
        assertEquals(boom,ms6.getMessageString());
        assertEquals(false,ms6.getIndicator());
        assertEquals("Players after dig with bomb should be still 1 for debug",1,b.getNumPlayers());
        
        //dig untouched with bomb, neighbours without bombs 
        
        assertEquals("Players before dig should be 1",1,b.getNumPlayers());
        MinesweeperMessage ms7 = mp.handleInput("dig 3 3");
        assertEquals(boom,ms7.getMessageString());
        assertEquals(false,ms7.getIndicator());
        assertEquals(1,b.getNumPlayers());
        assertEquals("Players after dig with bomb should be still 1 for debug",1,b.getNumPlayers());
        
        //flag untouched
        assertEquals("Players before dig should be 1",1,b.getNumPlayers());
        MinesweeperMessage ms8 = mp.handleInput("flag 0 1");
        assertEquals("1 1 1  \n"
                +    "F - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",ms8.getMessageString());
        assertEquals(false,ms8.getIndicator());
        assertEquals("Players after dig with bomb should be still 1 for debug",1,b.getNumPlayers());
        
        
        //Invalid commands 
        //flag flagged
        MinesweeperMessage ms8a = mp.handleInput("flag 0 1");
        assertEquals("1 1 1  \n"
                +    "F - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",ms8a.getMessageString());
        assertEquals(false,ms8a.getIndicator());

        //dig flagged
        MinesweeperMessage ms8b = mp.handleInput("dig 0 1");
        assertEquals("1 1 1  \n"
                +    "F - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",ms8b.getMessageString());
        assertEquals(false,ms8b.getIndicator());
        
        
        //Back to valid commands
        //deflag flagged
        MinesweeperMessage ms9 = mp.handleInput("deflag 0 1");
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",ms9.getMessageString());
        assertEquals(false,ms9.getIndicator());
        
        //Invalid commands again
        
        //deflag untouched
        MinesweeperMessage ms9a = mp.handleInput("deflag 0 3");
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",ms9a.getMessageString());
        assertEquals(false,ms9a.getIndicator());
        
        //deflag dug
        MinesweeperMessage ms9b = mp.handleInput("deflag 0 0");
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",ms9b.getMessageString());
        assertEquals(false,ms9b.getIndicator());
        
        
        //flag dug
        MinesweeperMessage ms9c = mp.handleInput("flag 0 0");
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",ms9c.getMessageString());
        assertEquals(false,ms9c.getIndicator());
        
        
        //dig untouched no bomb
        MinesweeperMessage ms10a = mp.handleInput("dig 0 1");
        assertEquals("1 1 1  \n"
                +    "1 - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",ms10a.getMessageString());
        assertEquals(false,ms10a.getIndicator());
        
        
        
        MinesweeperMessage ms10 = mp.handleInput("help");
        assertEquals(helpMessage,ms10.getMessageString());
        assertEquals(false,ms10.getIndicator());
        

        
        
        
        //invalid messages could be >2 or < 2 numbers after commands, message not part of grammar, wrong case
        String[] invalidMessages = {"dig","flag 1","deflag 1 1 1","DIG 1 1","Flag 1 1","deFLAG 1 1", "INVALID"};
        for(String m:invalidMessages){
            MinesweeperMessage msInv1 = mp.handleInput(m);
            assertEquals(helpMessage,msInv1.getMessageString());
            assertEquals(false,msInv1.getIndicator());
        }
        
        
        
        assertEquals("Players before bye",1,b.getNumPlayers());
        MinesweeperMessage ms11 = mp.handleInput("bye");
        assertEquals("",ms11.getMessageString());
        assertEquals(true,ms11.getIndicator());
        assertEquals("Players after bye should have decreased by 1",0,b.getNumPlayers());

        //DEBUG FALSE
        Boolean[] bombs2 = {true,false,false,false,
                           false,true,false,false,
                           false,false,false,false,
                           false,false,false,true,};
        List<Boolean> bombStatuses2 = Arrays.asList(bombs2);

        Board b2 = new Board(4,4,bombStatuses2);
        b2.addPlayer();
        b2.addPlayer();
        
        MinesweeperProtocol mp2 = new MinesweeperProtocol(b2,false);
        
        //bomb uncovered
        assertEquals("Players before dig should be 2",2,b2.getNumPlayers());
        MinesweeperMessage ms55 = mp2.handleInput("dig 1 1");
        assertEquals(boom,ms55.getMessageString());
        assertEquals(true,ms55.getIndicator());
        assertEquals("Players after dig should have gone down by 1 for bomb uncovered",1,b2.getNumPlayers());
        
        
        //no bomb covered
        MinesweeperMessage ms56 = mp2.handleInput("dig 0 3");
        assertEquals("- 1    \n"
                +    "1 1    \n"
                +    "    1 1\n"
                +    "    1 -\n",ms56.getMessageString());
        assertEquals(false,ms56.getIndicator());
        
        
        //bye
        assertEquals("Players before bye",1,b2.getNumPlayers());
        MinesweeperMessage ms66 = mp2.handleInput("bye");
        assertEquals("",ms66.getMessageString());
        assertEquals(true,ms66.getIndicator());
        assertEquals("Players after bye should have decreased by 1",0,b.getNumPlayers());

    }
    
    
    
}
