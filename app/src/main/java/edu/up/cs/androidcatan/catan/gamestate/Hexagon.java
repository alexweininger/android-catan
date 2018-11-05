package edu.up.cs.androidcatan.catan.gamestate;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class Hexagon {

    // instance variables
    // game variables
    private int resourceId;
    private int chitValue;
    private int hexagonId;

    /**
     * Hexagon constructor AW
     *
     * @param resourceType - resourceType type of hexagon
     * @param chitValue - dice value of hexagon
     */
    public Hexagon (int resourceType, int chitValue, int hexagonId) {
        this.resourceId = resourceType;
        this.chitValue = chitValue;
        this.hexagonId = hexagonId;
    }

    public Hexagon (Hexagon h) {
        this.setChitValue(h.getChitValue());
        this.setResourceId(h.getResourceId());
    }

    /**
     * @return - hexagon resource id, [0-4]
     */
    public int getResourceId () {
        return resourceId;
    }

    /**
     * @return
     */
    public int getChitValue () {
        return chitValue;
    }

    public void setResourceId (int resourceId) {
        this.resourceId = resourceId;
    }

    public void setChitValue (int chitValue) {
        this.chitValue = chitValue;
    }

    public int getHexagonId () {
        return this.hexagonId;
    }

    public void drawHexagon (Canvas canvas, int color, int xPos, int yPos, int size, boolean isRobber) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        int[][] points = calculateHexagonPoints(xPos, yPos, size);

        Path hexagonPath = createHexagonPath(points);
        canvas.drawPath(hexagonPath, paint);

        Paint blackFont = new Paint();
        blackFont.setColor(Color.BLACK);
        blackFont.setStyle(Paint.Style.FILL);

        blackFont.setTextSize(50);

        Paint robberPaint = new Paint();
        robberPaint.setColor(Color.MAGENTA);
        robberPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < points.length; i++) {
            Log.e("Hexagon", "drawHexagon: hello");
            canvas.drawCircle(points[i][0], points[i][1], 25, robberPaint);
        }

        if (isRobber) {

            canvas.drawCircle(points[3][0] + size, points[3][1] - size / 2, 25, robberPaint);
        } else {
            canvas.drawText("" + this.chitValue, points[3][0] + size / 2, points[3][1] - size / 2, blackFont);
        }

        //        RoadDrawable road = new RoadDrawable(points, random.nextInt(4));
        //        road.drawRoad(canvas);
    }

    /**
     * createHexagonPath() creates a Path object from given hexagon corner x and y values
     *
     * @param corners - 2d array of x and y cords for the corners
     * @return Path
     */
    public Path createHexagonPath (int[][] corners) {
        Path hexagonPath = new Path();
        hexagonPath.moveTo(corners[0][0], corners[0][1]);

        for (int i = 1; i < corners.length; i++) {
            hexagonPath.lineTo(corners[i][0], corners[i][1]);
        }
        hexagonPath.close();

        return hexagonPath;
    }

    /**
     * calculateHexagonPoints() generates an array of points (x, y) for the corners of a hexagon
     *
     * @param x - x position
     * @param y - y position
     * @param size - size, measured from center to a corner
     * @return int[][]
     */
    public int[][] calculateHexagonPoints (int x, int y, int size) {
        int[][] points = new int[6][2];
        double angle_deg, angle_rad;

        for (int i = 0; i < 6; i++) {

            angle_deg = 60 * i - 30;
            angle_rad = Math.PI / 180 * angle_deg;

            points[i][0] = (int) (x + size * Math.cos(angle_rad));
            points[i][1] = (int) (y + size * Math.sin(angle_rad));

            // Log.d("user", "\nx: " + points[i][0] + " y: " + points[i][1]);
        }
        return points;
    }

    /**
     * @return String representing the Hexagon object.
     */
    @Override
    public String toString () {
        return "id=" + this.hexagonId + "\tresId=" + this.resourceId + "\tchit=" + this.chitValue;
    }
}
