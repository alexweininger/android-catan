package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.Port;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;

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

    // colors of the resources
    protected int[] colors = {Color.argb(255, 165, 63, 4), Color.argb(255, 255, 225, 0), Color.argb(255, 0, 102, 25), Color.argb(255, 68, 86, 85), Color.argb(255, 123, 206, 107), Color.argb(255, 192, 193, 141)};

    // colors for each player, the last color is for no player (highlighting)
    public static int[] playerColors = {Color.RED, Color.WHITE, Color.argb(255, 255, 128, 17), Color.BLUE, Color.CYAN};

    public static int[] settlementPictures = {R.drawable.settlement_red, R.drawable.settlement_white, R.drawable.settlement_orange, R.drawable.settlement_blue, R.drawable.city_selected};

    public static int[] cityPictures = {R.drawable.city_red, R.drawable.city_white, R.drawable.city_orange, R.drawable.city_blue, R.drawable.settlement_selected};

    private Board board;
    private IntersectionDrawable[] intersections = new IntersectionDrawable[54]; // list of IntersectionDrawable objects
    ArrayList<HexagonDrawable> drawingHexagons = new ArrayList<>(); // list of HexagonDrawable objects

    private int highlightedHexagon = -1;

    private ArrayList<Integer> highlightedIntersections = new ArrayList<>();

    private boolean debugMode = false;

    /* ---------- Constructor ------------ */

    public HexagonGrid (Context context, Board board, int x, int y, int size, int margin, boolean debugMode) {
        super(context);
        setWillNotDraw(false);
        this.x = x;
        this.y = y;
        this.size = size;
        this.height = size * 2;
        this.width = size * Math.sqrt(3);
        this.margin = margin;
        this.board = board;
        this.debugMode = debugMode;
        generateIntersections();
        this.board = board;
    }

    /* ---------- Drawing methods ------------ */

    /**
     * Draws all of the components on the board.
     *
     * @param canvas Canvas to draw on.
     */
    public void drawGameBoard (Canvas canvas) {
        generateDrawableHexagons(x, y, size); // get hexes

        drawBorder(canvas);

        for (HexagonDrawable h : drawingHexagons) {
            h.drawHexagon(canvas, this.debugMode);
        } // draw each hexagon

        drawPorts(canvas);
        drawRoads(canvas);
        drawBuildings(canvas);

        for (IntersectionDrawable intersection : intersections) {
            intersection.drawIntersection(canvas, this.debugMode);
        } // draw each intersection

        this.invalidate();
    }

    /**
     * Draws all of the roads.
     *
     * @param canvas Canvas to draw on.
     */
    public void drawRoads (Canvas canvas) {
        Log.i(TAG, "drawRoads() called");

        Paint roadPaint = new Paint(); // paint for drawing the roads
        roadPaint.setStyle(Paint.Style.STROKE);
        roadPaint.setStrokeWidth(30);

        ArrayList<Road> dataRoads = this.board.getRoads(); // get list of all roads on the board
        Log.d(TAG, "drawRoads: roads: " + this.board.getRoads().toString());

        for (Road r : dataRoads) {
            roadPaint.setColor(playerColors[r.getOwnerId()]);
            canvas.drawLine(intersections[r.getIntersectionAId()].getXPos(), intersections[r.getIntersectionAId()].getYPos(), intersections[r.getIntersectionBId()].getXPos(), intersections[r.getIntersectionBId()].getYPos(), roadPaint);
        }
    }

    /**
     * @param canvas Canvas object to draw the buildings on.
     */
    public void drawBuildings (Canvas canvas) {
        Paint highlightPaint = new Paint();
        highlightPaint.setColor(Color.CYAN);
        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeWidth(5);

        // get all buildings on the board
        Building[] buildings = this.board.getBuildings();

        // go through each building
        for (int i = 0; i < buildings.length; i++) {
            if (buildings[i] != null) { // if we need to draw a building at this intersection

                // get center of intersection
                int xPos = this.intersections[i].getXPos();
                int yPos = this.intersections[i].getYPos();

                Drawable buildingPicture;
                if (buildings[i] instanceof Settlement) {

                    if (this.highlightedIntersections.contains(i)) { // if we need to highlight the building
                        Log.i(TAG, "drawBuildings: drawing highlighted settlement at " + i);
                        buildingPicture = this.getContext().getDrawable(settlementPictures[4]);
                    } else {
                        Log.i(TAG, "drawBuildings: drawing a settlement.");
                        buildingPicture = this.getContext().getDrawable(settlementPictures[buildings[i].getOwnerId()]);
                    }

                    if (buildingPicture != null) {
                        buildingPicture.setBounds(xPos - 50, yPos - 50, xPos + 50, yPos + 40);
                        buildingPicture.draw(canvas);
                    } else {
                        Log.e(TAG, "drawBuildings: building picture is null", new NullPointerException());
                    }
                } else if (buildings[i] instanceof City) {

                    if (this.highlightedIntersections.contains(i)) {
                        Log.i(TAG, "drawBuildings: drawing highlighted city at " + i);
                        buildingPicture = this.getContext().getDrawable(cityPictures[4]);
                    } else {
                        Log.i(TAG, "drawBuildings: drawing a city.");
                        buildingPicture = this.getContext().getDrawable(cityPictures[buildings[i].getOwnerId()]);
                    }
                    if (buildingPicture != null) {
                        yPos -= 15;
                        buildingPicture.setBounds(xPos - 50, yPos - 60, xPos + 50, yPos + 60);
                        buildingPicture.draw(canvas);
                    } else {
                        Log.e(TAG, "drawBuildings: building picture is null", new NullPointerException());
                    }
                }
            } else {
                if (this.highlightedIntersections.contains(i)) {
                    Log.i(TAG, "drawBuildings: drawing highlighted intersection at " + i);
                    int xPos = this.intersections[i].getXPos();
                    int yPos = this.intersections[i].getYPos();
                    canvas.drawCircle(xPos, yPos, 30, highlightPaint);
                }
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
        tanPaint.setColor(Color.argb(255, 226, 210, 113));

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
    public void generateDrawableHexagons (int x, int y, int size) {
        ArrayList<Hexagon> dataHexagons = board.getHexagonListForDrawing();
        drawingHexagons = new ArrayList<>();
        int[] rows = {1, 1, 0, 1, 1};
        int dataHexagonsIndex = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < hexagonsInEachRow[i]; j++) {
                boolean isRobberHexagon = this.board.getRobber().getHexagonId() == dataHexagons.get(dataHexagonsIndex).getHexagonId();
                boolean highlightedHexagon = this.highlightedHexagon == dataHexagons.get(dataHexagonsIndex).getHexagonId();
                boolean isDesertHexagon = dataHexagons.get(dataHexagonsIndex).getResourceId() == 5;

                int hexagonColor = this.colors[dataHexagons.get(dataHexagonsIndex).getResourceId()];
                int offsetX = (i % 2 == 0) ? (int) this.width / 2 + margin / 2 : 0;
                int xPos = offsetX + x + (int) ((this.width + this.margin) * (j + rows[i]));
                int yPos = y + (((this.height) * 3) / 4 + this.margin) * i;

                HexagonDrawable hexagon = new HexagonDrawable(this.getContext(), xPos, yPos, size, hexagonColor, isRobberHexagon, isDesertHexagon, dataHexagons.get(dataHexagonsIndex).getChitValue(), dataHexagons.get(dataHexagonsIndex).getHexagonId(), highlightedHexagon, dataHexagons.get(dataHexagonsIndex).getResourceId());
                drawingHexagons.add(hexagon);
                dataHexagonsIndex++;
            }
        }
    }

    /**
     * draws the ports at specific locations on the canvas
     *
     * @param canvas Canvas to draw ports on.
     */
    private void drawPorts (Canvas canvas) {
        ArrayList<Port> ports = this.board.getPortList();

        ports.get(0).drawPort(canvas, this.intersections[ports.get(0).getIntersectionA()].getXPos(), this.intersections[ports.get(0).getIntersectionA()].getYPos() - 100, 40, this.getContext(), this.intersections[ports.get(0).getIntersectionA()], this.intersections[ports.get(0).getIntersectionB()], debugMode);
        ports.get(1).drawPort(canvas, this.intersections[ports.get(1).getIntersectionB()].getXPos() + 100, this.intersections[ports.get(1).getIntersectionB()].getYPos() + 10, 25, this.getContext(), this.intersections[ports.get(1).getIntersectionA()], this.intersections[ports.get(1).getIntersectionB()], debugMode);
        ports.get(2).drawPort(canvas, this.intersections[ports.get(2).getIntersectionA()].getXPos() - 100, this.intersections[ports.get(2).getIntersectionA()].getYPos() + 10, 25, this.getContext(), this.intersections[ports.get(2).getIntersectionA()], this.intersections[ports.get(2).getIntersectionB()], debugMode);
        ports.get(3).drawPort(canvas, this.intersections[ports.get(3).getIntersectionA()].getXPos() - 100, this.intersections[ports.get(3).getIntersectionA()].getYPos() + 100, 40, this.getContext(), this.intersections[ports.get(3).getIntersectionA()], this.intersections[ports.get(3).getIntersectionB()], debugMode);
        ports.get(4).drawPort(canvas, this.intersections[ports.get(4).getIntersectionA()].getXPos() - 100, this.intersections[ports.get(4).getIntersectionA()].getYPos() + 100, 40, this.getContext(), this.intersections[ports.get(4).getIntersectionA()], this.intersections[ports.get(4).getIntersectionB()], debugMode);
        ports.get(5).drawPort(canvas, this.intersections[ports.get(5).getIntersectionA()].getXPos() + 50, this.intersections[ports.get(5).getIntersectionA()].getYPos() + 90, 35, this.getContext(), this.intersections[ports.get(5).getIntersectionA()], this.intersections[ports.get(5).getIntersectionB()], debugMode);
        ports.get(6).drawPort(canvas, this.intersections[ports.get(6).getIntersectionA()].getXPos() + 100, this.intersections[ports.get(6).getIntersectionA()].getYPos(), 40, this.getContext(), this.intersections[ports.get(6).getIntersectionA()], this.intersections[ports.get(6).getIntersectionB()], debugMode);
        ports.get(7).drawPort(canvas, this.intersections[ports.get(7).getIntersectionB()].getXPos() + 30, this.intersections[ports.get(7).getIntersectionB()].getYPos() + 100, 25, this.getContext(), this.intersections[ports.get(7).getIntersectionA()], this.intersections[ports.get(7).getIntersectionB()], debugMode);
        ports.get(8).drawPort(canvas, this.intersections[ports.get(8).getIntersectionA()].getXPos() + 150, this.intersections[ports.get(8).getIntersectionA()].getYPos(), 40, this.getContext(), this.intersections[ports.get(8).getIntersectionA()], this.intersections[ports.get(8).getIntersectionB()], debugMode);
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
        intersections[46] = new IntersectionDrawable(46, 1038, 1410);
        intersections[47] = new IntersectionDrawable(47, 1210, 1488);
        intersections[48] = new IntersectionDrawable(48, 1361, 1400);
        intersections[49] = new IntersectionDrawable(49, 1372, 1203);
        intersections[50] = new IntersectionDrawable(50, 1523, 1118);
        intersections[51] = new IntersectionDrawable(51, 1533, 924);
        intersections[52] = new IntersectionDrawable(52, 1684, 856);
        intersections[53] = new IntersectionDrawable(53, 1684, 642);
    }

    /* ---------- getters and setters ------------ */

    public Board getBoard () {
        return board;
    }

    public void setBoard (Board board) {
        this.board = board;
    }

    public IntersectionDrawable[] getIntersections () {
        return intersections;
    }

    public ArrayList<HexagonDrawable> getDrawingHexagons () {
        return drawingHexagons;
    }

    public int getHighlightedHexagon () { return highlightedHexagon;}

    public void setHighlightedHexagon (int highlightedHexagon) { this.highlightedHexagon = highlightedHexagon; }

    public void toggleDebugMode () { this.debugMode = !this.debugMode; }

    public ArrayList<Integer> getHighlightedIntersections () { return highlightedIntersections; }

    public void clearHighLightedIntersections () { this.highlightedIntersections = new ArrayList<>(); }

    public void addHighlightedIntersection (int intersection) {
        if (highlightedIntersections.size() > 1) {
            highlightedIntersections.remove(0);
            highlightedIntersections.add(intersection);
            Log.e(TAG, "addSelectedIntersection: Cannot add intersection to selectedIntersections because there are already more than 1 selected intersection.");
        } else {
            this.highlightedIntersections.add(intersection);
        }
    }

    public HexagonGrid (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HexagonGrid (Context context) {
        super(context);
    }
}
