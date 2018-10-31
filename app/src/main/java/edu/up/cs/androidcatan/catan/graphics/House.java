package edu.up.cs.androidcatan.catan.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class House {

    protected int x, y;
    protected int size;
    protected int color;

    public House(int x, int y){

        this.x = x;
        this.y = y;
        this.size = 50;
        this.color = Color.CYAN;
    }
    public House(int x, int y, int color){

        this.x = x;
        this.y = y;
        this.size = 50;
        this.color = color;
    }

    public void drawHouse(Canvas canvas){

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x+size, y+size, paint);
    }
}
