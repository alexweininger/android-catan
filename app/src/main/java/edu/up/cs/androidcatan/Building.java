package edu.up.cs.androidcatan;

/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 24th, 2018
 * https://github.com/alexweininger/game-state
 **/

public abstract class Building {

    private int victoryPoints, ownerId;
    private int[] resourceCost = new int[5];

    /**
     * Building constructor
     *
     * @param ownerId - player who owns and built building
     */
    public Building(int ownerId) {
        this.ownerId = ownerId;
    }

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
     * @return number of victory points
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * @return id of who owns the building
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
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
}
