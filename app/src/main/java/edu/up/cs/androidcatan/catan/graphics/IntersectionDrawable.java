package edu.up.cs.androidcatan.catan.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class IntersectionDrawable {
    private int intersectionId, xPos, yPos;

    /**
     * IntersectionDrawable conatructor
     * @param id he id of the intersection
     * @param x the x position
     * @param y the y position
     */
    IntersectionDrawable (int id, int x, int y) {
        this.intersectionId = id;
        this.xPos = x;
        this.yPos = y;
    }

    /**
     * drawing it on the board
     * @param canvas canvas being drawn on
     * @param debugMode true or false to show debugMode or not
     */
    void drawIntersection(Canvas canvas, boolean debugMode) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(42);

        if (debugMode) canvas.drawText("" + intersectionId, xPos, yPos, paint);
    }

    public int getIntersectionId () {
        return this.intersectionId;
    }

    public int getXPos () {
        return this.xPos;
    }

    public int getYPos () {
        return this.yPos;
    }

}
