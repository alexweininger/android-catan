package edu.up.cs.androidcatan.catan.gamestate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.Serializable;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.graphics.IntersectionDrawable;

import static android.content.ContentValues.TAG;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class Port implements Serializable {
    private static final long serialVersionUID = 6074407408138083737L;

    // representing each port
    private int intersectionA, intersectionB; // connected intersections
    private int tradeRatio, resourceId; // ratio and resource (-1 if mystery port)
    private int xPos, yPos, size; // position and size of port

    // constructor
    public Port(int intersectionA, int intersectionB, int tradeRatio, int resourceId) {
        this.intersectionA = intersectionA;
        this.intersectionB = intersectionB;
        this.tradeRatio = tradeRatio;
        this.resourceId = resourceId;
    }

    /**
     * Port copy constructor
     *
     * @param p Port to copy
     */
    public Port(Port p) {
        this.setIntersectionA(p.getIntersectionA());
        this.setTradeRatio(p.getTradeRatio());
        this.setResourceId(p.getResourceId());
        this.setIntersectionB(p.getIntersectionB());
        this.setSize(p.getSize());
        this.setXPos(p.getXPos());
        this.setYPos(p.getYPos());
    }

    /**
     * Draws a port on the canvas.
     *
     * @param canvas Canvas to draw the port on.
     */
    public void drawPort(Canvas canvas, int xPos, int yPos, int size, Context context, IntersectionDrawable a, IntersectionDrawable b, boolean debugMode) {

        int[] resourceDrawables = {R.drawable.brick_icon_25x25, R.drawable.grain_icon_25x25, R.drawable.lumber_icon_25x25, R.drawable.ore_icon_25x25, R.drawable.wool_icon_25x25};

        // x, y, and size variables
        this.xPos = xPos;
        this.yPos = yPos;
        this.size = size;

        // the port drawable (image)
        Drawable portPicture = context.getDrawable(R.drawable.port_boat);
        if (portPicture != null) { // make sure drawable is not null
            // set the drawable bounds
            portPicture.setBounds(xPos - size, yPos - size, xPos + size, yPos + size);
            portPicture.draw(canvas); // draw the drawable
        } else {
            Log.e(TAG, "drawPort: portPicture is null", new NullPointerException());
        }

        // if the size / 2 is less than 20 then make the size 20
        size = (size / 2 < 20) ? 20 : (size / 2);
        int offset = 30;

        // the font used to put the trade ratio on the port
        Paint ratioFont = new Paint();
        ratioFont.setTextSize(30);
        ratioFont.setColor(Color.WHITE);

        // if the port is not a mystery port (indicated by having a -1 resourceId)
        if (resourceId != -1) {
            // get the resource drawable
            Drawable resourcePicture = context.getDrawable(resourceDrawables[this.resourceId]);
            // check if the drawable is not null
            if (resourcePicture != null) {
                // set the bounds and draw the drawable
                resourcePicture.setBounds(xPos - size + offset, yPos - size + offset, xPos + size + offset, yPos + size + offset);
                resourcePicture.draw(canvas);
            } else {
                Log.e(TAG, "drawPort: portPicture is null", new NullPointerException());
            }
            // draw the ratio
            canvas.drawText("" + tradeRatio + ":1", xPos + offset, yPos, ratioFont);
        } else {
            // draw the ratio
            canvas.drawText("" + tradeRatio + ":1", xPos + offset, yPos, ratioFont);
        }
    }

    // getters and setters

    public int getIntersectionA() {
        return intersectionA;
    }

    public int getTradeRatio() {
        return tradeRatio;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setIntersectionA(int intersectionA) {
        this.intersectionA = intersectionA;
    }

    private void setTradeRatio(int tradeRatio) {
        this.tradeRatio = tradeRatio;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    private int getXPos() {
        return xPos;
    }

    private void setXPos(int xPos) {
        this.xPos = xPos;
    }

    private int getYPos() {
        return yPos;
    }

    private void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getIntersectionB() {
        return intersectionB;
    }

    public void setIntersectionB(int intersectionB) {
        this.intersectionB = intersectionB;
    }

    // toString
    @Override
    public String toString() {
        return "{" + "intersectionA=" + intersectionA + " rate=" + tradeRatio + " res=" + resourceId + '}';
    }
}
