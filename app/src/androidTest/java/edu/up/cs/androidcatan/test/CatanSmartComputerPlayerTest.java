package edu.up.cs.androidcatan.test;

import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.CatanSmartComputerPlayer;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;

import static junit.framework.Assert.assertEquals;

public class CatanSmartComputerPlayerTest {

    @Test
    //by Niraj Mali
    public void testTryMoveRobber(){
    }

    @Test
    public void testGetBuildingOfPlayer(){
        CatanGameState gs = new CatanGameState();
        //CatanSmartComputerPlayer smartComputerPlayer = new CatanSmartComputerPlayer("andrew");
        int ownerId = 1;
        gs.getBoard().addBuilding(0, new Settlement(ownerId));
        gs.getBoard().addBuilding(42, new Settlement(ownerId));
        //assertEquals(smartComputerPlayer.getBuildingOfPlayer(gs), 0);
        //assertEquals(smartComputerPlayer.getBuildingOfPlayer(gs), 42);
    }

    @Test
    //by Niraj Mali
    public void testGetPlayerRoads() {}

    @Test
    //by Niraj Mali
    public void testGetPlayerRoadIntersection(){}

    @Test
    //by Niraj Mali
    public void testCheckIntersectionResource(){}
}
