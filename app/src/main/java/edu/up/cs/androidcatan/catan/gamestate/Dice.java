package edu.up.cs.androidcatan.catan.gamestate;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/

import android.util.Log;

import java.io.Serializable;
import java.util.Random;

/**
 * dice class - done for now 10/10
 */
public class Dice implements Serializable {
    private static final long serialVersionUID = 2944606999369377855L;
    private static String TAG = "Dice";

    private int[] diceValues; // array of dice values

    public Dice() {
        this.diceValues = new int[2];
        this.diceValues[0] = 1;
        this.diceValues[1] = 1;
    }

    public Dice(Dice d) {
        this.setDiceValues(d.getDiceValues());
    }

    /**
     * roll() - sets both dice values to random int from 1 to 6 (inclusive)
     */
    public int roll() {
        Random random = new Random();
        this.diceValues[0] = random.nextInt(6) + 1;
        this.diceValues[1] = random.nextInt(6) + 1;

        if (this.diceValues[0] > 5 || this.diceValues[1] > 5) {
            Log.e(TAG, "roll: a dice had a value of 6");
        }
        Log.e(TAG, "" + this.toString());
        return this.diceValues[0] + this.diceValues[1];
    }

    public int[] getDiceValues(){
        return this.diceValues;
    }

    public void setDiceValues(int[] diceValues) {
        this.diceValues = diceValues;
    }
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Dice roll: ").append(diceValues[0]).append(", ").append(diceValues[1]);
        return str.toString();
    }

}
