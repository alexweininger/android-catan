package edu.up.cs.androidcatan.catan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.Dice;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.game.infoMsg.GameState;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 15th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanGameState extends GameState {

    private static final String TAG = "CatanGameState";

    private Dice dice; // dice object
    private static Board board; // board object

    private ArrayList<Player> playerList = new ArrayList<>(); // list of player objects
    private static ArrayList<Integer> developmentCards = new ArrayList<>(); // ArrayList of the development card in the deck

    private static int currentPlayerId; // id of player who is the current playing player
    private int currentDiceSum; // the sum of the dice at this very moment

    // game phases
    private static boolean isSetupPhase = true; // is it the setup phase
    private static boolean isActionPhase = false; // has the current player rolled the dice
    private static boolean isRobberPhase = false; // is the robber phase

    static final int setupPhaseTurnOrder[] = {0, 1, 2, 3, 3, 2, 1, 0};
    private static int setupPhaseTurnCounter;

    // robber
    private static boolean hasMovedRobber = false;
    // resourceCard index values: 0 = Brick, 1 = Lumber, 2 = Grain, 3 = Ore, 4 = Wool
    private static final int[] robberDiscardedResources = new int[]{0, 0, 0, 0, 0};  //Resets amount of discarded resources
    private static boolean[] robberPlayerListHasDiscarded = new boolean[]{false, false, false, false};

    // trophies
    private int currentLargestArmyPlayerId = -1; // player who currently has the largest army
    private int currentLongestRoadPlayerId = -1;

    public CatanGameState () {
        this.dice = new Dice();
        board = new Board();
        generateDevCardDeck();

        currentPlayerId = 0;
        this.currentDiceSum = 3;
        setupPhaseTurnCounter = 0;
        // add players to player list
        this.playerList.add(new Player(0));
        this.playerList.add(new Player(1));
        this.playerList.add(new Player(2));
        this.playerList.add(new Player(3));

        Log.i(TAG, board.toString());

    } // end CatanGameState constructor

    /**
     * CatanGameState deep copy constructor
     *
     * @param cgs - CatanGameState object to make a copy of
     */
    public CatanGameState (CatanGameState cgs) {
        this.setDice(new Dice(cgs.getDice()));
        this.setBoard(new Board(cgs.getBoard()));
        this.currentDiceSum = cgs.currentDiceSum;
        isActionPhase = isActionPhase;
        isSetupPhase = isSetupPhase;
        hasMovedRobber = cgs.getHasMovedRobber();
        this.currentLongestRoadPlayerId = cgs.currentLongestRoadPlayerId;
        this.currentLargestArmyPlayerId = cgs.currentLargestArmyPlayerId;

        setRobberPhase(cgs.getRobberPhase());
        this.setRobberPlayerListHasDiscarded(cgs.getRobberPlayerListHasDiscarded());
        this.setDevelopmentCards(cgs.getDevelopmentCards());
        this.setCurrentPlayerId(cgs.getCurrentPlayerId());
        this.setSetupPhaseTurnCounter(cgs.getSetupPhaseTurnCounter());

        // copy player list (using player deep copy const.)
        for (int i = 0; i < cgs.playerList.size(); i++) {
            this.playerList.add(new Player(cgs.playerList.get(i)));
        }
    } // end deep copy constructor

    /*-------------------------------------Dev Card Methods------------------------------------------*/

    /**
     * Creates a 'deck' of int representing the exact number each type of card. This allows us to accurately select a card at random.
     */
    private void generateDevCardDeck () {
        int[] devCardCounts = {14, 5, 2, 2, 2};
        developmentCards = new ArrayList<>();
        for (int i = 0; i < devCardCounts.length; i++) {
            for (int j = 0; j < devCardCounts[i]; j++) {
                developmentCards.add(i);
            }
        }
    }

    /**
     * @return The id of the development card the player drew randomly.
     */
    public int getRandomDevCard () {
        if (developmentCards.size() == 0)
            generateDevCardDeck();

        // generate random number from 0 to the length of the dev card deck
        Random random = new Random();
        int randomDevCard = random.nextInt(developmentCards.size());

        // get the random dev card id, then remove the card from the deck
        int drawnDevCard = developmentCards.get(randomDevCard);
        developmentCards.remove(randomDevCard);

        Log.d(TAG, "getRandomDevCard() returned: " + drawnDevCard);
        return drawnDevCard;
    }

    /*-------------------------------------Validation Methods------------------------------------------*/

    public Player getCurrentPlayer () {
        return this.playerList.get(currentPlayerId);
    }

    /**
     * @param playerId Player id
     * @return If id is valid.
     */
    private boolean valPlId (int playerId) {
        return playerId > -1 && playerId < 4;
    }

    /**
     * @param playerId - id to check
     * @return - if it is that players turn or not
     */
    private boolean checkTurn (int playerId) {
        if (valPlId(playerId)) {
            return playerId == currentPlayerId;
        }
        Log.e(TAG, "checkTurn: Invalid player id: " + playerId);
        return false;
    }

    /**
     * checkArmySize - after each turn checks who has the largest army (amount of played knight cards) with a minimum of 3 knight cards played.
     */
    private void checkArmySize () {
        Log.d(TAG, "checkArmySize() called");
        int max = -1;
        if (this.currentLargestArmyPlayerId != -1) {
            max = this.playerList.get(this.currentLargestArmyPlayerId).getArmySize();
        }
        int playerIdWithLargestArmy = -1;
        for (int i = 0; i < 4; i++) {
            if (this.playerList.get(i).getArmySize() > max) {
                max = this.playerList.get(i).getArmySize();
                playerIdWithLargestArmy = i;
            }
        }
        if (max > 2) {
            this.currentLargestArmyPlayerId = playerIdWithLargestArmy;
        }
    }

    public void updateTrophies () {
        this.setCurrentLongestRoadPlayerId(this.currentLongestRoadPlayerId = board.getPlayerWithLongestRoad(this.playerList));
        checkArmySize();
    }

    /*-------------------------------------Resource Methods------------------------------------------*/

    /**
     * handles resource production AW
     *
     * @param diceSum - dice sum
     */
    void produceResources (int diceSum) {
        Log.d(TAG, "produceResources() called with: diceSum = [" + diceSum + "]");
        if (isActionPhase) {
            Log.e(TAG, "produceResources: It is the action phase. Returned false.");
            return;
        }

        if (isSetupPhase) {
            Log.e(TAG, "produceResources: not producing any resources since it is the setup phase.");
            return;
        }

        ArrayList<Integer> productionHexagonIds = board.getHexagonsFromChitValue(diceSum);
        Log.i(TAG, "produceResources: Hexagons with chit value " + diceSum + ": " + productionHexagonIds.toString());
        for (Integer i : productionHexagonIds) {
            Hexagon hex = board.getHexagonFromId(i);
            Log.i(TAG, "produceResources: Hexagon " + i + " producing " + hex.getResourceId());

            ArrayList<Integer> receivingIntersections = board.getHexToIntIdMap().get(i);// intersections adjacent to producing hexagon tile
            Log.i(TAG, "produceResources: received intersections: " + receivingIntersections);

            // iterate through each intersection surrounding the producing hexagon
            for (Integer intersectionId : receivingIntersections) {
                Log.e(TAG, "produceResources: hex:" + hex.toString());
                // check if this intersection has a building
                if (board.getBuildings()[intersectionId] != null) {
                    this.playerList.get(board.getBuildings()[intersectionId].getOwnerId()).addResourceCard(hex.getResourceId(), board.getBuildings()[intersectionId].getVictoryPoints());
                    Log.i(TAG, "produceResources: Giving " + board.getBuildings()[intersectionId].getVictoryPoints() + " resources of type: " + hex.getResourceId() + " to player " + board.getBuildings()[intersectionId].getOwnerId());
                } else {
                    Log.i(TAG, "produceResources: No building located at intersection: " + intersectionId + " not giving any resources.");
                }
            }
        }
    }

    void produceResourcesForOneHex (int hexagonId) {
        Log.d(TAG, "produceResourcesForOneHex() called with: hexagonId = [" + hexagonId + "]");

        ArrayList<Integer> productionHexagonIds = board.getAdjacentHexagons(hexagonId);
        Log.i(TAG, "produceResources: Hexagons with adj. to hexagon:" + hexagonId + ": " + productionHexagonIds.toString());
        for (Integer i : productionHexagonIds) {
            Hexagon hex = board.getHexagonFromId(i);
            Log.i(TAG, "produceResources: Hexagon " + i + " producing " + hex.getResourceId());

            ArrayList<Integer> receivingIntersections = board.getHexToIntIdMap().get(i);// intersections adjacent to producing hexagon tile
            Log.i(TAG, "produceResources: received intersections: " + receivingIntersections);

            // iterate through each intersection surrounding the producing hexagon
            for (Integer intersectionId : receivingIntersections) {
                Log.e(TAG, "produceResources: hex:" + hex.toString());
                // check if this intersection has a building
                if (board.getBuildings()[intersectionId] != null) {
                    this.playerList.get(board.getBuildings()[intersectionId].getOwnerId()).addResourceCard(hex.getResourceId(), board.getBuildings()[intersectionId].getVictoryPoints());
                    Log.i(TAG, "produceResources: Giving " + board.getBuildings()[intersectionId].getVictoryPoints() + " resources of type: " + hex.getResourceId() + " to player " + board.getBuildings()[intersectionId].getOwnerId());
                } else {
                    Log.i(TAG, "produceResources: No building located at intersection: " + intersectionId + " not giving any resources.");
                }
            }
        }
    }

    /*----------------------------------------Robber Methods------------------------------------------*/
    public void setRobberPhase (boolean rp) {
        isRobberPhase = rp;
    }

    public boolean getRobberPhase () {
        return isRobberPhase;
    }

    /**
     * TODO implement
     * Check to see if resources need to be discarded
     *
     * @return - action success
     */
    public boolean checkPlayerResources (int playerId) {
        if (robberPlayerListHasDiscarded[playerId]) {
            //Returns false since player has already discarded cards
            Log.i(TAG, "checkPlayerResources: PLAYER HAS DISCARDED ALREADY");
            return false;
        }
        if (playerList.get(playerId).getTotalResourceCardCount() > 7) {
            //Returns true since player has more than 7 cards and has not discarded yet
            Log.i(TAG, "checkPlayerResources: PLAYER NEEDS TO DISCARD CARDS");
            return true;
        }
        Log.i(TAG, "checkPlayerResources: PLAYER DOES NOT NEED TO DISCARD");
        robberPlayerListHasDiscarded[playerId] = true;

        return false;
    }

    /**
     * Checking if we can actually discard the resources
     *
     * @param playerId
     * @param resourcesDiscarded
     * @return
     */
    public boolean validDiscard (int playerId, int[] resourcesDiscarded) {
        int totalDiscarded = 0;
        for (int i = 0; i < resourcesDiscarded.length; i++) {
            if (resourcesDiscarded[i] > playerList.get(playerId).getResourceCards()[i]) {
                Log.i(TAG, "validDiscard: Invalid due to not having enough resources, returning false");
                return false;
            }
            totalDiscarded += resourcesDiscarded[i];
        }
        Log.i(TAG, "discardResources: Amount is " + totalDiscarded + ", Need: " + playerList.get(playerId).getTotalResourceCardCount() / 2);
        return totalDiscarded == playerList.get(playerId).getTotalResourceCardCount() / 2;
    }

    /**
     * Discards resources when robber is played; makes sure it is exactly half of the player's hand;
     * if not, returns false
     *
     * @param playerId
     * @param resourcesDiscarded
     * @return
     */
    public boolean discardResources (int playerId, int[] resourcesDiscarded) {
        Log.w(TAG, "discardResources: " + this.getCurrentPlayer().printResourceCards());
        if (robberPlayerListHasDiscarded[playerId]) {
            Log.i(TAG, "discardResources: Player is not required to discard at this time");
            return true;
        }
        int totalDiscarded = 0;
        for (int i = 0; i < resourcesDiscarded.length; i++) {
            totalDiscarded += resourcesDiscarded[i];
        }
        Log.i(TAG, "discardResources: Amount is " + totalDiscarded);
        Log.i(TAG, "discardResources: Discarded resources");
        for (int i = 0; i < resourcesDiscarded.length; i++) {
            this.playerList.get(playerId).removeResourceCard(i, resourcesDiscarded[i]);
        }

        robberPlayerListHasDiscarded[playerId] = true;
        return true;
    }

    public boolean allPlayersHaveDiscarded () {
        for (int i = 0; i < robberPlayerListHasDiscarded.length; i++) {
            if (!robberPlayerListHasDiscarded[i]) {
                return false;
            }
        }
        Log.i(TAG, "Removed half of all resources from players with more than 7 cards\n");
        return true;
    }


    /**
     * If the player has rolled a 7, player will move the robber to another Hexagon that has settlements nearby
     *
     * @param hexagonId Hexagon the robber is going to move to.
     * @param playerId Player who is moving the robber.
     * @return action success.
     */
    public boolean moveRobber (int hexagonId, int playerId) {
        if (!valPlId(playerId)) {
            Log.d(TAG, "moveRobber: invalid player id: " + playerId);
            return false;
        }
        if (!checkTurn(playerId)) {
            Log.i(TAG, "moveRobber: it is not " + playerId + "'s turn.");
            return false;
        }
        if (board.moveRobber(hexagonId)) {
            Log.i(TAG, "moveRobber: Player " + playerId + " moved the Robber to Hexagon " + hexagonId);
            hasMovedRobber = true;
            return true;
        }
        Log.i(TAG, "moveRobber: Player " + playerId + "  cannot move the Robber to Hexagon " + hexagonId);
        return false;
    }

    /**
     * After the player has moved the Robber, the player will choose a player to steal from and receive a random card from their hand
     *
     * @param playerId - player stealing resources
     * @return - action success
     */
    public boolean robberSteal (int playerId, int stealingFromPlayerId) {
        int randomStolenResourceId = this.playerList.get(stealingFromPlayerId).getRandomCard();

        if (randomStolenResourceId < 0 || randomStolenResourceId > 4) {
            Log.e(TAG, "robberSteal: Received invalid resource card id: " + randomStolenResourceId + " from Player.getRandomDevCard method.");
        }

        // remove resource card from players inventory
        this.playerList.get(stealingFromPlayerId).removeResourceCard(randomStolenResourceId, 1);

        // add resource card to the stealing players inventory
        this.playerList.get(playerId).addResourceCard(randomStolenResourceId, 1);

        Log.i(TAG, "robberSteal: Stolen card " + randomStolenResourceId + " added to player: " + this.playerList.get(playerId));

        isRobberPhase = false;
        hasMovedRobber = false;

        // once they steal it is the end of the robber phase, so reset this array to false
        for (int i = 0; i < robberPlayerListHasDiscarded.length; i++) {
            robberPlayerListHasDiscarded[i] = false;
        }
        return true;
    }

    /*-------------------------------------Setup Phase Methods------------------------------------------*/

    /**
     * goes through each building and road to check how many are owned by the player
     * when they have 2 roads and 2 buildings, updateSetupPhase is false.
     *
     * @return if the game is still in the setup phase
     */
    boolean updateSetupPhase () {
        Log.d(TAG, "updateSetupPhase() called " + this.toString());
        int buildingCount = 0;
        for (Building building : board.getBuildings()) {
            if (building != null) buildingCount++;
        }
        if (board.getRoads().size() < 8 || buildingCount < 8) {
            Log.d(TAG, "updateSetupPhase() returned: " + true);
            return true;
        }
        Log.d(TAG, "updateSetupPhase() returned: " + false);
        return false;
    }

    /*-------------------------------------Getter/Setter Methods------------------------------------------*/

    public Dice getDice () { return dice; }

    public void setDice (Dice dice) { this.dice = dice; }

    public Board getBoard () { return board; }

    public void setBoard (Board board) { CatanGameState.board = board; }

    public ArrayList<Player> getPlayerList () { return playerList; }

    public void setPlayerList (ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public ArrayList<Integer> getDevelopmentCards () {
        return developmentCards;
    }

    public void setDevelopmentCards (ArrayList<Integer> developmentCards) {
        CatanGameState.developmentCards = developmentCards;
    }

    public int getCurrentDiceSum () {
        return currentDiceSum;
    }

    public void setCurrentDiceSum (int currentDiceSum) {
        this.currentDiceSum = currentDiceSum;
    }

    public int getCurrentPlayerId () {
        return currentPlayerId;
    }

    public void setCurrentPlayerId (int currentPlayerId) {
        CatanGameState.currentPlayerId = currentPlayerId;
    }

    public boolean isActionPhase () {
        return isActionPhase;
    }

    public void setActionPhase (boolean actionPhase) {
        isActionPhase = actionPhase;
    }

    public int getCurrentLargestArmyPlayerId () {
        return currentLargestArmyPlayerId;
    }

    public void setCurrentLargestArmyPlayerId (int currentLargestArmyPlayerId) {
        this.currentLargestArmyPlayerId = currentLargestArmyPlayerId;
    }

    public int getCurrentLongestRoadPlayerId () {
        return currentLongestRoadPlayerId;
    }

    public void setCurrentLongestRoadPlayerId (int currentLongestRoadPlayerId) {
        this.currentLongestRoadPlayerId = currentLongestRoadPlayerId;
    }

    public boolean isSetupPhase () {
        return isSetupPhase;
    }

    public void setSetupPhase (boolean setupPhase) {
        isSetupPhase = setupPhase;
    }

    public boolean isRobberPhase () {
        return isRobberPhase;
    }

    public boolean getHasMovedRobber () { return hasMovedRobber; }

    public boolean[] getRobberPlayerListHasDiscarded () {
        return robberPlayerListHasDiscarded;
    }

    public void setRobberPlayerListHasDiscarded (boolean[] robberPlayerListHasDiscarded) {
        CatanGameState.robberPlayerListHasDiscarded = robberPlayerListHasDiscarded;
    }

    public boolean isHasMovedRobber () {
        return hasMovedRobber;
    }

    public void setHasMovedRobber (boolean hasMovedRobber) {
        CatanGameState.hasMovedRobber = hasMovedRobber;
    }

    public int[] getRobberDiscardedResources () {
        return robberDiscardedResources;
    }


    public int getSetupPhaseTurnCounter () {
        return setupPhaseTurnCounter;
    }

    public void setSetupPhaseTurnCounter (int setupPhaseTurnCounter) {
        CatanGameState.setupPhaseTurnCounter = setupPhaseTurnCounter;
    }

    /*------------------------------------- toString ------------------------------------------*/

    /**
     * TODO update???
     *
     * @return String
     */
    @Override
    public String toString () {
        StringBuilder result = new StringBuilder();
        result.append(" ----------- CatanGameState toString ---------- \n");
        result.append("current Player: ").append(currentPlayerId).append(", ");
        result.append("diceVal: ").append(this.currentDiceSum).append(", ");
        result.append("actionPhase: ").append(isActionPhase).append(", ");
        result.append("setupPhase: ").append(isSetupPhase).append(", ");
        result.append("robberPhase: ").append(isRobberPhase).append(", ");
        result.append("largestArmy: ").append(this.currentLargestArmyPlayerId).append(", ");
        result.append("longestRoad: ").append(this.currentLongestRoadPlayerId).append("\n");
        result.append("Players that have discarded: ").append(Arrays.toString(robberPlayerListHasDiscarded)).append(", \n");
        for (Player player : playerList) {
            result.append(player.toString()).append("\n");
        }
        result.append(board.toString()).append("\n");
        return result.toString();
    }
}
