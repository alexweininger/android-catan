package edu.up.cs.androidcatan.catan.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.Serializable;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class RoadDrawable implements Serializable {

    private int[][] points;
    int position;
    private int[][] roadPoints = {{0,0},{0,0}, {0,0}, {0,0}};

    public RoadDrawable(int[][] points, int position) {
        this.points = points;
        this.position = position;
    }

    public void drawRoad(Canvas canvas) {

        int inP = this.position;
        int outP = this.position + 1;

        Paint line = new Paint();
        line.setColor(Color.DKGRAY);
        line.setStyle(Paint.Style.FILL);
        line.setStrokeWidth(10);

        roadPoints[0][0] = this.points[outP][0];
        roadPoints[0][1] = this.points[outP][1];

        roadPoints[1][0] = this.points[inP][0];
        roadPoints[1][1] = this.points[inP][1];

        canvas.drawLine(roadPoints[0][0], roadPoints[0][1], roadPoints[1][0], roadPoints[1][1], line);

    }
}
