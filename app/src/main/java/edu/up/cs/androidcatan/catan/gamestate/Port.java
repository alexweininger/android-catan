package edu.up.cs.androidcatan.catan.gamestate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import edu.up.cs.androidcatan.R;

public class Port {
    private int intersectionA, intersectionB, tradeRatio, resourceId;
    private int xPos, yPos, size;

    /**
     * @param intersectionA
     * @param tradeRatio
     * @param resourceId
     */
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
    }

    /**
     * @param canvas Canvas to draw the port on.
     */
    public void drawPort (Canvas canvas, int xPos, int yPos, int size, Context context) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.size = size;

        Drawable portPicture = context.getDrawable(R.drawable.port_boat);
        portPicture.setBounds(xPos - 40, yPos - 40, xPos + 40, yPos + 40);
        portPicture.draw(canvas);
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

    public void setTradeRatio (int tradeRatio) {
        this.tradeRatio = tradeRatio;
    }

    public void setResourceId (int resourceId) {
        this.resourceId = resourceId;
    }

    public int getxPos () {
        return xPos;
    }

    public void setxPos (int xPos) {
        this.xPos = xPos;
    }

    public int getyPos () {
        return yPos;
    }

    public void setyPos (int yPos) {
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
