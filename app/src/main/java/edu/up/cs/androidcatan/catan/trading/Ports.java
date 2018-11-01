package edu.up.cs.androidcatan.catan.trading;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ports {

    protected int x, y;
    protected int size;
    protected int color;
    protected Paint paint;

    public Ports(int x, int y){

        this.x = x;
        this.y = y;
        this.size = 50;
        this.color = Color.GRAY;
        paint = new Paint();

    }

    public void drawPort(Canvas canvas){

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, size, paint);
    }

}
