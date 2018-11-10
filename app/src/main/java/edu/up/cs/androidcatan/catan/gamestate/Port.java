package edu.up.cs.androidcatan.catan.gamestate;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Port {
    private int intersection, tradeRatio, resourceId;
    private int xPos, yPos, size;

    /**
     * @param intersection
     * @param tradeRatio
     * @param resourceId
     * @param xPos
     * @param yPos
     * @param size
     */
    public Port (int intersection, int tradeRatio, int resourceId, int xPos, int yPos, int size) {
        this.intersection = intersection;
        this.tradeRatio = tradeRatio;
        this.resourceId = resourceId;
        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Port copy constructor
     *
     * @param p Port to copy
     */
    public Port (Port p) {
        this.setIntersection(p.getIntersection());
        this.setTradeRatio(p.getTradeRatio());
        this.setResourceId(p.getResourceId());
    }

    public void drawPort (Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(xPos, yPos, size, paint);
    }

    public int getIntersection () {
        return intersection;
    }

    public int getTradeRatio () {
        return tradeRatio;
    }

    public int getResourceId () {
        return resourceId;
    }

    public void setIntersection (int intersection) {
        this.intersection = intersection;
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

    @Override
    public String toString () {
        return "{" + "intersection=" + intersection + " rate=" + tradeRatio + " res=" + resourceId + '}';
    }
}
