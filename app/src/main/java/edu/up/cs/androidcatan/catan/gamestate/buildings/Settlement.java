package edu.up.cs.androidcatan.catan.gamestate.buildings;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class Settlement extends Building {

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
