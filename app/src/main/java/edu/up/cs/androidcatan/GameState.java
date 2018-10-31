package edu.up.cs.androidcatan;

import android.util.Log;
import android.widget.EditText;

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
    public boolean initBuilding(boolean move, EditText edit) {
        if (move) {
            edit.append("Player 1 placed their settlements and roads!\n");
            edit.append("Player 2 placed their settlements and roads!\n");
            edit.append("Player 3 placed their settlements and roads!\n");
            return true;
        }

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
     * @param edit               - edit text
     * @return - action success
     */
    public boolean tradeWithPort(int playerId, int givenResourceId, int receivedResourceId, EditText edit) {
        // check if current player's turn and then if player has rolled dice
        if (playerId != this.currentPlayerId) {
            edit.append("It is not Player " + playerId + "'s turn!\n");
            Log.d("devInfo", "INFO: tradeWithPort - player " + playerId + " tried to trade with port, but it is player " + this.currentPlayerId + "'s turn.");
            return false;
        }
        // check if the turn is in the action phase
        if (!this.isActionPhase) {
            edit.append("Player " + playerId + " must roll dice first!\n");
            Log.d("devInfo", "INFO: tradeWithPort - player " + playerId + " tried to trade with port, but it isn't the action phase.");
            return false;
        }
        if (!valPlId(playerId)) {
            Log.d(TAG, "ERROR: tradeWithPort - invalid player id: " + playerId);
            return false;
        }

        // creating a random trade ratio
        Random random = new Random();
        int ratio = random.nextInt(1) + 2;

        // check if player has enough resources to complete trade
        if (this.playerList.get(playerId).removeResourceCard(givenResourceId, ratio)) {
            edit.append("Player" + playerId + " does not have enough resources!\n");
            return false;
        }
        this.playerList.get(playerId).addResourceCard(receivedResourceId, 1);

        edit.append("Player " + playerId + " traded " + ratio + " " + givenResourceId + " for a " + receivedResourceId + " with a Port!\n");
        Log.d("devInfo", "INFO: tradeWithPort - player " + playerId + " traded " + ratio + " " + givenResourceId + " for a " + receivedResourceId + " with port.\n");
        return true;
    }

    /**
     * Player trades with bank, gives resources and receives a resource; number depends on the resource
     *
     * @param playerId   - player attempting to trade with port
     * @param resGiven   - what player is giving in the trade
     * @param resReceive - what the player is receiving in the trade
     * @param edit       - edit text
     * @return - action success
     */
    public boolean tradeWithBank(int playerId, int resGiven, int resReceive, EditText edit) {
        if (!valPlId(playerId)) {
            Log.d(TAG, "ERROR: tradeWithBank - invalid player id: " + playerId);
            return false;
        }
        //Check if current player's turn and then if player has rolled dice
        if (playerId != this.currentPlayerId) {
            edit.append("It is not Player " + playerId + "'s turn!\n");
            Log.d(TAG, "ERROR: tradeWithBank - it is not " + playerId + "'s turn.");
            return false;
        }
        if (!this.isActionPhase) {
            edit.append("Player " + playerId + " must roll dice first!\n");
            return false;
        }

        //Setting ration then checking resources; if enough, we commence with trade
        Random random = new Random();
        int ratio = random.nextInt(1) + 2;

        // Player.removeResources returns false if the player does not have enough, if they do it removes them.
        if (!this.playerList.get(playerId).removeResourceCard(resGiven, ratio)) { // here it can do two checks at once. It can't always do this.
            edit.append("Player " + playerId + " does not have enough resources!\n");
            Log.d(TAG, "ERROR: tradeWithBank - not enough resources player id: " + playerId);
            return false;
        }

        this.playerList.get(playerId).addResourceCard(resReceive, 1); // add resource card to players inventory

        edit.append("Player " + playerId + " traded " + ratio + " " + resGiven + " for a " + resReceive + " with the Bank!\n");
        Log.d("devInfo", "INFO: tradeWithBank - player " + playerId + " traded " + ratio + " " + resGiven + " for a " + resReceive + " with bank.\n");
        return true;
    }

    /**
     * Player requests to build road ands Game State processes requests and returns true if build was successful
     *
     * @param playerId            - player building a road
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

        // create Settlement object and add to Board object
        Settlement settlement = new Settlement(playerId);
        this.board.addBuilding(intersectionId, settlement);


        return true;
    }

    /**
     * Player requests to build city and Gamestate processes requests and returns true if build was successful
     *
     * @param playerId       - player building a city
     * @param intersectionID
     * @param edit
     * @return - action success
     */
    public boolean buildCity(int playerId, int intersectionID, EditText edit) {
        if (!valPlId(playerId)) {
            Log.d(TAG, "ERROR: buildCity - invalid player id: " + playerId);
            return false;
        }
        if (!checkTurn(playerId)) {
            edit.append("It is not Player " + playerId + "'s turn!\n");
            Log.d(TAG, "ERROR: buildCity - it is not " + playerId + "'s turn.");
            return false;
        }
        if (!this.isActionPhase) {
            edit.append("Player " + playerId + " must roll dice first!\n");
            return false;
        }

        if (this.playerList.get(playerId).getResources().get("Ore") == 3 && this.playerList.get(playerId).getResources().get("Grain") == 2) {
            edit.append("Player " + playerId + " does not have enough resources!\n");
        }

        City city = new City(intersectionID, playerId);
        //board.addCity

        edit.append("Player " + playerId + " built a City!\n");
        return true;
    }

    /**
     * TODO needs to take a dev card id as parameter and buy that specific card
     * Player will choose "Development Card" from the build menu, confirm, and then add a random development card to their development card inventory
     *
     * @param playerId - player who is requesting to buy dev card
     * @param edit     -
     * @return - action success
     */
    public boolean buyDevCard(int playerId, EditText edit) {
        if (!valPlId(playerId)) {
            Log.d(TAG, "ERROR: buyDevCard - invalid player id: " + playerId);
            return false;
        }
        if (!checkTurn(playerId)) {
            edit.append("It is not Player " + playerId + "'s turn!\n");
            Log.d(TAG, "ERROR: buyDevCard - it is not " + playerId + "'s turn.");
            return false;
        }
        DevelopmentCard dc = new DevelopmentCard();
        int[] resources = this.playerList.get(playerId).getResourceCards();
        if (resources[1] > 0 && resources[2] > 0 && resources[3] > 0) {
            dc.build(this.playerList.get(playerId));
            return true;
        }
        return false;
    }

    /**
     * TODO needs to take a dev card id as parameter and use that specific card
     * Player will select a development card they own and use it; gamestate will determine legality and then carry out development cards function
     *
     * @param move
     * @param edit
     * @param playerId
     * @return - action success
     */
    public boolean useDevCard(boolean move, EditText edit, int playerId) {
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

        if (move) {
            edit.append("Player 3 used their Knight Card!\n");
            return true;
        }
        edit.append("Player 3 cannon use their Development Card!\n");
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

        if(randomStolenResourceId < 0 || randomStolenResourceId > 4) {
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
