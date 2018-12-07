package edu.up.cs.androidcatan.catan.gamestate.buildings;

import java.io.Serializable;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 * <p>
 * Class representing the Settlement building in the game of SoC.
 **/

public class Settlement extends Building implements Serializable {
    private static final long serialVersionUID = 71517631622057343L;

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
        return "[ S pId=" + this.getOwnerId() + "]";
    }
} // end Class
