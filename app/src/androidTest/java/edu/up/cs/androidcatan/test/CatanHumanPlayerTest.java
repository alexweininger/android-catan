package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.CatanHumanPlayer;
import edu.up.cs.androidcatan.catan.CatanLocalGame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CatanHumanPlayerTest {

    @Test
    public void tryBuildRoad()
    {
        CatanHumanPlayer catanHumanPlayer = new CatanHumanPlayer("Name");
        catanHumanPlayer.state.setSetupPhase(false);
        catanHumanPlayer.state.setActionPhase(true);

        catanHumanPlayer.state.getPlayerList().get(0).addResourceCard(0, 1);
        catanHumanPlayer.state.getPlayerList().get(0).addResourceCard(2,1);

        assertTrue(catanHumanPlayer.tryBuildRoad(1,2));

    }
}
