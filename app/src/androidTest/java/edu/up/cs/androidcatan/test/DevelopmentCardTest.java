package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DevelopmentCardTest {

    @Test //Written By: Alex
    public void setPlayable () {
        DevelopmentCard developmentCard = new DevelopmentCard(2);
        assertFalse(developmentCard.isPlayable());

        developmentCard.setPlayable(true);
        assertTrue(developmentCard.isPlayable());

        developmentCard.setPlayable(false);
        assertFalse(developmentCard.isPlayable());
    }

    @Test //Written By: Alex
    public void setDevCardId () {
        DevelopmentCard developmentCard = new DevelopmentCard(2);
        assertEquals(2, developmentCard.getDevCardId());

        developmentCard.setDevCardId(5);
        assertEquals(5, developmentCard.getDevCardId());
    }
}
