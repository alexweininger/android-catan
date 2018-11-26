package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;

import static org.junit.Assert.assertEquals;

public class RoadTest {

    @Test
    public void getOppositeIntersection()
    {
        Road road = new Road(0,5,2);
        assertEquals(2, road.getOppositeIntersection(5));
        assertEquals(5,road.getOppositeIntersection(2));
    }

    @Test
    public void getIntersecitonAId()
    {
        Road road = new Road(0,5,2);
        assertEquals(5,road.getIntersectionAId());
    }

    @Test
    public void getIntersecitonBId()
    {
        Road road = new Road(0,5,2);
        assertEquals(2,road.getIntersectionBId());
    }

    @Test
    public void getVictoryPoints()
    {
        Road road = new Road(0,5,2);
        assertEquals(0,road.getVictoryPoints());
    }
}
