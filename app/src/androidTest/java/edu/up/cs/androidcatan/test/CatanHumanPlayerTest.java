package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanHumanPlayer;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class CatanHumanPlayerTest {

    @Test
    public void tryBuildRoad()
    {
        CatanHumanPlayer catanHumanPlayer = new CatanHumanPlayer("Name");
        catanHumanPlayer.state.setSetupPhase(true);;
        catanHumanPlayer.state.setCurrentPlayerId(0);
        catanHumanPlayer.state.getPlayerList().get(0).addResourceCard(0, 1);
        catanHumanPlayer.state.getPlayerList().get(0).addResourceCard(2,1);

    }

}
