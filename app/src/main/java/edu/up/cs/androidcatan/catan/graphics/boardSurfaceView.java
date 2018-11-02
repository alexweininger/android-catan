package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class boardSurfaceView extends SurfaceView {

    ArrayList<Ports> ports = new ArrayList<>();
    ArrayList<House> houses = new ArrayList<>();

    int size;
    HexagonGrid grid;

    // constructors
    public boardSurfaceView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public boardSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    // TODO Alex
    public void createHexagons() {

        grid = new HexagonGrid(this.getContext(), 100, 200, 145, 40);
    }

    public void onDraw(Canvas canvas) {
//        canvas.drawARGB(255, 237, 237, 171);
//        //grid.drawGrid(canvas);
//        for (int i = 0; i < 9; i++) {
//            Log.d("user1", "" + i);
//            ports.get(i).drawPort(canvas);
//        }
//        Log.d("house", "yeet");
//        for (int j = 0; j < 4; j++) {
//            Log.d("house", "h" + j);
//            houses.get(j).drawHouse(canvas);
//        }
    }
}
