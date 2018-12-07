package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanLocalGame;

import static junit.framework.TestCase.assertTrue;

public class CatanLocalGameTest {

//    @Test
//    public void findWinner()
//    {
//        CatanGameState catanGameState = new CatanGameState();
//        CatanLocalGame catanLocalGame = new CatanLocalGame();
//
//        catanGameState.getPlayerList().get(0).setVictoryPointsPrivate(8);
//        catanGameState.getPlayerList().get(1).setVictoryPointsPrivate(2);
//        catanGameState.getPlayerList().get(2).setVictoryPointsPrivate(9);
//        catanGameState.getPlayerList().get(3).setVictoryPointsPrivate(7);
//
//        catanLocalGame.setState(catanGameState);
//        assertNull(catanLocalGame.checkIfGameOver());
//
//        catanGameState.getPlayerList().get(0).setVictoryPointsPrivate(10);
//
//        Log.d("TEST", "findWinner: " + catanLocalGame.checkIfGameOver());
//        assertNotNull(catanLocalGame.checkIfGameOver());
//    }

    @Test
    public void canMove() {
        CatanLocalGame catanLocalGame = new CatanLocalGame();
        assertTrue(catanLocalGame.canMove(0));
    }

}
