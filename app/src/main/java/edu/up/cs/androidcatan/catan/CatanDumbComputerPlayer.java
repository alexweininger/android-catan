package edu.up.cs.androidcatan.catan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberDiscardAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberMoveAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberStealAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
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
 * @version November 9th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanDumbComputerPlayer extends GameComputerPlayer {
    private static final String TAG = "CatanDumbComputerPlayer";

    private int[] robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};
    int hexId;

    /**
     * callback method--game's state has changed
     *
     * @param info the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo (GameInfo info) {
        Log.i(TAG, "receiveInfo() of player " + this.playerNum + " called.");

        if (!(info instanceof CatanGameState)) return; // must do this check at start of method!

        CatanGameState gs = (CatanGameState) info;
        Log.d(TAG, "receiveInfo: game state current player: " + gs.getCurrentPlayerId() + " this.playerNum: " + this.playerNum);

        Random random = new Random();

        /*------------------------------------CPUs Setup Phase Actions-----------------------------------------*/
        if (gs.isSetupPhase() && this.playerNum == gs.getCurrentPlayerId()) {
            Log.d(TAG, "receiveInfo: It is the setup phase. Computer player will now attempt to build a settlement and a road." + " " + this.playerNum);
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
            game.sendAction(new CatanBuildSettlementAction(this, true, this.playerNum, randSettlementIntersection)); // sending build settlement action

            // get adjacent intersections to what we just built
            ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getIntersectionGraph().get(randSettlementIntersection);

            Log.d(TAG, "receiveInfo: intersectionsToChooseFrom: " + intersectionsToChooseFrom + " " + this.playerNum);

            // choose a random intersection from those intersections
            int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
            int count = 0;
            // generate random intersection until we find a valid location to build our settlement
            while (!gs.getBoard().validRoadPlacement(this.playerNum, true, randSettlementIntersection, intersectionsToChooseFrom.get(randomRoadIntersection))) {
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
            game.sendAction(new CatanBuildRoadAction(this, true, this.playerNum, randSettlementIntersection, intersectionsToChooseFrom.get(randomRoadIntersection)));
            Log.d(TAG, "receiveInfo() returned: void");

            Log.d(TAG, "receiveInfo: Ending turn during the setup phase after building 1 road and 1 settlement" + " " + this.playerNum);
            game.sendAction(new CatanEndTurnAction(this));
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
            Building building = null;
            int action = random.nextInt(4);
            if (action == 0) //build  City
            {
                if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(City.resourceCost)) {
                    Log.d(TAG, "receiveInfo: Valid amount of resources to build city");
                    for (int n = 0; n < gs.getBoard().getBuildings().length; n++) {
                        if (gs.getBoard().getBuildings()[n] == null) {
                            Log.d(TAG, "receiveInfo: Nothing at this location on board");
                        }
                        else if (gs.getBoard().getBuildings()[n].getOwnerId() == this.playerNum) {
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
                if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(Settlement.resourceCost)){
                    Log.d(TAG, "receiveInfo: Valid amount of resources to building");
                    for (int n = 0; n < getPlayerRoadIntersection(getPlayerRoads(gs)).size(); n++){
                        if (gs.getBoard().validBuildingLocation(this.playerNum, false, n)){
                            Log.d(TAG, "receiveInfo: validBuildingLocation for a settlement");
                            game.sendAction(new CatanBuildSettlementAction(this, false, this.playerNum, n));
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

                    int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
                    for (int n = 0; n < intersectionsToChooseFrom.size(); n++){
                        if (gs.getBoard().validRoadPlacement(this.playerNum, false, roadCoordinate, intersectionsToChooseFrom.get(n))){
                            game.sendAction(new CatanBuildRoadAction(this, false, this.playerNum, roadCoordinate, intersectionsToChooseFrom.get(randomRoadIntersection)));
                            Log.d(TAG, "receiveInfo: CatanBuildRoadAction sent");

                            game.sendAction(new CatanEndTurnAction(this));

                            Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                            return;
                        }
                    }
                    Log.d(TAG, "receiveInfo: Problem with building a road");
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
                if (!gs.checkPlayerResources(playerNum)) {
                    Log.i(TAG, "receiveInfo: Computer " + playerNum + " does not need to discard, but still needs to send action.");
                    game.sendAction(new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard));
                    return;
                }

                //2b. Player needs to discard cards; player will randomly choose resources until half of their cards have been discarded
                else {
                    int randomResource = random.nextInt(5);
                    robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};

                    //3. Loop until computer has chosen enough cards to discard
                    while(!gs.validDiscard(playerNum, robberResourcesDiscard)){
                        if(robberResourcesDiscard[randomResource] >= gs.getPlayerList().get(playerNum).getResourceCards()[randomResource]){
                            robberResourcesDiscard[playerNum]++;
                        }
                        else{
                            randomResource = random.nextInt(5);
                        }
                    }

                    //4. Send discard action
                    Log.i(TAG, "receiveInfo: Computer is now discarding resources");
                    CatanRobberDiscardAction action = new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard);
                    game.sendAction(action);

//                    for (int i = 0; i < gs.getPlayerList().get(playerNum).getResourceCards().length; i++) {
//                        for (int j = 0; j < gs.getPlayerList().get(playerNum).getResourceCards()[i]; j++) {
//                            robberResourcesDiscard[i]++;
//                            Log.i(TAG, "receiveInfo: Player " + playerNum + " is discarding resources: Wanted- " + robberResourcesDiscard[i] + ", Actual- " + gs.getPlayerList().get(playerNum).getResourceCards()[i]);
//                            if (gs.validDiscard(playerNum, robberResourcesDiscard)) {
//                                Log.i(TAG, "receiveInfo: Computer is now discarding resources");
//                                CatanRobberDiscardAction action = new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard);
//                                game.sendAction(action);
//                                break;
//                            }
//                        }
//                        if (gs.getRobberPlayerListHasDiscarded()[playerNum]) {
//                            Log.i(TAG, "receiveInfo: Computer discarded cards!");
//                            break;
//                        }
//                    }
                }
                Log.i(TAG, "receiveInfo: Player is ending the discard phase!");
                return;
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
                Log.i(TAG, "receiveInfo: Computer is moving robber");

                //7. Check if player has move robber; if not move to random, VALID hexagon
                if (!gs.getHasMovedRobber()) {
                    Log.i(TAG, "receiveInfo: Computer Player hasMovedRobber: " + gs.getHasMovedRobber());
                    Log.i(TAG, "receiveInfo: Computer is moving the robber");
                    sleep(2000);

                    //8. Choose a random hex, then loop until valid
                    hexId = random.nextInt(gs.getBoard().getHexagons().size());
                    while(!tryMoveRobber(hexId, gs)){
                        hexId = random.nextInt(gs.getBoard().getHexagons().size());
                    }

                    //9. Send action to move the robber
                    Log.d(TAG, "receiveInfo: Computer is placing robber on hex " + hexId);
                    sleep(2000);
                    CatanRobberMoveAction action = new CatanRobberMoveAction(this, playerNum, hexId);
                    game.sendAction(action);
                    return;
                }

                /*----------------Steal Resource Phase--------------*/

                //10. Computer chooses a random intersection to steal from
                sleep(500);
                // get adjacent intersections around the hexagon
                ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);

                int randomIntersectionIdx = random.nextInt(intersections.size());
                int intersectionId = intersections.get(randomIntersectionIdx);

                while(!gs.getBoard().hasBuilding(intersectionId) || gs.getBoard().getBuildingAtIntersection(intersectionId).getOwnerId() == playerNum){
                    randomIntersectionIdx = random.nextInt(intersections.size());
                    intersectionId = intersections.get(randomIntersectionIdx);
                }

                //11. Valid intersection found, steal from this player
                Log.i(TAG, "receiveInfo: Computer is now stealing from player " + gs.getBoard().getBuildingAtIntersection(intersectionId).getOwnerId());
                // send CatanRobberStealAction to the game
                game.sendAction(new CatanRobberStealAction(this, this.playerNum, gs.getBoard().getBuildingAtIntersection(intersectionId).getOwnerId()));
                return;
            }
        }

        /* ----------------------------------- CPUs Normal Action Phase ------------------------------------ */
        if (!gs.isRobberPhase() && this.playerNum == gs.getCurrentPlayerId()) {
            Log.e(TAG, "receiveInfo: returning a CatanEndTurnAction");
            game.sendAction(new CatanEndTurnAction(this));
        }

        // not setup phase if statement END

    }// receiveInfo() END

    CatanDumbComputerPlayer (String name) {
        super(name);
    }

    private boolean tryMoveRobber (int hexId, CatanGameState gs) {

        if (hexId == -1) {
            Log.d(TAG, "tryMoveRobber: Invalid hex ID from CPU");
            return false;
        }

        if (hexId == gs.getBoard().getRobber().getHexagonId()) {
            Log.d(TAG, "tryMoveRobber: Same hexId as robber");
            return false;
        }
        if(gs.getBoard().getHexagons().get(hexId).getResourceId() == 5){
            Log.d(TAG, "tryMoveRobber: Desert tile selected; invalid.");
            return false;
        }

        ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);

        for (Integer intersection : intersections) {
            if (gs.getBoard().getBuildings()[intersection] != null) {
                if (gs.getBoard().getBuildings()[intersection].getOwnerId() != playerNum) {
                    return true;
                }
            }
        }
        Log.d(TAG, "tryMoveRobber: ");
        return false;
    }

    private ArrayList<Road> getPlayerRoads (CatanGameState gs) {
        ArrayList<Road> playerRoads = new ArrayList<>();
        for (int n = 0; n < gs.getBoard().getRoads().size(); n++) {
            if (gs.getBoard().getRoads().get(n).getOwnerId() == this.playerNum) {
                playerRoads.add(gs.getBoard().getRoads().get(n));
            }
        }
        return playerRoads;
    }

    private ArrayList<Integer> getPlayerRoadIntersection (ArrayList<Road> playerRoads) {
        ArrayList<Integer> intersections = new ArrayList<>();
        for (int n = 0; n < playerRoads.size(); n++) {
            intersections.add(playerRoads.get(n).getIntersectionAId());
            intersections.add(playerRoads.get(n).getIntersectionBId());
        }
        ArrayList<Integer> noRepeatIntersections = new ArrayList<>();
        for (int n = 0; n < intersections.size(); n++) {
            for (int j = n + 1; j < intersections.size(); j++) {
                if (intersections.get(n) != intersections.get(j)) {
                    noRepeatIntersections.add(n);
                }
            }
            Log.d(TAG, "With repeat Intersections: " + intersections.toString());
            Log.d(TAG, "No repeat Intersections: " + noRepeatIntersections.toString());
        }
        return intersections;
    }
} // CatanDumbComputerPlayer class END
