package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.Port;

import static org.junit.Assert.assertEquals;

public class PortTest {

    @Test //Written By: Alex Weininger
    public void getIntersectionA () {
        Port port = new Port(5, 2, 2, 2);
        assertEquals(5, port.getIntersectionA());

        port.setIntersectionA(8);
        assertEquals(8, port.getIntersectionA());
    }

    @Test //Written By: Alex Weininger
    public void getIntersecionB () {
        Port port = new Port(5, 9, 2, 2);
        assertEquals(9, port.getIntersectionB());

        port.setIntersectionB(4);
        assertEquals(4, port.getIntersectionB());
    }

    @Test //Written By: Alex Weininger
    public void getResourceId () {
        Port port = new Port(8, 2, 3, 4);
        assertEquals(4, port.getResourceId());

        port.setResourceId(0);
        assertEquals(0, port.getResourceId());
    }
}
