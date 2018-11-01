package edu.up.cs.androidcatan.catan.buildings;

import java.util.Arrays;

import edu.up.cs.androidcatan.catan.buildings.Building;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class Road extends Building {

    private int intersectionAId, intersectionBId;
    public static final int[] resourceCost = {1, 0, 1, 0, 0}; // Brick, Grain, Lumber, Ore, Wool

    /**
     * @param intersectionAId -
     * @param intersectionBId -
     */
    public Road(int playerId, int intersectionAId, int intersectionBId) {
        super(playerId);

        this.intersectionAId = intersectionAId;
        this.intersectionBId = intersectionBId;
    }

    /**
     * @param intersectionId - intersection id of one end of the road
     * @return - the intersection id of the other intersection the road is connected to
     */
    public int getOppositeIntersection(int intersectionId) {
        if (intersectionId == this.intersectionAId) {
            return this.intersectionBId;
        } else {
            return this.intersectionAId;
        }
    }

    // getters

    public int getIntersectionAId() {
        return this.intersectionAId;
    }

    public int getIntersectionBId() {
        return this.intersectionBId;
    }

    public int getVictoryPoints() {
        return 0;
    }

    public static int[] getResourceCost() { return resourceCost;}

    @Override
    public String toString() {
        return "Road{" +
                "intersectionAId=" + intersectionAId +
                ", intersectionBId=" + intersectionBId +
                ", resourceCost=" + Arrays.toString(resourceCost) +
                '}';
    }
}
