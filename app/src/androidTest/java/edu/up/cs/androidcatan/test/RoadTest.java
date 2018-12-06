package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;

import static org.junit.Assert.assertEquals;

public class RoadTest {

    @Test // by Alex and Daniel
    public void getOppositeIntersection() {
        Road road = new Road(0, 5, 2);
        assertEquals(2, road.getOppositeIntersection(5));
        assertEquals(5, road.getOppositeIntersection(2));
    }

    @Test // by Alex and Daniel
    public void getIntersectionAId() {
        Road road = new Road(0, 5, 2);
        assertEquals(5, road.getIntersectionAId());
    }

    @Test // by Alex and Daniel
    public void getIntersectionBId() {
        Road road = new Road(0, 5, 2);
        assertEquals(2, road.getIntersectionBId());
    }

    @Test // by Alex and Daniel
    public void getVictoryPoints() {
        Road road = new Road(0, 5, 2);
        assertEquals(0, road.getVictoryPoints());
    }
}
