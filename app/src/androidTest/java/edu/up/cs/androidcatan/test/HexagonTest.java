package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.Board;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class HexagonTest {

    @Test
    public void hexagonBoard() {
        Board board = new Board();
        boolean hGraph[][] = board.getHGraph();
        assertTrue(hGraph[5][14]);
        assertTrue(hGraph[5][0]);
        assertTrue(hGraph[5][14]);
        assertTrue(hGraph[5][15]);
        assertTrue(hGraph[5][16]);
        assertTrue(hGraph[5][6]);
    }
}
