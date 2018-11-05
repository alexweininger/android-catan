package catan.tests;

import android.test.AndroidTestCase;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.CatanGameState;
import edu.up.cs.androidcatan.catan.Player;

import static org.junit.Assert.*;

public class ExampleUnitTest{// extends AndroidTestCase {

    @Test
    public static void example() {
        System.out.println("Hello");
    }

    @Test
    public static void testUpdateVictoryPoints(){
        CatanGameState gameState;
        gameState = new CatanGameState();
        gameState.updateVictoryPoints();
        boolean flag = false;
        int[] playerVictoryPoints = gameState.getPlayerVictoryPoints();
        for (int n = 0; n < playerVictoryPoints.length; n++){
            if (playerVictoryPoints[n] <= 10 && playerVictoryPoints[n] >= 0){
                flag = true;
            }
        }
        assertTrue(flag);
    }

    @Test
    public static void testRemoveResourceCardTotalCardCount(){
        boolean flag = false;
        Player player = new Player(0);
        int[] playerCardsBefore = player.getResourceCards();
        player.removeResourceCard(1, 1);
        int[] playerCardsAfter = player.getResourceCards();

        if (playerCardsBefore.length != (playerCardsAfter.length +1)){
            flag = true;
        }
        assertTrue(flag);
    }
    
}
