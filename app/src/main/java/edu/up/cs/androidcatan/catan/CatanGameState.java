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
 * https://github.com/alexweininger/android-catan
 **/

public class CatanGameState extends GameState {

    //Serializable variables
    private static final String TAG = "CatanGameState";
    private static final long serialVersionUID = -5201889928776982853L;

    private Dice dice; // dice object
    private Board board; // board object

    private ArrayList<Player> playerList = new ArrayList<>(); // list of player objects
    private ArrayList<Integer> developmentCards = new ArrayList<>(); // ArrayList of the development card in the deck

    private int currentPlayerId; // id of player who is the current playing player
    private int currentDiceSum; // the sum of the dice at this very moment

    // game phases
    private boolean isSetupPhase = true; // is it the setup phase
    private boolean isActionPhase = false; // has the current player rolled the dice
    private boolean isRobberPhase = false; // is the robber phase
    private int playerStealingFrom = 0; // playerNum of who is getting a resource taken during Robber Steal Phase

    //Setup phase variables
    static final int setupPhaseTurnOrder[] = {0, 1, 2, 3, 3, 2, 1, 0};
    private int setupPhaseTurnCounter;

    // robber
    private boolean hasMovedRobber = false;
    // resourceCard index values: 0 = Brick, 1 = Lumber, 2 = Grain, 3 = Ore, 4 = Wool
    private final int[] robberDiscardedResources = new int[]{0, 0, 0, 0, 0};  //Resets amount of discarded resources
    private boolean[] robberPlayerListHasDiscarded = new boolean[]{false, false, false, false};

    // trophies
    private int currentLargestArmyPlayerId = -1; // player who currently has the largest army
    private int currentLongestRoadPlayerId = -1;

    /**
     * constructor for CatanGameState
     */
    public CatanGameState() {
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
    public CatanGameState(CatanGameState cgs) {
        this.setDice(new Dice(cgs.getDice()));
        if (cgs.getBoard() == null) {
            Log.e(TAG, "CatanGameState: cgs.getBoard() is null");
        }
        this.setBoard(new Board(cgs.board));
        this.currentDiceSum = cgs.currentDiceSum;
        Log.d(TAG, "CatanGameState: cgs.hasMovedRobber=" + cgs.hasMovedRobber);
        this.hasMovedRobber = cgs.hasMovedRobber;
        this.currentLongestRoadPlayerId = cgs.currentLongestRoadPlayerId;
        this.currentLargestArmyPlayerId = cgs.currentLargestArmyPlayerId;
        this.isSetupPhase = cgs.isSetupPhase;
        this.isRobberPhase = cgs.isRobberPhase;
        System.arraycopy(cgs.robberPlayerListHasDiscarded, 0, this.robberPlayerListHasDiscarded, 0, cgs.robberPlayerListHasDiscarded.length);
        this.developmentCards.addAll(cgs.getDevelopmentCards());
        this.currentPlayerId = cgs.currentPlayerId;
        this.setupPhaseTurnCounter = cgs.setupPhaseTurnCounter;
        this.isActionPhase = cgs.isActionPhase;
        this.playerStealingFrom = cgs.playerStealingFrom;
        System.arraycopy(cgs.robberPlayerListHasDiscarded, 0, this.robberPlayerListHasDiscarded, 0, cgs.robberPlayerListHasDiscarded.length);
        this.robberPlayerListHasDiscarded = Arrays.copyOf(cgs.robberPlayerListHasDiscarded, cgs.robberPlayerListHasDiscarded.length);
        // copy player list (using player deep copy const.)
        for (int i = 0; i < cgs.playerList.size(); i++) {
            this.playerList.add(new Player(cgs.playerList.get(i)));
        }
    } // end deep copy constructor

    /*-------------------------------------Dev Card Methods------------------------------------------*/

    /**
     * Creates a 'deck' of int representing the exact number each type of card. This allows us to accurately select a card at random.
     */
    private void generateDevCardDeck() {
        //Specified dev card ratios in real game deck
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
    public int getRandomDevCard() {
        if (developmentCards.size() == 0) generateDevCardDeck();

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

    /**
     * @return Player object
     */
    public Player getCurrentPlayer() {
        return this.playerList.get(currentPlayerId);
    }

    /**
     * @param playerId Player id
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
            return playerId == currentPlayerId;
        }
        Log.e(TAG, "checkTurn: Invalid player id: " + playerId);
        return false;
    }

    /**
     * checkArmySize - after each turn checks who has the largest army (amount of played knight cards) with a minimum of 3 knight cards played.
     *
     * @param playerId
     */
    public void checkArmySize(int playerId) {
        Log.d(TAG, "checkArmySize() called");

        this.playerList.get(playerId).setArmySize(this.playerList.get(playerId).getArmySize() + 1);

        if(currentLargestArmyPlayerId == -1 && this.playerList.get(playerId).getArmySize() >= 3){
            currentLargestArmyPlayerId = playerId;
            return;
        }

        if(currentLargestArmyPlayerId == -1){
            return;
        }
        if(this.playerList.get(playerId).getArmySize() > this.playerList.get(currentLargestArmyPlayerId).getArmySize()) {
             currentLargestArmyPlayerId = playerId;
             return;
        }

        return;
    }

    /*-------------------------------------Resource Methods------------------------------------------*/

    /**
     * handles resource production AW
     *
     * @param diceSum - dice sum
     */
    void produceResources(int diceSum) {
        Log.d(TAG, "produceResources() called with: diceSum = [" + diceSum + "]");

        //Make sure it is not the action phase
        if (isActionPhase) {
            Log.e(TAG, "produceResources: It is the action phase. Returned false.");
            return;
        }

        //Make sure it is not the setup phase
        if (isSetupPhase) {
            Log.e(TAG, "produceResources: not producing any resources since it is the setup phase.");
            return;
        }

        //Get Hexagons with chit values matching the dice value rolled
        ArrayList<Integer> productionHexagonIds = board.getHexagonsFromChitValue(diceSum);
        Log.i(TAG, "produceResources: Hexagons with chit value " + diceSum + ": " + productionHexagonIds.toString());

        //Iterate through the Hexagons
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

    /**
     * sets if the robberPhase is true or false
     *
     * @param rp true or false
     */
    public void setRobberPhase(boolean rp) {
        this.isRobberPhase = rp;
    }

    /**
     * @return true or false for if the robberPhase is happening
     */
    public boolean getRobberPhase() {
        return isRobberPhase;
    }

    /**
     * Check to see if resources need to be discarded
     *
     * @return - action success
     */
    public boolean checkIfPlayerHasDiscarded(int playerId) {
        if (robberPlayerListHasDiscarded[playerId]) {
            //Returns false since player has already discarded cards
            Log.i(TAG, "checkIfPlayerHasDiscarded: PLAYER HAS DISCARDED ALREADY playerId=" + playerId);
            return true;
        }
        if (playerList.get(playerId).getTotalResourceCardCount() > 7) {
            //Returns true since player has more than 7 cards and has not discarded yet
            Log.i(TAG, "checkIfPlayerHasDiscarded: PLAYER NEEDS TO DISCARD CARDS playerId=" + playerId);
            return true;
        }
        Log.i(TAG, "checkIfPlayerHasDiscarded: PLAYER DOES NOT NEED TO DISCARD playerId=" + playerId);
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
    public boolean validDiscard(int playerId, int[] resourcesDiscarded) {
        int totalDiscarded = 0;
        Log.i(TAG, "discardResources: Amount is " + totalDiscarded + ", Need: " + playerList.get(playerId).getTotalResourceCardCount() / 2);

        //Make sure player has the correct amount of resources; iterate through and add to total
        for (int i = 0; i < resourcesDiscarded.length; i++) {
            if (resourcesDiscarded[i] > playerList.get(playerId).getResourceCards()[i]) {
                Log.i(TAG, "validDiscard: Invalid due to not having enough resources, returning false");
                return false;
            }
            totalDiscarded += resourcesDiscarded[i];
        }

        //Check if total discarded is the required amount (half their resources rounded down)
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
    public boolean discardResources(int playerId, int[] resourcesDiscarded) {
        Log.w(TAG, "discardResources: " + this.getCurrentPlayer().printResourceCards());

        //If they've already discarded, no need to discard, return true
        if (robberPlayerListHasDiscarded[playerId]) {
            Log.i(TAG, "discardResources: Player is not required to discard at this time");
            return true;
        }

        Log.i(TAG, "discardResources: Discarded resources");
        for (int i = 0; i < resourcesDiscarded.length; i++) {
            this.playerList.get(playerId).removeResourceCard(i, resourcesDiscarded[i]);
        }

        robberPlayerListHasDiscarded[playerId] = true;
        return true;
    }

    /**
     * for everyplayer in the game, checks if they need to discards cards
     *
     * @return true or false
     */
    public boolean allPlayersHaveDiscarded() {
        for (boolean aRobberPlayerListHasDiscarded : robberPlayerListHasDiscarded) {
            if (!aRobberPlayerListHasDiscarded) {
                return false;
            }
        }
        Log.i(TAG, "Removed half of all resources from players with more than 7 cards\n");
        return true;
    }


    /**
     * find which player is doing the best in terms of victory points
     *
     * @param excludedPlayerId the player whose turn it is so they can be excluded
     * @return
     */
    public int getPlayerWithMostVPsExcludingCurrentPlayer(int excludedPlayerId) {

        //Default Value so we can set the first player ID as player in lead for comparisons
        int playerInLead = -1;
        for (Player player : this.getPlayerList()) {

            //Make sure we are not including the player we are excluding
            if (player.getPlayerId() != excludedPlayerId) {

                //Default player to start with
                if (playerInLead == -1) {
                    playerInLead = player.getPlayerId();
                }

                //Compare and change player with most victory points if needed
                else {
                    if (this.getPlayerList().get(playerInLead).getVictoryPoints() < this.getPlayerList().get(player.getPlayerId()).getVictoryPoints()) {
                        playerInLead = player.getPlayerId();
                    }
                }
            }
        }

        return playerInLead;
    }

    /**
     * If the player has rolled a 7, player will move the robber to another Hexagon that has settlements nearby
     *
     * @param hexagonId Hexagon the robber is going to move to.
     * @param playerId  Player who is moving the robber.
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
        if (board.moveRobber(hexagonId)) {
            Log.i(TAG, "moveRobber: Player " + playerId + " moved the Robber to Hexagon " + hexagonId);
            hasMovedRobber = true;
            return true;
        }
        Log.i(TAG, "moveRobber: Player " + playerId + "  cannot move the Robber to Hexagon " + hexagonId);

        playerStealingFrom = playerId;
        return false;
    }

    /**
     * Getter to see who is getting their resources taken
     *
     * @return
     */
    public int getPlayerStealingFrom() {
        return playerStealingFrom;
    }

    /**
     * After the player has moved the Robber, the player will choose a player to steal from and receive a random card from their hand
     *
     * @param playerId - player stealing resources
     * @return - action success
     */
    public boolean robberSteal(int playerId, int stealingFromPlayerId) {
        if (playerId == stealingFromPlayerId) {
            Log.e(TAG, "robberSteal: Trying to steal from self, error.");
            return false;
        }
        if (playerId < 0 || playerId > 3 || stealingFromPlayerId < 0 || stealingFromPlayerId > 3) {
            return false;
        }

        int randomStolenResourceId = this.playerList.get(stealingFromPlayerId).getRandomCard();

        if (randomStolenResourceId == -1) {
            isRobberPhase = false;
            hasMovedRobber = false;
            // once they steal it is the end of the robber phase, so reset this array to false
            for (int i = 0; i < robberPlayerListHasDiscarded.length; i++) {
                robberPlayerListHasDiscarded[i] = false;
            }
            return true;
        }

        if (randomStolenResourceId < 0 || randomStolenResourceId > 4) {
            Log.e(TAG, "robberSteal: Received invalid resource card id: " + randomStolenResourceId + " from Player.getRandomCard method.");
            return false;
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
    public boolean updateSetupPhase() {
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

    /*-------------------------------------Largest Army methods------------------------------------------*/



    /*-------------------------------------Getter/Setter Methods------------------------------------------*/

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
        return this.currentLongestRoadPlayerId;
    }

    public void setCurrentLongestRoadPlayerId(int currentLongestRoadPlayerId) {
        this.currentLongestRoadPlayerId = currentLongestRoadPlayerId;
    }

    public boolean isSetupPhase() {
        return isSetupPhase;
    }

    public void setSetupPhase(boolean setupPhase) {
        isSetupPhase = setupPhase;
    }

    public boolean isRobberPhase() {
        return isRobberPhase;
    }

    public boolean getHasMovedRobber() {
        return hasMovedRobber;
    }

    public boolean[] getRobberPlayerListHasDiscarded() {
        return robberPlayerListHasDiscarded;
    }

    public void setRobberPlayerListHasDiscarded(boolean[] robberPlayerListHasDiscarded) {
        this.robberPlayerListHasDiscarded = robberPlayerListHasDiscarded;
    }

    public boolean isHasMovedRobber() {
        return hasMovedRobber;
    }

    public void setHasMovedRobber(boolean hasMovedRobber) {
        this.hasMovedRobber = hasMovedRobber;
    }

    public int[] getRobberDiscardedResources() {
        return robberDiscardedResources;
    }


    public int getSetupPhaseTurnCounter() {
        return setupPhaseTurnCounter;
    }

    public void setSetupPhaseTurnCounter(int setupPhaseTurnCounter) {
        this.setupPhaseTurnCounter = setupPhaseTurnCounter;
    }

    /*------------------------------------- toString ------------------------------------------*/

    /**
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(" ----------- CatanGameState toString ---------- \n");
        result.append(this.dice.toString());
        result.append("current Player: ").append(currentPlayerId).append(", ");
        result.append("diceVal: ").append(this.currentDiceSum).append(", ");
        result.append("actionPhase: ").append(isActionPhase).append(", ");
        result.append("setupPhase: ").append(isSetupPhase).append(", ");
        result.append("robberPhase: ").append(isRobberPhase).append(", ");
        result.append("hasMovedRobber: ").append(hasMovedRobber).append(", ");
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
