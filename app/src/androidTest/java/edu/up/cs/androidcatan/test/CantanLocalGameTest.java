package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.CatanLocalGame;

import static org.junit.Assert.assertEquals;

public class CantanLocalGameTest {

    @Test //Written By: Daniel
    public void checkIfGameOver() {
        CatanGameState cantanGameState = new CatanGameState();
        CatanLocalGame catanLocalGame = new CatanLocalGame();

        cantanGameState.getPlayerList().get(0).setVictoryPoints(8);
        cantanGameState.getPlayerList().get(1).setVictoryPoints(2);
        cantanGameState.getPlayerList().get(2).setVictoryPoints(9);
        cantanGameState.getPlayerList().get(3).setVictoryPoints(7);

        assertEquals(null, catanLocalGame.checkIfGameOver());

        cantanGameState.getPlayerList().get(0).setVictoryPoints(10);

        //assertEquals(catanLocalGame.playerNames[0] + " wins!", catanLocalGame.checkIfGameOver());
    }
}
