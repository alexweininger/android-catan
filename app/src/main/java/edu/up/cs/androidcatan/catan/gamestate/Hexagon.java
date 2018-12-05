package edu.up.cs.androidcatan.catan.gamestate;

import java.io.Serializable;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 8th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class Hexagon implements Serializable {

    private static final long serialVersionUID = 675408522730573292L;
    // instance variables
    private int resourceId, chitValue, hexagonId;

    /**
     * Hexagon constructor AW
     *
     * @param resourceType - resourceType type of hexagon
     * @param chitValue - dice value of hexagon
     */
    public Hexagon (int resourceType, int chitValue, int hexagonId) {
        this.hexagonId = hexagonId;
        this.resourceId = resourceType;
        this.chitValue = chitValue;
    }

    /**
     * Copy constructor for a Hexagon object.
     *
     * @param h Hexagon object to create a copy from.
     */
    public Hexagon (Hexagon h) {
        this.setHexagonId(h.getHexagonId());
        this.setChitValue(h.getChitValue());
        this.setResourceId(h.getResourceId());
    }

    /**
     * @return - hexagon resource id, [0-4]
     */
    public int getResourceId () {
        return resourceId;
    }

    public void setHexagonId (int hexagonId) {
        this.hexagonId = hexagonId;
    }

    /**
     * @return chit value of the hexagon
     */
    public int getChitValue () {
        return chitValue;
    }

    public void setResourceId (int resourceId) {
        this.resourceId = resourceId;
    }

    public void setChitValue (int chitValue) {
        this.chitValue = chitValue;
    }

    public int getHexagonId () {
        return this.hexagonId;
    }

    /**
     * @return String representing the Hexagon object.
     */
    @Override
    public String toString () {
        return "id=" + this.hexagonId + "\tresId=" + this.resourceId + "\tchit=" + this.chitValue;
    }
}
