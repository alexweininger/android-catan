package edu.up.cs.androidcatan.catan.gamestate.buildings;

import java.io.Serializable;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class Road extends Building implements Serializable {

    private static final long serialVersionUID = -2163345533367819700L;

    private int intersectionAId, intersectionBId; // intersections the road is located
    public static final int[] resourceCost = {1, 0, 1, 0, 0}; // Brick, Grain, Lumber, Ore, Wool

    /**
     * @param intersectionAId 1st intersection of road
     * @param intersectionBId 2nd intersection of road
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

    @Override
    public String toString() {
        return "{ p=" + this.getOwnerId() + " from=" + intersectionAId + " to=" + intersectionBId + " }";
    }
}
