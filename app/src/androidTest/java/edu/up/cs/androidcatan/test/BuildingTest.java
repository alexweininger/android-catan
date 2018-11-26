package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;

import static org.junit.Assert.*;

public class BuildingTest {

    @Test
    public void getOwnerId() {
        Building building = new City(1);
        assertEquals(1, building.getOwnerId());
    }

    @Test
    public void setOwnerId() {
        Building building = new City(0);
        building.setOwnerId(-1);

        assertEquals(0, building.getOwnerId());

        building.setOwnerId(5);
        assertEquals(0, building.getOwnerId());

        building.setOwnerId(2);
        assertEquals(2, building.getOwnerId());
    }

}