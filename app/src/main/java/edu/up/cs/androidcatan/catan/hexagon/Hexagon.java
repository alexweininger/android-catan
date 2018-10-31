package edu.up.cs.androidcatan.catan.hexagon;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 30th, 2018
 * https://github.com/alexweininger/android-catan
 **/

public class Hexagon {
    private int resourceId;
    private int chitValue;

    /**
     * Hexagon constructor AW
     *
     * @param resourceType - resourceType type of hexagon
     * @param chitValue    - dice value of hexagon
     */
    public Hexagon(int resourceType, int chitValue) {
        this.resourceId = resourceType;
        this.chitValue = chitValue;
    }

    /**
     * @return - hexagon resource id, [0-4]
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * @return
     */
    public int getChitValue() {
        return chitValue;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("Hexagon{ ");
        sb.append("resourceType: ");
        sb.append(resourceId);
        sb.append(", chitValue: ");
        sb.append(chitValue);
        sb.append("}");

        return sb.toString();
    }
}
