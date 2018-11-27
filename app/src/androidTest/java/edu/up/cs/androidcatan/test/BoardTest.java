package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class BoardTest {

    @Test //Written By: Andrew
    public void testValidBuildingLocationSetupPhaseEmptyIntersection () {
        Board board = new Board();
        assertTrue(board.validBuildingLocation(0, true, 0));
        assertFalse(board.validBuildingLocation(0, true, -1));
        assertFalse(board.validBuildingLocation(0, true, 90));
        assertFalse(board.validBuildingLocation(-3, true, -1));
        assertFalse(board.validBuildingLocation(-1, true, 5));      //bug
        assertTrue(board.validBuildingLocation(3, true, 53));
        assertTrue(board.validBuildingLocation(2, true, 24));
    }

    @Test //Written By: Andrew
    public void testValidBuildingLocationSetupPhaseTakenIntersection () {
        Board board = new Board();
        board.addBuilding(0, new City(0));
        assertFalse(board.validBuildingLocation(1, true, 0));
        assertFalse(board.validBuildingLocation(0, true, 0));
        assertFalse(board.validBuildingLocation(0, true, -1));
        assertFalse(board.validBuildingLocation(0, true, 90));
        assertFalse(board.validBuildingLocation(-3, true, -1));
        assertFalse(board.validBuildingLocation(-1, true, 5));      //bug
        assertTrue(board.validBuildingLocation(3, true, 53));
        assertTrue(board.validBuildingLocation(2, true, 24));
    }

    @Test //Written By: Andrew
    public void testHGraph () {
        Board board = new Board();
        boolean hGraph[][] = board.getHGraph();
        assertTrue(hGraph[5][14]);
        assertTrue(hGraph[5][0]);
        assertTrue(hGraph[5][14]);
        assertTrue(hGraph[5][15]);
        assertTrue(hGraph[5][16]);
        assertTrue(hGraph[5][6]);
    }

    @Test //Written By: Andrew
    public void testIsConnected () {
        Board board = new Board();

        board.getBuildings()[1] = new Settlement(1);

        assertFalse(board.isConnected(1, 30));
        assertFalse(board.isConnected(1, -20));
        assertFalse(board.isConnected(0, 1)); //wrong playerId
        assertFalse(board.isConnected(1, 2));

        assertTrue(board.isConnected(1, 1));
    }

    @Test //Written By: Andrew and Alex Weininger
    public void testValidRoadPlacement () {
        Board board = new Board();

        board.getBuildings()[1] = new Settlement(1);

        assertTrue(board.validRoadPlacement(1, false, 1, 2));
        assertFalse(board.validRoadPlacement(1, false, 6, 7));
    }

    @Test //Written By: Andrew and Alex Weininger
    public void testAddRoadArray () {
        Board board = new Board();

        board.addRoad(1, 1, 2);
        if (board.getRoads().size() != 0) {
            assert true;
        } else {
            assert false;
        }
    }

    @Test //Written By: Andrew
    public void testAddRoadMatrix () {
        Board board = new Board();

        board.addRoad(1, 1, 2);
        assertEquals(board.getRoadMatrix()[1][2].getOwnerId(), 1);
        assertFalse(board.getRoadMatrix()[1][2].getOwnerId() == 3);
    }

    @Test //Written By: Andrew
    public void testHasRoad () {
        Board board = new Board();

        board.getBuildings()[1] = new Settlement(1);
        board.addRoad(1, 1, 2);

        assertTrue(board.hasRoad(1));
        assertTrue(board.hasRoad(2));

        assertFalse(board.hasRoad(45));
        assertFalse(board.hasRoad(-20));
    }

    @Test //Written By: Andrew
    public void testGetPlayerWithLongestRoad () {
        Board board = new Board();
        CatanGameState gameState = new CatanGameState();

        assertEquals(board.getPlayerWithLongestRoad(gameState.getPlayerList()), -1);

        assertFalse(board.getPlayerWithLongestRoad(gameState.getPlayerList()) == 0);
        assertFalse(board.getPlayerWithLongestRoad(gameState.getPlayerList()) == 1);
        assertFalse(board.getPlayerWithLongestRoad(gameState.getPlayerList()) == 2);
        assertFalse(board.getPlayerWithLongestRoad(gameState.getPlayerList()) == 3);
    }

    @Test //Written By: Andrew
    public void testValidBuildingLocation () {
        Board board = new Board();
        int playerId = -1;

        assertFalse(board.validBuildingLocation(playerId, false, -1));
        assertFalse(board.validBuildingLocation(1, false, 2));
        assertFalse(board.validBuildingLocation(4, true, 2));

        board.getBuildings()[0] = new Settlement(1);
        board.addRoad(1, 0, 1);

        assertTrue(board.validBuildingLocation(1, true, 20));
        assertFalse(board.validBuildingLocation(1, true, 0));
    }

    @Test //Written By: Andrew and Alex Weininger
    public void testValidCityLocation () {
        Board board = new Board();

        board.getBuildings()[0] = new Settlement(1);

        assertFalse(board.validCityLocation(1, -3));
        assertFalse(board.validCityLocation(-1, 3));
        assertFalse(board.validCityLocation(-1, -4));
        assertFalse(board.validCityLocation(1, -1));

        assertTrue(board.validCityLocation(1, 0));
    }

    @Test // by Alex Weininger
    public void testGetHexagonListForDrawing () {
        Board board = new Board();
        assertEquals(19, board.getHexagonListForDrawing().size());
        assertFalse(board.getHexagonListForDrawing().size() == 18);
    }

    @Test // by Alex Weininger
    public void testGetIntersectionOwners () {
        Board board = new Board();
        board.getBuildings()[0] = new Settlement(1);

        assertEquals(1, (int) board.getIntersectionOwners(0).get(0));
        assertEquals(1, board.getIntersectionOwners(0).size());

        assertFalse(board.getIntersectionOwners(0).get(0) == -3);
    }

    @Test // by Andrew
    public void testGetRoadsAtIntersection () {
        Board board = new Board();

        board.addRoad(1, 0, 1);

        assertEquals(0, board.getRoadsAtIntersection(0).get(0).getIntersectionAId());
        assertEquals(1, board.getRoadsAtIntersection(1).get(0).getIntersectionBId());
        assertEquals(1, board.getRoadsAtIntersection(0).get(0).getOppositeIntersection(0));
        assertEquals(1, board.getRoadsAtIntersection(0).size());

        assertFalse(board.getRoadsAtIntersection(0).get(0).getIntersectionAId() == -1);
        assertFalse(board.getRoadsAtIntersection(1).get(0).getIntersectionBId() == -1);
        assertFalse(board.getRoadsAtIntersection(0).get(0).getOppositeIntersection(0) == 0);
    }

    @Test // by Alex Weininger and Andrew Lang
    public void testGetHexagonsFromChitValue () {
        Board board = new Board();

        assertEquals(1, board.getHexagonsFromChitValue(2).size());
        assertEquals(1, board.getHexagonsFromChitValue(12).size());
        assertEquals(2, board.getHexagonsFromChitValue(8).size());
        assertEquals(2, board.getHexagonsFromChitValue(4).size());

        assertFalse(board.getHexagonsFromChitValue(7).size() == 3);
        assertFalse(board.getHexagonsFromChitValue(9).size() == 0);
        assertFalse(board.getHexagonsFromChitValue(20).size() == 1);
        assertFalse(board.getHexagonsFromChitValue(-20).size() == 1);
        assertFalse(board.getHexagonsFromChitValue(100).size() == 1);

    }

    @Test // by Andrew
    public void testMoveRobber () {
        Board board = new Board();

        board.getRobber().setHexagonId(1);

        assertFalse(board.moveRobber(1));
        assertFalse(board.moveRobber(25));
        assertFalse(board.moveRobber(-25));

        assertTrue(board.moveRobber(2));
        assertTrue(board.moveRobber(10));
        assertTrue(board.moveRobber(0));
    }

    @Test // by Andrew
    public void testAddBuilding () {
        Board board = new Board();

        board.getBuildings()[0] = new Settlement(1);

        assertFalse(board.addBuilding(0, board.getBuildings()[0]));
        assertFalse(board.addBuilding(-10, board.getBuildings()[0]));
        assertFalse(board.addBuilding(54, board.getBuildings()[0]));

        assertTrue(board.addBuilding(10, board.getBuildings()[0]));
        assertTrue(board.addBuilding(1, board.getBuildings()[0]));
    }

    @Test // by Andrew
    public void testHasBuilding () {
        Board board = new Board();
        Building settlement = new Settlement(0);
        board.addBuilding(0, settlement);

        assertTrue(board.hasBuilding(0));

        assertFalse(board.hasBuilding(53));
        assertFalse(board.hasBuilding(100));
        assertFalse(board.hasBuilding(-20));
    }

    @Test // by Andrew
    public void testGetBuildingAtIntersection () {
        Board board = new Board();

        board.getBuildings()[0] = new Settlement(1);

        assertEquals(board.getBuildingAtIntersection(0), board.getBuildings()[0]);
    }

    @Test // by Andrew and Alex Weininger
    public void testGetAdjacentHexagons () {
        Board board = new Board();

        assertEquals(6, board.getAdjacentHexagons(0).size());
        assertEquals(4, board.getAdjacentHexagons(7).size());
        assertEquals(6, board.getAdjacentHexagons(6).size());

        assertFalse(board.getAdjacentHexagons(14).size() == 0);
        assertFalse(board.getAdjacentHexagons(19).size() == 1);
        assertFalse(board.getAdjacentHexagons(-1).size() == 1);
        assertFalse(board.getAdjacentHexagons(0).size() == 7);
    }

    @Test // by Andrew and Alex Weininger
    public void testGetIntersectionId () {
        Board board = new Board();

        assertEquals(0, board.getIntersectionId(0, 0));
        assertEquals(6, board.getIntersectionId(1, 0));
        assertEquals(board.getIntersectionId(-1, 1), -1);

        assertFalse(board.getIntersectionId(0, -1) == 0);
        assertFalse(board.getIntersectionId(5, 4) == 0);
        assertFalse(board.getIntersectionId(1, 4) == 20);
    }

    @Test // by Andrew Lang
    public void testGetHexagonFromId () {
        Board board = new Board();

        assertEquals(1, board.getHexagonFromId(1).getHexagonId());
        assertEquals(10, board.getHexagonFromId(10).getHexagonId());
        assertNull(board.getHexagonFromId(19));
        assertNull(board.getHexagonFromId(-2));

        assertFalse(board.getHexagonFromId(5).getHexagonId() == 0);
    }

    @Test // by Andrew and Alex
    public void testGenerateChitList () {
        Board board = new Board();

        assertEquals(18, board.generateChitList().size());
        assertTrue(board.generateChitList().contains(2));
        assertTrue(board.generateChitList().contains(12));

        assertFalse(board.generateChitList().contains(1));
        assertFalse(board.generateChitList().contains(-1));
        assertFalse(board.generateChitList().contains(13));
        assertFalse(board.generateChitList().contains(7));
    }
}