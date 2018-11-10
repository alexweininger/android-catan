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
     */
    public Port (int intersection, int tradeRatio, int resourceId) {
        this.intersection = intersection;
        this.tradeRatio = tradeRatio;
        this.resourceId = resourceId;
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

    /**
     * @param canvas Canvas to draw the port on.
     * @param xPos X position of the port.
     * @param yPos Y position of the port.
     * @param size Size of the port.
     */
    public void drawPort (Canvas canvas, int xPos, int yPos, int size) {

        this.size = size;
        this.xPos = xPos;
        this.yPos = yPos;

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
