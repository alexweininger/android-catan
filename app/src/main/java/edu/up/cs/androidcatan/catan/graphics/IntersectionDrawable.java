package edu.up.cs.androidcatan.catan.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class IntersectionDrawable {
    private int intersectionId, xPos, yPos;

    IntersectionDrawable (int id, int x, int y) {
        this.intersectionId = id;
        this.xPos = x;
        this.yPos = y;
    }

    void drawIntersection (Canvas canvas, boolean debugMode) {
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
