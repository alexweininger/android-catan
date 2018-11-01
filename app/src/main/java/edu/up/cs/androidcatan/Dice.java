package edu.up.cs.androidcatan;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/

import java.util.Random;

/**
 * dice class - done for now 10/10
 */
public class Dice {
    private int[] diceValues; // array of dice values

    public Dice() {
        this.diceValues = new int[2];
    }

    /**
     * roll() - sets both dice values to random int from 1 to 6 (inclusive)
     */
    public int roll() {
        Random random = new Random();
        this.diceValues[0] = random.nextInt(5) + 1;
        this.diceValues[1] = random.nextInt(5) + 1;

        return diceValues[0] + diceValues[1];
    }

    /**
     * getSum
     *
     * @return the sum of the dice values
     */
    public int getSum() {
        return diceValues[0] + diceValues[1];
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Dice roll: ").append(diceValues[0]).append(", ").append(diceValues[1]);
        return str.toString();
    }
}
