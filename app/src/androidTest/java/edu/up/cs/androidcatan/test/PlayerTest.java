package edu.up.cs.androidcatan.test;

import org.junit.Test;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.Player;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerTest {

    @Test
    public void testRemoveResourceCard() {
        Player p = new Player(0);
        assertFalse(p.removeResourceCard(0, 1));
        assertFalse(p.removeResourceCard(1, 1));
        assertFalse(p.removeResourceCard(2, 1));
        assertFalse(p.removeResourceCard(3, 1));

        assertFalse(p.removeResourceCard(-1, 1));
        assertFalse(p.removeResourceCard(10, 1));

        assertFalse(p.removeResourceCard(0, -1));
        assertFalse(p.removeResourceCard(0, 10));

        p.setResourceCards(new int[]{1, 0, 0, 0});
        assertTrue(p.removeResourceCard(0, 1));

        p.setResourceCards(new int[]{1, 0, 0, 0});
        assertFalse(p.removeResourceCard(0, -1));

        p.setResourceCards(new int[]{1, 0, 0, 0});
        assertFalse(p.removeResourceCard(0, 10));
    }

    @Test
    public void testCheckResourceCard() {
        Player player = new Player(0);
        player.setResourceCards(new int[]{3, 3, 3, 3, 3});
        assertFalse(player.checkResourceCard(-1, 5));
        assertFalse(player.checkResourceCard(8, 2));

        assertTrue(player.checkResourceCard(2, 2));
    }

    @Test
    public void testHasResourceBundle() {
        Player player = new Player(0);
        player.setResourceCards(new int[]{0, 0, 0, 0, 0});
        assertFalse(player.hasResourceBundle(new int[]{3, 3, 3, 3, 3}));
        assertFalse(player.hasResourceBundle(new int[]{-2, 3, 4, 5, 9}));

        player.setResourceCards(new int[]{5, 5, 5, 5, 5});
        assertTrue(player.hasResourceBundle(new int[]{0, 0, 0, 0, 0}));
        assertTrue(player.hasResourceBundle(new int[]{3, 3, 3, 3, 3}));
        assertFalse(player.hasResourceBundle(new int[]{9, 15, 2, 2, 2}));
    }

    @Test
    public void testRemoveResourceBundle() {
        Player player = new Player(0);
        assertFalse(player.removeResourceBundle(new int[] {2,6,4,8,0}));
        player.setResourceCards(new int[] {0,0,0,0,0});
        assertFalse(player.removeResourceBundle(new int[] {2,0,0,0,0}));

        player.setResourceCards(new int[] {5,5,5,5,5});
        assertTrue(player.removeResourceBundle(new int[] {0,0,0,0,0}));
        assertTrue(player.removeResourceBundle(new int[] {2,2,2,0,0}));

        Player p = new Player(0);
        assertFalse(p.removeResourceBundle(Settlement.resourceCost));
        p.setResourceCards(new int[] {1, 0, 1, 0, 0});

        assertTrue(p.removeResourceBundle(Road.resourceCost));
        assertFalse(p.removeResourceBundle(Road.resourceCost));
        assertFalse(p.removeResourceBundle(new int[] {}));
        assertTrue(p.removeResourceBundle(new int[5]));
        assertFalse(p.removeResourceBundle(new int[] {0, 0, 0}));
    }

    @Test
    public void testUseDevCard()
    {
        Player player = new Player(0);
        assertFalse(player.useDevCard(9));
        assertFalse(player.useDevCard(-2));
        assertFalse(player.useDevCard(0));
        ArrayList<Integer> devCards = new ArrayList<>();
        devCards.add(2);
        devCards.add(3);
        devCards.add(4);
        devCards.add(0);
        player.setDevelopmentCards(devCards);
        assertTrue(player.useDevCard(2));
        assertFalse(player.useDevCard(6));
        assertTrue(player.useDevCard(0));
    }


}