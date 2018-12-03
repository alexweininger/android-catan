package edu.up.cs.androidcatan.catan.gamestate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.graphics.IntersectionDrawable;

import static android.content.ContentValues.TAG;

public class Port {
    private int intersectionA, intersectionB, tradeRatio, resourceId;
    private int xPos, yPos, size;

    public Port (int intersectionA, int intersectionB, int tradeRatio, int resourceId) {
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
    public Port (Port p) {
        this.setIntersectionA(p.getIntersectionA());
        this.setTradeRatio(p.getTradeRatio());
        this.setResourceId(p.getResourceId());
        this.setIntersectionB(p.getIntersectionB());
        this.setSize(p.getSize());
        this.setXPos(p.getXPos());
        this.setYPos(p.getYPos());
    }

    /**
     * @param canvas Canvas to draw the port on.
     */
    public void drawPort (Canvas canvas, int xPos, int yPos, int size, Context context, IntersectionDrawable a, IntersectionDrawable b, boolean debugMode) {

        int[] resourceDrawables = {R.drawable.brick_icon_25x25, R.drawable.grain_icon_25x25, R.drawable.lumber_icon_25x25, R.drawable.ore_icon_25x25, R.drawable.wool_icon_25x25};

        this.xPos = xPos;
        this.yPos = yPos;
        this.size = size;

        Paint portLinePaint = new Paint();
        portLinePaint.setColor(Color.BLUE);
        portLinePaint.setStrokeWidth(10);

        if (debugMode) {
            canvas.drawLine(xPos, yPos, b.getXPos(), b.getYPos(), portLinePaint);
            canvas.drawLine(xPos, yPos, a.getXPos(), a.getYPos(), portLinePaint);
        }

        Drawable portPicture = context.getDrawable(R.drawable.port_boat);
        if (portPicture != null) {
            portPicture.setBounds(xPos - size, yPos - size, xPos + size, yPos + size);
            portPicture.draw(canvas);
        } else {
            Log.e(TAG, "drawPort: portPicture is null", new NullPointerException());
        }

        size = (size / 2 < 20)? 20:(size / 2);
        int offset = 30;

        Paint ratioFont = new Paint();
        ratioFont.setTextSize(30);
        ratioFont.setColor(Color.WHITE);

        if (resourceId != -1) {
            Drawable resourcePicture = context.getDrawable(resourceDrawables[this.resourceId]);
            if (resourcePicture != null) {
                resourcePicture.setBounds(xPos - size + offset, yPos - size + offset, xPos + size + offset, yPos + size + offset);
                resourcePicture.draw(canvas);
            } else {
                Log.e(TAG, "drawPort: portPicture is null", new NullPointerException());
            }
            canvas.drawText("" + tradeRatio, xPos + offset, yPos, ratioFont);
        } else {
            canvas.drawText("" + tradeRatio, xPos + offset, yPos, ratioFont);
        }
    }

    public int getIntersectionA () {
        return intersectionA;
    }

    public int getTradeRatio () {
        return tradeRatio;
    }

    public int getResourceId () {
        return resourceId;
    }

    public void setIntersectionA (int intersectionA) {
        this.intersectionA = intersectionA;
    }

    private void setTradeRatio (int tradeRatio) {
        this.tradeRatio = tradeRatio;
    }

    public void setResourceId (int resourceId) {
        this.resourceId = resourceId;
    }

    private int getXPos () {
        return xPos;
    }

    private void setXPos (int xPos) {
        this.xPos = xPos;
    }

    private int getYPos () {
        return yPos;
    }

    private void setYPos (int yPos) {
        this.yPos = yPos;
    }

    public int getSize () {
        return size;
    }

    public void setSize (int size) {
        this.size = size;
    }

    public int getIntersectionB () {
        return intersectionB;
    }

    public void setIntersectionB (int intersectionB) {
        this.intersectionB = intersectionB;
    }

    @Override
    public String toString () {
        return "{" + "intersectionA=" + intersectionA + " rate=" + tradeRatio + " res=" + resourceId + '}';
    }
}
