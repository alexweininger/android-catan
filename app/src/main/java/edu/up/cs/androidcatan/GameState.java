package edu.up.cs.androidcatan;

import android.util.Log;
import gameframework.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 30th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class GameState {

    private static final String TAG = "GameState";

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


    GameState() { // GameState constructor
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
    } // end GameState constructor

    /**
     * TODO use deep copies of other classes
     * GameState deep copy constructor
     *
     * @param gameState - GameState object to make a copy of
     */
    GameState(GameState gameState) {
        this.dice = gameState.dice;
        this.currentPlayerId = gameState.currentPlayerId;
        this.currentDiceSum = gameState.currentDiceSum;
        this.isActionPhase = gameState.isActionPhase;
        this.board = new Board(gameState.board); // FIXME
        this.currentLongestRoadPlayerId = gameState.currentLongestRoadPlayerId;
        this.currentLargestArmyPlayerId = gameState.currentLargestArmyPlayerId;

        for (int i = 0; i < gameState.playerList.size(); i++) {
            this.playerList.add(new Player(gameState.playerList.get(i)));
        }

        for (int i = 0; i < gameState.playerVictoryPoints.length; i++) {
            this.playerVictoryPoints[i] = gameState.playerVictoryPoints[i];
            this.playerPrivateVictoryPoints[i] = gameState.playerPrivateVictoryPoints[i];
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
                Log.d("devInfo", "INFO: valAction - it is not the action phase.");
                return false;
            }
            Log.d("devInfo", "INFO: valAction - it is not " + playerId + "'s turn.");
            return false;
        }
        Log.d("devInfo", "INFO: valAction - invalid player id: " + playerId);
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
     * checkRoadLength - after each turn check if any player has longest road, with a min of 5 road segments
     * probably just calls a method in board?
     * recursion???
     */
    private void checkRoadLength() {
        int max = -1;
        int playerIdWithLongestRoad = -1;
        if (currentLongestRoadPlayerId != -1) {
            max = playerVictoryPoints[currentLargestArmyPlayerId];
        }
        for (int i = 0; i < 4; i++) {
            if (board.getPlayerRoadLength(i) > max) {
                max = board.getPlayerRoadLength(i);
                playerIdWithLongestRoad = i;
            }
        }
        if (max > 4) {
            this.currentLongestRoadPlayerId = playerIdWithLongestRoad;
        }
    }

    /**
     * updates the victory points of each player, should be called after every turn
     */
    private void updateVictoryPoints() {
        if (this.currentLongestRoadPlayerId != -1) {
            this.playerVictoryPoints[this.currentLongestRoadPlayerId] -= 2;
        }
        checkRoadLength();
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

        // TODO go through all buildings and tally up players victory points
    }

    /**
     * handles resource production AW
     *
     * @param diceSum - dice sum
     */
    private void produceResources(int diceSum) {
        if (isActionPhase) {
            Log.e(TAG, "produceResources: It is the action phase.");
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

    /**
     * TODO Method for the very first turn for each player; player will select coordinates for two roads and two settlements at the beginning of the game
     *
     * @return - action success
     */
    public boolean initBuilding() {

        return false;
    } // end initBuilding action method

    /**
     * Player sends action to game state and game state return number with resources depending on settlements players own and where they're located.
     *
     * @param playerId - player that attempts to roll the dice
     * @return - action success
     */
    public boolean rollDice(int playerId) {
        if (!valPlId(playerId)) {
            Log.e(TAG, "rollDice: Invalid player id: " + playerId);
            return false;
        }

        if (playerId != this.currentPlayerId) {
            Log.i(TAG, "rollDice: Player " + playerId + " tried to roll the dice, but it is player " + this.currentPlayerId + "'s turn.");
            return false;
        }

        if (this.isActionPhase) {
            Log.i(TAG, "rollDice: Player " + playerId + " tried to roll the dice, but it is the action phase during " + this.currentPlayerId + "'s turn.");
            return false;
        }

        int rollNum = dice.roll();
        Log.i(TAG, "rollDice: Player " + playerId + " rolled a " + rollNum);

        produceResources(rollNum);

        this.isActionPhase = true;

        return true;
    } // end rollDice action method

    /**
     * action for a player ending their turn, increments currentPlayerId. As of now does no checks.
     * error checking:
     * - valid player id
     * - it is players turn
     *
     * @param playerId - player requesting to end turn
     * @return - action success
     */
    public boolean endTurn(int playerId) {
        if (!valAction(playerId)) {
            return false;
        }

        if (this.currentPlayerId == 3) {
            this.currentPlayerId = 0;
        } else {
            this.currentPlayerId++;
        }

        Log.i(TAG, "endTurn: Player " + this.currentPlayerId + " has ended their turn. It is now player " + this.currentPlayerId + "'s turn.");

        updateVictoryPoints();
        return true;
    } // end endTurn method

    /**
     * Player trades with ports, gives resources and receives a resource;
     * number depends on the resource
     * error checking:
     * - checks if it is given players turn
     * - checks if it is the action phase of the turn
     * - checks if the player has enough resources to trade
     *
     * @param playerId           - player attempting to trade with port
     * @param givenResourceId    - what player is giving in the trade
     * @param receivedResourceId - what the player is receiving in the trade
     * @return - action success
     */
    public boolean tradeWithPort(int playerId, int givenResourceId, int receivedResourceId) {
        // check if current player's turn and then if player has rolled dice
        if(!valAction(playerId)) {
            return false;
        }
        
        // creating a random trade ratio
        Random random = new Random();
        int ratio = random.nextInt(1) + 2;

        // check if player has enough resources to complete trade
        if (this.playerList.get(playerId).removeResourceCard(givenResourceId, ratio)) {
            Log.i(TAG, "tradeWithPort: Player" + playerId + " does not have enough resources!");
            return false;
        }
        this.playerList.get(playerId).addResourceCard(receivedResourceId, 1);
        Log.i(TAG, "tradeWithPort: Player " + playerId + " traded " + ratio + " " + givenResourceId + " for a " + receivedResourceId + " with port.");
        return true;
    }

    /**
     * Player trades with bank, gives resources and receives a resource; number depends on the resource
     *
     * @param playerId   - player attempting to trade with port
     * @param resGiven   - what player is giving in the trade
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
     * @param playerId            - player building a road
     * @param startIntersectionID - intersection id
     * @param endIntersectionID   - intersection id
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
        if(!this.playerList.get(playerId).removeResourceBundle(Road.resourceCost)) {
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
     * @param playerId       - player building a settlement
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
        if(!this.playerList.get(playerId).removeResourceBundle(Settlement.resourceCost)) {
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
     * @param playerId       - player building a city
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
        if(!this.playerList.get(playerId).removeResourceBundle(City.resourceCost)) {
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
        if(!p.removeResourceBundle(DevelopmentCard.resourceCost)) {
            return false;
        }

        // add random dev card to players inventory
        p.addDevelopmentCard(getRandomCard());
        return true;
    }

    /**
     * TODO needs to take a dev card id as parameter and use that specific card
     * Player will select a development card they own and use it; Game State will determine legality and then carry out development cards function
     *
     * @param playerId
     * @return - action success
     */
    public boolean useDevCard(int playerId) {
        if (!valPlId(playerId)) {
            Log.d(TAG, "ERROR: useDevCard - invalid player id: " + playerId);
            return false;
        }
        if (!checkTurn(playerId)) {

        }
        DevelopmentCard dc = new DevelopmentCard();
        if (playerId == this.currentPlayerId) {
            //playerList.get(playerId).useDevCard(dc.generateDevCardDeck());

        }

        return false;
    }

    /**
     * TODO
     * Player chooses cards to discard if they own more than 7 cards and robber is activated
     *
     * @return - action success
     */
    public boolean robberDiscard(ArrayList<Integer> resourceCards) {
        for (int n = 0; n < 4; n++) {
            int handSize = this.playerList.get(n).getResources().size();
            if (handSize > 7) {
                int newHandSize = handSize / 2;
                // TODO !!! somehow need to make users select newHandSize resource cards to discard !!!
                for (int x = 0; x < resourceCards.size(); x++) {
                    this.playerList.get(n).removeResourceCard(resourceCards.get(x), 1);
                }
            }
        }
        Log.i(TAG, "Removed half of all resources from players with more than 7 cards\n");
        return true;
    }

    /**
     * If the player has rolled a 7, player will move the robber to another Hexagon that has settlements nearby
     *
     * @param hexagonId
     * @param playerId
     * @return
     */
    public boolean robberMove(int hexagonId, int playerId) {
        if (!valPlId(playerId)) {
            Log.d(TAG, "robberMove: invalid player id: " + playerId);
            return false;
        }
        if (!checkTurn(playerId)) {
            Log.i(TAG, "robberMove: it is not " + playerId + "'s turn.");
            return false;
        }
        if (this.board.moveRobber(hexagonId)) {
            Log.i(TAG, "robberMove: Player " + playerId + " moved the Robber to Hexagon " + hexagonId);
            return true;
        }
        Log.i(TAG, "robberMove: Player " + playerId + "  cannot move the Robber to Hexagon " + hexagonId);
        return false;
    }

    /**
     * After the player has moved the Robber, the player will choose a player to steal from and receive a random card from their hand
     *
     * @param hexagonId - hexagon that the robber is moved to
     * @param playerId  - player stealing resources
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


    /**
     * TODO update???
     *
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String str = "";

        result.append("GameState:\n");
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
            result.append(this.playerList.get(i).toString() + " "); // TODO
            result.append("\n\n");
        }
        result.append(this.board.toString());

        result.append("currentLargestArmyPlayerId: " + this.currentLargestArmyPlayerId + "\n");
        result.append("currentLongestRoadPlayerId: " + this.currentLongestRoadPlayerId + "\n\n");

        for (int i = 0; i < this.playerList.size(); i++) {
            // TODO ???
        }
        str = result.toString();
        return str;
    } // end GameState toString()
}
