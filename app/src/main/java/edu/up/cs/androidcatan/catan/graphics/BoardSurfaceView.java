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
    ArrayList<House> houses = new ArrayList<>();

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

    public void BoardSurfaceView(BoardSurfaceView b) {
        this.setGrid(b.getGrid());
    }

    public void createHexagons(Board board) {
        this.grid = new HexagonGrid(this.getContext(), board, 100, 200, 155, 50);
    }

    public ArrayList<Ports> getPorts() {
        return ports;
    }

    public void setPorts(ArrayList<Ports> ports) {
        this.ports = ports;
    }

    public ArrayList<House> getHouses() {
        return houses;
    }

    public void setHouses(ArrayList<House> houses) {
        this.houses = houses;
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

    public void onDraw(Canvas canvas) {
        canvas.drawARGB(255, 237, 237, 171);
        if (grid == null) {
            Log.e(TAG, "onDraw: grid is null");
        } else {
            grid.drawGrid(canvas);
        }
    }
}
