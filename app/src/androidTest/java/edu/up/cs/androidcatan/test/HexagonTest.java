package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.Hexagon;

import static org.junit.Assert.assertEquals;

public class HexagonTest {

    @Test //Written By: Niraj
    public void getResourceId()
    {
        Hexagon hexagon = new Hexagon(0,2,6);
        assertEquals(0, hexagon.getResourceId());

        hexagon.setResourceId(3);
        assertEquals(3, hexagon.getResourceId());
    }

    @Test //Written By: Niraj
    public void getChitValue()
    {
        Hexagon hexagon = new Hexagon(0,2,6);
        assertEquals(2, hexagon.getChitValue());

        hexagon.setChitValue(5);
        assertEquals(5, hexagon.getChitValue());
    }

    @Test //Written By: NIraj
    public void getHexagonId()
    {
        Hexagon hexagon = new Hexagon(0,2,6);
        assertEquals(6, hexagon.getHexagonId());

        hexagon.setHexagonId(9);
        assertEquals(9, hexagon.getHexagonId());
    }
}
