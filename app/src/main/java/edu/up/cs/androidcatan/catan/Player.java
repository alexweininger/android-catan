package edu.up.cs.androidcatan.catan;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.game.GameHumanPlayer;
import edu.up.cs.androidcatan.game.GameMainActivity;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;

// todo removed unused imports please

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 30th, 2018
 * https://github.com/alexweininger/android-catan
 **/

public class Player extends GameHumanPlayer implements OnClickListener {

    private static final String TAG = "Player"; // TAG used for Logging

    /* ----- Player instance variables ----- */

    // resourceCard index values: 0 = Brick, 1 = Grain, 2 = Lumber, 3 = Ore, 4 = Wool
    private int[] resourceCards = {4, 2, 4, 0, 2}; // array for number of each resource card a player has TODO change o all 0s

    // array for relating resource card names to resource card ids in the resourceCards array above
    private static final String[] resourceCardIds = {"Brick", "Grain", "Lumber", "Ore", "Wool"};

    // ArrayList of the development cards the player owns
    private ArrayList<DevelopmentCard> developmentCards = new ArrayList<>();

    // number of buildings the player has to build {roads, settlements, cities}
    private int[] buildingInventory = {15, 5, 4};

    // determined by how many knight dev cards the player has played, used for determining who currently has the largest army trophy
    private int armySize;

    // playerId
    private int playerId;

    /**
     * Player constructor
     */
    public Player(int id) {
        super("" + id + ""); // todo lol wtf is this
        this.playerId = id;
        this.armySize = 0;
    }

    /**
     * deepCopy constructor
     *
     * @param p - Player object to copy
     */
    public Player(Player p) {
        super("" + p.getPlayerId() + "");
        this.setPlayerId(p.getPlayerId());
        this.setArmySize(p.getArmySize());
        this.setDevelopmentCards(p.getDevelopmentCards());
        this.setBuildingInventory(p.getBuildingInventory());
        this.setResourceCards(p.getResourceCards());
    }

    // TODO Figure out what these methods from the GameHumanPlayer and OnClickListener do and implement them TODO @DB @NJ

    public void onClick(View button) {

    }

    public void setAsGui(GameMainActivity activity) {

    }

    public View getTopView() {
        //FIXME return myActivity.findViewById(R.id.top_gui_layout);
        return null;
    }

    public void receiveInfo(GameInfo info) {

    }

    /**
     * error checking:
     * - checks for valid resourceCardId
     *
     * @param resourceCardId - index value of resource to add (0-4) defined above
     * @param numToAdd       - number of resource cards of this type to add to the players inventory AW
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
     * @param numToCheckFor  - number of resources to make sure the player has
     * @return - whether they have at least that many resources of the given type
     */
    public boolean checkResourceCard(int resourceCardId, int numToCheckFor) {
        if (resourceCardId < 0 || resourceCardId >= 5) { // check for valid resourceCardId
            Log.d("devError", "ERROR removeResourceCard: given resourceCardId: " + resourceCardId + " is invalid. Must be an integer (0-4).");
            return false; // did not remove resource cards to players inventory
        }
        return this.resourceCards[resourceCardId] < numToCheckFor;
    }

    /**
     * @param resourceCost - resourceCost array, e.g. Settlement.resourceCost
     * @return - true of false, does the player have all of these resources?
     */
    public boolean checkResourceBundle(int[] resourceCost) {
        for (Integer id : resourceCost) {
            if (!checkResourceCard(id, resourceCost[id])) {
                return false;
            }
        }
        return true;
    }

    /**
     * error checking:
     * - error checks for valid resourceCardId
     * - error checks for preventing negative resource card counts
     *
     * @param resourceCardId - id of resource card to remove from players inventory
     * @param numToRemove    - number of resource cards of this type to remove
     * @return - if numToRemove resource card(s) have been removed from the players inventory
     */
    public boolean removeResourceCard(int resourceCardId, int numToRemove) {
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

    public boolean removeResourceBundle(int[] resourceCost) {
        if (checkResourceBundle(resourceCost)) {
            Log.e(TAG, "removeResourceBundle: Cannot remove resource bundle from player " + this.playerId + ". Insufficient resources. Must do error checking before calling this method!");
            return false;
        }
        for (int i : resourceCost) {
            if (!this.removeResourceCard(i, resourceCost[i])) {
                Log.e(TAG, "removeResourceBundle: Cannot remove resource bundle from player " + this.playerId + ". Player.removeResourceCard method returned false.");
                return false;
            }
        }
        Log.d(TAG, "removeResourceBundle successfully removed resourceCost = [" + resourceCost.toString() + "] from players inventory.");
        return true;
    }


    /**
     * @return String showing the number of each resource card the player has
     */
    public String printResourceCards() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.resourceCards.length; i++) {
            str.append(this.resourceCardIds[i]).append(": ").append(this.resourceCards[i]).append(", ");
        }
        return str.toString();
    }

    /**
     * @return -
     */
    public int[] getBuildingInventory() {
        return buildingInventory;
    }

    /**
     * @param buildingInventory
     */
    public void setBuildingInventory(int[] buildingInventory) {
        this.buildingInventory = buildingInventory;
    }

    /**
     * @param playerId
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * @return - resource card array
     */
    public int[] getResourceCards() {
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
    public int getArmySize() {
        return armySize;
    }

    /**
     * @param armySize the size of the player's army
     */
    public void setArmySize(int armySize) {
        this.armySize = armySize;
    }

    /**
     * @param devCard dev card to add
     */
    public void addDevelopmentCard(DevelopmentCard devCard) {
        developmentCards.add(devCard);
    }

    /**
     * @param devCard dev card to remove
     * @return if action was possible
     */
    public boolean useDevCard(DevelopmentCard devCard) {
        if (developmentCards.contains(devCard)) {
            developmentCards.remove(devCard);
            return true;
        }
        return false;
    }

    public void decrementBuildingInventory(int buildingId) {
        this.buildingInventory[buildingId]--;
    }

    // use to allow the player to use the dev card they built the turn prior
    public void setDevelopmentCardsAsPlayable() {
        for (int i = 0; i < developmentCards.size(); i++) {
            developmentCards.get(i).setPlayable(true);
        }
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
    public ArrayList<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * @param developmentCards List of DevelopmentCards the player currently has.
     */
    public void setDevelopmentCards(ArrayList<DevelopmentCard> developmentCards) {
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
        return result;
    }

    /**
     * @return - A random resourceCard is removed from the players inventory and returned.
     */
    public int getRandomCard() {

        if (this.getTotalResourceCardCount() < 1) {
            Log.e(TAG, "getRandomCard: Player does not have any resources cards.");
            return -1;
        }

        Random random = new Random();
        int randomResourceId;
        do {
            randomResourceId = random.nextInt(4); // 0-4
        } while (!checkResourceCard(randomResourceId, 1));

        if (!removeResourceCard(randomResourceId, 1)) {
            Log.e(TAG, "getRandomCard: Player does not have random card that was checked for.");
            return -1;
        }

        return randomResourceId;
    }

    /**
     * @return string representation of a Player
     */
    @Override
    public String toString() {
        return "\nPlayer " + this.playerId + ": " +
                "\nresourceCards=" + this.printResourceCards() +
                "\ndevelopmentCards=" + this.developmentCards +
                "\nbuildingInventory=" + Arrays.toString(this.buildingInventory) +
                ", armySize=" + this.armySize;
    }
}


