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
    public void testCheckPlayerResources(){
        CatanGameState state = new CatanGameState();
        assertFalse(state.checkPlayerResources(0));
        assertTrue(state.getRobberPlayerListHasDiscarded()[0]);
        state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, true, true});
        assertFalse(state.checkPlayerResources(0));
    }

    @Test
    public void testValidDiscard(){}

    @Test
    public void testDiscardResources(){}

    @Test
    public void testAllPlayersHaveDiscarded(){}

    @Test
    public void testMoveRobber(){}

    @Test
    public void testRobberSteal() {}


}
