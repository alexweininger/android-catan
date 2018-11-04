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

    // drawing variables
    protected int color;
    protected int size;
    protected int xPos;
    protected int yPos;

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
