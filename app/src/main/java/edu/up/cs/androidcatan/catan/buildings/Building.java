package edu.up.cs.androidcatan.catan.buildings;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 24th, 2018
 * https://github.com/alexweininger/game-state
 **/

public abstract class Building {

    private int ownerId;

    /**
     * Building constructor
     *
     * @param ownerId - player who owns and built building
     */
    public Building(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    abstract public int getVictoryPoints();

    abstract public String toString();

    public int getOwnerId() {
        return this.ownerId;
    }
}
