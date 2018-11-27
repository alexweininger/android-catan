package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;

import static org.junit.Assert.assertEquals;

public class DevelopmentCardTest {

    @Test //Written By: Alex
    public void setPlayable()
    {
        DevelopmentCard developmentCard = new DevelopmentCard(2);
        assertEquals(false, developmentCard.isPlayable());

        developmentCard.setPlayable(true);
        assertEquals(true, developmentCard.isPlayable());

        developmentCard.setPlayable(false);
        assertEquals(false, developmentCard.isPlayable());
    }

    @Test //Written By: Alex
    public void setDevCardId()
    {
        DevelopmentCard developmentCard = new DevelopmentCard(2);
        assertEquals(2, developmentCard.getDevCardId());

        developmentCard.setDevCardId(5);
        assertEquals(5, developmentCard.getDevCardId());
    }


}
