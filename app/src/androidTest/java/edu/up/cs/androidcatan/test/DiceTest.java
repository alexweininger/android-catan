package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.Dice;

import static org.junit.Assert.assertTrue;

public class DiceTest {

    @Test //Written By: Alex Weininger
    public void rollTest () {
        Dice dice = new Dice();
        for (int i = 0; i < 2000; i++) {
            assertTrue(dice.roll() <= 12 && dice.roll() > 0);
        }
    }
}