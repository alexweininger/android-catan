package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import java.io.Serializable;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class BoardSurfaceView extends SurfaceView implements Serializable {
    private static final String TAG = "BoardSurfaceView";

    int size;
    HexagonGrid grid;
    private Canvas canvas;
    private boolean ready;

    // constructors
    public BoardSurfaceView (Context context) {
        super(context);
        setWillNotDraw(false);
        ready = false;
    }

    public BoardSurfaceView (Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    /**
     * draws the board on the screen
     * @param canvas canvas of where to draw
     */
    public void onDraw (Canvas canvas) {
        if (!ready) {
            Log.e(TAG, "onDraw: not ready");
            return;
        }
        if (grid == null) {
            Log.e(TAG, "onDraw: grid is null");
            this.invalidate();
        } else {
            Log.i(TAG, "onDraw: drawing grid");
            grid.drawGameBoard(canvas);
        }
    }

    public int getSize () {
        return size;
    }

    public void setSize (int size) {
        this.size = size;
    }

    public HexagonGrid getGrid () {
        return grid;
    }

    public void setGrid (HexagonGrid grid) {
        this.grid = grid;
    }

    public Canvas getCanvas () {
        return canvas;
    }

    public void setCanvas (Canvas canvas) {
        this.canvas = canvas;
    }

    public void setReady (boolean ready) {
        this.ready = ready;
    }
}
