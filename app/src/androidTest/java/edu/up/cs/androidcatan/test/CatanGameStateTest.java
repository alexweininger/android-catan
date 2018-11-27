package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.Player;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CatanGameStateTest {

    /*----------------------------------------Robber Methods------------------------------------------*/
    @Test
    //by Niraj Mali
    public void testSetRobberPhase () {
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
    public void testCheckPlayerResources () {
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
    public void testValidDiscard () {
        CatanGameState state = new CatanGameState();
        int[] resourcesToRemove = new int[]{0, 0, 0, 0, 0};
        assertTrue(state.validDiscard(0, resourcesToRemove));
        resourcesToRemove[0] = 2;
        assertFalse(state.validDiscard(0, resourcesToRemove));
        state.getPlayerList().get(0).addResourceCard(0, 8);
        resourcesToRemove[0] = 8;
        assertTrue(state.validDiscard(0, resourcesToRemove));
        resourcesToRemove[1] = 1;
        assertFalse(state.validDiscard(0, resourcesToRemove));
    }

    @Test
    //by Niraj Mali
    public void testDiscardResources () {
        CatanGameState state = new CatanGameState();
        int[] resourcesToRemove = new int[]{0, 0, 0, 0, 0};

        state.setRobberPlayerListHasDiscarded(new boolean[]{true, true, true, true});
        assertTrue(state.discardResources(0, resourcesToRemove));
        //TODO Further test cases needed
    }

    @Test
    //by Niraj Mali
    public void testAllPlayersHaveDiscarded () {
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
    public void testMoveRobber () {
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
    public void testRobberSteal () {

    }

    @Test // by Alex Weininger
    public void testGetRandomResourceCard () {
        CatanGameState state = new CatanGameState();

        // test drawing a random dev card 100 times, since this will make sure it 'reshuffles' the deck a few times
        for (int i = 0; i < 100; i++) {
            assertTrue(state.getRandomDevCard() > -1);
            assertTrue(state.getRandomDevCard() < 5);
        }
    }

    @Test
    public void testGetCurrentPlayerId () {
        CatanGameState state = new CatanGameState();
        assertTrue(state.getCurrentPlayerId() > -1);
        assertTrue(state.getCurrentPlayerId() < 4);
    }

    @Test
    public void testGetCurrentPlayerObject () {
        CatanGameState state = new CatanGameState();
        assertNotNull(state.getCurrentPlayer());
        assertNotNull(state.getCurrentPlayer());

        assertTrue(state.getCurrentPlayer() instanceof Player);
    }

    @Test
    public void testProduceResources () {
        CatanGameState state = new CatanGameState();

    }
}
