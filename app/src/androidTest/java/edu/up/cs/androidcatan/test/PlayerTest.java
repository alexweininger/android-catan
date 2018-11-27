package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.Player;

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

        p.setResourceCards(new int[] {1, 0, 0, 0});
        assertTrue(p.removeResourceCard(0,1));

        p.setResourceCards(new int[] {1, 0, 0, 0});
        assertFalse(p.removeResourceCard(0,-1));

        p.setResourceCards(new int[] {1, 0, 0, 0});
        assertFalse(p.removeResourceCard(0,10));
    }
}
