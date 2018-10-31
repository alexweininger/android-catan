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

public class City extends Building {

    public static final int[] resourceCost = {0, 2, 0, 3, 0}; // Brick, Grain, Lumber, Ore, Wool

    /**
     * @param ownerId id of who owns the building
     */
    public City(int intersectionID, int ownerId) {
        super(ownerId);
    }

    @Override
    public int getVictoryPoints() {
        return 2;
    }

    @Override
    public String toString() {
        return "City{" +
                "resourceCost=" + Arrays.toString(resourceCost) +
                '}';
    }
}
