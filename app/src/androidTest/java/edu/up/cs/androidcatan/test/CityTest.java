package edu.up.cs.androidcatan.test;

import org.junit.Test;

import edu.up.cs.androidcatan.catan.gamestate.buildings.City;

import static org.junit.Assert.assertEquals;

public class CityTest {

    @Test //Written By: Niraj Mali
    public void getVictoryPoints() {
        City city = new City(0);
        assertEquals(2, city.getVictoryPoints());
    }
}
