package edu.up.cs.androidcatan;

import java.util.HashMap;

/** Settlement class
 * @author Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version October th, 2018
 * https://github.com/alexweininger/game-state
 **/
public class Settlement extends Building {

    private int intersectionID;
    private static HashMap<String, Integer> checkResources = new HashMap<>();

    /**
     *
     * @param ownerId - player id of who owns the settlement
     */
    public Settlement(int intersectionID, int ownerId) {
        super("Settlement", 1, ownerId);
        this.intersectionID = intersectionID;
        HashMap<String, Integer> checkResources = new HashMap<String, Integer>();
    } // end constructor

    /**
     *
     * @return string representation of a settlement
     */
    @Override
    public String toString() {
        return super.toString();
    } // end toString

    public static void cityResourcePriceMake(){
        checkResources.put("brick", 3);
        checkResources.put("grain", 2);
        checkResources.put("wood", 3);
        checkResources.put("wool", 2);
    }
} // end Class
