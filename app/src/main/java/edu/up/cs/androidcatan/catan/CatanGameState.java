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
 * @version November 8th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanGameState extends GameState {

    private static final String TAG = "CatanGameState";

    private Dice dice; // dice object
    private Board board; // board object

    private ArrayList<Player> playerList = new ArrayList<>(); // list of player objects
    private ArrayList<Integer> developmentCards = new ArrayList<>(); // ArrayList of the development card in the deck

    private int[] playerVictoryPoints = new int[4]; // victory points of each player
    private int[] playerPrivateVictoryPoints = new int[4]; // private victory points

    private int currentPlayerId; // id of player who is the current playing player
    private int currentDiceSum; // the sum of the dice at this very moment

    // game phases
    private boolean isSetupPhase = true; // is it the setup phase
    private boolean isActionPhase = false; // has the current player rolled the dice
    private boolean isRobberPhase = false; // is the robber phase

    // robber
    private boolean hasDiscarded = false;
    private boolean hasMovedRobber = false;
    // resourceCard index values: 0 = Brick, 1 = Lumber, 2 = Grain, 3 = Ore, 4 = Wool
    private int[] robberDiscardedResources = new int[]{0, 0, 0, 0, 0};  //How many resources the player would like to discard
    private boolean[] robberPlayerListHasDiscarded = new boolean[]{false, false, false, false};

    // trophies
    private int currentLargestArmyPlayerId = -1; // player who currently has the largest army
    private int currentLongestRoadPlayerId = -1;

    public CatanGameState () {
        this.dice = new Dice();
        this.board = new Board();
        generateDevCardDeck();

        //
        this.currentPlayerId = 0;
        this.currentDiceSum = 3;

        // add players to player list
        this.playerList.add(new Player(0));
        this.playerList.add(new Player(1));
        this.playerList.add(new Player(2));
        this.playerList.add(new Player(3));

        Log.i(TAG, this.board.toString());

        // set all vic points to 0 to start
        for (int i = 0; i < playerVictoryPoints.length; i++) {
            playerVictoryPoints[i] = 0;
            playerPrivateVictoryPoints[i] = 0;
        }
    } // end CatanGameState constructor

    /**
     * CatanGameState deep copy constructor
     *
     * @param cgs - CatanGameState object to make a copy of
     */
    public CatanGameState (CatanGameState cgs) {
        this.setDice(new Dice(cgs.getDice()));
        this.setBoard(new Board(cgs.getBoard()));

        this.currentPlayerId = cgs.currentPlayerId;
        this.currentDiceSum = cgs.currentDiceSum;
        this.isActionPhase = cgs.isActionPhase;
        this.isSetupPhase = cgs.isSetupPhase;
        this.hasDiscarded = cgs.hasDiscarded;
        this.hasMovedRobber = cgs.hasMovedRobber;
        this.currentLongestRoadPlayerId = cgs.currentLongestRoadPlayerId;
        this.currentLargestArmyPlayerId = cgs.currentLargestArmyPlayerId;

        this.setRobberPhase(cgs.getRobberPhase());
        this.setRobberDiscardedResources(cgs.getRobberDiscardedResources());
        this.setRobberPlayerListHasDiscarded(cgs.getRobberPlayerListHasDiscarded());

        this.setPlayerPrivateVictoryPoints(cgs.getPlayerPrivateVictoryPoints());
        this.setPlayerVictoryPoints(cgs.getPlayerVictoryPoints());
        this.setDevelopmentCards(cgs.getDevelopmentCards());

        this.setBoard(cgs.getBoard());

        // copy player list (using player deep copy const.)
        for (int i = 0; i < cgs.playerList.size(); i++)
            this.playerList.add(new Player(cgs.playerList.get(i)));

        // copy victory points of each player
        for (int i = 0; i < cgs.playerVictoryPoints.length; i++) {
            this.playerVictoryPoints[i] = cgs.playerVictoryPoints[i];
            this.playerPrivateVictoryPoints[i] = cgs.playerPrivateVictoryPoints[i];
        }
    } // end deep copy constructor

    /*-------------------------------------Dev Card Methods------------------------------------------*/

    /**
     * Creates a 'deck' of int representing the exact number each type of card. This allows us to accurately select a card at random.
     */
    private void generateDevCardDeck () {
        int[] devCardCounts = {14, 5, 2, 2, 2};
        for (int i = 0; i < devCardCounts.length; i++) {
            for (int j = 0; j < devCardCounts[i]; j++) {
                this.developmentCards.add(i);
            }
        }
    }

    /**
     * @return The id of the development card the player drew randomly.
     */
    public int getRandomCard () {
        // generate random number from 0 to the length of the dev card deck
        Random random = new Random();
        int randomDevCard = random.nextInt(developmentCards.size() - 1);

        // get the random dev card id, then remove the card from the deck
        int drawnDevCard = developmentCards.get(randomDevCard);
        developmentCards.remove(randomDevCard);

        Log.d(TAG, "getRandomCard() returned: " + drawnDevCard);
        return drawnDevCard;
    }

    /**
     * TODO ???
     * Method determines whether it is a valid move to use one of their dev cards or not
     *
     * @param playerId - player playing development card
     * @param devCardId - id of the development card
     * @return - action success
     */
    public boolean useDevCard (int playerId, int devCardId) {
        Log.d(TAG, "useDevCard() called with: playerId = [" + playerId + "], devCardId = [" + devCardId + "]");

        Log.e(TAG, "useDevCard: this method is not implemented yet...");
        return true;
    }

    /*-------------------------------------Validation Methods------------------------------------------*/

    public Player getCurrentPlayer () {
        return this.playerList.get(this.currentPlayerId);
    }

    /**
     * @param playerId -
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
            return playerId == this.currentPlayerId;
        }
        Log.e(TAG, "checkTurn: Invalid player id: " + playerId);
        return false;
    }

    /**
     * todo maybe this is deprecated?
     * validates the player id, checks if its their turn, and checks if it is the action phase
     *
     * @param playerId - player id to validate an action for
     * @return - can this player make an action?
     */
    private boolean valAction (int playerId) {
        if (valPlId(playerId)) {
            if (checkTurn(playerId)) {
                if (this.isActionPhase) {
                    return true;
                }
                Log.i(TAG, "valAction - it is not the action phase.");
                return false;
            }
            Log.i(TAG, "valAction - it is not " + playerId + "'s turn.");
            return false;
        }
        Log.i(TAG, "valAction - invalid player id: " + playerId);
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
            // if the award has already been given out remove the awarded VP from that player
            if (currentLargestArmyPlayerId != -1) {
                this.playerVictoryPoints[currentLargestArmyPlayerId] -= 2;
            }
            // update the player witht he kargest army
            this.currentLargestArmyPlayerId = playerIdWithLargestArmy;
            // add 2 VP to who ever has the largest army
            this.playerVictoryPoints[currentLargestArmyPlayerId] += 2;
        }
    }

    // TODO: Finish updateVictoryPoints method

    /**
     * Method updates the victory points count of the current player based off the actions taken within the turn
     */
    public void updateVictoryPoints () {
        Log.d(TAG, "updateVictoryPoints() called");

        Log.w(TAG, "updateVictoryPoints: Reset victory points to 0 before calculations.");

        for (int i = 0; i < this.playerVictoryPoints.length; i++) {
            this.playerVictoryPoints[i] = 0;
        }

        for (int n = 0; n < this.playerList.size(); n++) {
            if (playerList.get(n).getPlayerId() == this.board.getPlayerWithLongestRoad(playerList)) {
                playerVictoryPoints[playerList.get(n).getPlayerId()] += 2;
            }
        }

        for (int i = 0; i < this.playerList.size(); i++) {
            this.playerVictoryPoints[i] += this.playerList.get(i).getVictoryPointsFromDevCard();
        }

        // goes through all buildings and the amount of victory points to the player to who owns the building
        Building[] buildings = this.board.getBuildings();

        for (Building building : buildings) {
            if (building != null) {
                Log.w(TAG, "updateVictoryPoints: building.getOwnerId: " + building.getOwnerId() + " building.getVictoryPoints: " + building.getVictoryPoints());
                playerVictoryPoints[building.getOwnerId()] += building.getVictoryPoints();
            }
        }
        checkArmySize();
    }

    /*-------------------------------------Resource Methods------------------------------------------*/

    /**
     * handles resource production AW
     *
     * @param diceSum - dice sum
     */
    public void produceResources (int diceSum) {
        Log.d(TAG, "produceResources() called with: diceSum = [" + diceSum + "]");
        if (isActionPhase) {
            Log.e(TAG, "produceResources: It is the action phase. Returned false.");
            return;
        }

        if (this.isSetupPhase) {
            Log.e(TAG, "produceResources: not producing any resources since it is the setup phase.");
            return;
        }

        ArrayList<Integer> productionHexagonIds = board.getHexagonsFromChitValue(diceSum);
        Log.i(TAG, "produceResources: Hexagons with chit value " + diceSum + ": " + productionHexagonIds.toString());
        for (Integer i : productionHexagonIds) {
            Hexagon hex = board.getHexagonFromId(i);
            Log.i(TAG, "produceResources: Hexagon " + i + " producing " + hex.getResourceId());

            ArrayList<Integer> receivingIntersections = this.board.getHexToIntIdMap().get(i);// intersections adjacent to producing hexagon tile
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

    /*-------------------------------------Action Methods------------------------------------------*/

    /**
     * Player sends action to game state and game state return number with resources depending on settlements players own and where they're located.
     *
     * @return - action success
     */
    public boolean rollDice () {
        Log.d(TAG, "rollDice() called.");

        if (this.isActionPhase) {
            Log.e(TAG, "rollDice: Player " + currentPlayerId + " tried to roll the dice, but it is the action phase during " + this.currentPlayerId + "'s turn. Returned false.");
            return false;
        }

        this.currentDiceSum = dice.roll();
        Log.i(TAG, "rollDice: Player " + currentPlayerId + " rolled a " + this.currentDiceSum);
        // if the robber is rolled
        if (this.currentDiceSum == 7) {
            Log.i(TAG, "rollDice: The robber has been activated.");
            //            this.isRobberPhase = true;
        } else {
            Log.i(TAG, "rollDice: Calling the produceResources method.");
            produceResources(this.currentDiceSum);
        }

        Log.i(TAG, "rollDice: Set isActionPhase to true.");
        this.isActionPhase = true;

        return true;
    } // end rollDice action method

    /**
     * action for a player ending their turn, increments currentPlayerId. As of now does no checks.
     *
     * @return - action success
     */
    public boolean endTurn () {
        Log.d(TAG, "endTurn() called");

        // if it is not the setup phase
        if (!this.isSetupPhase()) {
            // check if it is the action phase
            if (!isActionPhase) {
                Log.e(TAG, "endTurn: Player tried to end their turn, but it is not the action phase. Returning false.");
                return false;
            }
        }
        //method is called at the end of every turn to give an accurate victory point count
        updateVictoryPoints();

        /*for (DevelopmentCard developmentCard : playerList.get(currentPlayerId).getDevelopmentCards()) {
            developmentCard.setPlayable(true);
        }*/

        this.isActionPhase = false;

        Log.i(TAG, "endTurn: Player " + this.currentPlayerId + " has ended their turn. It is now player " + (this.currentPlayerId + 1) + "'s turn.");

        //call to get the player with the longest road given the current player list
        this.board.getPlayerWithLongestRoad(playerList);

        if (this.currentPlayerId == 3) {
            this.currentPlayerId = 0;
        } else {
            this.currentPlayerId++;
        }

        // update the setup phase boolean variable using the method that does setup phase completion check
        this.setSetupPhase(updateSetupPhase());
        return true;
    } // end endTurn method

    /*---------------------------------------Trading Methods------------------------------------------*/

    /**
     * Player trades with bank, gives resources and receives a resource; number depends on the resource
     *
     * @param playerId - player attempting to trade with port
     * @param resGiven - what player is giving in the trade
     * @param resReceive - what the player is receiving in the trade
     * @return - action success
     */
    //TODO implement
    public boolean tradeWithBank (int playerId, int resGiven, int resReceive) {

        // Player.removeResources returns false if the player does not have enough, if they do it removes them.
        if (!this.playerList.get(playerId).removeResourceCard(resGiven, 4)) {
            Log.e(TAG, "tradeWithBank - not enough resources, player id: " + playerId);
            return false;
        }
        this.playerList.get(playerId).addResourceCard(resReceive, 1); // add resource card to players inventory
        Log.w(TAG, "tradeWithBank - player " + playerId + " traded " + 4 + " " + resGiven + " for a " + resReceive + " with bank.\n");
        return true;
    } // end tradeWithBank

    /*----------------------------------------Robber Methods------------------------------------------*/
    public void setRobberPhase (boolean rp) {
        this.isRobberPhase = rp;
    }

    public boolean getRobberPhase () {
        return this.isRobberPhase;
    }

    /**
     * TODO implement
     * Check to see if resources need to be discarded
     *
     * @return - action success
     */
    public boolean needsToDiscardHalf (int playerId) {
        if (robberPlayerListHasDiscarded[playerId]) {
            //Returns false since player has already discarded cards
            Log.i(TAG, "needsToDiscardHalf: PLAYER HAS DISCARDED ALREADY");
            return false;
        }
        if (playerList.get(playerId).getTotalResourceCardCount() > 7) {
            //Returns true since player has more than 7 cards and has not discarded yet
            Log.i(TAG, "needsToDiscardHalf: PLAYER NEEDS TO DISCARD CARDS. returning true");
            return true;
        }
        Log.i(TAG, "needsToDiscardHalf: PLAYER DOES NOT NEED TO DISCARDS");
        robberPlayerListHasDiscarded[playerId] = true;
        hasDiscarded = true;

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
        Log.d(TAG, "validDiscard() called with: playerId = [" + playerId + "], resourcesDiscarded = [" + Arrays.toString(resourcesDiscarded) + "]");

        int totalDiscarded = 0;
        for (int i = 0; i < resourcesDiscarded.length; i++) {

            if (resourcesDiscarded[i] > playerList.get(playerId).getResourceCards()[i]) {
                Log.i(TAG, "validDiscard: Invalid cannot have negative resources.");
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
    public boolean discardResources (int playerId, ArrayList<Integer> resourcesDiscarded) {
        Log.d(TAG, "discardResources() called with: playerId = [" + playerId + "], resourcesDiscarded = [" + resourcesDiscarded + "]");
        Log.w(TAG, "discardResources: " + this.playerList.get(playerId).printResourceCards());

        if (needsToDiscardHalf(playerId)) {
            for (int i = 0; i < resourcesDiscarded.size(); i++) {
                if (!this.getPlayerList().get(playerId).removeResourceCard(resourcesDiscarded.get(i), 1)) {
                    Log.e(TAG, "discardResources: BAD", new Exception("error bc not enough resources but this is bad"));
                }
            }

            //        if (playerId == currentPlayerId) { todo wtf
            //            hasDiscarded = true;
            //        } else {
            //            hasDiscarded = true;
            //            hasMovedRobber = true;
            //        }

            robberPlayerListHasDiscarded[playerId] = true;
            return true;
        } else {
            // return true if they dont need to discard
            return true;
        }
    }

    public boolean allPlayersHaveDiscarded () {
        Log.d(TAG, "allPlayersHaveDiscarded() called");
        for (int i = 0; i < robberPlayerListHasDiscarded.length; i++) {
            if (robberPlayerListHasDiscarded[i] == false) {
                return false;
            }
        }
        Log.i(TAG, "Removed half of all resources from players with more than 7 cards\n");
        return true;
    }

    /**
     * After the player has moved the Robber, the player will choose a player to steal from and receive a random card from their hand
     *
     * @param playerId - player stealing resources
     * @return - action success
     */
    public boolean robberSteal (int playerId, int stealId) {
        // check if valid player if
        if (!valPlId(playerId)) {
            Log.e(TAG, "robberSteal: invalid player id: " + playerId);
            return false;
        }

        // check if it is the players turn
        if (!checkTurn(playerId)) {
            Log.i(TAG, "robberSteal: it is not " + playerId + "'s turn.");
            return false;
        }

        // As of now this selects a random player and then steals a random card from their inventory. TODO enable the player moving the robber to choose to steal a resource from the players who have buildings adjacent to the new robbers location
        Random random = new Random();
        int randomStolenResourceId = this.playerList.get(random.nextInt(3)).getRandomCard();

        if (randomStolenResourceId < 0 || randomStolenResourceId > 4) {
            Log.e(TAG, "robberSteal: Received invalid resource card id: " + randomStolenResourceId + " from Player.getRandomCard method.");
        }

        this.playerList.get(playerId).addResourceCard(randomStolenResourceId, 1);
        Log.i(TAG, "robberSteal: Stolen card " + randomStolenResourceId + " added to player: " + this.playerList.get(playerId));

        isRobberPhase = false;
        hasDiscarded = false;
        hasMovedRobber = false;

        for (int i = 0; i < robberPlayerListHasDiscarded.length; i++) {
            robberPlayerListHasDiscarded[i] = false;
        }
        return true;
    }

    //    public boolean hasSevenPlusCards(Player player){
    //        if(player.getTotalResourceCardCount() > 7){
    //            Log.i(TAG, "hasSevenPlusCards: Player has more than seven cards");
    //            return true;
    //        }
    //        Log.i(TAG, "hasSevenPlusCards: Player has 7 or less cards");
    //        return false;
    //    }

    /*-------------------------------------Setup Phase Methods------------------------------------------*/

    /**
     * goes through each building and road to check how many are owned by the player
     * when they have 2 roads and 2 buildings, updateSetupPhase is false.
     *
     * @return if the game is still in the setup phase
     */
    public boolean updateSetupPhase () {
        Log.d(TAG, "updateSetupPhase() called");
        Log.e(TAG, "updateSetupPhase: " + this.toString());
        //        int roadCount = 0;
        int buildingCount = 0;
        for (Building building : board.getBuildings()) {
            if (building != null) {
                buildingCount++;
            }
        }
        if (board.getRoads().size() < 8 || buildingCount < 8) {
            return true;
        }
        Log.d(TAG, "updateSetupPhase() returned: " + false);
        return false;
    }

    /*-------------------------------------Getter/Setter Methods------------------------------------------*/

    public Dice getDice () {
        return dice;
    }

    public void setDice (Dice dice) {
        this.dice = dice;
    }

    public Board getBoard () {
        return board;
    }

    public void setBoard (Board board) {
        this.board = board;
    }

    public ArrayList<Player> getPlayerList () {
        return playerList;
    }

    public void setPlayerList (ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public ArrayList<Integer> getDevelopmentCards () {
        return developmentCards;
    }

    public void setDevelopmentCards (ArrayList<Integer> developmentCards) {
        this.developmentCards = developmentCards;
    }

    public int[] getPlayerVictoryPoints () {
        return playerVictoryPoints;
    }

    public void setPlayerVictoryPoints (int[] playerVictoryPoints) {
        this.playerVictoryPoints = playerVictoryPoints;
    }

    public int[] getPlayerPrivateVictoryPoints () {
        return playerPrivateVictoryPoints;
    }

    public void setPlayerPrivateVictoryPoints (int[] playerPrivateVictoryPoints) {
        this.playerPrivateVictoryPoints = playerPrivateVictoryPoints;
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
        this.currentPlayerId = currentPlayerId;
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
        return this.isSetupPhase;
    }

    public void setSetupPhase (boolean setupPhase) {
        this.isSetupPhase = setupPhase;
    }

    public boolean isRobberPhase () {
        return isRobberPhase;
    }

    public int[] getRobberDiscardedResource () {
        return robberDiscardedResources;
    }

    public boolean isHasDiscarded () { return hasDiscarded;}

    public boolean getHasMovedRobber () { return hasMovedRobber; }

    public boolean[] getRobberPlayerListHasDiscarded () {
        return robberPlayerListHasDiscarded;
    }

    public void setRobberPlayerListHasDiscarded (boolean[] robberPlayerListHasDiscarded) {
        this.robberPlayerListHasDiscarded = robberPlayerListHasDiscarded;
    }

    public void playerHasDiscardedResources (int playerId) {
        this.robberPlayerListHasDiscarded[playerId] = true;
    }

    public void setHasDiscarded (boolean hasDiscarded) {
        this.hasDiscarded = hasDiscarded;
    }

    public boolean isHasMovedRobber () {
        return hasMovedRobber;
    }

    public void setHasMovedRobber (boolean hasMovedRobber) {
        this.hasMovedRobber = hasMovedRobber;
    }

    public int[] getRobberDiscardedResources () {
        return robberDiscardedResources;
    }

    public void setRobberDiscardedResources (int[] robberDiscardedResources) {
        this.robberDiscardedResources = robberDiscardedResources;
    }

    /*-------------------------------------toString------------------------------------------*/

    /**
     * TODO update???
     *
     * @return String
     */
    @Override
    public String toString () {
        StringBuilder result = new StringBuilder();

        result.append(" ----------- CatanGameState toString ---------- \n");
        result.append("current Player: ").append(this.currentPlayerId).append(", ");
        result.append("diceVal: ").append(this.currentDiceSum).append(", ");
        result.append("actionPhase: ").append(this.isActionPhase).append(", ");
        result.append("setupPhase: ").append(this.isSetupPhase).append(", ");
        result.append("robberPhase: ").append(this.isRobberPhase).append(", ");
        result.append("largestArmy: ").append(this.currentLargestArmyPlayerId).append(", ");
        result.append("longestRoad: ").append(this.currentLongestRoadPlayerId).append("\n");
        result.append("Players that have discarded: ").append(Arrays.toString(this.robberPlayerListHasDiscarded)).append(", \n");

        for (Player player : playerList) {
            result.append(player.toString()).append("\n");
        }
        result.append(this.board.toString()).append("\n");

        return result.toString();
    } // end CatanGameState toString()
}
