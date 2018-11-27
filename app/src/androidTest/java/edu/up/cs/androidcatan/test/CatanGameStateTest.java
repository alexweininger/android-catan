package edu.up.cs.androidcatan.test;

import android.util.Log;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;

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

        //TODO Check for when player actually has resources and needs to discard
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
    }

    @Test
    //by Niraj Mali
    public void testDiscardResources(){

    }

    @Test
    //by Niraj Mali
    public void testAllPlayersHaveDiscarded(){

    }

    @Test
    //by Niraj Mali
    public void testMoveRobber(){

    }

    @Test
    //by Niraj Mali
    public void testRobberSteal(){

    }


}
