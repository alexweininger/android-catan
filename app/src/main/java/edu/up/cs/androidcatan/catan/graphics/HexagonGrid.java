package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 9th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class HexagonGrid extends BoardSurfaceView {

    private static final String TAG = "HexagonGrid";

    /* ---------- Instance variables ------------ */

    protected int x, y, height, margin;
    protected double width;

    int[] hexagonsInEachRow = {3, 4, 5, 4, 3}; // hexagons in each row

    protected int[] colors = {Color.argb(255, 165, 63, 4), Color.argb(255, 123, 206, 107), Color.argb(255, 0, 102, 25), Color.argb(255, 68, 86, 85), Color.argb(255, 255, 225, 0), Color.argb(255, 192, 193, 141)};

    public static int[] playerColors = {Color.RED, Color.WHITE, Color.argb(255, 255, 128, 17), Color.BLUE};

    private Board board;
    private IntersectionDrawable[] intersections = new IntersectionDrawable[54]; // list of IntersectionDrawable objects
    ArrayList<RoadDrawable> roads = new ArrayList<>(); // list of Road objects
    ArrayList<HexagonDrawable> drawingHexagons = new ArrayList<>(); // list of HexagonDrawable objects

    /* ---------- Constructors ------------ */

    public HexagonGrid (Context context, Board board, int x, int y, int size, int margin) {
        super(context);
        setWillNotDraw(false);
        this.x = x;
        this.y = y;
        this.size = size;
        this.height = size * 2;
        this.width = size * Math.sqrt(3);
        this.margin = margin;
        this.board = new Board(board);
        generateIntersections();
    }

    public HexagonGrid (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HexagonGrid (Context context) {
        super(context);
    }

    /* ---------- Drawing methods ------------ */

    /**
     * Draws all of the components on the board.
     *
     * @param canvas Canvas to draw on.
     */
    public void drawGrid (Canvas canvas) {
        getHexagons(x, y, size); // get hexes

        drawBorder(canvas);

        for (HexagonDrawable h : drawingHexagons) {
            h.drawHexagon(canvas);
        } // draw each hexagon

        drawRoads(canvas);
        drawBuildings(canvas);

        for (IntersectionDrawable intersection : intersections) {
            intersection.drawIntersection(canvas);
        } // draw each intersection

        this.invalidate();
    }

    /**
     * Draws all of the roads.
     *
     * @param canvas Canvas to draw on.
     */
    public void drawRoads (Canvas canvas) {
        Log.d(TAG, "drawRoads() called with: canvas = [" + canvas + "]");

        Paint roadPaint = new Paint(); // paint for drawing the roads
        roadPaint.setStyle(Paint.Style.STROKE);
        roadPaint.setStrokeWidth(25);

        ArrayList<Road> dataRoads = this.board.getRoads(); // get list of all roads on the board

        for (Road r : dataRoads) {
            roadPaint.setColor(playerColors[r.getOwnerId()]);

            canvas.drawLine(intersections[r.getIntersectionAId()].getxPos(), intersections[r.getIntersectionAId()].getyPos(), intersections[r.getIntersectionBId()].getxPos(), intersections[r.getIntersectionBId()].getyPos(), roadPaint);
        }

//        for (int k = 0; k < dataRoads.size(); k++) { // for each road stored on the board
//            Road r = dataRoads.get(k);
//
//            roadPaint.setColor(playerColors[r.getOwnerId()]);
//
//            canvas.drawLine(intersections[r.getIntersectionAId()].getxPos(), intersections[r.getIntersectionAId()].getyPos(), intersections[r.getIntersectionBId()].getxPos(), intersections[r.getIntersectionBId()].getyPos(), roadPaint);
//        }
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

                canvas.drawRect(xPos - 30, yPos + 30, xPos + 30, yPos - 30, bldgPaint);
            }
        }
    }

    /**
     * Draws the blue ocean and the tan background for the island.
     *
     * @param canvas Canvas to draw upon.
     */
    public void drawBorder (Canvas canvas) {
        canvas.drawColor(Color.argb(255, 160, 206, 255)); // set the background to ocean color

        Paint tanPaint = new Paint(); // paint for island background
        tanPaint.setColor(Color.argb(255, 255, 246, 183));

        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;

        canvas.drawCircle(centerX, centerY - 15, 665, tanPaint);
    }

    /**
     * Generates the individual hexagon objects from the Hexagon class.
     *
     * @param x X position.
     * @param y Y position.
     * @param size Size of the hexagons.
     */
    public void getHexagons (int x, int y, int size) {
        ArrayList<Hexagon> dataHexagons = board.getHexagonListForDrawing();
        drawingHexagons = new ArrayList<>();

        int[] rows = {1, 1, 0, 1, 1};
        int dataHexagonsIndex = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < hexagonsInEachRow[i]; j++) {

                int hexagonColor = this.colors[dataHexagons.get(dataHexagonsIndex).getResourceId()];
                //                Log.d(TAG, "getHexagons: board.getRobber().getHexagonId(): " + board.getRobber().getHexagonId() + " current hex id: " + dataHexagons.get(dataHexagonsIndex).getHexagonId());

                boolean isRobberHexagon = board.getRobber().getHexagonId() == dataHexagons.get(dataHexagonsIndex).getHexagonId();

                if (isRobberHexagon) {
                    Log.w(TAG, "getHexagons: Robber is at hexagon id: " + dataHexagons.get(dataHexagonsIndex).getHexagonId());
                }

                boolean isDesertHexagon = dataHexagons.get(dataHexagonsIndex).getResourceId() == 5;
                if (isDesertHexagon)
                    Log.w(TAG, "getHexagons: desert tile found to be at drawing hexagon id: " + dataHexagonsIndex + " and data hex id: " + dataHexagons.get(dataHexagonsIndex).getHexagonId());

                int offsetX = (i % 2 == 0) ? (int) this.width / 2 + margin / 2 : 0;
                int xPos = offsetX + x + (int) ((this.width + this.margin) * (j + rows[i]));
                int yPos = y + (((this.height) * 3) / 4 + this.margin) * i;

                HexagonDrawable hexagon = new HexagonDrawable(this.getContext(), xPos, yPos, size, hexagonColor, isRobberHexagon, isDesertHexagon, dataHexagons.get(dataHexagonsIndex).getChitValue(), dataHexagons.get(dataHexagonsIndex).getHexagonId());

                drawingHexagons.add(hexagon);

                Log.w(TAG, "getHexagons: dataHexagonsIndex: " + dataHexagonsIndex + " current hexagon id: " + dataHexagons.get(dataHexagonsIndex).getHexagonId());
                dataHexagonsIndex++;
            }
        }
    }

    /**
     * Generates locations of intersections for drawing.
     */
    public void generateIntersections () {
        intersections[0] = new IntersectionDrawable(0, 1049, 642);
        intersections[1] = new IntersectionDrawable(1, 887, 574);
        intersections[2] = new IntersectionDrawable(2, 726, 642);
        intersections[3] = new IntersectionDrawable(3, 726, 856);
        intersections[4] = new IntersectionDrawable(4, 887, 924);
        intersections[5] = new IntersectionDrawable(5, 1049, 856);
        intersections[6] = new IntersectionDrawable(6, 1210, 574);
        intersections[7] = new IntersectionDrawable(7, 1210, 360);
        intersections[8] = new IntersectionDrawable(8, 1049, 292);
        intersections[9] = new IntersectionDrawable(9, 887, 360);
        intersections[10] = new IntersectionDrawable(10, 726, 292);
        intersections[11] = new IntersectionDrawable(11, 564, 360);
        intersections[12] = new IntersectionDrawable(12, 564, 574);
        intersections[13] = new IntersectionDrawable(13, 403, 642);
        intersections[14] = new IntersectionDrawable(14, 403, 856);
        intersections[15] = new IntersectionDrawable(15, 564, 924);
        intersections[16] = new IntersectionDrawable(16, 564, 1138);
        intersections[17] = new IntersectionDrawable(17, 726, 1206);
        intersections[18] = new IntersectionDrawable(18, 887, 1138);
        intersections[19] = new IntersectionDrawable(19, 1049, 1206);
        intersections[20] = new IntersectionDrawable(20, 1210, 1138);
        intersections[21] = new IntersectionDrawable(21, 1210, 924);
        intersections[22] = new IntersectionDrawable(22, 1372, 856);
        intersections[23] = new IntersectionDrawable(23, 1372, 642);
        intersections[24] = new IntersectionDrawable(24, 1533, 574);
        intersections[25] = new IntersectionDrawable(25, 1523, 379);
        intersections[26] = new IntersectionDrawable(26, 1372, 292);
        intersections[27] = new IntersectionDrawable(27, 1361, 97);
        intersections[28] = new IntersectionDrawable(28, 1210, 20);
        intersections[29] = new IntersectionDrawable(29, 1038, 97);
        intersections[30] = new IntersectionDrawable(30, 887, 20);
        intersections[31] = new IntersectionDrawable(31, 715, 97);
        intersections[32] = new IntersectionDrawable(32, 564, 20);
        intersections[33] = new IntersectionDrawable(33, 403, 97);
        intersections[34] = new IntersectionDrawable(34, 403, 292);
        intersections[35] = new IntersectionDrawable(35, 241, 379);
        intersections[36] = new IntersectionDrawable(36, 241, 574);
        intersections[37] = new IntersectionDrawable(37, 89, 661);
        intersections[38] = new IntersectionDrawable(38, 89, 836);
        intersections[39] = new IntersectionDrawable(39, 241, 924);
        intersections[40] = new IntersectionDrawable(40, 241, 1138);
        intersections[41] = new IntersectionDrawable(41, 403, 1206);
        intersections[42] = new IntersectionDrawable(42, 403, 1400);
        intersections[43] = new IntersectionDrawable(43, 564, 1488);
        intersections[44] = new IntersectionDrawable(44, 726, 1400);
        intersections[45] = new IntersectionDrawable(45, 887, 1488);
        intersections[46] = new IntersectionDrawable(46, 1038, 1400);
        intersections[47] = new IntersectionDrawable(47, 1210, 1488);
        intersections[48] = new IntersectionDrawable(48, 1361, 1400);
        intersections[49] = new IntersectionDrawable(49, 1372, 1203);
        intersections[50] = new IntersectionDrawable(50, 1523, 1118);
        intersections[51] = new IntersectionDrawable(51, 1533, 924);
        intersections[52] = new IntersectionDrawable(52, 1684, 856);
        intersections[53] = new IntersectionDrawable(53, 1684, 642);
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

    /* ---------- getters and setters ------------ */

    public int[] getColors () {
        return colors;
    }

    public void setColors (int[] colors) {
        this.colors = colors;
    }

    public ArrayList<RoadDrawable> getRoads () {
        return roads;
    }

    public ArrayList<HexagonDrawable> getDrawingHexagons () {
        return drawingHexagons;
    }

    public void setDrawingHexagons (ArrayList<HexagonDrawable> drawingHexagons) {
        this.drawingHexagons = drawingHexagons;
    }
}
