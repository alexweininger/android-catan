package edu.up.cs.androidcatan.catan;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class Player implements Serializable {
    private static final long serialVersionUID = 1235142098074598148L;
    private static final String TAG = "Player"; // TAG used for Logging

    /* ------------------------- Player instance variables ----------------------- */

    private int playerId;  // player id (0-1)

    // array for relating resource card names to resource card ids in the resourceCards array above
    private static final String[] resourceCardIds = {"Brick", "Grain", "Lumber", "Ore", "Wool"};

    // resourceCard index values: 0 = Brick, 1 = Grain, 2 = Lumber, 3 = Ore, 4 = Wool
    private int[] resourceCards = {0, 0, 0, 0, 0}; // array for number of each resource card a player has

    // ArrayList of the development cards the player owns
    private ArrayList<Integer> developmentCards = new ArrayList<>();
    // list of dev cards the player has built on their turn
    private ArrayList<Integer> devCardsBuiltThisTurn = new ArrayList<>();

    // number of buildings the player has to build {roads, settlements, cities}
    private int[] buildingInventory = {15, 5, 4};

    // how many knight dev cards the player has played, used for  the largest army trophy
    private int armySize;

    private int victoryPoints; // victory points that will be shown to other players
    private int victoryPointsPrivate; // victory points including dev cards
    private int victoryPointsFromDevCard; // victory points from dev cards

    /**
     * Player constructor
     */
    public Player(int id) {
        this.playerId = id;
        this.armySize = 0;
        this.victoryPointsFromDevCard = 0;
    }

    /**
     * deepCopy constructor
     *
     * @param p - Player object to copy
     */
    Player(Player p) {
        this.playerId = p.playerId;
        this.armySize = p.armySize;
        this.setBuildingInventory(p.getBuildingInventory());
        this.setVictoryPointsFromDevCard(p.getVictoryPointsFromDevCard());
        this.victoryPointsPrivate = p.victoryPointsPrivate;
        this.victoryPoints = p.victoryPoints;
        this.developmentCards.addAll(p.developmentCards); //copy dev cards
        System.arraycopy(p.resourceCards, 0, this.resourceCards, 0, p.resourceCards.length); // copy resource cards
        this.devCardsBuiltThisTurn = new ArrayList<>(); // reset the array list
        this.devCardsBuiltThisTurn.addAll(p.devCardsBuiltThisTurn); // add all the items
    }

    /**
     * error checking:
     * - checks for valid resourceCardId
     *
     * @param resourceCardId - index value of resource to add (0-4) defined above
     * @param numToAdd - number of resource cards of this type to add to the players inventory AW
     */
    public void addResourceCard(int resourceCardId, int numToAdd) {
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
    public boolean checkResourceCard(int resourceCardId, int numToCheckFor) {
        Log.i(TAG, "checkResourceCard() called with: resourceCardId = [" + resourceCardId + "], numToCheckFor = [" + numToCheckFor + "]");
        if (resourceCardId < 0 || resourceCardId >= 5) { // check for valid resourceCardId
            Log.d("devError", "ERROR removeResourceCard: given resourceCardId: " + resourceCardId + " is invalid. Must be an integer (0-4).");
            return false; // did not remove resource cards to players inventory
        }

        if (numToCheckFor < 0) {
            Log.e(TAG, "checkResourceCard: numToCheckFor cannot be negative. numToCheckFor: " + numToCheckFor);
            return false;
        }

        // return true if player has greater or equal to num to check for
        Log.d(TAG, "checkResourceCard() returned: " + (this.resourceCards[resourceCardId] >= numToCheckFor));
        return this.resourceCards[resourceCardId] >= numToCheckFor;
    }

    /**
     * @param resourceCost - resourceCost array, e.g. Settlement.resourceCost
     * @return - true of false, does the player have all of these resources?
     */
    public boolean hasResourceBundle(int[] resourceCost) {
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

    /**
     * @param devCard dev card to remove
     * @return if action was possible
     */
    public boolean useDevCard(int devCard) {
        if (developmentCards.contains(devCard)) {
            developmentCards.remove(devCard);
            return true;
        }
        return false;
    }

    /**
     * removes a dev card from the players hand
     *
     * @param removeCardNum the number of the dev card to remove
     */
    public void removeDevCard(int removeCardNum) {
        Log.i(TAG, "removeDevCard BEFORE REMOVING: " + this.developmentCards.toString());
        this.developmentCards.remove((Integer) removeCardNum);
        Log.i(TAG, "removeDevCard AFTER REMOVING: " + this.developmentCards.toString());
    }

    /**
     * gets the player compare the players hand of dev cards and the ones that have been built this turn
     *
     * @return arrayList of Integers that correspond to dev cards that can be played this turn
     * which is total dev cards difference the dev cards built on this turn
     */
    public ArrayList<Integer> getPlayableDevCards() {
        ArrayList<Integer> playableDevCards = new ArrayList<>(developmentCards);

        Log.d(TAG, "Checking which dev cards are playable");
        Log.d(TAG, "Playable" + playableDevCards);
        Log.d(TAG, "Bought this turn" + devCardsBuiltThisTurn);

        for (int i = 0; i < devCardsBuiltThisTurn.size(); i++) {

            if (playableDevCards.contains(devCardsBuiltThisTurn.get(i))) {
                Log.d(TAG, "Removed a Dev Card from the players hand as it was bought this turn.");
                playableDevCards.remove(devCardsBuiltThisTurn.get(i));
            }
        }

        // return the players total dev cards difference the dev cards built on this turn
        return playableDevCards;
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
    public boolean removeResourceCard(int resourceCardId, int numToRemove) {
        Log.d(TAG, "removeResourceCard() called with: resourceCardId = [" + resourceCardId + "], numToRemove = [" + numToRemove + "]");
        if (resourceCardId < 0 || resourceCardId >= 5) { // check for valid resourceCardId
            Log.i(TAG, "removeResourceCard: given resourceCardId: " + resourceCardId + " is invalid. Must be an integer (0-4).");
            return false; // did not remove resource cards to players inventory
        } else {
            if (numToRemove < 0) {
                Log.e(TAG, "removeResourceCard: numToRemove cannot be negative. numToRemove: " + numToRemove);
                return false;
            }
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
    public boolean removeResourceBundle(int[] resourceCost) {
        if (resourceCost == null) return false; // check if null
        if (resourceCost.length != 5) return false; // check if the length is somehow less than 5

        Log.i(TAG, "removeResourceBundle() called with: resourceCost = [" + Arrays.toString(resourceCost) + "]");
        Log.i(TAG, "removeResourceBundle: players resources: " + Arrays.toString(this.resourceCards));

        // check if they have the resources to remove
        if (!hasResourceBundle(resourceCost)) {
            Log.e(TAG, "removeResourceBundle: Cannot remove resource bundle from player " + this.playerId + ". Insufficient resources. Must do error checking before calling this method!");
            return false;
        }

        // remove the resources in the resourceCost array from the players resources
        for (int i = 0; i < resourceCost.length; i++) {
            Log.w(TAG, "removeResourceBundle: attempting to remove " + resourceCost[i] + " of resource type " + i + " from player.");
            if (!this.removeResourceCard(i, resourceCost[i])) { // if this returns false, we have an issue
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
    String printResourceCards() {
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
    private int[] getBuildingInventory() {
        return buildingInventory;
    }

    /**
     * @param buildingInventory building inventory of the player
     */
    private void setBuildingInventory(int[] buildingInventory) {
        this.buildingInventory = buildingInventory;
    }

    /**
     * @return - resource card array
     */
    int[] getResourceCards() {
        return this.resourceCards;
    }

    /**
     * @param resourceCards - resource card array
     */
    public void setResourceCards(int[] resourceCards) {
        this.resourceCards = resourceCards;
    }

    /**
     * @return the size of the player's army
     */
    int getArmySize() {
        return armySize;
    }

    /**
     * @param armySize the size of the player's army
     */
    void setArmySize(int armySize) {
        this.armySize = armySize;
    }

    /**
     * @return victory points from dev cards
     */
    private int getVictoryPointsFromDevCard() {
        return victoryPointsFromDevCard;
    }

    /**
     * @param victoryPointsFromDevCard the amount of points from dev cards they have
     */
    private void setVictoryPointsFromDevCard(int victoryPointsFromDevCard) {
        this.victoryPointsFromDevCard = victoryPointsFromDevCard;
    }

    /**
     * @return the player's id
     */
    public int getPlayerId() {
        return this.playerId;
    }

    /**
     * @return - list of players' development cards
     */
    public ArrayList<Integer> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * @param developmentCards List of DevelopmentCards the player currently has.
     */
    public void setDevelopmentCards(ArrayList<Integer> developmentCards) {
        this.developmentCards = developmentCards;
    }

    /**
     * @return The total amount of resourceCards a player has.
     */
    public int getTotalResourceCardCount() {
        int result = 0;
        for (int resourceCard : this.resourceCards) {
            result += resourceCard;
        }
        Log.d(TAG, "getTotalResourceCardCount() returned: " + result);
        return result;
    }

    /**
     * @return - A random resourceCard is removed from the players inventory and returned.
     */
    int getRandomCard() {
        if (this.getTotalResourceCardCount() < 1) {
            Log.e(TAG, "getRandomDevCard: Player does not have any resources cards.");
            return -1;
        }

        Random random = new Random();
        int randomResourceId;
        do {
            randomResourceId = random.nextInt(5); // 0-4
        } while (!checkResourceCard(randomResourceId, 1));

        Log.d(TAG, "getRandomDevCard() returned: " + randomResourceId);
        return randomResourceId;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    void addVictoryPoints(int number) {
        this.victoryPoints += number;
    }

    void addPrivateVictoryPoints(int number) {
        this.victoryPointsPrivate += number;
    }

    int getVictoryPointsPrivate() {
        return this.victoryPointsPrivate;
    }

    /**
     * adds a dev card to list of dev cards that were built this turn
     *
     * @param devCard the number of the dev card
     */
    void addDevCardsBuiltThisTurn(int devCard) {
        devCardsBuiltThisTurn.add(devCard);
    }

    /**
     * gets the list of Integer of dev card numbers
     *
     * @return arrayList of Integer objects
     */
    ArrayList<Integer> getDevCardsBuiltThisTurn() {
        return devCardsBuiltThisTurn;
    }

    void setDevCardsBuiltThisTurn(ArrayList<Integer> devCardsBuiltThisTurn) {
        this.devCardsBuiltThisTurn = devCardsBuiltThisTurn;
    }


    /**
     * @return string representation of a Player
     */
    @Override
    public String toString() {
        return " Player id: " + this.playerId + ", " + "DevCards: " + this.developmentCards + ", BldgInv: " + Arrays.toString(this.buildingInventory) + ", army: " + this.armySize + "\n\tResources: " + this.printResourceCards();
    }
}


