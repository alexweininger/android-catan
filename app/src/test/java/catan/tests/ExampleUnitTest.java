package catan.tests;

import android.test.AndroidTestCase;
import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.Dice;

import static junit.framework.Assert.*;

public class ExampleUnitTest{
    @Test
    public void checkDiceRolling()
    {
        Dice dice = new Dice();
        int roll = dice.roll();
        boolean flag = false;
        if (roll > 1 && roll < 13) {
            flag = true;
        }
        assertTrue(flag);
    }

}
