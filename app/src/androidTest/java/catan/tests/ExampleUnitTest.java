package catan.tests;

import android.test.AndroidTestCase;

import org.junit.*;

import edu.up.cs.androidcatan.catan.gamestate.Board;

import static junit.framework.Assert.*;

public class ExampleUnitTest{
    @Test
    public void example(){
        System.out.println("Hello");
    }

    @Test
    public void testValidBuildingLocation(){
        Board board = new Board();
        assertTrue(board.validBuildingLocation(0, true, 0));
        assertFalse(board.validBuildingLocation(0, true, -1));
        assertFalse(board.validBuildingLocation(0, true, 90));
        assertFalse(board.validBuildingLocation(-3, true, -1));
        assertFalse(board.validBuildingLocation(-1, true, 5));      //bug
        assertTrue(board.validBuildingLocation(3, true, 53));
        assertTrue(board.validBuildingLocation(2, true, 24));
    }
}
