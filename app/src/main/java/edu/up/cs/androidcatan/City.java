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

    /**
     *
     * @param ownerId id of who owns the building
     */
    public City(int intersectionID, int ownerId) {
        super("City", 2, ownerId);
        this.intersectionID = intersectionID;
        HashMap<String, Integer> checkResources = new HashMap<String, Integer>();
    }

    /**
     *
     * @return string representation of a City
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("City toString()\n");
        sb.append(super.toString());

        return sb.toString();
    }

    public static void cityResourcePriceMake(){
        checkResources.put("ore", 3);
        checkResources.put("grain", 2);
    }

    public int getProductionNumber() {
        return productionNumber;
    }

    public void setProductionNumber(int productionNumber) {
        this.productionNumber = productionNumber;
    }
}
