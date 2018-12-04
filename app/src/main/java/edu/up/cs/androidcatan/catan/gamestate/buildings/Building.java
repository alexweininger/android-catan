package edu.up.cs.androidcatan.catan.gamestate.buildings;

import java.io.Serializable;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public abstract class Building implements Serializable {

    private static final long serialVersionUID = 5405656877589675668L;
    private int ownerId = -1;
    /**
     * Building constructor
     *
     * @param ownerId - player who owns and built building
     */
    public Building(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return this.ownerId;
    }
    public void setOwnerId(int ownerId) {
        if (ownerId < 0 || ownerId > 3) {
            return;
        }
        this.ownerId = ownerId;
    }

    abstract public int getVictoryPoints();
    abstract public String toString();
}
