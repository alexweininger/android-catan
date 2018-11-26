package edu.up.cs.androidcatan.test;

import org.junit.Test;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.Player;
import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class BoardTest {

    @Test
    public void testValidBuildingLocationSetupPhaseEmptyIntersection(){
        Board board = new Board();
        assertTrue(board.validBuildingLocation(0, true, 0));
        assertFalse(board.validBuildingLocation(0, true, -1));
        assertFalse(board.validBuildingLocation(0, true, 90));
        assertFalse(board.validBuildingLocation(-3, true, -1));
        assertFalse(board.validBuildingLocation(-1, true, 5));      //bug
        assertTrue(board.validBuildingLocation(3, true, 53));
        assertTrue(board.validBuildingLocation(2, true, 24));
    }

    @Test
    public void testValidBuildingLocationSetupPhaseTakenIntersection(){
        Board board = new Board();
        board.addBuilding(0, new City(0, 0));
        assertFalse(board.validBuildingLocation(1, true, 0));
        assertFalse(board.validBuildingLocation(0, true, 0));
        assertFalse(board.validBuildingLocation(0, true, -1));
        assertFalse(board.validBuildingLocation(0, true, 90));
        assertFalse(board.validBuildingLocation(-3, true, -1));
        assertFalse(board.validBuildingLocation(-1, true, 5));      //bug
        assertTrue(board.validBuildingLocation(3, true, 53));
        assertTrue(board.validBuildingLocation(2, true, 24));
    }

    @Test
    public void testAddBuilding(){
        Board board = new Board();
        Board boardTrue = new Board();
        Building settlement = new Settlement(0);

        board.addBuilding(3, settlement);
    }

    @Test
    public void testHasBuilding(){
        Board board = new Board();
        Building settlement = new Settlement(0);
        board.addBuilding(0, settlement);
        assertTrue(board.hasBuilding(0));
        assertFalse(board.hasBuilding(53));
    }

    @Test
    public void testHGraph() {
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