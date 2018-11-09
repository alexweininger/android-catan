package edu.up.cs.androidcatan.catan.gamestate;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 8th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class Hexagon {

    // instance variables
    private int resourceId;
    private int chitValue;
    private int hexagonId;

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
