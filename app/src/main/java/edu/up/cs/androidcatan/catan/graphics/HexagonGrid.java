package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;

public class HexagonGrid extends BoardSurfaceView {

    private static final String TAG = "HexagonGrid";

    // instance variables
    protected int x, y;
    protected int height;
    protected double width;
    protected int margin;
    protected int[] numTiles = {4, 3, 3, 3, 4};
    protected int[] colors = {Color.argb(255, 221, 135, 68), Color.argb(255, 123, 206, 107), Color.argb(255, 0, 102, 25), Color.argb(255, 68, 86, 85), Color.argb(255, 255, 225, 0), Color.argb(255, 192, 193, 141)};


    private Board board;
    public int[] dataToDrawMap = {11, 10, 9, 12, 3, 2, 8, 13, 4, 0, 1, 7, 14, 5, 6, 18, 15, 16, 17};


    ArrayList<RoadDrawable> roads = new ArrayList<>();

    ArrayList<HexagonDrawable> drawingHexagons = new ArrayList<>();

    public HexagonGrid (Context context, Board board, int x, int y, int size, int margin) {
        super(context);
        setWillNotDraw(false);

        this.x = x;
        this.y = y;
        //this.size = size;     TODO Size appears to be unneeded
        this.height = size * 2;
        this.width = size * Math.sqrt(3);
        this.margin = margin;
        this.board = new Board(board); // todo is this copy 100% perfect?
        getHexagons(x, y, size);
    }

    public void drawGrid (Canvas canvas) {
        for (HexagonDrawable h : drawingHexagons) {
            h.drawHexagon(canvas);
        }
        drawRoads(canvas);
    }

    public void drawRoads(Canvas canvas) {
        Log.d(TAG, "drawRoads() called with: canvas = [" + canvas + "]");
        ArrayList<Road> dataRoads = this.board.getRoads();

        Collection<Integer> overlap = new ArrayList<>();
        for (int k = 0; k < dataRoads.size(); k++) {
            Log.i(TAG, "drawRoads: k:" + k);
            Road r = dataRoads.get(k);

            overlap.addAll(board.getIntToHexIdMap().get(r.getIntersectionAId()));
            overlap.retainAll(board.getIntToHexIdMap().get(r.getIntersectionBId()));

            Log.w(TAG, "getHexagons: overlap: " + overlap.toString());

            if (overlap.size() == 2) {
                // draw a road
                Log.i(TAG, "drawRoads: drawing a road");
                int[][] points;
                int[][] points2;

                points = this.drawingHexagons.get(this.dataToDrawMap[((ArrayList<Integer>) overlap).get(0)]).getHexagonPoints();
                points2 = this.drawingHexagons.get(this.dataToDrawMap[((ArrayList<Integer>) overlap).get(1)]).getHexagonPoints();

                Log.i(TAG, "drawRoads: points: " + Arrays.toString(points));

                if (points == null || points.length == 0) {
                    Paint roadPaint = new Paint();
                    roadPaint.setColor(Color.DKGRAY);
                    roadPaint.setStyle(Paint.Style.FILL);

                    int radius = 25;
                    int cx = points[5][0];
                    int cy = points[5][1];

                    canvas.drawCircle(cx, cy, radius, roadPaint);

                    int cx2 = points2[5][0];
                    int cy2 = points2[5][1];

                    canvas.drawCircle(cx2, cy2, radius, roadPaint);
                }
            } else if (overlap.size() == 1) {
                ArrayList<Integer> hexes = board.getIntToHexIdMap().get(k);
                Hexagon h = board.getHexagonFromId(hexes.get(0));

                for (int i = 0; i < this.dataToDrawMap.length; i++) {
                    //if (dataToDrawMap[i] == )
                }

                //canvas.drawCircle(cx, cy, radius, roadPaint);
            }

        }
    }

    // method that generates the individual hexagon objects from the Hexagon class
    public void getHexagons (int x, int y, int size) {

        ArrayList<Hexagon> dataHexagons = board.getHexagonListForDrawing();

        drawingHexagons = new ArrayList<>();

        int[] rows = {1, 1, 0, 1, 1};
        int[] hexagonsInEachRow = {3, 4, 5, 4, 3};
        int offsetX;

        int dataHexagonsIndex = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < hexagonsInEachRow[i]; j++) {

                int hexagonColor = this.colors[dataHexagons.get(dataHexagonsIndex).getResourceId()];
                boolean isRobberHexagon = board.getRobber().getHexagonId() == dataHexagons.get(dataHexagonsIndex).getHexagonId();
                boolean isDesertHexagon = dataHexagons.get(dataHexagonsIndex).getResourceId() == 5;

                if (isDesertHexagon) {
                    Log.w(TAG, "getHexagons: desert tile found to be at hexagon id: " + dataHexagonsIndex);
                }

                offsetX = (i % 2 == 0) ? (int) this.width / 2 + margin / 2 : 0;

                int xPos = offsetX + x + (int) ((this.width + this.margin) * (j + rows[i]));
                int yPos = y + (((this.height) * 3) / 4 + this.margin) * i;

                HexagonDrawable hexagon = new HexagonDrawable(this.getContext(), xPos, yPos, size, hexagonColor, isRobberHexagon, isDesertHexagon, dataHexagons.get(dataHexagonsIndex).getChitValue());

                int[][] points = hexagon.getHexagonPoints();



                //roads.add(new RoadDrawable(points, 0));

                drawingHexagons.add(hexagon);
                dataHexagonsIndex++;
            }
        }
    }

    public <T> List<T> intersection (List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public void generateDataHexToDrawHexMap () {

    }

    /* ----- getters and setters ------ */

    public int getXVal () {
        return x;
    }

    public void setX (int x) {
        this.x = x;
    }

    public int getYVal () {
        return y;
    }

    public void setY (int y) {
        this.y = y;
    }

    public int getGridHeight () {
        return height;
    }

    public void setGridHeight (int height) {
        this.height = height;
    }

    public double getGridWidth () {
        return width;
    }

    public void setWidth (double width) {
        this.width = width;
    }

    public int getMargin () {
        return margin;
    }

    public void setMargin (int margin) {
        this.margin = margin;
    }

    public int[] getNumTiles () {
        return numTiles;
    }

    public void setNumTiles (int[] numTiles) {
        this.numTiles = numTiles;
    }

    public int[] getColors () {
        return colors;
    }

    public void setColors (int[] colors) {
        this.colors = colors;
    }

    public ArrayList<RoadDrawable> getRoads () {
        return roads;
    }

    public void setRoads (ArrayList<RoadDrawable> roads) {
        this.roads = roads;
    }

    public ArrayList<HexagonDrawable> getDrawingHexagons () {
        return drawingHexagons;
    }

    public void setDrawingHexagons (ArrayList<HexagonDrawable> drawingHexagons) {
        this.drawingHexagons = drawingHexagons;
    }
}
