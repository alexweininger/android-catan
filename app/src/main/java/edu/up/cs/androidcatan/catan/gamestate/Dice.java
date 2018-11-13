package edu.up.cs.androidcatan.catan.gamestate;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.util.Random;

import edu.up.cs.androidcatan.R;

import static android.content.ContentValues.TAG;

/**
 * dice class - done for now 10/10
 */
public class Dice {
    private int[] diceValues; // array of dice values

    public Dice() {
        this.diceValues = new int[2];
    }

    public Dice(Dice d) {
        this.setDiceValues(d.getDiceValues());
    }

    /**
     * roll() - sets both dice values to random int from 1 to 6 (inclusive)
     */
    public int roll() {
        Random random = new Random();
        this.diceValues[0] = random.nextInt(5) + 1;
        this.diceValues[1] = random.nextInt(5) + 1;

        return this.diceValues[0] + this.diceValues[1];
    }

    /**
     * getSum
     *
     * @return the sum of the dice values
     */
    public int getSum() {
        return this.diceValues[0] + this.diceValues[1];
    }
    public int[] getDiceValues(){
        return this.diceValues;
    };

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
