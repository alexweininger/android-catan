package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;

import static junit.framework.TestCase.assertTrue;

public class CatanHumanPlayerTest {

    @Test //Written By: Niraj
    public void tryBuildRoad () {
        CatanGameState state = new CatanGameState();
        state.setSetupPhase(true);
        state.setCurrentPlayerId(0);
        state.getPlayerList().get(0).addResourceCard(0, 1);
        state.getPlayerList().get(0).addResourceCard(2, 1);

        assertTrue(state.isSetupPhase());
    }
}
