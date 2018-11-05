package catan.tests;

import org.junit.Test;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;

im

public class ExampleUnitTest {
    @Test
    public static void example () {

    }


    @Test
    public static void testGenerateHexagonTiles () {

        Board board = new Board();

        ArrayList<Hexagon> hexes = board.getHexagons();

        // count the amount that each resource type appears on the board
        int[] count = {0, 0, 0, 0};

        for (Hexagon hex : hexes) {
            count[hex.getResourceId()]++;
        }

        for (int i : count) {
            assert i <= 4;
        }

        assert true;
    }

}
