package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;

import static org.junit.Assert.*;

public class SettlementTest {

    @Test
    public void getVictoryPoints() {
        Settlement settlement = new Settlement(0);
        assertEquals(1, settlement.getVictoryPoints());
    }

}