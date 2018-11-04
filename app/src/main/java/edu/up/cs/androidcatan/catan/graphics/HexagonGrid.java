package edu.up.cs.androidcatan.catan.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class HexagonGrid extends BoardSurfaceView {

    // instance variables
    protected int x, y;
    protected int height;
    protected double width;
    protected int margin;
    protected int[] numTiles = {4, 3, 3, 3, 4};
    protected int[] colors = {Color.GREEN, Color.argb(255, 204, 153, 0), Color.RED, Color.argb(255, 194, 194, 214), Color.argb(255, 102, 102, 204)};

    protected int[] robberLocation = {2, 2};

    // TODO roads
    ArrayList<RoadDrawable> roads = new ArrayList<>();

    ArrayList<HexagonDrawable> hexagons = new ArrayList<>();

    public HexagonGrid(Context context, int x, int y, int size, int margin) {
        super(context);
        setWillNotDraw(false);

        this.x = x;
        this.y = y;
        //this.size = size;     TODO Size appears to be unneeded
        this.height = size * 2;
        this.width = size * Math.sqrt(3);
        this.margin = margin;

        getHexagons(x, y, size);

    }

    // TODO do we need this constructor?
    public HexagonGrid(Context context, AttributeSet attrs, int x, int y, int size, int margin) {
        super(context, attrs);
        setWillNotDraw(false);

        this.x = x;
        this.y = y;
        this.height = size * 2;
        this.width = size * (int) Math.sqrt(3);
        this.margin = margin;

        getHexagons(x, y, size);
    }

    public void drawGrid(Canvas canvas) {
        for (HexagonDrawable h : hexagons) {
            h.drawHexagon(canvas);
        }

        // TODO roads
        for (RoadDrawable r : roads) {
            r.drawRoad(canvas);
        }
    }

    // method that generates the individual hexagon objects from the Hexagon class
    public void getHexagons(int x, int y, int size) {

        hexagons = new ArrayList<HexagonDrawable>();

        int[] rows = {1, 1, 0, 1, 1};
        int[] hexagonsInEachRow = {3, 4, 5, 4, 3};
        int offsetX;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < hexagonsInEachRow[i]; j++) {

                // TODO robber should be redone
                boolean isRobber = false;
                if (robberLocation[0] == i && robberLocation[1] == j) {
                    isRobber = true;
                    Log.d("user", "grid robber");
                }

                int color = getTile(i, j);

                offsetX = (i % 2 == 0) ? (int) this.width / 2 + margin / 2 : 0;

                HexagonDrawable hexagon = new HexagonDrawable(this.getContext(), offsetX + x + (int) ((this.width + this.margin) * (j + rows[i])), y + (((this.height) * 3) / 4 + this.margin) * i, size, color, isRobber);

                //int[][] points = hexagon.getHexagonPoints();

                //roads.add(new RoadDrawable(points, 0));

                hexagons.add(hexagon);
            }
        }
    }

    
    public int getXVal() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }


    public int getYVal() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public int getGridHeight() {
        return height;
    }

    public void setGridHeight(int height) {
        this.height = height;
    }


    public double getGridWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int[] getNumTiles() {
        return numTiles;
    }

    public void setNumTiles(int[] numTiles) {
        this.numTiles = numTiles;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public int[] getRobberLocation() {
        return robberLocation;
    }

    public void setRobberLocation(int[] robberLocation) {
        this.robberLocation = robberLocation;
    }

    public ArrayList<RoadDrawable> getRoads() {
        return roads;
    }

    public void setRoads(ArrayList<RoadDrawable> roads) {
        this.roads = roads;
    }

    public ArrayList<HexagonDrawable> getHexagons() {
        return hexagons;
    }

    public void setHexagons(ArrayList<HexagonDrawable> hexagons) {
        this.hexagons = hexagons;
    }

    // getTile method generated random tiles to fill the grid
    // TODO needs to be redone
    public int getTile(int i, int j) {

        if (i == 2 && j == 2) {
            return Color.GRAY;
        }

        int max = numTiles.length - 1;
        int min = 0;
        int tile = 0;
        Random random = new Random();
        int randomNumber = random.nextInt((max - min) + 1) + min;
        while (numTiles[randomNumber] < 0) {
            randomNumber = random.nextInt((max - min) + 1) + min;
        }
        tile = randomNumber;
        numTiles[randomNumber]--;
        return colors[tile];
    }


}
