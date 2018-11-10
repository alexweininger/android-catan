package edu.up.cs.androidcatan.catan.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class IntersectionDrawable {
    int intersectionId;
    int xPos;
    int yPos;

    public IntersectionDrawable (int id, int x, int y) {
        this.intersectionId = id;
        this.xPos = x;
        this.yPos = y;
    }

    public void drawIntersection(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(32);

        canvas.drawText("" + intersectionId, xPos, yPos, paint);
    }

    public int getIntersectionId() {
        return intersectionId;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

}
