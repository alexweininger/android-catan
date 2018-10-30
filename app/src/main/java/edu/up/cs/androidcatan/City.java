package edu.up.cs.androidcatan;

import java.util.HashMap;

/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 24th, 2018
 * https://github.com/alexweininger/game-state
 **/

public class City extends Building {

    private int intersectionID;
    private static HashMap<String, Integer> checkResources = new HashMap<>();
    private int productionNumber = 2;
    private final static int[] resourceCost = {0, 3, 0, 2, 0};

    /**
     * @param ownerId id of who owns the building
     */
    public City(int intersectionID, int ownerId) {
        super(ownerId);
        this.intersectionID = intersectionID;
        HashMap<String, Integer> checkResources = new HashMap<String, Integer>();
    }


    public static void cityResourcePriceMake() {
        checkResources.put("ore", 3);
        checkResources.put("grain", 2);
    }

    /**
     * @return string representation of a City
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("City toString()\n");
        sb.append(super.toString());

        return sb.toString();
    }
}
