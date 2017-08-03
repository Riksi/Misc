/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * TODO: Description
 */
public class BoardTest {
    
    // TODO: Testing strategy: see detailed strategy under different tests
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testSquare(){
        //Newly created square should have bombCounts = 0
        //and be in state UNTOUCHED
        
        //Change in bombCounts after
        //Should increase/decrease by 1 each time
        //Should not decrease below zero 
        
        //Only UNTOUCHED squares should return true for canDig
        
        //TODO: test for exception
        
        //Modifications to state
        //{UNTOUCHED, DUG, FLAGGED} x {flag, deflag}
        //{UNTOUCHED, DUG, FLAGGED} x {bomb, no bomb} x {dig}
        //Can just repeat a lot of these tests for Board???
        
        
        Square s = new Square(false,0,0);
        assertEquals(s.getBombCounts(),0);
        assertEquals(s.getState(),SquareState.UNTOUCHED);
        assertEquals(s.hasBomb(),false);
        assertEquals(true,s.canDig());
        Square sbomb = new Square(true,0,0);
        assertEquals(sbomb.hasBomb(),true);
        assertEquals(true,sbomb.canDig());
        
        //Adding bombs
        //s.addBombCounts();
        //assertEquals(s.getBombCounts(), 1);
        //s.addBombCounts();
        //assertEquals(s.getBombCounts(), 2);
        
        //Flag untouched, flagged
        s.flag();
        assertEquals(s.getState(), SquareState.FLAGGED);
        assertEquals(false,s.canDig());
        s.flag();
        assertEquals(s.getState(), SquareState.FLAGGED);
        
        
        //Dig, flagged, no bomb, bomb
        s.dig();
        assertEquals(s.getState(),SquareState.FLAGGED);
        sbomb.flag();
        assertEquals(false,sbomb.canDig());
        sbomb.dig();
        assertEquals(sbomb.getState(),SquareState.FLAGGED);
        
        //Deflag flagged, untouched, no bomb, bomb
        s.deflag();
        assertEquals(s.getState(), SquareState.UNTOUCHED);
        assertEquals(true,s.canDig());
        s.deflag();
        assertEquals(s.getState(), SquareState.UNTOUCHED);
        
        sbomb.deflag();
        assertEquals(s.getState(), SquareState.UNTOUCHED);
        assertEquals(true,s.canDig());
        sbomb.deflag();
        assertEquals(s.getState(), SquareState.UNTOUCHED);
        
        //Dig, untouched no bomb, dug 
        s.dig();
        assertEquals(s.getState(),SquareState.DUG);
        s.dig();
        assertEquals(false,s.canDig());
        assertEquals(s.getState(),SquareState.DUG);
        
        //Dig, untouched, bomb
        sbomb.dig();
        assertEquals(sbomb.getState(),SquareState.DUG);
        sbomb.dig();
        assertEquals(false,sbomb.canDig());
        assertEquals(sbomb.getState(),SquareState.DUG);
        assertEquals(sbomb.hasBomb(), false);
        
        //Flag dug
        s.flag();
        assertEquals(s.getState(),SquareState.DUG);
        s.deflag();
        assertEquals(s.getState(),SquareState.DUG);
        
    }
    
    public void testSquareToString(){
        //Untouched
        //Flagged
        //Dug - no neighbour bombs
        //Dug - neighbour bombs
        //Dug - different value if number of neighbours
        //with bombs changes 
        
        Square w = new Square(true,0,0);
        Square x = new Square(false,0,1);
        Square y = new Square(true,0,2);
        
        w.addNeighbour(x);
        x.addNeighbour(y);
        
        assertEquals("-",x.toString());
        x.flag();
        assertEquals("F",x.toString());
        x.deflag();
        x.dig();
        assertEquals("2",x.toString());
        w.dig();
        
        //no bombs in neighbours
        assertEquals(" ",w.toString());
        
        //Goes down to 1, then when no bombs in neighbours, becomes " "
        assertEquals("1",x.toString());
        y.dig();
        assertEquals(" ",x.toString());
        
        
    }
    
    
    
    /*public void testNeighbSquares(){
        //If neighbour squares don't satisfy
        //all of the conditions that they 
        //are untouched, have no bomb,
        //and have zero bomb counts, then 
        //they are not dug
        //Partition into squares who satisfy
        //and don't satisfy condition
        //and assume among them the
        //the states of dug, flagged, with bomb,
        //without bomb, with bomb neighbours,
        //without bomb neighbours
        
        //|*1U|*1U|
        //| 2U| 2U|
        //| 0U| 0U|
        //Going left to right
        
        Square s1 = new Square(true);
        
        Square s2 = new Square(true);
        
        Square s3 = new Square(true);
        
        Square s4 = new Square(false);
        
        Square s5 = new Square(false);
        Square s6 = new Square(false);
        
        
        s1.addNeighbour(s2);
        s1.addNeighbour(s3);
        s1.addNeighbour(s4);
        
        s2.addNeighbour(s1);
        s2.addNeighbour(s3);
        s2.addNeighbour(s4);
        
        s4.addNeighbour(s1);
        s4.addNeighbour(s2);
        s4.addNeighbour(s3);
        s4.addNeighbour(s5);
        s4.addNeighbour(s6);
        
        s3.addNeighbour(s1);
        s3.addNeighbour(s2);
        s3.addNeighbour(s4);
        s3.addNeighbour(s5);
        s3.addNeighbour(s6);
        
        s5.addNeighbour(s3);
        s5.addNeighbour(s4);
        s5.addNeighbour(s6);
        
        s6.addNeighbour(s3);
        s6.addNeighbour(s5);
        s6.addNeighbour(s6);
        
        //Test that counts is sum of neighbours
        //with bombs
        assertEquals(s1.getBombCounts(),1);
        assertEquals(s2.getBombCounts(),1);
        assertEquals(s3.getBombCounts(),2);
        assertEquals(s4.getBombCounts(),2);
        assertEquals(s5.getBombCounts(),0);
        assertEquals(s6.getBombCounts(),0);
        
        s6.flag();
        s1.dig();
        
        //| 1D|*0U|
        //| 1U| 1U|
        //| 0U| 0F|
        //Going left to right
        
        //Won't worry about behaviour of initially dug square 
        //as we have already tested that
        
        assertEquals(s2.getBombCounts(),0);
        assertEquals(s2.getState(),SquareState.FLAGGED);
        
        assertEquals(s3.getBombCounts(),1);
        assertEquals(s3.getState(),SquareState.UNTOUCHED);
        
        assertEquals(s4.getBombCounts(),1);
        assertEquals(s4.getState(),SquareState.UNTOUCHED);
        
        assertEquals(s5.getBombCounts(),0);
        assertEquals(s5.getState(),SquareState.UNTOUCHED);
        
        assertEquals(s6.getBombCounts(),0);
        assertEquals(s6.getState(),SquareState.FLAGGED);
        
        //| 1D|*0U|
        //| 1U| 1U|
        //| 0U| 0F|
        
        s2.dig();
        
        
        //| 0D| 0D|
        //| 0D| 0D|
        //| 0D| 0F|
        assertEquals(s1.getBombCounts(),0);
        assertEquals(s1.getState(),SquareState.DUG);
        
        assertEquals(s3.getBombCounts(),0);
        assertEquals(s3.getState(),SquareState.DUG);
        
        assertEquals(s4.getBombCounts(),0);
        assertEquals(s4.getState(),SquareState.DUG);
        
        assertEquals(s5.getBombCounts(),0);
        assertEquals(s5.getState(),SquareState.DUG);
        
        assertEquals(s6.getBombCounts(),0);
        assertEquals(s6.getState(),SquareState.FLAGGED);
        
    }*/
    
    //I think that to test board - since the behaviour of the 
    //squares has already been tested, can just test the string
    //representation each time - having tested the toString
    //method first
    

    @Test
    public void testAddAndIsNeighbour(){
        Square s0 = new Square(false,0,0);
        Square s1 = new Square(true,0,1);
        assertEquals(s0.isNeighbour(s1),false);
        assertEquals(s1.isNeighbour(s0),false);
        s0.addNeighbour(s1);
        assertEquals(s0.isNeighbour(s1),true);
        assertEquals(s1.isNeighbour(s0),true);
    }
    

    
    @Test
    public void testNeighbourCounts(){
        Square s0 = new Square(true,0,0);
        assertEquals(s0.getBombCounts(),0);
        Square s1 = new Square(true,0,1);
        s0.addNeighbour(s1);
        assertEquals(s0.getBombCounts(),1);
        assertEquals(s1.getBombCounts(),1);
        Square s2 = new Square(true,0,2);
        s1.addNeighbour(s2);
        assertEquals(s1.getBombCounts(),2);
        assertEquals(s2.getBombCounts(),1);
        
        s1.dig();
        assertEquals(s0.getBombCounts(),0);
        assertEquals(s1.getBombCounts(),2);
        assertEquals(s2.getBombCounts(),0);
    }
    
    @Test
    public void testNeighbourState(){
        //Previously we ensured that counts of neighbours
        //decreased if a square had a bomb, now we ensure
        //that state is modified 
        
        //('u', '~b', '~n') -> ('d', '~b', '~n')
        Square s0 = new Square(false,0,0);
        Square s1 = new Square(false,0,1);
        s0.addNeighbour(s1);
        s0.dig();
        assertEquals(s1.getState(),SquareState.DUG);
        
        //('u', '~b', 'n') -> ('d', '~b', 'n')
        Square s2 = new Square(false,0,0);
        Square s3 = new Square(false,0,1);
        Square s3n = new Square(true,0,2);
        s2.addNeighbour(s3);
        s3.addNeighbour(s3n);
        s2.dig();
        assertEquals(s3.getState(),SquareState.DUG);
        
        //('u', 'b', '~n') -> ('u', 'b', '~n')
        Square s4 = new Square(false,0,0);
        Square s5 = new Square(true,0,1);
        s4.addNeighbour(s5);
        s4.dig();
        assertEquals(s5.getState(),SquareState.UNTOUCHED);
        
        //('u', 'b', 'n') -> ('u', 'b', 'n')
        Square s6 = new Square(false,0,0);
        Square s7 = new Square(true,0,1);
        Square s7n = new Square(true,0,2);
        s6.addNeighbour(s7);
        s7.addNeighbour(s7n);
        s6.dig();
        assertEquals(s7.getState(),SquareState.UNTOUCHED);
        
        
        //('f', '~b', '~n') -> ('f', '~b', '~n')
        Square s8 = new Square(false,0,0);
        Square s9 = new Square(false,0,1);
        s8.addNeighbour(s9);
        s9.flag();
        s8.dig();
        assertEquals(s9.getState(),SquareState.FLAGGED);
        //Now since we have a pair 
        //('d', '~b', '~n'),('f', '~b', '~n')
        //can deflag s9 and dig it and test 
        //('d', '~b', '~n') -> ('d', '~b', '~n')
        //for s8
        s9.deflag();
        s9.dig();
        assertEquals(s8.getState(),SquareState.DUG);
        
        //('f', '~b', 'n') -> ('f','~b','n')
        Square s10 = new Square(false,0,0);
        Square s11 = new Square(false,0,1);
        Square s11n = new Square(true,0,2);
        s10.addNeighbour(s11);
        s11.addNeighbour(s11n);
        s11.flag();
        s10.dig();
        assertEquals(s11.getState(),SquareState.FLAGGED);

        
        //('f', 'b', '~n') -> ('f', 'b', '~n') 
        Square s12 = new Square(false,0,0);
        Square s13 = new Square(true,0,1);
        s12.addNeighbour(s13);
        s13.flag();
        assertEquals(s13.getState(),SquareState.FLAGGED);
        //Now we have
        //('d', '~b', 'n'),('f', 'b', '~n')
        //so can unflag s13 and dig it and test 
        //('d', '~b', 'n') -> ('d', '~b', 'n')
        //for s12
        s13.deflag();
        s13.dig();
        assertEquals(s12.getState(),SquareState.DUG);
        
        //('f', '~b', '~n') -> ('f', '~b', '~n') 
        Square s14 = new Square(false,0,0);
        Square s15 = new Square(false,0,1);
        s14.addNeighbour(s15);
        s15.flag();
        s14.dig();
        assertEquals(s15.getState(),SquareState.FLAGGED);

    }
        
    @Test
    public void testBoard(){
        //Basically test the configurations as above
        //but calling the functions from the board class
        //Three versions of constructor
        //Call the ones where dimensions specified with x = y, x < y, x > y
        
        Board b1 = new Board();
        assertEquals(10,b1.getWidth());
        assertEquals(10,b1.getLength());
        
        //Before modification of b1 copy's observers should return the same results
        //Afterwards toString should be different
        Board b1Copy = new Board(b1);
        assertEquals(b1.getWidth(),b1Copy.getWidth());
        assertEquals(b1.getLength(),b1Copy.getLength());
        assertEquals(b1.toString(),b1Copy.toString());
        b1.dig(1, 1);
        assertEquals(false,b1.toString()==b1Copy.toString());
        
        Board b2 = new Board(7,7);
        assertEquals(7,b2.getWidth());
        assertEquals(7,b2.getLength());
        
        Board b3 = new Board(9,7);
        assertEquals(9,b3.getWidth());
        assertEquals(7,b3.getLength());
        
        Board b4 = new Board(7,9);
        assertEquals(7,b4.getWidth());
        assertEquals(9,b4.getLength());
        
        Boolean[] bombs = {true,false,false,false,
                   false,true,false,false,
                   false,false,true,false,
                   false,false,false,true,};
        List<Boolean> bombStatuses = Arrays.asList(bombs);

        Board b5 = new Board(4,4,bombStatuses);
        assertEquals(4,b5.getWidth());
        assertEquals(4,b5.getLength());
        
        Board b6 = new Board(2,8,bombStatuses);
        assertEquals(2,b6.getWidth());
        assertEquals(8,b6.getLength());
        
        Board b7 = new Board(8,2,bombStatuses);
        assertEquals(8,b7.getWidth());
        assertEquals(2,b7.getLength());
        
        Board point = new Board(1,1);
        assertEquals(1,point.getWidth());
        assertEquals(1,point.getLength());
        
        Board horzLine = new Board(1,9);
        assertEquals(1,horzLine.getWidth());
        assertEquals(9,horzLine.getLength());
        
        Board vertLine = new Board(9,1);
        assertEquals(9,vertLine.getWidth());
        assertEquals(1,vertLine.getLength());
        
        
        
        
    }
    
    @Test
    public void testBoardToString(){
        //Should reflect untouched, dug, flagged, bomb counts (including changes)
        Boolean[] bombs = {true,false,false,false,
                           false,true,false,false,
                           false,false,true,false,
                           false,false,false,true,};
        List<Boolean> bombStatuses = Arrays.asList(bombs);

        Board b = new Board(4,4,bombStatuses);
        
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
        b.flag(1,0);
        
        assertEquals("- F - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
        b.deflag(1,0);
        
        b.dig(1,0);
        
        assertEquals("- 2 - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
        b.dig(0,0);
        
        assertEquals("1 1 - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
        b.dig(3,0);
        
        assertEquals("1 1 1  \n"
                +    "- - 2 1\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
    }
    
    @Test
    public void testBoardModifications(){
        Boolean[] bombs = {true,false,false,false,
                   false,true,false,false,
                   false,false,true,false,
                   false,false,false,true,};
        List<Boolean> bombStatuses = Arrays.asList(bombs);
        
        Board b = new Board(4,4,bombStatuses);
        
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
        b.flag(1,0);
        
        assertEquals("- F - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
        b.deflag(1,0);
        
        b.dig(1,0);
        
        assertEquals("- 2 - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
        b.dig(0,0);
        
        assertEquals("1 1 - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
        
        b.dig(3,0);
        
        assertEquals("1 1 1  \n"
                +    "- - 2 1\n"
                +    "- - - -\n"
                +    "- - - -\n",b.toString());
    }
    
    
    @Test
    public void testBoardPlayerFunctions(){
        Board b = new Board();
        assertEquals(0,b.getNumPlayers());
        b.addPlayer();
        assertEquals(1,b.getNumPlayers());
        assertEquals(2,b.addAndGetPlayers());
        assertEquals(2,b.getNumPlayers());
    }
    
    
    @Test
    public void testMinesweeperMessage(){
        //The two types of constructor
         MinesweeperMessage bv1 = new MinesweeperMessage("- -\n- -\n");
         assertEquals("- -\n- -\n",bv1.getMessageString());
         assertEquals(false,bv1.getIndicator());
         
         MinesweeperMessage bv2f = new MinesweeperMessage("- F\n- -\n",false);
         assertEquals("- F\n- -\n",bv2f.getMessageString());
         assertEquals(false,bv2f.getIndicator());
         
         MinesweeperMessage bv2t = new MinesweeperMessage("-  \n- -\n",true);
         assertEquals("-  \n- -\n",bv2t.getMessageString());
         assertEquals(true,bv2t.getIndicator());
        
    }
    
    @Test
    public void testBoardModify(){
        //Have already tested the commands in the Square class
        //So we will partition the inputs as follows
        //One or both coordinates invalid - no change
        //Valid coordinates, invalid command (dig flagged, flag flagged, flag dug, deflag dug, deflag untouched) - no change
        //Valid coordinates, valid command (dig untouched with and without bomb, dig untouched neighbours with and without bombs, 
        //flag untouched, deflag flagged) 
        
        Boolean[] bombs = {true,true,false,false,
                           false,true,false,false,
                           false,false,false,false,
                           false,false,false,true,};
        List<Boolean> bombStatuses = Arrays.asList(bombs);
        
        Board b = new Board(4,4,bombStatuses);
        
        //Invalid coordinates
        
        MinesweeperMessage bv1a = b.dig(1,9);
        
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",bv1a.getMessageString());
        
        assertEquals(false,bv1a.getIndicator());
        
        MinesweeperMessage bv1b = b.dig(9,1);
        
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",bv1b.getMessageString());
        
        assertEquals(false,bv1b.getIndicator());
        
        MinesweeperMessage bv1c = b.dig(9,9);
        
        assertEquals("- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",bv1c.getMessageString());
        
        assertEquals(false,bv1c.getIndicator());
        
        //Valid coordinates, valid command
        
        //dig untouched with bomb, neighbours with bombs
        MinesweeperMessage bv2 = b.dig(1,0);
        
        assertEquals("- 2 - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",bv2.getMessageString());
        assertEquals(true,bv2.getIndicator());
        
        //dig untouched with bomb, neighbours with bombs change in neighbour bombs
        MinesweeperMessage bv2b = b.dig(0,0);
        
        assertEquals("1 1 - -\n"
                +    "- - - -\n"
                +    "- - - -\n"
                +    "- - - -\n",bv2b.getMessageString());
        assertEquals(true,bv2b.getIndicator());
        
        //dig untouched with bomb, neighbours without bombs 
        MinesweeperMessage bv3 = b.dig(3,3);
        
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv3.getMessageString());
        assertEquals(true,bv3.getIndicator());
        
        //flag untouched
        MinesweeperMessage bv4 = b.flag(0,1);
        assertEquals("1 1 1  \n"
                +    "F - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv4.getMessageString());
        assertEquals(false,bv4.getIndicator());
        
        //Invalid commands 
        //flag flagged
        MinesweeperMessage bv5 = b.flag(0,1);
        assertEquals("1 1 1  \n"
                +    "F - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv5.getMessageString());
        assertEquals(false,bv5.getIndicator());
        
        
        
        //dig flagged
        MinesweeperMessage bv5b = b.dig(0,1);
        assertEquals("1 1 1  \n"
                +    "F - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv5b.getMessageString());
        assertEquals(false,bv5b.getIndicator());
        
        
        //Back to valid commands
        //deflag flagged
        MinesweeperMessage bv4b = b.deflag(0,1);
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv4b.getMessageString());
        assertEquals(false,bv4b.getIndicator());
        
        //Invalid commands again
        
        //deflag untouched
        MinesweeperMessage bv6 = b.deflag(0,3);
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv6.getMessageString());
        assertEquals(false,bv6.getIndicator());
        
        //deflag dug
        MinesweeperMessage bv6b = b.deflag(0,0);
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv6b.getMessageString());
        assertEquals(false,bv6b.getIndicator());
        
        
        //flag dug
        MinesweeperMessage bv6c = b.flag(0,0);
        assertEquals("1 1 1  \n"
                +    "- - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv6c.getMessageString());
        assertEquals(false,bv6c.getIndicator());
        
        //dig untouched no bomb
        MinesweeperMessage bv7 = b.dig(0,1);
        assertEquals("1 1 1  \n"
                +    "1 - 1  \n"
                +    "1 1 1  \n"
                +    "       \n",bv7.getMessageString());
        assertEquals(false,bv7.getIndicator());
        
    }
    
}
