package edu.up.cs.androidcatan;

import java.util.HashMap;

/**
 * Settlement class
 *
 * @author Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version October th, 2018
 * https://github.com/alexweininger/game-state
 **/
public class Settlement extends Building {

    private static int victoryPoints = 1;
    private static final int[] resourceCost = {1, 0, 1, 1, 1};

    /**
     * @param ownerId - player id of who owns the settlement
     */
    public Settlement(int ownerId) {
        super(ownerId);
    } // end constructor

    public int[] getResourceCost() {
        return resourceCost;
    }

    /**
     * @return string representation of a settlement
     */
    @Override
    public String toString() {
        return super.toString();
    } // end toString
} // end Class
