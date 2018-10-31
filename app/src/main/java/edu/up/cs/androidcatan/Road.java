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
public class Road extends Building {

    private int intersectionAId, intersectionBId;
    private final int[] resourceCost = {1, 0, 1, 0, 0}; // Brick, Grain, Lumber, Ore, Wool

    /**
     * @param intersectionAId -
     * @param intersectionBId -
     */
    Road(int intersectionAId, int intersectionBId, int ownerId) {
        super(ownerId);

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

    public int[] getResourceCost() {
        return this.resourceCost;
    }

    public int getIntersectionAId() {
        return this.intersectionAId;
    }

    public int getIntersectionBId() {
        return this.intersectionBId;
    }

    public int getVictoryPoints() {
        return 0;
    }

    @Override
    public String toString() {
        return "Road{" +
                "intersectionAId=" + intersectionAId +
                ", intersectionBId=" + intersectionBId +
                ", resourceCost=" + Arrays.toString(resourceCost) +
                '}';
    }
}
