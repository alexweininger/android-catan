package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.gamestate.Board;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class BoardSurfaceView extends SurfaceView {

    private final String TAG = "BoardSurfaceView";

    ArrayList<Ports> ports = new ArrayList<>();

    int size;
    HexagonGrid grid;

    // constructors
    public BoardSurfaceView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public BoardSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public void onDraw(Canvas canvas) {
        if (grid == null) {
            Log.e(TAG, "onDraw: grid is null");
        } else {
            Log.i(TAG, "onDraw: drawing grid");
            grid.drawGameBoard(canvas);
        }
    }

    public void createHexagons(Board board) {
        this.grid = new HexagonGrid(this.getContext(), board, 100, 210, 175, 20, false);
    }

    public ArrayList<Ports> getPorts() {
        return ports;
    }

    public void setPorts(ArrayList<Ports> ports) {
        this.ports = ports;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public HexagonGrid getGrid() {
        return grid;
    }

    public void setGrid(HexagonGrid grid) {
        this.grid = grid;
    }
}
