package edu.up.cs.androidcatan.catan;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberDiscardAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberMoveAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberStealAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;
import edu.up.cs.androidcatan.game.GameComputerPlayer;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/
public class CatanDumbComputerPlayer extends GameComputerPlayer implements Serializable {
    private static final String TAG = "CatanDumbComputerPlayer";

    private int lastSettlementIntersectionId = -1;
    private int[] robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};
    private int hexId;
    ArrayList<Integer> buildingsBuiltOnThisTurn = new ArrayList<>();
    private boolean movedRobber;

    /**
     * callback method--game's state has changed
     *
     * @param info the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        Log.i(TAG, "receiveInfo() of player " + this.playerNum + " called.");

        if (!(info instanceof CatanGameState)) return; // must do this check at start of method!

        //creates a new game state object
        CatanGameState gs = (CatanGameState) info;
        Log.d(TAG, "receiveInfo: game state current player: " + gs.getCurrentPlayerId() + " this.playerNum: " + this.playerNum);
        if (this.playerNum != gs.getCurrentPlayerId() && !gs.isRobberPhase()) {
            Log.w(TAG, "receiveInfo: not my turn and not the robber phase, returning playerNum=" + this.playerNum + " current player=" + gs.getCurrentPlayerId());
            return;
        }
        int settlementCount = 0;
        int roadCount = 0;

        // Get the number of settlements the player has built.
        for (Building building : gs.getBoard().getBuildings()) {
            if (building != null && building.getOwnerId() == this.playerNum) {
                settlementCount++;
            }
        }

        // Get the number of roads the player has built.
        for (Road road : gs.getBoard().getRoads()) {
            if (road.getOwnerId() == this.playerNum) {
                roadCount++;
            }
        }

        Log.i(TAG, "receiveInfo: roadCount: " + roadCount + " settlementCount: " + settlementCount);

        Random random = new Random();

        /*------------------------------------CPUs Setup Phase Actions-----------------------------------------*/

        // if it is the setup phase and the players turn
        if (gs.isSetupPhase() && this.playerNum == gs.getCurrentPlayerId()) {
            if (roadCount + settlementCount >= 8) {
                Log.e(TAG, "receiveInfo: It is the setup phase, but player has already built 4 things. Ending turn.");
                Log.i(TAG, "receiveInfo: sending CatanEndTurnAction to the game. playerNum=" + this.playerNum);
                buildingsBuiltOnThisTurn.clear();
                lastSettlementIntersectionId = -1;
                game.sendAction(new CatanEndTurnAction(this));
                return;
            }
            if (buildingsBuiltOnThisTurn.size() > 1) {
                Log.i(TAG, "receiveInfo: built 2 or more things ending turn.");
                game.sendAction(new CatanEndTurnAction(this));
                buildingsBuiltOnThisTurn.clear();
                lastSettlementIntersectionId = -1;
                return;
            }
            Log.d(TAG, "receiveInfo: It is the setup phase. Computer player will now attempt to build a settlement or a road." + " " + this.playerNum);

            // if they have not built a settlement on this turn
            if (!buildingsBuiltOnThisTurn.contains(1)) {
                Log.i(TAG, "receiveInfo: Player has not built a settlement, will attempt now. playerNum=" + this.playerNum);
                sleep(1000);
                int randSettlementIntersection = random.nextInt(53);
                // generate random intersection until we find a valid location to build our settlement
                while (!gs.getBoard().validBuildingLocation(this.playerNum, true, randSettlementIntersection)) {
                    sleep(1000); // sleep
                    Log.d(TAG, "receiveInfo: generating new building location" + " " + this.playerNum);
                    randSettlementIntersection = random.nextInt(53);
                }
                Log.e(TAG, "receiveInfo: exiting settlement look for " + this.playerNum);

                // send the build settlement action to the game
                Log.d(TAG, "receiveInfo: sending a CatanBuildSettlementAction to the game with intersection id: " + randSettlementIntersection + " " + this.playerNum);
                game.sendAction(new CatanBuildSettlementAction(this, gs.isSetupPhase(), this.playerNum, randSettlementIntersection)); // sending build settlement action
                this.lastSettlementIntersectionId = randSettlementIntersection;
                buildingsBuiltOnThisTurn.add(1);
                return;
                // else if the player has not built a road on this turn
            } else if (!buildingsBuiltOnThisTurn.contains(0)) {
                Log.i(TAG, "receiveInfo: Player has not built a road, will attempt now. playerNum=" + this.playerNum);
                // get adjacent intersections to what we just built
                ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getIntersectionGraph().get(lastSettlementIntersectionId);

                Log.d(TAG, "receiveInfo: intersectionsToChooseFrom: " + intersectionsToChooseFrom + " " + this.playerNum);

                // choose a random intersection from those intersections
                int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
                int count = 0;
                // generate random intersection until we find a valid location to build our settlement
                while (!gs.getBoard().validRoadPlacement(this.playerNum, gs.isSetupPhase(), lastSettlementIntersectionId, intersectionsToChooseFrom.get(randomRoadIntersection))) {
                    if (count > 5) {
                        Log.e(TAG, "receiveInfo: CANNOT PLACE ROAD" + " " + this.playerNum);
                        break;
                    }
                    Log.e(TAG, "receiveInfo: generating new road intersection" + " " + this.playerNum);
                    sleep(1000); // sleep
                    randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
                    count++;
                }

                sleep(2000); // sleep
                // send the game a build road action
                Log.i(TAG, "receiveInfo: sending a CatanBuildRoadAction to the game." + " " + this.playerNum);
                game.sendAction(new CatanBuildRoadAction(this, gs.isSetupPhase(), this.playerNum, lastSettlementIntersectionId, intersectionsToChooseFrom.get(randomRoadIntersection)));
                buildingsBuiltOnThisTurn.add(0);
                Log.i(TAG, "receiveInfo() returned: void");
                return;
            }
            Log.d(TAG, "receiveInfo: Ending turn during the setup phase after building 1 road and 1 settlement " + this.playerNum);
            game.sendAction(new CatanEndTurnAction(this));
            buildingsBuiltOnThisTurn.clear();
            lastSettlementIntersectionId = -1;
            return;
        } // setup phase if statement END

        /*------------------------------Setup Phase End------------------------------------------*/


        /*-------------------------------CPUs Roll Dice Action--------------------------------------*/
        if (!gs.isSetupPhase() && !gs.isActionPhase() && gs.getCurrentPlayerId() == playerNum) {
            sleep(1000);
            Log.i(TAG, "receiveInfo: RollDiceAction by DumbComputerPlayer " + this.playerNum);
            game.sendAction(new CatanRollDiceAction(this));
            sleep(500);
            return;
        }

        /*----------------------------------Build Actions------------------------------------------*/
        if (!gs.isSetupPhase() && gs.isActionPhase() && gs.getCurrentPlayerId() == this.playerNum && !gs.isRobberPhase()) {
            sleep(1000);
            Building building;
            int action = random.nextInt(4);
            if (action == 0) //build  City
            {
                if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(City.resourceCost)) {
                    Log.d(TAG, "receiveInfo: Valid amount of resources to build city");
                    for (int n = 0; n < gs.getBoard().getBuildings().length; n++) {
                        if (gs.getBoard().getBuildings()[n] == null) {
                            Log.d(TAG, "receiveInfo: Nothing at this location on board");
                        } else if (gs.getBoard().getBuildings()[n].getOwnerId() == this.playerNum) {
                            Log.d(TAG, "receiveInfo: valid owner id");
                            building = gs.getBoard().getBuildings()[n];
                            if (building instanceof Settlement) {
                                game.sendAction(new CatanBuildCityAction(this, false, this.playerNum, n));
                                Log.d(TAG, "receiveInfo: CatanBuildCityAction sent");
                                game.sendAction(new CatanEndTurnAction(this));
                                Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                                return;
                            }
                        }
                    }
                }
            } else if (action == 1) //build a settlement
            {
                Log.d(TAG, "Dumb AI randomly tried to build a settlement");
                if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(Settlement.resourceCost)) {
                    for (int n = 0; n < getPlayerRoadIntersection(getPlayerRoads(gs)).size(); n++) {
                        //cycling through the amount, not the proper value at the intersection
                        if (gs.getBoard().validBuildingLocation(this.playerNum, false, getPlayerRoadIntersection(getPlayerRoads(gs)).get(n))) {
                            Log.d(TAG, "receiveInfo: validBuildingLocation for a settlement");
                            game.sendAction(new CatanBuildSettlementAction(this, false, this.playerNum, getPlayerRoadIntersection(getPlayerRoads(gs)).get(n)));
                            Log.d(TAG, "receiveInfo: CatanBuildSettlementAction sent");
                            game.sendAction(new CatanEndTurnAction(this));
                            Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                            return;
                        }
                    }
                }

            } else if (action == 2)// build a Road
            {
                Log.d(TAG, "Dumb AI randomly tried to build a road");
                if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(Road.resourceCost)) {

                    // get road endpoints for players roads
                    ArrayList<Integer> individualRoads = getPlayerRoadIntersection(getPlayerRoads(gs));
                    int randIntersection = random.nextInt(individualRoads.size());

                    // get random intersection from those road intersections
                    int roadCoordinate = individualRoads.get(randIntersection);

                    // get all adjacent intersections
                    ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getIntersectionGraph().get(roadCoordinate);
                    Log.d(TAG, "IntersectionsToChooseFrom for coordinate: " + roadCoordinate + " for the following cords: " + intersectionsToChooseFrom.toString());

                    //int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
                    for (int n = 0; n < intersectionsToChooseFrom.size(); n++) {
                        if (gs.getBoard().validRoadPlacement(this.playerNum, false, roadCoordinate, intersectionsToChooseFrom.get(n))) {
                            game.sendAction(new CatanBuildRoadAction(this, false, this.playerNum, roadCoordinate, intersectionsToChooseFrom.get(n)));
                            //was random road intersection
                            Log.d(TAG, "receiveInfo: CatanBuildRoadAction sent");

                            game.sendAction(new CatanEndTurnAction(this));

                            Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                            return;
                        }
                    }
                    Log.d(TAG, "receiveInfo: Problem with building a road");
                    game.sendAction(new CatanEndTurnAction(this));
                    return;
                }
            } else {
                Log.d(TAG, "Dumb AI randomly chose to do nothing");
            }
        }


        /*------------------------------Build Actions End------------------------------------------*/

        /*-------------------------------CPUs Robber Actions--------------------------------------*/
        if (gs.isRobberPhase()) {
            Log.i(TAG, "receiveInfo: Computer has reached the Robber Phase");
            sleep(500);
            /*--------------------Discard Phase--------------------*/

            //1. Check if this player has discarded
            if (!gs.getRobberPlayerListHasDiscarded()[playerNum]) {
                Log.i(TAG, "receiveInfo: Computer player " + playerNum + " needs to discard!!!");

                //2a. Check if the player needs to discard cards; if not, send discard action with empty resource list;
                //   GameState will handle logic.
                if (!gs.checkIfPlayerHasDiscarded(playerNum)) {
                    Log.i(TAG, "receiveInfo: Computer " + playerNum + " does not need to discard, but still needs to send action.");
                    game.sendAction(new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard));
                    return;
                }

                //2b. Player needs to discard cards; player will randomly choose resources until half of their cards have been discarded
                else {
                    int randomResource = random.nextInt(5);
                    robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};

                    //3. Loop until computer has chosen enough cards to discard
                    while (!gs.validDiscard(playerNum, robberResourcesDiscard)) {
                        if (robberResourcesDiscard[randomResource] < gs.getPlayerList().get(this.playerNum).getResourceCards()[randomResource]) {
                            robberResourcesDiscard[randomResource]++;
                        }

                        randomResource = random.nextInt(5);
                    }

                    //4. Send discard action
                    Log.i(TAG, "receiveInfo: Computer is now discarding resources");
                    CatanRobberDiscardAction action = new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard);
                    game.sendAction(action);
                    Log.i(TAG, "receiveInfo: Player is ending the discard phase!");
                    return;
                }
            }

            //5. Wait until all players have completed their discard phase
            if (!gs.allPlayersHaveDiscarded() && gs.getCurrentPlayerId() == playerNum) {
                Log.d(TAG, "receiveInfo: Not all players have discarded!!!!");
                return;
            }

            Log.i(TAG, "receiveInfo: Robber Phase --> Move Robber Phase");

            /*----------------------Move Robber Phase----------------*/

            //6. If it is this players turn, continue to rest of robber phase; otherwise player is done
            if (gs.getCurrentPlayerId() == playerNum) {
                Log.i(TAG, "receiveInfo: Computer is moving robber playerNum=" + playerNum);

                //7. Check if player has move robber; if not move to random, VALID hexagon
                if (!gs.getHasMovedRobber()) {
                    Log.i(TAG, "receiveInfo: Computer Player playerId=" + playerNum + " hasMovedRobber: " + gs.getHasMovedRobber());
                    Log.i(TAG, "receiveInfo: Computer is moving the robber");
                    sleep(2000);

                    //8. Choose a random hex, then loop until valid
                    hexId = random.nextInt(gs.getBoard().getHexagons().size());
                    while (!tryMoveRobber(hexId, gs)) {
                        hexId = random.nextInt(gs.getBoard().getHexagons().size());
                    }
                    return;
                } else {
                    Log.i(TAG, "receiveInfo: gs.hasMovedRobber = " + true);
                }

                /*----------------Steal Resource Phase--------------*/
                Log.d(TAG, "receiveInfo: player has reached the stealing phase playerId=" + playerNum);
                //10. Computer chooses a random intersection to steal from
                sleep(500);
                // get adjacent intersections around the hexagon
                ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);

                int randomIntersectionIdx = random.nextInt(intersections.size());
                int intersectionId = intersections.get(randomIntersectionIdx);

                while (!gs.getBoard().hasBuilding(intersectionId) || gs.getBoard().getBuildingAtIntersection(intersectionId).getOwnerId() == playerNum) {
                    randomIntersectionIdx = random.nextInt(intersections.size());
                    intersectionId = intersections.get(randomIntersectionIdx);
                }

                //11. Valid intersection found, steal from this player
                Log.i(TAG, "receiveInfo: Computer is now stealing from player " + gs.getBoard().getBuildingAtIntersection(intersectionId).getOwnerId());
                // send CatanRobberStealAction to the game
                game.sendAction(new CatanRobberStealAction(this, this.playerNum, gs.getBoard().getBuildingAtIntersection(intersectionId).getOwnerId()));
                return;
            } else {
                Log.d(TAG, "receiveInfo: it is the robber phase and not my turn playerId=" + playerNum);
            }
        }

        /*----------------------------------Build Actions------------------------------------------*/
        if (!gs.isSetupPhase() && gs.isActionPhase() && gs.getCurrentPlayerId() == this.playerNum && !gs.isRobberPhase()) {
            sleep(1000);
            Building building;
            int action = random.nextInt(4);
            if (action == 0) {
                if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(City.resourceCost)) {
                    Log.d(TAG, "receiveInfo: Valid amount of resources to build city");
                    for (int n = 0; n < gs.getBoard().getBuildings().length; n++) {
                        if (gs.getBoard().getBuildings()[n] == null) {
                            Log.d(TAG, "receiveInfo: Nothing at this location on board");
                        } else if (gs.getBoard().getBuildings()[n].getOwnerId() == this.playerNum) {
                            Log.d(TAG, "receiveInfo: valid owner id");
                            building = gs.getBoard().getBuildings()[n];
                            if (building instanceof Settlement) {
                                game.sendAction(new CatanBuildCityAction(this, false, this.playerNum, n));
                                Log.d(TAG, "receiveInfo: CatanBuildCityAction sent");
                                return;
                            }
                        }
                    }
                }
            } else if (action == 1) {
                Log.d(TAG, "Dumb AI randomly tried to build a settlement");
                if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(Settlement.resourceCost)) {
                    Log.d(TAG, "receiveInfo: Valid amount of resources to building");
                    for (int n = 0; n < getPlayerRoadIntersection(getPlayerRoads(gs)).size(); n++) {
                        if (gs.getBoard().validBuildingLocation(this.playerNum, false, n)) {
                            Log.d(TAG, "receiveInfo: validBuildingLocation for a settlement");
                            game.sendAction(new CatanBuildSettlementAction(this, false, this.playerNum, n));
                            Log.d(TAG, "receiveInfo: CatanBuildSettlementAction sent");
                        }
                    }
                }

            } else if (action == 2) {
                Log.d(TAG, "Dumb AI randomly tried to build a road");
                if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(Road.resourceCost)) {

                    // get road endpoints for players roads
                    ArrayList<Integer> individualRoads = getPlayerRoadIntersection(getPlayerRoads(gs));
                    int randIntersection = random.nextInt(individualRoads.size());

                    // get random intersection from those road intersections
                    int roadCoordinate = individualRoads.get(randIntersection);

                    // get all adjacent intersections
                    ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getIntersectionGraph().get(roadCoordinate);

                    int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
                    for (int n = 0; n < intersectionsToChooseFrom.size(); n++) {
                        if (gs.getBoard().validRoadPlacement(this.playerNum, false, roadCoordinate, intersectionsToChooseFrom.get(n))) {
                            game.sendAction(new CatanBuildRoadAction(this, false, this.playerNum, roadCoordinate, intersectionsToChooseFrom.get(randomRoadIntersection)));
                            Log.d(TAG, "receiveInfo: CatanBuildRoadAction sent");
                            return;
                        }
                    }
                    Log.d(TAG, "receiveInfo: Problem with building a road");
                }
            } else {
                Log.d(TAG, "Dumb AI randomly chose to do nothing");
                game.sendAction(new CatanEndTurnAction(this));
                Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                return;
            }
        }

        /*------------------------------Build Actions End------------------------------------------*/


        /* ----------------------------------- CPUs Normal Action Phase ------------------------------------ */
        if (!gs.isRobberPhase() && this.playerNum == gs.getCurrentPlayerId()) {
            Log.e(TAG, "receiveInfo: returning a CatanEndTurnAction because it is not the robber phase and it is my turn playerId=" + playerNum);
            game.sendAction(new CatanEndTurnAction(this));
            return;
        }
    }// receiveInfo() END

    /**
     * Constructor for the computer player
     *
     * @param name name of the player to be used on the soreboard anf whenever the player is referenced
     */
    CatanDumbComputerPlayer(String name) {
        super(name);
    }

    /**
     * The method that attempts to move the robber
     *
     * @param hexId the ID of the tile to move attempt moving the robber to
     * @param gs    the current game state
     * @return either true or false depending if moving the robber was valid
     */
    private boolean tryMoveRobber(int hexId, CatanGameState gs) {
        Log.d(TAG, "tryMoveRobber() called with: hexId = [" + hexId + "], gs = [" + gs + "]");
        if (gs.getHasMovedRobber()) {
            Log.d(TAG, "tryMoveRobber() returned: " + false + " because the robber has already been moved.");
            return false;
        }
        if (hexId == -1) {
            Log.d(TAG, "tryMoveRobber: Invalid hex ID from CPU");
            return false;
        }

        if (hexId == gs.getBoard().getRobber().getHexagonId()) {
            Log.d(TAG, "tryMoveRobber: Same hexId as robber");
            return false;
        }
        if (gs.getBoard().getHexagons().get(hexId).getResourceId() == 5) {
            Log.d(TAG, "tryMoveRobber: Desert tile selected; invalid.");
            return false;
        }

        ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);

        for (Integer intersection : intersections) {
            if (gs.getBoard().getBuildings()[intersection] != null) {
                if (gs.getBoard().getBuildings()[intersection].getOwnerId() != playerNum) {
                    CatanRobberMoveAction action = new CatanRobberMoveAction(this, playerNum, hexId);
                    game.sendAction(action);
                    gs.setHasMovedRobber(true);
                    return true;
                }
            }
        }
        Log.d(TAG, "tryMoveRobber: returned " + false);
        return false;
    }

    /**
     * looks through the list of buildings that have been built then find which ones are own by
     * the player add adds them to an arrayList
     *
     * @param gs the current game state
     * @return arraylist of Road objects that the player owns
     */
    private ArrayList<Road> getPlayerRoads(CatanGameState gs) {
        ArrayList<Road> playerRoads = new ArrayList<>();
        for (int n = 0; n < gs.getBoard().getRoads().size(); n++) {
            if (gs.getBoard().getRoads().get(n).getOwnerId() == this.playerNum) {
                playerRoads.add(gs.getBoard().getRoads().get(n));
            }
        }
        return playerRoads;
    }

    /**
     * finds the intersections that the roads the player owns
     *
     * @param playerRoads arrayList of Road Objects that the player owns
     * @return and arrayList of Integers of the intersections that the roads the player owns are on
     */
    private ArrayList<Integer> getPlayerRoadIntersection(ArrayList<Road> playerRoads) {
        ArrayList<Integer> intersections = new ArrayList<>();
        for (int n = 0; n < playerRoads.size(); n++) {
            intersections.add(playerRoads.get(n).getIntersectionAId());
            intersections.add(playerRoads.get(n).getIntersectionBId());
        }
        ArrayList<Integer> noRepeatIntersections = new ArrayList<>();
        for (int n = 0; n < intersections.size(); n++) {
            for (int j = n + 1; j < intersections.size(); j++) {
                if (!Objects.equals(intersections.get(n), intersections.get(j))) {
                    noRepeatIntersections.add(n);
                }
            }
            Log.d(TAG, "With repeat Intersections: " + intersections.toString());
            Log.d(TAG, "No repeat Intersections: " + noRepeatIntersections.toString());
        }
        return intersections;
    }
} // CatanDumbComputerPlayer class END
