package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
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
    protected int[] playerColors = {Color.RED, Color.WHITE, Color.BLUE, Color.CYAN};
    public int[] dataToDrawMap = {11, 10, 9, 12, 3, 2, 8, 13, 4, 0, 1, 7, 14, 5, 6, 18, 15, 16, 17};
    // public int[] drawToDataMap = {11, 10, 9, 12, 3, 2, 8, 13, 4, 0, 1, 7, 14, 5, 6, 18, 15, 16, 17};
    private Board board;
    private Building[] buildlings;

    ArrayList<RoadDrawable> roads = new ArrayList<>();

    ArrayList<HexagonDrawable> drawingHexagons = new ArrayList<>();

    public HexagonGrid (Context context, Board board, int x, int y, int size, int margin) {
        super(context);
        setWillNotDraw(false);

        this.x = x;
        this.y = y;
        this.size = size;
        this.height = size * 2;
        this.width = size * Math.sqrt(3);
        this.margin = margin;
        this.board = new Board(board); // todo is this copy 100% perfect?
        this.buildlings = this.board.getBuildings();
        getHexagons(x, y, size);
    }

    public void drawGrid (Canvas canvas) {
        for (HexagonDrawable h : drawingHexagons) {
            h.drawHexagon(canvas);
        }
        drawRoads(canvas);
        drawBuildings();
        this.invalidate();
    }

    public static int[][] append (int[][] a, int[][] b) {
        int[][] result = new int[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public void drawRoads (Canvas canvas) {
        Log.d(TAG, "drawRoads() called with: canvas = [" + canvas + "]");

        // get list of all roads on the board
        ArrayList<Road> dataRoads = this.board.getRoads();

        Collection<Integer> overlap = new ArrayList<>();

        // for each road stored on the board
        for (int k = 0; k < dataRoads.size(); k++) {
            Road r = dataRoads.get(k);

            // add each intersections adjacent hexagons to the "overlap" list
            overlap.addAll(board.getIntToHexIdMap().get(r.getIntersectionAId()));
            overlap.retainAll(board.getIntToHexIdMap().get(r.getIntersectionBId()));

            // print "overlap" list
            Log.w(TAG, "drawRoads: overlap: " + overlap.toString());

            // if there are exactly 2 overlapping hexagons (right now we need this)
            if (overlap.size() == 2) {
                Log.i(TAG, "drawRoads: drawing a road");
                Paint roadPaint = new Paint();
                roadPaint.setColor(playerColors[r.getOwnerId()]);
                roadPaint.setStyle(Paint.Style.FILL);

                // draw a circle in the center of each hexagon
                int dataA = 0;
                int dataB = 0;
                for (int i = 0; i < dataToDrawMap.length; i++) {
                    if (dataToDrawMap[i] == ((ArrayList<Integer>) overlap).get(0)) {
                        dataA = i;
                    } else if (dataToDrawMap[i] == ((ArrayList<Integer>) overlap).get(1)) {
                        dataB = i;
                    }
                }

                int[][] hexagonAPoints = drawingHexagons.get(dataA).getHexagonPoints();
                int[][] hexagonBPoints = drawingHexagons.get(dataB).getHexagonPoints();

                int[] pointA = {hexagonAPoints[5][0], hexagonAPoints[5][1] + size};
                int[] pointB = {hexagonBPoints[5][0], hexagonBPoints[5][1] + size};
                int cxA = hexagonAPoints[5][0];
                int cyA = hexagonAPoints[5][1] + size;

                int cxB = hexagonBPoints[5][0];
                int cyB = hexagonBPoints[5][1] + size;

                canvas.drawCircle(pointA[0], pointA[1], 25, roadPaint);
                canvas.drawCircle(pointB[0], pointB[1], 25, roadPaint);

                int diff = getDistBtwPts(pointA, pointB);

                Log.e(TAG, "drawRoads: diff: " + diff);

                int[] roadPointsA = {cxA, cyA - size};
                int[] roadPointsB = {cxB, cyB + size};

                canvas.drawCircle(roadPointsA[0], roadPointsA[1], 25, roadPaint);
                canvas.drawCircle(roadPointsB[0], roadPointsB[1], 25, roadPaint);

                int[] midpoint = {(roadPointsA[0] + roadPointsB[0]) / 2,(roadPointsA[1] + roadPointsB[1]) / 2};

                canvas.drawCircle(midpoint[0], midpoint[1], 25, roadPaint);

//                canvas.drawLine(roadPointsA[0], roadPointsA[1], roadPointsB[0],  roadPointsB[1], roadPaint);
//                Path roadPath = new Path();
//
//                // starting point
//                roadPath.moveTo(roadPointsA[0], roadPointsA[1]);
//
//                // upper right
//                roadPath.lineTo(roadPointsA[0] + margin, roadPointsA[1] - margin);
//
//                // lower right
//                roadPath.lineTo(roadPointsB[0] + margin, roadPointsB[1] - margin);
//
//                // lower left
//                roadPath.lineTo(roadPointsB[0], roadPointsB[1]);
//
//                // upper left
//                roadPath.lineTo(roadPointsA[0], roadPointsA[1]);
//
//                canvas.drawPath(roadPath, roadPaint);

            } else if (overlap.size() == 1) {
                Log.e(TAG, "drawRoads: overlap size is 1.");
                ArrayList<Integer> hexes = board.getIntToHexIdMap().get(k);
                Hexagon h = board.getHexagonFromId(hexes.get(0));

                for (int i = 0; i < this.dataToDrawMap.length; i++) {
                    //if (dataToDrawMap[i] == )
                }

                //canvas.drawCircle(cx, cy, radius, roadPaint);
            }

        }
    }

    public void drawBuildings () {
        Building[] buildings = this.board.getBuildings();

        // go through each building
        for (int i = 0; i < buildings.length; i++) {
            if (buildings[i] != null) {

                // get hexes adjacent to building
                ArrayList<Integer> hexes = board.getIntToHexIdMap().get(i);

                ArrayList<Integer> intersections = this.board.getHexToIntIdMap().get(0);

                // for each adjacent hex, add its adjacent intersections to the array list
                for (int j = 1; j < hexes.size(); j++) {
                    intersections.retainAll(this.board.getHexToIntIdMap().get(j));
                }

                Log.e(TAG, "drawBuildings: all intersections adjacent to adjacent hexes" + intersections);

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

    /**
     * @param pt1 First ordered x y pair.
     * @param pt2 Second ordered x y pair.
     * @return Distance between the given points.
     */
    public int getDistBtwPts (int[] pt1, int[] pt2) {
        return (int) Math.hypot(pt1[0] - pt2[1], pt1[0] - pt2[1]);
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

                ArrayList<Road> dataRoads = this.board.getRoads();

                // for
                for (int k = 0; k < dataRoads.size(); k++) {

                    // get

                }

                //roads.add(new RoadDrawable(points, 0));

                drawingHexagons.add(hexagon);
                dataHexagonsIndex++;
            }
        }
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
