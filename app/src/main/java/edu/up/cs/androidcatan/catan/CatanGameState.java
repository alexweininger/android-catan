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
 * @version November 8th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanGameState extends GameState{

    private static final String TAG = "CatanGameState";

    private Dice dice; // dice object
    private Board board; // board object

    private ArrayList<Player> playerList = new ArrayList<>(); // list of player objects

    private ArrayList<Integer> developmentCards = new ArrayList<>(); // ArrayList of the development card in the deck

    private int[] playerVictoryPoints = new int[4]; // victory points of each player
    private int[] playerPrivateVictoryPoints = new int[4]; // private victory points

    private int currentDiceSum; // the sum of the dice at this very moment

    private int currentPlayerId; // id of player who is the current playing player
    private boolean isActionPhase = false; // has the current player rolled the dice
    private boolean isSetupPhase = true;
    private boolean isRobberPhase = false;
    private boolean hasDiscarded = false;
    private boolean hasMovedRobber = false;
    private int currentLargestArmyPlayerId = -1; // player who currently has the largest army
    private int currentLongestRoadPlayerId = -1;

    // resourceCard index values: 0 = Brick, 1 = Lumber, 2 = Grain, 3 = Ore, 4 = Wool
    private int[] robberDiscardedResources = new int[]{0,0,0,0,0};  //How many resources the player would like to discard


    public CatanGameState () { // CatanGameState constructor
        this.dice = new Dice();
        this.board = new Board();
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
    public CatanGameState (CatanGameState cgs) {
        this.setDice(new Dice(cgs.getDice()));
        this.setBoard(new Board(cgs.getBoard()));

        this.currentPlayerId = cgs.currentPlayerId;
        this.currentDiceSum = cgs.currentDiceSum;
        this.isActionPhase = cgs.isActionPhase;
        this.isSetupPhase = cgs.isSetupPhase;
        this.isRobberPhase = cgs.isRobberPhase;
        this.hasDiscarded = cgs.hasDiscarded;
        this.hasMovedRobber = cgs.hasMovedRobber;
        this.currentLongestRoadPlayerId = cgs.currentLongestRoadPlayerId;
        this.currentLargestArmyPlayerId = cgs.currentLargestArmyPlayerId;

        this.setPlayerPrivateVictoryPoints(cgs.getPlayerPrivateVictoryPoints());
        this.setPlayerVictoryPoints(cgs.getPlayerVictoryPoints());
        this.setDevelopmentCards(cgs.getDevelopmentCards());

        this.setBoard(cgs.getBoard());

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

        return drawnDevCard;
    }

    /**
     * Player will choose "Development Card" from the build menu, confirm, and then add a random development card to their development card inventory.
     *
     * @param playerId Player id of the player who's requesting to buy a development card.
     * @return - Action success.
     */
    public boolean buyDevCard (int playerId) {
        // check if player id is valid and if action phase of players turn
        if (!valAction(playerId)) {
            return false;
        }

        Player player = this.playerList.get(playerId);

        // check if player can build dev card
        if (!player.checkResourceBundle(DevelopmentCard.resourceCost)) {
            return false;
        }

        // remove resources from players inventory (also does checks)
        if (!player.removeResourceBundle(DevelopmentCard.resourceCost)) {
            return false;
        }

        // add random dev card to players inventory
        player.addDevelopmentCard(getRandomCard());
        return true;
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

        DevelopmentCard developmentCard = new DevelopmentCard(devCardId);
        return true;
    }

    /*-------------------------------------Validation Methods------------------------------------------*/

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
     * TODO we should be calling this somewhere right? - AW
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

        for (int n = 0; n < this.playerList.size(); n++){
            if (playerList.get(n).getPlayerId() == this.board.getPlayerWithLongestRoad(playerList)){
                playerVictoryPoints[playerList.get(n).getPlayerId()] += 2;
            }
        }

        // goes through all buildings and the amount of victory points to the player to who owns the building
        Building[] buildings = this.board.getBuildings();

        for (Building building : buildings) {
            if (building != null) {
                Log.w(TAG, "updateVictoryPoints: building.getOwnerId: " + building.getOwnerId() + " building.getVictoryPoints: " + building.getVictoryPoints());
                playerVictoryPoints[building.getOwnerId()] += building.getVictoryPoints();
            }
        }
    }

    /*-------------------------------------Resource Methods------------------------------------------*/

    /**
     * handles resource production AW
     *
     * @param diceSum - dice sum
     */
    private void produceResources (int diceSum) {
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

        int rollNum = dice.roll();
        this.currentDiceSum = rollNum;
        Log.i(TAG, "rollDice: Player " + currentPlayerId + " rolled a " + rollNum);
        // if the robber is rolled
        if (rollNum == 7) {
            // todo activate robber
            Log.i(TAG, "rollDice: The robber has been activated.");
            this.isRobberPhase = true;
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
     * TODO
     * Player trades with ports, gives resources and receives a resource;
     * number depends on the resource
     * error checking:
     * - checks if it is the action phase of the turn
     * - checks if the player has enough resources to trade
     *
     * @param playerId - player attempting to trade with port
     * @param lostResourceId - what player is giving in the trade
     * @param receivedResourceId - what the player is receiving in the trade
     * @return - action success
     */
    public boolean tradeWithPort (int playerId, int intersectionId, int lostResourceId, int receivedResourceId) {
        Log.d(TAG, "tradeWithPort() called with: playerId = [" + playerId + "], intersectionId = [" + intersectionId + "], givenResourceId = [" + lostResourceId + "], receivedResourceId = [" + receivedResourceId + "]");
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
        int tradeResourceId = this.board.getPortList().get(intersectionId).getResourceId();

        // check if player has enough resources to complete trade
        if (this.playerList.get(playerId).removeResourceCard(lostResourceId, 0)) {
            Log.i(TAG, "tradeWithPort: Player" + playerId + " does not have enough resources!");
            return false;
        }

        //adds the resource they gained and removes the ones they lost to their hand
        this.playerList.get(playerId).addResourceCard(receivedResourceId, 1);
        this.playerList.get(playerId).removeResourceCard(lostResourceId, tradeRatio);
        Log.i(TAG, "tradeWithPort: Player " + playerId + " traded " + tradeRatio + " " + lostResourceId + " for a " + receivedResourceId + " with port.");
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
    //TODO implement
    public boolean tradeWithBank (int playerId, int resGiven, int resReceive) {
        if (this.isActionPhase) {
            return false;
        }

        //Setting ratio then checking resources; if enough, we commence with trade
        Random random = new Random();
        int ratio = random.nextInt(1) + 2;

        // Player.removeResources returns false if the player does not have enough, if they do it removes them.
        if (!this.playerList.get(playerId).removeResourceCard(resGiven, ratio)) { // here it can do two checks at once. It can't always do this.
            Log.e(TAG, "tradeWithBank - not enough resources, player id: " + playerId);
            return false;
        }

        this.playerList.get(playerId).addResourceCard(resReceive, 1); // add resource card to players inventory
        this.playerList.get(playerId).removeResourceCard(resGiven, ratio); //removes resource cards from players inventory

        Log.w(TAG, "tradeWithBank - player " + playerId + " traded " + ratio + " " + resGiven + " for a " + resReceive + " with bank.\n");
        return true;
    } // end tradeWithBank

    /*---------------------------------------Building Methods------------------------------------------*/

    /**
     * Player requests to build road ands Game State processes requests and returns true if build was successful
     *
     * @param playerId - player building a road
     * @param startIntersectionID - intersection id
     * @param endIntersectionID - intersection id
     * @return - action success
     */
    public boolean buildRoad (int playerId, int startIntersectionID, int endIntersectionID) {
        Log.d(TAG, "buildRoad() called with: playerId = [" + playerId + "], startIntersectionID = [" + startIntersectionID + "], endIntersectionID = [" + endIntersectionID + "]");

        // if it is not the setup phase, check if it is the action phase
        if (!this.isSetupPhase) {
            if (!this.isActionPhase) {
                Log.e(TAG, "buildRoad: Not setup phase, and not action phase. Returning false.");
                return false;
            }
        }

        // check if they have enough resources to build a road
        if (!this.playerList.get(playerId).checkResourceBundle(Road.resourceCost)) {
            Log.e(TAG, "buildRoad: Player " + playerId + " does not have enough resources.\n");
            Log.e(TAG, "buildRoad: Player " + playerId + " resources: " + this.getPlayerList().get(playerId).printResourceCards());
            return false;
        }

        // check if it is a valid road placement
        if (!board.validRoadPlacement(playerId, this.isSetupPhase, startIntersectionID, endIntersectionID)) {
            Log.e(TAG, "buildRoad: Invalid road placement: " + startIntersectionID + ", " + endIntersectionID);
            return false;
        }

        // remove resources from players inventory (also does checks)
        if (!this.playerList.get(playerId).removeResourceBundle(Road.resourceCost)) {
            Log.e(TAG, "buildRoad: Player.removeResourceBundle returned false.");
            return false;
        }

        // add road to the board
        this.board.addRoad(playerId, startIntersectionID, endIntersectionID);
        Log.w(TAG, "buildRoad: Player " + playerId + " built a road. Returning true.");
        return true;
    } // end buildRoad

    /**
     * Player requests to build settlement and Gamestate processes requests and returns true if build was successful
     *
     * @param playerId - player building a settlement
     * @param intersectionId - intersection the player wants to build at
     * @return - action success
     */
    public boolean buildSettlement (int playerId, int intersectionId) {
        Log.d(TAG, "buildSettlement() called with: playerId = [" + playerId + "], intersectionId = [" + intersectionId + "]");
        if (!this.isSetupPhase) {
            // validates the player id, checks if its their turn, and checks if it is the action phase
            if (!valAction(playerId)) {
                Log.d(TAG, "buildSettlement() Not setup phase, and not action phase. Returning false." + false);
                return false;
            }
        } else {


            Log.i(TAG, "buildSettlement: Player " + playerId + " now has resources: " + this.getPlayerList().get(playerId).printResourceCards());
        }

        // check if player has the required resources to build a Settlement
        if (!this.playerList.get(playerId).checkResourceBundle(Settlement.resourceCost)) {
            Log.e(TAG, "buildSettlement: Player " + playerId + " does not have enough resources to build.\n");
            Log.e(TAG, "buildSettlement: Player " + playerId + " resources: " + this.getPlayerList().get(playerId).printResourceCards());
            return false;
        }

        // check if the selected building location is valid
        if (!this.board.validBuildingLocation(playerId, this.isSetupPhase, intersectionId)) {
            Log.e(TAG, "buildSettlement: validBuildingLocation returned false.");
            return false;
        }

        // remove resources from players inventory (also does checks)
        if (!this.playerList.get(playerId).removeResourceBundle(Settlement.resourceCost)) {
            Log.e(TAG, "buildSettlement: Player.removeResourceBundle returned false.");
            Log.e(TAG, "buildSettlement: Player " + playerId + " resources: " + this.getPlayerList().get(playerId).printResourceCards());
            return false;
        }

        // create Settlement object and add to Board object
        Settlement settlement = new Settlement(playerId);
        this.board.addBuilding(intersectionId, settlement);
        Log.w(TAG, "buildSettlement: Player " + playerId + " built a settlement. Returning true.");
        return true;
    }

    /**
     * Player requests to build city and Game State processes requests and returns true if build was successful
     *
     * @param playerId - player building a city
     * @param intersectionId - intersection
     * @return - action success
     */
    public boolean buildCity (int playerId, int intersectionId) {
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

    /*----------------------------------------Robber Methods------------------------------------------*/
    public void setRobberPhase (boolean rp) {
        this.isRobberPhase = rp;
    }

    public boolean getRobberPhase () {
        return this.isRobberPhase;
    }

    /**
     *
     * @param playerId
     * @return
     */
    public boolean checkPlayerResources(int playerId){
        if(hasDiscarded){
            return false;
        }
        if(playerList.get(playerId).getTotalResourceCardCount() > 7){
            return true;
        }

        return false;
    }

    /**
     * Checking if we can actually discard the resources
     *
     * @param playerId
     * @param resourcesDiscarded
     * @return
     */
    public boolean validDiscard(int playerId, int[] resourcesDiscarded){
        int totalDiscarded = 0;
        for(int i = 0; i < resourcesDiscarded.length; i++){
            if(resourcesDiscarded[i] > playerList.get(playerId).getResourceCards()[i]){
                Log.i(TAG, "validDiscard: Invalid due to not having enough resources, returning false");
                return false;
            }
            totalDiscarded += resourcesDiscarded[i];
        }
        Log.i(TAG, "discardResources: Amount is " + totalDiscarded);
        if(totalDiscarded == playerList.get(playerId).getTotalResourceCardCount()/2){
            return true;
        }
        return false;
    }

    /**
     * Discards resources when robber is played; makes sure it is exactly half of the player's hand;
     * if not, returns false
     *
     * @param playerId
     * @param resourcesDiscarded
     * @return
     */
    public boolean discardResources(int playerId, int[] resourcesDiscarded){
        int totalDiscarded = 0;
        for(int i = 0; i < resourcesDiscarded.length; i++){
            totalDiscarded += resourcesDiscarded[i];
        }
        Log.i(TAG, "discardResources: Amount is " + totalDiscarded);
        Log.i(TAG, "discardResources: Discarded resources");
        for(int i = 0; i < resourcesDiscarded.length; i++){
            this.playerList.get(playerId).removeResourceCard(i, resourcesDiscarded[i]);
        }
        if(playerId == currentPlayerId){
            hasDiscarded = true;
        }
        else{
            hasDiscarded = true;
            hasMovedRobber = true;
        }
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
        if (this.board.moveRobber(hexagonId)) {
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

    public int[] getRobberDiscardedResource(){
        return robberDiscardedResources;
    }

    public boolean isHasDiscarded() { return hasDiscarded;}

    public boolean isHasMovedRobber() { return hasMovedRobber; }

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

        for (Player player : playerList) {
            result.append(player.toString()).append("\n");
        }
        result.append(this.board.toString()).append("\n");

        return result.toString();
    } // end CatanGameState toString()
}
