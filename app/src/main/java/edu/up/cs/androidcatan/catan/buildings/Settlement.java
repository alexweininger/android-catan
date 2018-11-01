package edu.up.cs.androidcatan.catan.buildings;

import java.util.Arrays;

import edu.up.cs.androidcatan.catan.buildings.Building;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 30th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class Settlement extends Building {

    public final static int[] resourceCost = {1, 1, 1, 0, 1}; // Brick, Grain, Lumber, Ore, Wool

    /**
     * @param ownerId - player id of who owns the settlement
     */
    public Settlement(int ownerId) {
        super(ownerId);
    } // end constructor

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
