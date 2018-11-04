package edu.up.cs.androidcatan.catan.gamestate;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class Hexagon {

    // instance variables
    // game variables
    private int resourceId;
    private int chitValue;

    /**
     * Hexagon constructor AW
     *
     * @param resourceType - resourceType type of hexagon
     * @param chitValue - dice value of hexagon
     */
    public Hexagon(int resourceType, int chitValue) {
        this.resourceId = resourceType;
        this.chitValue = chitValue;
    }

    public Hexagon(Hexagon h) {
        this.setChitValue(h.getChitValue());
        this.setResourceId(h.getResourceId());
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

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public void setChitValue(int chitValue) {
        this.chitValue = chitValue;
    }

    public void drawHexagon(Canvas canvas) {
        Paint paint = new Paint();

    }

    /**
     * calculateHexagonPoints() generates an array of points (x, y) for the corners of a hexagon
     *
     * @param x - x position
     * @param y - y position
     * @param size - size, measured from center to a corner
     * @return int[][]
     */
    public int[][] calculateHexagonPoints(int x, int y, int size) {
        int[][] points = new int[6][2];
        double angle_deg, angle_rad;

        for (int i = 0; i < 6; i++) {

            angle_deg = 60 * i - 30;
            angle_rad = Math.PI / 180 * angle_deg;

            points[i][0] = (int) (x + size * Math.cos(angle_rad));
            points[i][1] = (int) (y + size * Math.sin(angle_rad));

            // Log.d("user", "\nx: " + points[i][0] + " y: " + points[i][1]);
        }
        this.points = points;
        return points;
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
