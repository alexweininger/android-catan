package edu.up.cs.androidcatan.catan.gamestate;

import android.graphics.Path;

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
        this.resourceId = resourceType;
        this.chitValue = chitValue;
        this.hexagonId = hexagonId;
    }

    public Hexagon (Hexagon h) {
        this.setChitValue(h.getChitValue());
        this.setResourceId(h.getResourceId());
    }

    /**
     * @return - hexagon resource id, [0-4]
     */
    public int getResourceId () {
        return resourceId;
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
     * createHexagonPath() creates a Path object from given hexagon corner x and y values
     *
     * @param corners - 2d array of x and y cords for the corners
     * @return Path
     */
    public Path createHexagonPath (int[][] corners) {
        Path hexagonPath = new Path();
        hexagonPath.moveTo(corners[0][0], corners[0][1]);

        for (int i = 1; i < corners.length; i++) {
            hexagonPath.lineTo(corners[i][0], corners[i][1]);
        }
        hexagonPath.close();

        return hexagonPath;
    }

    /**
     * calculateHexagonPoints() generates an array of points (x, y) for the corners of a hexagon
     *
     * @param x - x position
     * @param y - y position
     * @param size - size, measured from center to a corner
     * @return int[][]
     */
    public static int[][] calculateHexagonPoints (int x, int y, int size) {
        int[][] points = new int[6][2];
        double angle_deg, angle_rad;

        for (int i = 0; i < 6; i++) {
            angle_deg = 60 * i - 30;
            angle_rad = Math.PI / 180 * angle_deg;

            points[i][0] = (int) (x + size * Math.cos(angle_rad));
            points[i][1] = (int) (y + size * Math.sin(angle_rad));
        }
        return points;
    }

    /**
     * @return String representing the Hexagon object.
     */
    @Override
    public String toString () {
        return "id=" + this.hexagonId + "\tresId=" + this.resourceId + "\tchit=" + this.chitValue;
    }
}
