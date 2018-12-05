package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import java.io.Serializable;

import edu.up.cs.androidcatan.R;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 8th, 2018
 * https://github.com/alexweininger/android-catan
 **/

public class HexagonDrawable extends BoardSurfaceView implements Serializable {

    private static final String TAG = "HexagonDrawable"; // log tag

    // instance variables concerning graphics
    private Context context;
    protected int x, y;
    protected Path hexagonPath;
    protected int[][] points;
    protected int size;
    protected int color;
    private boolean highlight;
    protected int resourceId;

    // instance variables concerning game logic
    protected int hexagonId;
    protected int chitValue;
    protected boolean isRobber, isDesert;

    static int[] resourceDrawables = {R.drawable.brick_icon_25x25, R.drawable.grain_icon_25x25, R.drawable.lumber_icon_25x25, R.drawable.ore_icon_25x25, R.drawable.wool_icon_25x25};

    /**
     *  the method for drawing the hexagon shaped tiles on the board
     * @param context the app context
     * @param x the x position
     * @param y the y position
     * @param size how big the hexagon is
     * @param color the colour of the tile
     * @param isRobber true or false for showing the robber or not
     * @param isDesert true or false for if it is the desert tile or now
     * @param chitValue the value of the tile
     * @param hexagonId the id of where it is on the board
     * @param highlight the selection box around the hexagon
     * @param resourceId which resource it provides
     */
    public HexagonDrawable (Context context, int x, int y, int size, int color, boolean isRobber, boolean isDesert, int chitValue, int hexagonId, boolean highlight, int resourceId) {
        super(context);
        setWillNotDraw(false);
        this.context = context;
        this.x = x;
        this.y = y;
        this.size = size; // size can also be thought of as the radius
        this.color = color;
        this.isDesert = isDesert;
        this.isRobber = isRobber;
        this.chitValue = chitValue;
        this.hexagonId = hexagonId;
        this.highlight = highlight;
        this.resourceId = resourceId;
    }

    // constructors needed by android
    public HexagonDrawable (Context context) {
        super(context);
    }

    public HexagonDrawable (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param canvas Canvas to draw the hexagon upon.
     * @param debugMode Debug mode.
     */
    public void drawHexagon (Canvas canvas, boolean debugMode) {
        Paint hexagonPaint = new Paint();
        hexagonPaint.setColor(this.color);
        hexagonPaint.setStyle(Paint.Style.FILL);

        Paint blackFont = new Paint();
        blackFont.setColor(Color.BLACK);
        blackFont.setStyle(Paint.Style.FILL);
        blackFont.setTextSize(50);

        Paint highlightPaint = new Paint();
        highlightPaint.setColor(Color.CYAN);
        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeWidth(10f);

        points = calculateHexagonPoints(this.x, this.y, this.size);

        Path hexagonPath = createHexagonPath(points);
        canvas.drawPath(hexagonPath, hexagonPaint);

        if (this.highlight) canvas.drawPath(hexagonPath, highlightPaint);

        Paint robberPaint = new Paint();
        robberPaint.setColor(Color.DKGRAY);
        robberPaint.setStyle(Paint.Style.FILL);

        if (debugMode) {
            blackFont.setTextSize(30);
            canvas.drawText("id: " + this.hexagonId, points[5][0] - 15, points[5][1] + 100 + this.size / 2, blackFont);
            canvas.drawText("resId: " + this.resourceId, points[5][0] - 15, points[5][1] + 150 + this.size / 2, blackFont);
            blackFont.setTextSize(50);
        }

        if (!this.isDesert) {
            if (this.chitValue == 6 || this.chitValue == 8)
                blackFont.setColor(Color.argb(255, 255, 0, 0));
            if (this.chitValue < 10)
                canvas.drawText("" + this.chitValue, points[5][0] - 15, points[5][1] + this.size / 2 + 50, blackFont);
            else
                canvas.drawText("" + this.chitValue, points[5][0] - 25, points[5][1] + this.size / 2 + 50, blackFont);
        }

        int cx = points[5][0];
        int cy = points[5][1] + this.size;

        if (this.isRobber) {
            Log.d(TAG, "drawHexagon: Drawing the robber at hexagon: " + this.hexagonId);
            Drawable robberDrawable = context.getDrawable(R.drawable.robber);
            if (robberDrawable != null) {
                robberDrawable.setBounds(cx - 60, cy - 60, cx + 60, cy + 60);
                robberDrawable.draw(canvas);
            } else {
                Log.e(TAG, "drawHexagon: resourceDrawable is null", new NullPointerException());
            }

        }
        if (this.resourceId < 5 && this.resourceId >= 0) {
            Drawable resourceDrawable = context.getDrawable(resourceDrawables[this.resourceId]);
            if (resourceDrawable != null) {
                resourceDrawable.setBounds(cx - 30, cy - 30 + 100, cx + 30, cy + 30 + 100);
                resourceDrawable.draw(canvas);
            } else {
                Log.e(TAG, "drawHexagon: resourceDrawable is null", new NullPointerException());
            }
        } else if (resourceId != 5) {
            Log.e(TAG, "drawHexagon: resourceId is out of bounds", new Exception());
        }
        Paint intersectionPaint = new Paint();
        intersectionPaint.setColor(Color.DKGRAY);
        intersectionPaint.setStyle(Paint.Style.STROKE);
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

        for (int i = 0; i < 6; i++) {
            double angle_deg = 60 * i - 30;
            double angle_rad = Math.PI / 180 * angle_deg;

            points[i][0] = (int) (x + size * Math.cos(angle_rad));
            points[i][1] = (int) (y + size * Math.sin(angle_rad));
        }
        this.points = points;
        return points;
    }

    /**
     * createHexagonPath() creates a Path object from given hexagon corner x and y values
     *
     * @param corners - 2d array of x and y cords for the corners
     * @return Path
     */
    public Path createHexagonPath (int[][] corners) {
        this.hexagonPath = new Path();
        this.hexagonPath.moveTo(corners[0][0], corners[0][1]);

        for (int i = 1; i < corners.length; i++) {
            this.hexagonPath.lineTo(corners[i][0], corners[i][1]);
        }
        this.hexagonPath.close();
        return this.hexagonPath;
    }

    /**
     * @return Array of the corner points of a hexagon.
     */
    public int[][] getHexagonPoints () {
        return this.points;
    }
}
