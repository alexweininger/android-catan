package edu.up.cs.androidcatan;

import java.util.Arrays;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 30th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class Settlement extends Building {

    private final int[] resourceCost = {1, 1, 1, 0, 1}; // Brick, Grain, Lumber, Ore, Wool

    /**
     * @param ownerId - player id of who owns the settlement
     */
    Settlement(int ownerId) {
        super(ownerId);
    } // end constructor

    public int[] getResourceCost() {
        return this.resourceCost;
    }

    public int getVictoryPoints() {
        return 1;
    }

    @Override
    public String toString() {
        return "Settlement{" +
                "resourceCost=" + Arrays.toString(resourceCost) +
                '}';
    }
} // end Class
