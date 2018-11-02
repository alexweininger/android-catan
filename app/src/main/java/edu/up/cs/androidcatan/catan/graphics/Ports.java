package edu.up.cs.androidcatan.catan.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
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
