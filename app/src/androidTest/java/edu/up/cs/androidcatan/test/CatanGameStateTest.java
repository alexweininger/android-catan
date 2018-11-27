package edu.up.cs.androidcatan.test;

import android.util.Log;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.*;

public class CatanGameStateTest {

    /*----------------------------------------Robber Methods------------------------------------------*/
    @Test
    //by Niraj Mali
    public void testSetRobberPhase() {
        CatanGameState state = new CatanGameState();
        state.setRobberPhase(true);
        assertTrue(state.getRobberPhase());
        assertFalse(!state.getRobberPhase());
        state.setRobberPhase(false);
        assertTrue(!state.getRobberPhase());
        assertFalse(state.getRobberPhase());
    }

    @Test
    //by Niraj Mali
    public void testCheckPlayerResources(){
        CatanGameState state = new CatanGameState();
        assertFalse(state.checkPlayerResources(0));
        assertTrue(state.getRobberPlayerListHasDiscarded()[0]);
        state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, true, true});
        assertFalse(state.checkPlayerResources(0));

        state.getPlayerList().get(0).addResourceCard(0, 7);
        assertFalse(state.checkPlayerResources(0));
        state.setRobberPlayerListHasDiscarded(new boolean[]{false, false, false, false});
        state.getPlayerList().get(0).addResourceCard(0, 1);
        assertEquals(8, state.getPlayerList().get(0).getTotalResourceCardCount());
        assertTrue(state.checkPlayerResources(0));
    }

    @Test
    //by Niraj Mali
    public void testValidDiscard(){
        CatanGameState state = new CatanGameState();
        int[] resourcesToRemove = new int[]{0, 0, 0, 0, 0};
        assertTrue(state.validDiscard(0, resourcesToRemove));
        resourcesToRemove[0] = 2;
        assertFalse(state.validDiscard(0, resourcesToRemove));
        state.getPlayerList().get(0).addResourceCard(0, 8);
        resourcesToRemove[0] = 4;
        assertTrue(state.validDiscard(0, resourcesToRemove));
        resourcesToRemove[1] = 1;
        assertFalse(state.validDiscard(0, resourcesToRemove));
    }

    @Test
    //by Niraj Mali
    public void testDiscardResources(){
        CatanGameState state = new CatanGameState();
        int[] resourcesToRemove = new int[]{0, 0, 0, 0, 0};

        state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, true, true});
        assertTrue(state.discardResources(0, resourcesToRemove));
        //TODO Further test cases needed
    }

    @Test
    //by Niraj Mali
    public void testAllPlayersHaveDiscarded(){
        CatanGameState state = new CatanGameState();
        assertFalse(state.allPlayersHaveDiscarded());
        state.setRobberPlayerListHasDiscarded(new boolean[]{true, false, true, true});
        assertFalse(state.allPlayersHaveDiscarded());
        state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, true, true});
        assertTrue(state.allPlayersHaveDiscarded());
        state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, true, false});
        assertFalse(state.allPlayersHaveDiscarded());
        state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, false, false});
        assertFalse(state.allPlayersHaveDiscarded());
        state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, true, true});
        assertTrue(state.allPlayersHaveDiscarded());
    }

    @Test
    //by Niraj Mali
    public void testMoveRobber(){
        CatanGameState state = new CatanGameState();
        state.setCurrentPlayerId(0);
        assertFalse(state.moveRobber(0, -1));
        assertFalse(state.moveRobber(0, 1));
        assertFalse(state.moveRobber(-1, 0));
        assertFalse(state.moveRobber(60, 0));
        assertTrue(state.moveRobber(0, 0));
        //TODO May need some more cases
    }

    @Test
    //by Niraj Mali
    public void testRobberSteal(){
        CatanGameState state = new CatanGameState();
        assertFalse(state.robberSteal(0, 0));
        assertFalse(state.robberSteal(-1, 0));
        assertFalse(state.robberSteal(0, -1));
        assertFalse(state.robberSteal(0, 5));
        assertFalse(state.robberSteal(5, -1));
        assertFalse(state.robberSteal(5, 0));
        assertFalse(state.robberSteal(0, 0));
        assertFalse(state.robberSteal(0, 1));

        state.getPlayerList().get(1).addResourceCard(0, 1);
        assertTrue(state.robberSteal(0, 1));
        assertFalse(state.robberSteal(0, 4));
    }

    @Test
    //by Niraj Mali
    public void testUpdateSetupPhase () {
        CatanGameState state = new CatanGameState();
        assertFalse(state.updateSetupPhase());
        state.getBoard().addBuilding(0, new Settlement(0));
        state.getBoard().addBuilding(1, new Settlement(0));
        state.getBoard().addBuilding(2, new Settlement(0));
        state.getBoard().addBuilding(3, new Settlement(0));
        state.getBoard().addBuilding(4, new Settlement(0));
        state.getBoard().addBuilding(5, new Settlement(0));
        state.getBoard().addBuilding(6, new Settlement(0));
        state.getBoard().addBuilding(7, new Settlement(0));
        assertFalse(state.updateSetupPhase());
        state.getBoard().addBuilding(8, new Settlement(0));
        assertFalse(state.updateSetupPhase());
        state.getBoard().addRoad(0, 0, 1);
        state.getBoard().addRoad(0, 1, 2);
        state.getBoard().addRoad(0, 2, 3);
        state.getBoard().addRoad(0, 3, 4);
        state.getBoard().addRoad(0, 4, 5);
        state.getBoard().addRoad(0, 5, 6);
        state.getBoard().addRoad(0, 6, 7);
        state.getBoard().addRoad(0, 7, 8);
        assertTrue(state.updateSetupPhase());
    }

}
