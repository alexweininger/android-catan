package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;

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

                //int[][] points = hexagon.getHexagonPoints();

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
