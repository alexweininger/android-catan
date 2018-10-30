package edu.up.cs.androidcatan;

/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 24th, 2018
 * https://github.com/alexweininger/game-state
 **/

import java.util.HashMap;

public class Building {

    //ensures the player has enough resources to build the requested building
    private String buildingName;
    private static HashMap<String, Integer> resourceCost = new HashMap<>();
    private int victoryPoints, ownerId;

    /** Building constructor
     * @param buildingName - name of the building, we may remove this later
     * @param victoryPoints - number of victory points building grants the owner on building
     * @param ownerId - player who owns and built building
     */
    public Building(String buildingName, int victoryPoints, int ownerId) {
        this.buildingName = buildingName;
        this.victoryPoints = victoryPoints;
        this.ownerId = ownerId;
    }

    /** build TODO
     *
     * @param player - player who is building the building

    public void build(Player player) {
        // TODO does this work?
        player.removeResources("Brick", this.resourceCost.get("Brick"));
        player.removeResources("Ore", this.resourceCost.get("Ore"));
        player.removeResources("Sheep", this.resourceCost.get("Sheep"));
        player.removeResources("Wheat", this.resourceCost.get("Wheat"));
        player.removeResources("Wood", this.resourceCost.get("Wood"));

        //assigns the player's id the building signifying who owns it
        setOwnerId(player.getPlayerId());
        //adds the building to the player's array list of built buildings
        // TODO board.addBuilding(this);

        //TODO: mark when a location is taken on the board (Use setIntersectionId(int intersectionId))
    }*/

    /**
     *
     * @return string representation of a Building
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("Building{");
        sb.append("buildingName='");
        sb.append(buildingName);
        sb.append('\'');
        sb.append(", resourceCost=");
        sb.append(resourceCost);
        sb.append(", victoryPoints=");
        sb.append(victoryPoints);
        sb.append(", intersectionId=");
        sb.append(", ownerID=");
        sb.append(ownerId);
        sb.append('}');

        return sb.toString();
    }

    /**
     *
     * @return the name of a building
     */
    /*build
     *
     * Build a building; overrided by subclasses to specify which resources to take. It will be
     * Iterating through the reqResources HashMap to taking the needed values from the player
     * resources HashMap.
     *
     * Once resources are taken
     *

    public void build(Player player)
    {
        for(HashMap.Entry<String, Integer> entry: resourceCost.entrySet()){
            if(entry.getValue() < resourceCost.get(entry.getKey())){
                player.removeResources(entry.getKey(), entry.getValue());
            }
        }

        //assigns the player's id the building signifying who owns it
        setOwnerId(player.getPlayerId());
        //adds the building to the player's array list of built buildings
        player.addBuilding(this);

        //TODO: mark when a location is taken on the board (Use setIntersectionId(int intersectionId))
    }*/

    /*hasResources
     *
     * Iterates through reqResources HashMap and checks the Key values in the resources
     * HashMap to see if there is a sufficient amount of resources to build the called building.
     *
     * Return true if there are enough resources; return false if otherwise
     *

    public static boolean hasResources(HashMap<String, Integer> resources){    //TODO Logic needs to be tested
        for(HashMap.Entry<String, Integer> entry: resources.entrySet()){
            if(entry.getValue() < resourceCost.get(entry.getKey())){
                return false;
            }
        }
        return true;
    }*/


    public String getBuildingName() {
        return buildingName;
    }

    /**
     *
     * @param buildingName the of building
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     *
     * @return number of victory points
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     *
     * @param victoryPoints sets the number
     */
    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    /**
     *
     * @return id of who owns the building
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     *
     * @param ownerId if of who owns the building
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

}
