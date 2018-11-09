package catan.tests;

import org.junit.Test;

import java.util.ArrayList;

import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public static void example () {

    }


    @Test
    public static void testGenerateHexagonTilesResourceDistribution () {

        int[] resourceTypeCount = {3, 4, 4, 3, 4, 1};
        int[] chitValuesCount = {1, 0, 1, 2, 2, 2, 2, 0, 2, 2, 2, 2, 1};
        int[] resources = {0, 1, 2, 3, 4, 5};

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

        assertEquals(count[0], 0);

        assert true;
    }

    @Test
    public static void testGenerateHexagonTilesChitDistribution() {

    }

}
