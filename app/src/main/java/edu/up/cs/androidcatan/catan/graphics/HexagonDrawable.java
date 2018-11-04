package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Random;

public class HexagonDrawable extends BoardSurfaceView {

    protected int x;
    protected int y;
    protected int size;
    protected int color;

    protected Path hexagonPath;
    protected int[][] points;

    protected boolean isRobber;
    protected boolean isDesert;
    protected int chitValue;

    public HexagonDrawable(Context context, int x, int y, int size, int color, boolean isRobber, boolean isDesert, int chitValue) {
        super(context);
        setWillNotDraw(false);

        this.x = x;
        this.y = y;
        this.size = size; // size can also be thought of as the radius
        this.color = color;
        this.isDesert = isDesert;
        this.isRobber = isRobber;
        this.chitValue = chitValue;
    }

    public  void drawHexagon(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.color);
        paint.setStyle(Paint.Style.FILL);

        Random random = new Random();

        points = calculateHexagonPoints(this.x, this.y, this.size);

        Path hexagonPath = createHexagonPath(points);
        canvas.drawPath(hexagonPath, paint);

        Paint blackFont = new Paint();
        blackFont.setColor(Color.BLACK);
        blackFont.setStyle(Paint.Style.FILL);

        blackFont.setTextSize(50);

        Paint robberPaint = new Paint();
        robberPaint.setColor(Color.DKGRAY);
        robberPaint.setStyle(Paint.Style.FILL);

        if (!this.isDesert) {
            if (this.chitValue == 6 || this.chitValue == 8) {
                blackFont.setColor(Color.argb(255, 163, 40, 40));
            }
            if (this.chitValue < 10) {
                canvas.drawText("" + this.chitValue, points[5][0] - 15, points[5][1] + this.size / 2, blackFont);
            } else {
                canvas.drawText("" + this.chitValue, points[5][0] - 25, points[5][1] + this.size / 2, blackFont);
            }
        }

        int radius = 40;
        int cx = points[5][0];
        int cy = points[5][1] + this.size;

        if(this.isRobber) {
            canvas.drawCircle(cx, cy, radius, robberPaint);
        }

        Paint intersectionPaint = new Paint();
        intersectionPaint.setColor(Color.DKGRAY);
        intersectionPaint.setStyle(Paint.Style.STROKE);

        // draw a circle at each intersection
        for (int i = 0; i < 6; i++) {
            // canvas.drawCircle(points[i][0], points[i][1], 50, intersectionPaint);
        }
    }

    /** calculateHexagonPoints() generates an array of points (x, y) for the corners of a hexagon
     * @param x - x position
     * @param y - y position
     * @param size - size, measured from center to a corner
     * @return int[][]
     */
    public int[][] calculateHexagonPoints(int x, int y, int size) {
        int[][] points = new int[6][2];
        double angle_deg, angle_rad;

        for (int i = 0; i < 6; i++) {

            angle_deg = 60 * i - 30;
            angle_rad = Math.PI / 180 * angle_deg;

            points[i][0] = (int) (x + size * Math.cos(angle_rad));
            points[i][1] = (int) (y + size * Math.sin(angle_rad));

            // Log.d("user", "\nx: " + points[i][0] + " y: " + points[i][1]);
        }
        this.points = points;
        return points;
    }

	/** createHexagonPath() creates a Path object from given hexagon corner x and y values
	 * @param corners - 2d array of x and y cords for the corners
	 * @return Path
	 */
    public Path createHexagonPath(int[][] corners) {
        hexagonPath = new Path();
        hexagonPath.moveTo(corners[0][0], corners[0][1]);

        for(int i = 1; i < corners.length; i++) {
            hexagonPath.lineTo(corners[i][0], corners[i][1]);
        }
        hexagonPath.close();

        return hexagonPath;
    }

    public int[][] getHexagonPoints() {
        return this.points;
    }

}
