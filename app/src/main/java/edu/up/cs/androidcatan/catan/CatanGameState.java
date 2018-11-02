package edu.up.cs.androidcatan.catan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs.androidcatan.catan.gamestate.Board;
import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.catan.gamestate.Dice;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;
import edu.up.cs.androidcatan.game.infoMsg.GameState;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 30th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanGameState extends GameState {

    private static final String TAG = "CatanGameState";

    private Dice dice; // dice object
    private Board board = new Board(); // board object

    private ArrayList<Player> playerList = new ArrayList<>(); // list of player objects

    private ArrayList<Integer> developmentCards = new ArrayList<>(); // ArrayList of the development card in the deck

    // victory points of each player
    private int[] playerVictoryPoints = new int[4];
    private int[] playerPrivateVictoryPoints = new int[4]; // private victory points

    private int currentDiceSum;
    private int currentPlayerId; // id of player who is the current playing player
    private boolean isActionPhase = false; // has the current player rolled the dice
    private int currentLargestArmyPlayerId = -1; // player who currently has the largest army
    private int currentLongestRoadPlayerId = -1;


    CatanGameState() { // CatanGameState constructor
        this.dice = new Dice();
        generateDevCardDeck();

        this.currentPlayerId = 0;
        this.currentDiceSum = 3;

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
    public CatanGameState(CatanGameState cgs) {
        this.setDice(new Dice(cgs.getDice()));
        this.setBoard(new Board(cgs.getBoard()));

        this.currentPlayerId = cgs.currentPlayerId;
        this.currentDiceSum = cgs.currentDiceSum;
        this.isActionPhase = cgs.isActionPhase;
        this.currentLongestRoadPlayerId = cgs.currentLongestRoadPlayerId;
        this.currentLargestArmyPlayerId = cgs.currentLargestArmyPlayerId;
        this.setPlayerPrivateVictoryPoints(cgs.getPlayerPrivateVictoryPoints());
        this.setPlayerVictoryPoints(cgs.getPlayerVictoryPoints());
        this.setDevelopmentCards(cgs.getDevelopmentCards());

        // copy player list (using player deep copy const.)
        for (int i = 0; i < cgs.playerList.size(); i++) {
            this.playerList.add(new Player(cgs.playerList.get(i)));
        }

        // copy victory points of each player
        for (int i = 0; i < cgs.playerVictoryPoints.length; i++) {
            this.playerVictoryPoints[i] = cgs.playerVictoryPoints[i];
            this.playerPrivateVictoryPoints[i] = cgs.playerPrivateVictoryPoints[i];
        }
    } // end deep copy constructor

    /**
     * creates a deck of int representing the exact number each type of card
     */
    private void generateDevCardDeck() {
        int[] devCardCounts = {14, 5, 2, 2, 2};
        for (int i = 0; i < devCardCounts.length; i++) {
            for (int j = 0; j < devCardCounts[i]; j++) {
                this.developmentCards.add(i);
            }
        }
    }

    /**
     * @return the random dev card the player drew
     */
    public DevelopmentCard getRandomCard() {
        Random random = new Random();
        int randomDevCard = random.nextInt(developmentCards.size() - 1);
        int drawnDevCard = developmentCards.get(randomDevCard);
        developmentCards.remove(randomDevCard);
        return new DevelopmentCard(drawnDevCard);

    }

    /**
     * @param playerId -
     * @return If id is valid.
     */
    private boolean valPlId(int playerId) {
        return playerId > -1 && playerId < 4;
    }

    /**
     * @param playerId - id to check
     * @return - if it is that players turn or not
     */
    private boolean checkTurn(int playerId) {
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
    private boolean valAction(int playerId) {
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
    private void checkArmySize() {
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

    /**
     * todo
     * <p>
     * updateLongestRoadPlayer - after each turn check if any player has longest road, with a min of 5 road segments
     */
    private void updateLongestRoadPlayer() {
//        int max = -1;
//        int playerIdWithLongestRoad = -1;
//        if (currentLongestRoadPlayerId != -1) {
//            max = playerVictoryPoints[currentLargestArmyPlayerId];
//        }
//        for (int i = 0; i < 4; i++) {
//            if (board.getPlayerWithLongestRoad(this.playerList) > max) {
//                max = board.getPlayerWithLongestRoad(i);
//                playerIdWithLongestRoad = i;
//            }
//        }
//        if (max > 4) {
//            this.currentLongestRoadPlayerId = playerIdWithLongestRoad;
//        }
    }

    /**
     * Gets the player who has the longest road.
     */
    private int checkLongestRoad() { // todo this should be called somewhere...
        return this.board.getPlayerWithLongestRoad(this.playerList);
    }

    /**
     * updates the victory points of each player, should be called after every turn
     */
    /* TODO lol pls remove when we can
    private void updateVictoryPoints() {
        if (this.currentLongestRoadPlayerId != -1) {
            this.playerVictoryPoints[this.currentLongestRoadPlayerId] -= 2;
        }
        //updateLongestRoadPlayer();
        if (this.currentLongestRoadPlayerId != -1) {
            this.playerVictoryPoints[this.currentLongestRoadPlayerId] += 2;
        }

        if (this.currentLargestArmyPlayerId != -1) {
            this.playerVictoryPoints[this.currentLargestArmyPlayerId] -= 2;
        }
        checkArmySize();
        if (this.currentLargestArmyPlayerId != -1) {
            this.playerVictoryPoints[this.currentLargestArmyPlayerId] += 2;
        }

        // goes through all buildings and the amount of victory points to the player to who owns the building
        for(int i = 0; i < board.getBuildings().length; i++)
        {
            playerVictoryPoints[board.getBuildings()[i].getOwnerId()] += board.getBuildings()[i].getVictoryPoints();
        }
    }*/

    //TODO: ANDREW'S DONT FUCKING TOUCH @DANIEL; I TOUCHED THIS BECAUSE IT HAD A BUG - AW
    private void updateVictoryPoints() {
        //calculates the longest road for the players and checks if it is the current player
        if (board.getPlayerWithLongestRoad(playerList) != currentLongestRoadPlayerId) {
            currentLongestRoadPlayerId = board.getPlayerWithLongestRoad(playerList);
        }

        // goes through all buildings and the amount of victory points to the player to who owns the building
        Building[] buildings = this.board.getBuildings();

        for (Building building : buildings) {
            if (building != null) {
                playerVictoryPoints[building.getOwnerId()] += building.getVictoryPoints();
            }
        }
    }

    /**
     * handles resource production AW
     *
     * @param diceSum - dice sum
     */
    private void produceResources(int diceSum) {
        if (isActionPhase) {
            Log.e(TAG, "produceResources: It is the action phase. Returned false.");
            return;
        }
        ArrayList<Integer> productionHexagonIds = board.getHexagonsFromChitValue(diceSum);
        Log.i(TAG, "produceResources: Hexagons with chit value " + diceSum + ": " + productionHexagonIds.toString());
        for (Integer i : productionHexagonIds) {
            Hexagon hex = board.getHexagonFromId(i);
            Log.i(TAG, "produceResources: Hexagon " + i + " producing " + hex.getResourceId());

            ArrayList<Integer> receivingIntersections = this.board.getAdjacentIntersections(i); // intersections adjacent to producing hexagon tile

            for (Integer intersectionId : receivingIntersections) {

                Building b = this.board.getBuildingAtIntersection(intersectionId);
                if (null != b) {

                    this.playerList.get(b.getOwnerId()).addResourceCard(hex.getResourceId(), b.getVictoryPoints());
                    Log.i(TAG, "produceResources: Giving " + b.getVictoryPoints() + " resources of type: " + hex.getResourceId() + " to player " + b.getOwnerId());
                }
            }
        }
    }

    /* ----- action methods ----- */

    /**
     * TODO Method for the very first turn for each player; player will select coordinates for two roads and two settlements at the beginning of the game
     *
     * @return - action success
     */
    public boolean setupBuilding() {

        return false;
    } // end setupBuilding action method

    /**
     * Player sends action to game state and game state return number with resources depending on settlements players own and where they're located.
     *
     * @return - action success
     */
    public boolean rollDice() {
        Log.d(TAG, "rollDice() called.");

        if (this.isActionPhase) {
            Log.e(TAG, "rollDice: Player " + currentPlayerId + " tried to roll the dice, but it is the action phase during " + this.currentPlayerId + "'s turn. Returned false.");
            return false;
        }

        int rollNum = dice.roll();
        Log.i(TAG, "rollDice: Player " + currentPlayerId + " rolled a " + rollNum);

        // if the robber is rolled
        if (rollNum == 7) {
            // todo activate robber
            Log.i(TAG, "rollDice: The robber has been activated.");
        } else {
            Log.i(TAG, "rollDice: Calling the produceResources method.");
            produceResources(rollNum);
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
    public boolean endTurn() {
        if (!isActionPhase) {
            Log.e(TAG, "endTurn: Player tried to end their turn, but it is not the action phase. Returning false.");
            return false;
        }

        if (this.currentPlayerId == 3) {
            this.currentPlayerId = 0;
        } else {
            this.currentPlayerId++;
        }

        Log.i(TAG, "endTurn: Player " + this.currentPlayerId + " has ended their turn. It is now player " + this.currentPlayerId + "'s turn.");

        updateVictoryPoints();

        for (DevelopmentCard developmentCard : playerList.get(currentPlayerId).getDevelopmentCards()) {
            developmentCard.setPlayable(true);
        }

        return true;
    } // end endTurn method

    /**
     * TODO
     * Player trades with ports, gives resources and receives a resource;
     * number depends on the resource
     * error checking:
     * - checks if it is the action phase of the turn
     * - checks if the player has enough resources to trade
     *
     * @param playerId - player attempting to trade with port
     * @param givenResourceId - what player is giving in the trade
     * @param receivedResourceId - what the player is receiving in the trade
     * @return - action success
     */
    public boolean tradeWithPort(int playerId, int intersectionId, int givenResourceId, int receivedResourceId) {
        // check if current player's turn and then if player has rolled dice
        if (!valAction(playerId)) {
            return false;
        }

        // check if the intersection has a building on it
        if (!board.hasBuilding(intersectionId)) {
            return false;
        }

        // check if the player owns the building
        if (board.getBuildings()[intersectionId].getOwnerId() != playerId) {
            return false;
        }

        // code to commence trade
        int tradeRatio = this.board.getPortList().get(intersectionId).getTradeRatio();
        int tradeResrouceId = this.board.getPortList().get(intersectionId).getResourceId();

        // check if player has enough resources to complete trade
        if (this.playerList.get(playerId).removeResourceCard(givenResourceId, 0)) {
            Log.i(TAG, "tradeWithPort: Player" + playerId + " does not have enough resources!");
            return false;
        }
        this.playerList.get(playerId).addResourceCard(receivedResourceId, 1);
        Log.i(TAG, "tradeWithPort: Player " + playerId + " traded " + tradeRatio + " " + givenResourceId + " for a " + receivedResourceId + " with port.");
        return true;
    }

    /**
     * Player trades with bank, gives resources and receives a resource; number depends on the resource
     *
     * @param playerId - player attempting to trade with port
     * @param resGiven - what player is giving in the trade
     * @param resReceive - what the player is receiving in the trade
     * @return - action success
     */
    public boolean tradeWithBank(int playerId, int resGiven, int resReceive) {
        if (valAction(playerId)) {
            return false;
        }

        //Setting ration then checking resources; if enough, we commence with trade
        Random random = new Random();
        int ratio = random.nextInt(1) + 2;

        // Player.removeResources returns false if the player does not have enough, if they do it removes them.
        if (!this.playerList.get(playerId).removeResourceCard(resGiven, ratio)) { // here it can do two checks at once. It can't always do this.
            Log.d(TAG, "ERROR: tradeWithBank - not enough resources player id: " + playerId);
            return false;
        }

        this.playerList.get(playerId).addResourceCard(resReceive, 1); // add resource card to players inventory

        Log.d("devInfo", "INFO: tradeWithBank - player " + playerId + " traded " + ratio + " " + resGiven + " for a " + resReceive + " with bank.\n");
        return true;
    }

    /**
     * Player requests to build road ands Game State processes requests and returns true if build was successful
     *
     * @param playerId - player building a road
     * @param startIntersectionID - intersection id
     * @param endIntersectionID - intersection id
     * @return - action success
     */
    public boolean buildRoad(int playerId, int startIntersectionID, int endIntersectionID) {
        if (!valAction(playerId)) {
            return false;
        }

        if (this.playerList.get(playerId).checkResourceBundle(Road.resourceCost)) {
            Log.i(TAG, "buildRoad: BuildRoad - player " + playerId + " does not have enough resources.\n");
            return false;
        }

        if (!board.validRoadPlacement(playerId, startIntersectionID, endIntersectionID)) {
            Log.i(TAG, "buildRoad: Invalid road placement: " + startIntersectionID + ", " + endIntersectionID);
            return false;
        }

        // remove resources from players inventory (also does checks)
        if (!this.playerList.get(playerId).removeResourceBundle(Road.resourceCost)) {
            Log.e(TAG, "buildRoad: Player.removeResourceBundle returned false.");
            return false;
        }

        this.board.addRoad(playerId, startIntersectionID, endIntersectionID); // add road to the board
        Log.i(TAG, "buildRoad: Player " + playerId + " built a road.");
        return true;
    }

    /**
     * Player requests to build settlement and Gamestate processes requests and returns true if build was successful
     *
     * @param playerId - player building a settlement
     * @param intersectionId - intersection the player wants to build at
     * @return - action success
     */
    public boolean buildSettlement(int playerId, int intersectionId) {
        // validates the player id, checks if its their turn, and checks if it is the action phase
        if (!valAction(playerId)) {
            return false;
        }

        // check if player has the required resources to build a Settlement
        if (!this.playerList.get(playerId).checkResourceBundle(Settlement.resourceCost)) {
            Log.i(TAG, "buildSettlement: Player " + playerId + " does not have enough resources to build.\n");
            return false;
        }

        // check if the selected building location is valid
        if (!this.board.validBuildingLocation(playerId, intersectionId)) {
            return false;
        }

        // remove resources from players inventory (also does checks)
        if (!this.playerList.get(playerId).removeResourceBundle(Settlement.resourceCost)) {
            Log.e(TAG, "buildSettlement: Player.removeResourceBundle returned false.");
            return false;
        }

        // create Settlement object and add to Board object
        Settlement settlement = new Settlement(playerId);
        this.board.addBuilding(intersectionId, settlement);
        Log.i(TAG, "buildSettlement: Player " + playerId + " built a Settlement.");

        return true;
    }

    /**
     * Player requests to build city and Game State processes requests and returns true if build was successful
     *
     * @param playerId - player building a city
     * @param intersectionId - intersection
     * @return - action success
     */
    public boolean buildCity(int playerId, int intersectionId) {
        Log.d(TAG, "buildCity() called with: playerId = [" + playerId + "], intersectionId = [" + intersectionId + "]");
        // check if valid player id, turn, and action phase
        if (!valAction(playerId)) {
            Log.e(TAG, "buildCity: valAction failed.");
            return false;
        }

        // check if player has enough resources
        if (!this.playerList.get(playerId).checkResourceBundle(City.resourceCost)) {
            Log.i(TAG, "buildCity: Player " + playerId + " does not have enough resources to build a City.");
            return false;
        }

        // remove resources from players inventory (also does checks)
        if (!this.playerList.get(playerId).removeResourceBundle(City.resourceCost)) {
            Log.e(TAG, "buildCity: Player.removeResourceBundle returned false.");
            return false;
        }

        // create City object and add to Board object
        City city = new City(intersectionId, playerId);
        this.board.addBuilding(intersectionId, city);

        Log.i(TAG, "buildCity: Player " + playerId + " built a city.");
        return true;
    }

    /**
     * TODO needs to take a dev card id as parameter and buy that specific card
     * Player will choose "Development Card" from the build menu, confirm, and then add a random development card to their development card inventory
     *
     * @param playerId - player who is requesting to buy dev card
     * @return - action success
     */
    public boolean buyDevCard(int playerId) {
        // check if player id is valid and if action phase of players turn
        if (!valAction(playerId)) {
            return false;
        }

        Player p = this.playerList.get(playerId);

        // check if player can build dev card
        if (!p.checkResourceBundle(DevelopmentCard.resourceCost)) {
            return false;
        }

        // remove resources from players inventory (also does checks)
        if (!p.removeResourceBundle(DevelopmentCard.resourceCost)) {
            return false;
        }

        // add random dev card to players inventory
        p.addDevelopmentCard(getRandomCard());
        return true;
    }

    /**
     * @param playerId - player playing development card
     * @param devCardId - id of the development card
     * @return - action success
     */
    public boolean useDevCard(int playerId, int devCardId) {

        if (!valAction(playerId)) {
            return false;
        }

        DevelopmentCard dc = new DevelopmentCard(devCardId);
        return true;
    }

    /**
     * TODO
     * Player chooses cards to discard if they own more than 7 cards and robber is activated
     *
     * @return - action success
     */
    public boolean robberDiscard(ArrayList<Integer> resourceCards) {
        for (Player player : this.playerList) {
            int handSize = player.getTotalResourceCardCount();
            if (handSize > 7) {
                int newHandSize = handSize / 2;
                // TODO !!! somehow need to make users select newHandSize resource cards to discard !!!
                for (int x = 0; x < resourceCards.size(); x++) {
                    player.removeResourceCard(resourceCards.get(x), 1);
                }
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
    public boolean moveRobber(int hexagonId, int playerId) {
        if (!valPlId(playerId)) {
            Log.d(TAG, "moveRobber: invalid player id: " + playerId);
            return false;
        }
        if (!checkTurn(playerId)) {
            Log.i(TAG, "moveRobber: it is not " + playerId + "'s turn.");
            return false;
        }
        if (this.board.moveRobber(hexagonId)) {
            Log.i(TAG, "moveRobber: Player " + playerId + " moved the Robber to Hexagon " + hexagonId);
            return true;
        }
        Log.i(TAG, "moveRobber: Player " + playerId + "  cannot move the Robber to Hexagon " + hexagonId);
        return false;
    }

    /**
     * After the player has moved the Robber, the player will choose a player to steal from and receive a random card from their hand
     *
     * @param hexagonId - hexagon that the robber is moved to
     * @param playerId - player stealing resources
     * @return - action success
     */
    public boolean robberSteal(int hexagonId, int playerId) {
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
        return true;
    }

    public Dice getDice() {
        return dice;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public ArrayList<Integer> getDevelopmentCards() {
        return developmentCards;
    }

    public void setDevelopmentCards(ArrayList<Integer> developmentCards) {
        this.developmentCards = developmentCards;
    }

    public int[] getPlayerVictoryPoints() {
        return playerVictoryPoints;
    }

    public void setPlayerVictoryPoints(int[] playerVictoryPoints) {
        this.playerVictoryPoints = playerVictoryPoints;
    }

    public int[] getPlayerPrivateVictoryPoints() {
        return playerPrivateVictoryPoints;
    }

    public void setPlayerPrivateVictoryPoints(int[] playerPrivateVictoryPoints) {
        this.playerPrivateVictoryPoints = playerPrivateVictoryPoints;
    }

    public int getCurrentDiceSum() {
        return currentDiceSum;
    }

    public void setCurrentDiceSum(int currentDiceSum) {
        this.currentDiceSum = currentDiceSum;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public boolean isActionPhase() {
        return isActionPhase;
    }

    public void setActionPhase(boolean actionPhase) {
        isActionPhase = actionPhase;
    }

    public int getCurrentLargestArmyPlayerId() {
        return currentLargestArmyPlayerId;
    }

    public void setCurrentLargestArmyPlayerId(int currentLargestArmyPlayerId) {
        this.currentLargestArmyPlayerId = currentLargestArmyPlayerId;
    }

    public int getCurrentLongestRoadPlayerId() {
        return currentLongestRoadPlayerId;
    }

    public void setCurrentLongestRoadPlayerId(int currentLongestRoadPlayerId) {
        this.currentLongestRoadPlayerId = currentLongestRoadPlayerId;
    }

    /**
     * TODO update???
     *
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String str = "";

        result.append("CatanGameState:\n");
        result.append("Current Player: ").append(this.currentPlayerId).append("\n");
        result.append(this.currentPlayerId);
        result.append("\n");
        result.append("Current Dice Sum: ");
        result.append(this.currentDiceSum);
        result.append("\n");
        result.append("isActionPhase: ");
        result.append(this.isActionPhase);
        result.append("\n");

        for (int i = 0; i < this.playerList.size(); i++) {
            result.append(this.playerList.get(i).toString()).append(" "); // TODO
            result.append("\n\n");
        }
        result.append(this.board.toString());

        result.append("currentLargestArmyPlayerId: ").append(this.currentLargestArmyPlayerId).append("\n\n");
        result.append("currentLongestRoadPlayerId: ").append(this.currentLongestRoadPlayerId).append("\n\n");

        str = result.toString();
        return str;
    } // end CatanGameState toString()
}
