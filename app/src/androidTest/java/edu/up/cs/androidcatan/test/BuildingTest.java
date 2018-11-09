package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;

import static org.junit.Assert.*;

public class BuildingTest {

    @Test
    public void getOwnerId() {
        Building city = new City(0, 1);
        assertEquals(1, city.getOwnerId());
    }

    @Test
    public void setOwnerId() {
        Building city = new City(1, 0);
        city.setOwnerId(-1);

        assertEquals(0, city.getOwnerId());

        city.setOwnerId(5);
        assertEquals(0, city.getOwnerId());

        city.setOwnerId(2);
        assertEquals(2, city.getOwnerId());
    }

}