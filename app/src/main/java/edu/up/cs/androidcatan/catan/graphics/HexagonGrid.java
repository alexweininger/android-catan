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

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 8th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class HexagonGrid extends BoardSurfaceView {

    private static final String TAG = "HexagonGrid";

    // instance variables
    protected int x, y;
    protected int height;
    protected double width;
    protected int margin;
    protected int[] numTiles = {6, 5, 5, 5, 6};
    //protected int[] numTiles = {4, 3, 3, 3, 4}; ORIGINAL BEFORE CHANGE
    int[] hexagonsInEachRow = {3, 4, 5, 4, 3}; // hexagons in each row
    protected int[] colors = {Color.argb(255, 221, 135, 68), Color.argb(255, 123, 206, 107), Color.argb(255, 0, 102, 25), Color.argb(255, 68, 86, 85), Color.argb(255, 255, 225, 0), Color.argb(255, 192, 193, 141)};
    protected int[] playerColors = {Color.RED, Color.WHITE, Color.argb(255, 255, 128, 17), Color.BLUE};
    public int[] dataToDrawMap = {11, 10, 9, 12, 3, 2, 8, 13, 4, 0, 1, 7, 14, 5, 6, 18, 15, 16, 17};
    // public int[] drawToDataMap = {11, 10, 9, 12, 3, 2, 8, 13, 4, 0, 1, 7, 14, 5, 6, 18, 15, 16, 17};
    private Board board;
    private Building[] buildlings;

    private Intersection[] intersections = new Intersection[54]; // list of Intersection objects
    ArrayList<RoadDrawable> roads = new ArrayList<>(); // list of Road objects
    ArrayList<HexagonDrawable> drawingHexagons = new ArrayList<>(); // list of HexagonDrawable objects

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
        generateIntersections();
    }

    public void drawGrid (Canvas canvas) {
        // draw each hexagon
        for (HexagonDrawable h : drawingHexagons) {
            h.drawHexagon(canvas);
        }

        drawRoads(canvas);
        drawBuildings(canvas);

        //draw intersections
        for (Intersection intersection : intersections) {
            intersection.drawIntersection(canvas);
        }
        //getIntersections(this.x, this.y, this.size, canvas);
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
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);

        // get list of all roads on the board
        ArrayList<Road> dataRoads = this.board.getRoads();

        Collection<Integer> overlap = new ArrayList<>();

        // for each road stored on the board
        for (int k = 0; k < dataRoads.size(); k++) {
            Road r = dataRoads.get(k);

            canvas.drawLine(intersections[r.getIntersectionAId()].getxPos(), intersections[r.getIntersectionAId()].getyPos(), intersections[r.getIntersectionBId()].getxPos(), intersections[r.getIntersectionBId()].getyPos(), paint);

//            // add each intersections adjacent hexagons to the "overlap" list
//            overlap.addAll(board.getIntToHexIdMap().get(r.getIntersectionAId()));
//            overlap.retainAll(board.getIntToHexIdMap().get(r.getIntersectionBId()));
//
//            // print "overlap" list
//            Log.w(TAG, "drawRoads: overlap: " + overlap.toString());
//
//            // if there are exactly 2 overlapping hexagons (right now we need this)
//            if (overlap.size() == 2) {
//                Log.i(TAG, "drawRoads: drawing a road");
//                Paint roadPaint = new Paint();
//                roadPaint.setColor(playerColors[r.getOwnerId()]);
//                roadPaint.setStyle(Paint.Style.FILL);
//
//                // draw a circle in the center of each hexagon
//                int dataA = 0;
//                int dataB = 0;
//                for (int i = 0; i < dataToDrawMap.length; i++) {
//                    if (dataToDrawMap[i] == ((ArrayList<Integer>) overlap).get(0)) {
//                        dataA = i;
//                    } else if (dataToDrawMap[i] == ((ArrayList<Integer>) overlap).get(1)) {
//                        dataB = i;
//                    }
//                }
//
//                int[][] hexagonAPoints = drawingHexagons.get(dataA).getHexagonPoints();
//                int[][] hexagonBPoints = drawingHexagons.get(dataB).getHexagonPoints();
//
//                int[] pointA = {hexagonAPoints[5][0], hexagonAPoints[5][1] + size};
//                int[] pointB = {hexagonBPoints[5][0], hexagonBPoints[5][1] + size};
//                int cxA = hexagonAPoints[5][0];
//                int cyA = hexagonAPoints[5][1] + size;
//
//                int cxB = hexagonBPoints[5][0];
//                int cyB = hexagonBPoints[5][1] + size;
//
//                // draw a circle in the center of each hexagon
//                //                canvas.drawCircle(pointA[0], pointA[1], 25, roadPaint);
//                //                canvas.drawCircle(pointB[0], pointB[1], 25, roadPaint);
//
//                int diff = getDistBtwPts(pointA, pointB);
//
//                Log.e(TAG, "drawRoads: diff: " + diff);
//
//                int[] roadPointsA = {cxA, cyA - size};
//                int[] roadPointsB = {cxB, cyB + size};
//
//                // ???
//                //                canvas.drawCircle(roadPointsA[0], roadPointsA[1], 25, roadPaint);
//                //                canvas.drawCircle(roadPointsB[0], roadPointsB[1], 25, roadPaint);
//
//                int[] midpoint = {(roadPointsA[0] + roadPointsB[0]) / 2, (roadPointsA[1] + roadPointsB[1]) / 2};
//
//                canvas.drawCircle(midpoint[0], midpoint[1], 25, roadPaint);
//
//                //                canvas.drawLine(roadPointsA[0], roadPointsA[1], roadPointsB[0],  roadPointsB[1], roadPaint);
//                //                Path roadPath = new Path();
//                //
//                //                // starting point
//                //                roadPath.moveTo(roadPointsA[0], roadPointsA[1]);
//                //
//                //                // upper right
//                //                roadPath.lineTo(roadPointsA[0] + margin, roadPointsA[1] - margin);
//                //
//                //                // lower right
//                //                roadPath.lineTo(roadPointsB[0] + margin, roadPointsB[1] - margin);
//                //
//                //                // lower left
//                //                roadPath.lineTo(roadPointsB[0], roadPointsB[1]);
//                //
//                //                // upper left
//                //                roadPath.lineTo(roadPointsA[0], roadPointsA[1]);
//                //
//                //                canvas.drawPath(roadPath, roadPaint);
//
//            } else if (overlap.size() == 1) {
//                Log.e(TAG, "drawRoads: overlap size is 1.");
//                ArrayList<Integer> hexes = board.getIntToHexIdMap().get(k);
//                Hexagon h = board.getHexagonFromId(hexes.get(0));
//
//                for (int i = 0; i < this.dataToDrawMap.length; i++) {
//                    //if (dataToDrawMap[i] == )
//                }
//
//                //canvas.drawCircle(cx, cy, radius, roadPaint);
//            }

        }
    }

    /**
     * @param canvas Canvas object to draw the buildings on.
     */
    public void drawBuildings (Canvas canvas) {
        Paint bldgPaint = new Paint();

        Building[] buildings = this.board.getBuildings();

        // go through each building
        for (int i = 0; i < buildings.length; i++) {
            if (buildings[i] != null) {
                bldgPaint.setColor(playerColors[buildings[i].getOwnerId()]);

                // get center of intersection
                int xPos = this.intersections[i].getxPos();
                int yPos = this.intersections[i].getyPos();

                canvas.drawRect(xPos - 20, yPos + 20, xPos + 20, yPos - 20, bldgPaint);

            }
        }
    }

    // todo remove if we don't use
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

        int offsetX;

        int dataHexagonsIndex = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < hexagonsInEachRow[i]; j++) {

                int hexagonColor = this.colors[dataHexagons.get(dataHexagonsIndex).getResourceId()];
                Log.d(TAG, "getHexagons: board.getRobber().getHexagonId(): " + board.getRobber().getHexagonId() + " current hex id: " + dataHexagons.get(dataHexagonsIndex).getHexagonId());

                boolean isRobberHexagon = board.getRobber().getHexagonId() == dataHexagons.get(dataHexagonsIndex).getHexagonId();

                if (isRobberHexagon) {
                    Log.w(TAG, "getHexagons: isRobberHexagon = " + isRobberHexagon + " at hexagon id: " + dataHexagons.get(dataHexagonsIndex).getHexagonId());
                }

                boolean isDesertHexagon = dataHexagons.get(dataHexagonsIndex).getResourceId() == 5;

                if (isDesertHexagon) {
                    Log.w(TAG, "getHexagons: desert tile found to be at drawing hexagon id: " + dataHexagonsIndex + " and data hex id: " + dataHexagons.get(dataHexagonsIndex).getHexagonId());
                }

                offsetX = (i % 2 == 0) ? (int) this.width / 2 + margin / 2 : 0;

                int xPos = offsetX + x + (int) ((this.width + this.margin) * (j + rows[i]));
                int yPos = y + (((this.height) * 3) / 4 + this.margin) * i;

                HexagonDrawable hexagon = new HexagonDrawable(this.getContext(), xPos, yPos, size, hexagonColor, isRobberHexagon, isDesertHexagon, dataHexagons.get(dataHexagonsIndex).getChitValue(), dataHexagons.get(dataHexagonsIndex).getHexagonId());

                drawingHexagons.add(hexagon);
                Log.w(TAG, "getHexagons: dataHexagonsIndex: " + dataHexagonsIndex + " current hexagon id: " + dataHexagons.get(dataHexagonsIndex).getHexagonId());
                dataHexagonsIndex++;
            }
        }
    }

    public void generateIntersections(){
        intersections[0] = new Intersection(0, 1049, 642);
        intersections[1] = new Intersection(1, 887, 574);
        intersections[2] = new Intersection(2, 726, 642);
        intersections[3] = new Intersection(3, 726, 856);
        intersections[4] = new Intersection(4, 887, 924);
        intersections[5] = new Intersection(5, 1049, 856);
        intersections[6] = new Intersection(6, 1210, 574);
        intersections[7] = new Intersection(7, 1210, 360);
        intersections[8] = new Intersection(8, 1049, 292);
        intersections[9] = new Intersection(9, 887, 360);
        intersections[10] = new Intersection(10, 726, 292);
        intersections[11] = new Intersection(11, 564, 360);
        intersections[12] = new Intersection(12, 564, 574);
        intersections[13] = new Intersection(13, 403, 642);
        intersections[14] = new Intersection(14, 403, 856);
        intersections[15] = new Intersection(15, 564, 924);
        intersections[16] = new Intersection(16, 564, 1138);
        intersections[17] = new Intersection(17, 726, 1206);
        intersections[18] = new Intersection(18, 887, 1138);
        intersections[19] = new Intersection(19, 1049, 1206);
        intersections[20] = new Intersection(20, 1210, 1138);
        intersections[21] = new Intersection(21, 1210, 924);
        intersections[22] = new Intersection(22, 1372, 856);
        intersections[23] = new Intersection(23, 1372, 642);
        intersections[24] = new Intersection(24, 1533, 574);
        intersections[25] = new Intersection(25, 1523, 379);
        intersections[26] = new Intersection(26, 1372, 292);
        intersections[27] = new Intersection(27, 1361, 97);
        intersections[28] = new Intersection(28, 1210, 10);
        intersections[29] = new Intersection(29, 1038, 97);
        intersections[30] = new Intersection(30, 887, 10);
        intersections[31] = new Intersection(31, 715, 97);
        intersections[32] = new Intersection(32, 564, 10);
        intersections[33] = new Intersection(33, 403, 97);
        intersections[34] = new Intersection(34, 403, 292);
        intersections[35] = new Intersection(35, 241, 379);
        intersections[36] = new Intersection(36, 241, 574);
        intersections[37] = new Intersection(37, 89, 661);
        intersections[38] = new Intersection(38, 89, 836);
        intersections[39] = new Intersection(39, 241, 924);
        intersections[40] = new Intersection(40, 241, 1138);
        intersections[41] = new Intersection(41, 403, 1206);
        intersections[42] = new Intersection(42, 403, 1400);
        intersections[43] = new Intersection(43, 564, 1488);
        intersections[44] = new Intersection(44, 726, 1400);
        intersections[45] = new Intersection(45, 887, 1488);
        intersections[46] = new Intersection(46, 1038, 1400);
        intersections[47] = new Intersection(47, 1210, 1488);
        intersections[48] = new Intersection(48, 1361, 1400);
        intersections[49] = new Intersection(49, 1372, 1203);
        intersections[50] = new Intersection(50, 1523, 1118);
        intersections[51] = new Intersection(51, 1533, 924);
        intersections[52] = new Intersection(52, 1684, 856);
        intersections[53] = new Intersection(53, 1684, 642);
    }

//    public void getIntersections (int x, int y, int size, Canvas canvas) {
//
//        int offsetX;
//        int[] rows = {1, 1, 0, 1, 1};
//        Paint intersectionPaint = new Paint();
//        intersectionPaint.setTextSize(24);
//        intersectionPaint.setColor(Color.RED);
//
//        int count = 0;
//        for (int i = 0; i < 5; i++) {
//
//            for (int j = 0; j < hexagonsInEachRow[i]; j++) {
//
//                for (int k = 0; k < 4; k++) {
//
//                }
//
//                offsetX = (i % 2 == 0) ? (int) this.width / 2 + margin / 2 : 0;
//
//                int xPos = offsetX + x + (int) ((this.width + this.margin) * (j + rows[i]));
//                int yPos = y + (((this.height) * 3) / 4 + this.margin) * i;
//
////                canvas.drawCircle(xPos, yPos + size, 25, intersectionPaint);
////                canvas.drawCircle(xPos, yPos - size, 25, intersectionPaint);
//                int top = yPos + size;
//                int bottom = yPos - size;
//                canvas.drawText(xPos + ", " + top, xPos, yPos + size, intersectionPaint);
//                canvas.drawText(xPos + ", " + bottom, xPos, yPos - size, intersectionPaint);
//
//                if(count == 0){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[4][0];
//                    int cornerY = points[4][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                    cornerX = points[0][0];
//                    cornerY = points[0][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
//                if(count == 1 || count == 2|| count == 6){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[0][0];
//                    int cornerY = points[0][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
//                if(count == 3){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[4][0];
//                    int cornerY = points[4][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
//                if(count == 7){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[4][0];
//                    int cornerY = points[4][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                    cornerX = points[3][0];
//                    cornerY = points[3][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
//                if(count == 11){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[0][0];
//                    int cornerY = points[0][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                    cornerX = points[1][0];
//                    cornerY = points[1][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
//                if(count == 12){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[3][0];
//                    int cornerY = points[3][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
//                if(count == 15){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[1][0];
//                    int cornerY = points[1][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
//                if(count == 16){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[3][0];
//                    int cornerY = points[3][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                    cornerX = points[1][0];
//                    cornerY = points[1][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
//                if(count == 17 || count == 18){
//                    int[][] points = Hexagon.calculateHexagonPoints(xPos, yPos, size);
//                    int cornerX = points[1][0];
//                    int cornerY = points[1][1];
//                    canvas.drawText(cornerX + ", " + cornerY, cornerX, cornerY, intersectionPaint);
//                }
////                int[] topCenter = drawingHexagons.get(i).getHexagonPoints()[5];
////                int[] bottomCenter = drawingHexagons.get(i).getHexagonPoints()[2];
////
////                canvas.drawCircle(topCenter[0], topCenter[1], 25, intersectionPaint);
////                canvas.drawCircle(bottomCenter[0], bottomCenter[1], 25, intersectionPaint);
//                count++;
//
//            }
//
//
//        }
//    }

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
