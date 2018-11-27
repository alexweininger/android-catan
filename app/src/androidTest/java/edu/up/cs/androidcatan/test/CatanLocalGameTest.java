package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.CatanLocalGame;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CatanLocalGameTest {

    @Test
    public void findWinner()
    {
        CatanGameState cantanGameState = new CatanGameState();
        CatanLocalGame catanLocalGame = new CatanLocalGame();

        cantanGameState.getPlayerList().get(0).setVictoryPointsPrivate(8);
        cantanGameState.getPlayerList().get(1).setVictoryPointsPrivate(2);
        cantanGameState.getPlayerList().get(2).setVictoryPointsPrivate(9);
        cantanGameState.getPlayerList().get(3).setVictoryPointsPrivate(7);

        assertEquals(-1, catanLocalGame.findWinner(cantanGameState));

        cantanGameState.getPlayerList().get(0).setVictoryPointsPrivate(10);

        assertEquals(0, catanLocalGame.findWinner(cantanGameState));
    }

    @Test
    public void canMove()
    {
        CatanLocalGame catanLocalGame = new CatanLocalGame();
        assertTrue(catanLocalGame.canMove(0));
    }

}
