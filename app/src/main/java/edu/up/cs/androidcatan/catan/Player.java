package edu.up.cs.androidcatan.catan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 9th, 2018
 * https://github.com/alexweininger/android-catan
 **/

public class Player {

    private static final String TAG = "Player"; // TAG used for Logging

    /* ----- Player instance variables ----- */

    // resourceCard index values: 0 = Brick, 1 = Grain, 2 = Lumber, 3 = Ore, 4 = Wool
    private int[] resourceCards = {0, 0, 0, 0, 0}; // array for number of each resource card a player has

    // array for relating resource card names to resource card ids in the resourceCards array above
    private static final String[] resourceCardIds = {"Brick", "Grain", "Lumber", "Ore", "Wool"};

    // ArrayList of the development cards the player owns
    private ArrayList<Integer> developmentCards = new ArrayList<>();

    // number of buildings the player has to build {roads, settlements, cities}
    private int[] buildingInventory = {15, 5, 4};

    // determined by how many knight dev cards the player has played, used for determining who currently has the largest army trophy
    private int armySize;

    private int playerId;  // player

    private int victoryPoints;
    private int victoryPointsPrivate;
    private int victoryPointsFromDevCard;

    /**
     * Player constructor
     */
    public Player (int id) {
        this.playerId = id;
        this.armySize = 0;
        this.victoryPointsFromDevCard = 0;
    }

    /**
     * deepCopy constructor
     *
     * @param p - Player object to copy
     */
    Player (Player p) {
        this.setPlayerId(p.getPlayerId());
        this.setArmySize(p.getArmySize());
        this.setDevelopmentCards(p.getDevelopmentCards());
        this.setBuildingInventory(p.getBuildingInventory());
        this.setResourceCards(p.getResourceCards());
        this.setVictoryPointsFromDevCard(p.getVictoryPointsFromDevCard());
        this.setVictoryPoints(p.getVictoryPoints());
        this.setVictoryPointsPrivate(p.getVictoryPointsPrivate());
    }

    void addVictoryPointsDevCard () {
        this.victoryPointsFromDevCard += 1;
    }

    /**
     * error checking:
     * - checks for valid resourceCardId
     *
     * @param resourceCardId - index value of resource to add (0-4) defined above
     * @param numToAdd - number of resource cards of this type to add to the players inventory AW
     */
    public void addResourceCard (int resourceCardId, int numToAdd) {
        if (resourceCardId < 0 || resourceCardId >= 5) { // check for a valid resourceCardId
            Log.d("devError", "ERROR addResourceCard: given resourceCardId: " + resourceCardId + " is invalid. Must be an integer (0-4).");
        } else {
            Log.d("devInfo", "INFO addResourceCard: added numToAdd: " + numToAdd + " resourceCardId: " + resourceCardId + " to playerId: " + this.playerId + " resourceCards.");
            this.resourceCards[resourceCardId] += numToAdd; // increase count of the resource card
        }
    }

    /**
     * @param resourceCardId - resource to check
     * @param numToCheckFor - number of resources to make sure the player has
     * @return - whether they have at least that many resources of the given type
     */
    boolean checkResourceCard (int resourceCardId, int numToCheckFor) {
        Log.i(TAG, "checkResourceCard() called with: resourceCardId = [" + resourceCardId + "], numToCheckFor = [" + numToCheckFor + "]");
        if (resourceCardId < 0 || resourceCardId >= 5) { // check for valid resourceCardId
            Log.d("devError", "ERROR removeResourceCard: given resourceCardId: " + resourceCardId + " is invalid. Must be an integer (0-4).");
            return false; // did not remove resource cards to players inventory
        }
        // return true if player has greater or equal to num to check for
        Log.d(TAG, "checkResourceCard() returned: " + (this.resourceCards[resourceCardId] >= numToCheckFor));
        return this.resourceCards[resourceCardId] >= numToCheckFor;
    }

    /**
     * @param resourceCost - resourceCost array, e.g. Settlement.resourceCost
     * @return - true of false, does the player have all of these resources?
     */
    boolean hasResourceBundle (int[] resourceCost) {
        Log.d(TAG, "hasResourceBundle() called with: resourceCost = [" + Arrays.toString(resourceCost) + "]");
        Log.i(TAG, "hasResourceBundle: " + this.printResourceCards());
        for (int i = 0; i < resourceCost.length; i++) {
            if (!checkResourceCard(i, resourceCost[i])) {
                Log.d(TAG, "hasResourceBundle() returned: " + false);
                return false;
            }
        }
        Log.d(TAG, "hasResourceBundle() returned: " + true);
        return true;
    }

    void removeDevCard (int removeCardNum) {
        Log.i(TAG, "removeDevCard BEFORE REMOVING: " + this.developmentCards.toString());
        this.developmentCards.remove((Integer) removeCardNum);
        Log.i(TAG, "removeDevCard AFTER REMOVING: " + this.developmentCards.toString());
    }

    /**
     * error checking:
     * - error checks for valid resourceCardId
     * - error checks for preventing negative resource card counts
     *
     * @param resourceCardId - id of resource card to remove from players inventory
     * @param numToRemove - number of resource cards of this type to remove
     * @return - if numToRemove resource card(s) have been removed from the players inventory
     */
    public boolean removeResourceCard (int resourceCardId, int numToRemove) {
        Log.d(TAG, "removeResourceCard() called with: resourceCardId = [" + resourceCardId + "], numToRemove = [" + numToRemove + "]");
        if (resourceCardId < 0 || resourceCardId >= 5) { // check for valid resourceCardId
            Log.i(TAG, "removeResourceCard: given resourceCardId: " + resourceCardId + " is invalid. Must be an integer (0-4).");
            return false; // did not remove resource cards to players inventory
        } else {
            if (this.resourceCards[resourceCardId] >= numToRemove) { // check to prevent negative card counts
                Log.i(TAG, "removeResourceCard: removed numToRemove: " + numToRemove + " resourceCardId: " + resourceCardId + " from playerId: " + this.playerId + " resourceCards.");
                this.resourceCards[resourceCardId] -= numToRemove; // remove cards
                return true; // removed cards to players inventory
            } else {
                Log.i(TAG, "removeResourceCard: cannot remove numToRemove: " + numToRemove + " resourceCardId: " + resourceCardId + " from playerId: " + this.playerId + ". Player currently has " + this.resourceCards[resourceCardId] + " cards of this resource.");
                return false; // did not remove resource cards to players inventory
            }
        }
    }

    /**
     * @param resourceCost Array of the amounts of each resource an action costs.
     * @return If the player has ALL of the resources.
     */
    boolean removeResourceBundle (int[] resourceCost) {
        Log.d(TAG, "removeResourceBundle() called with: resourceCost = [" + Arrays.toString(resourceCost) + "]");
        Log.w(TAG, "removeResourceBundle: players resources: " + Arrays.toString(this.resourceCards));
        if (!hasResourceBundle(resourceCost)) {
            Log.e(TAG, "removeResourceBundle: Cannot remove resource bundle from player " + this.playerId + ". Insufficient resources. Must do error checking before calling this method!");
            return false;
        }
        for (int i = 0; i < resourceCost.length; i++) {
            Log.w(TAG, "removeResourceBundle: attempting to remove " + resourceCost[i] + " of resource type " + i + " from player.");
            if (!this.removeResourceCard(i, resourceCost[i])) {
                Log.e(TAG, "removeResourceBundle: Cannot remove resource bundle from player " + this.playerId + ". Player.removeResourceCard method returned false.");
                return false;
            }
        }
        Log.d(TAG, "removeResourceBundle successfully removed resourceCost = [" + Arrays.toString(resourceCost) + "] from players inventory.");
        return true;
    }


    /**
     * @return String showing the number of each resource card the player has
     */
    String printResourceCards () {
        StringBuilder str = new StringBuilder();
        str.append("[");
        for (int i = 0; i < this.resourceCards.length; i++) {
            str.append(resourceCardIds[i]).append("=").append(this.resourceCards[i]);
            if (i != this.resourceCards.length - 1) {
                str.append(", ");
            }
        }
        str.append("]");
        return str.toString();
    }

    /**
     * @return -
     */
    public int[] getBuildingInventory () {
        return buildingInventory;
    }

    /**
     * @param buildingInventory
     */
    private void setBuildingInventory (int[] buildingInventory) {
        this.buildingInventory = buildingInventory;
    }

    /**
     * @param playerId
     */
    private void setPlayerId (int playerId) {
        this.playerId = playerId;
    }

    /**
     * @return - resource card array
     */
    public int[] getResourceCards () {
        return this.resourceCards;
    }

    /**
     * @param resourceCards - resource card array
     */
    private void setResourceCards (int[] resourceCards) {
        this.resourceCards = resourceCards;
    }

    /**
     * @return the size of the player's army
     */
    public int getArmySize () {
        return armySize;
    }

    /**
     * @param armySize the size of the player's army
     */
    public void setArmySize (int armySize) {
        this.armySize = armySize;
    }

    /**
     * @return victory points from dev cards
     */
    public int getVictoryPointsFromDevCard () { return victoryPointsFromDevCard; }

    /**
     * @param victoryPointsFromDevCard the amount of points from dev cards they have
     */
    public void setVictoryPointsFromDevCard (int victoryPointsFromDevCard) { this.victoryPointsFromDevCard = victoryPointsFromDevCard; }

    /**
     * @param devCard dev card to remove
     * @return if action was possible
     */
    public boolean useDevCard (int devCard) {
        if (developmentCards.contains(devCard)) {
            developmentCards.remove(devCard);
            return true;
        }
        return false;
    }

    public void decrementBuildingInventory (int buildingId) { // TODO
        this.buildingInventory[buildingId]--;
    }

    // use to allow the player to use the dev card they built the turn prior TODO
    public void setDevelopmentCardsAsPlayable () {
        for (int i = 0; i < developmentCards.size(); i++) {
            //developmentCards.get(i).setPlayable(true);
        }
    }

    /**
     * @return the player's id
     */
    public int getPlayerId () {
        return this.playerId;
    }

    /**
     * @return - list of players' development cards
     */
    public ArrayList<Integer> getDevelopmentCards () {
        return developmentCards;
    }

    /**
     * @param developmentCards List of DevelopmentCards the player currently has.
     */
    public void setDevelopmentCards (ArrayList<Integer> developmentCards) {
        this.developmentCards = developmentCards;
    }

    /**
     * @return The total amount of resourceCards a player has.
     */
    int getTotalResourceCardCount () {
        int result = 0;
        for (int resourceCard : this.resourceCards)
            result += resourceCard;
        Log.d(TAG, "getTotalResourceCardCount() returned: " + result);
        return result;
    }

    /**
     * @return - A random resourceCard is removed from the players inventory and returned.
     */
    int getRandomCard () {
        if (this.getTotalResourceCardCount() < 1) {
            Log.e(TAG, "getRandomCard: Player does not have any resources cards.");
            return -1;
        }

        Random random = new Random();
        int randomResourceId;
        do {
            randomResourceId = random.nextInt(5); // 0-4
        } while (!checkResourceCard(randomResourceId, 1));

        Log.d(TAG, "getRandomCard() returned: " + randomResourceId);
        return randomResourceId;
    }

    public static String[] getResourceCardIds () {
        return resourceCardIds;
    }

    public int getVictoryPoints () {
        return victoryPoints;
    }

    public void addVictoryPoints(int number) {
        this.victoryPoints += number;
    }

    public void addPrivateVictoryPoints(int number) {
        this.victoryPointsPrivate += number;
    }

    public void setVictoryPoints (int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public int getVictoryPointsPrivate () {
        return victoryPointsPrivate;
    }

    public void setVictoryPointsPrivate (int victoryPointsPrivate) {
        this.victoryPointsPrivate = victoryPointsPrivate;
    }

    /**
     * @return string representation of a Player
     */
    @Override
    public String toString () {
        return " Player id: " + this.playerId + ", " + "DevCards: " + this.developmentCards + ", BldgInv: " + Arrays.toString(this.buildingInventory) + ", army: " + this.armySize + "\n\tResources: " + this.printResourceCards();
    }
}


