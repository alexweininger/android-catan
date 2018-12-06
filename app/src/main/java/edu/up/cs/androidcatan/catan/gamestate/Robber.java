package edu.up.cs.androidcatan.catan.gamestate;

import java.io.Serializable;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class Robber implements Serializable {

    private static final long serialVersionUID = -3386481719747647524L;

    private int hexagonId; // hexagon where the robber is located

    /**
     * Robber constructor
     *
     * @param currentHexagonId - where the robber is currently
     */
    public Robber(int currentHexagonId) {
        this.hexagonId = currentHexagonId;
    }

    //deep copy constructor
    public Robber(Robber r) {
        this.hexagonId = r.hexagonId;
    }

    //sets the new position of the Robber to be moved
    public void setHexagonId(int newHexagonId) {
        this.hexagonId = newHexagonId;
    }

    //returns the current location of the Robber
    public int getHexagonId() {
        return hexagonId;
    }

    // toString
    @Override
    public String toString() {
        return "Robber toString(): the robber is at " + this.hexagonId;
    } // end robber toString
}
