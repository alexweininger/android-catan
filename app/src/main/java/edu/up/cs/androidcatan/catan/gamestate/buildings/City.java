package edu.up.cs.androidcatan.catan.gamestate.buildings;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class City extends Building implements Serializable {

    public static final int[] resourceCost = {0, 2, 0, 3, 0}; // Brick, Grain, Lumber, Ore, Wool
    private static final long serialVersionUID = 8273227009457819686L;

    /**
     * @param ownerId id of who owns the building
     */
    public City(int ownerId) {
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
