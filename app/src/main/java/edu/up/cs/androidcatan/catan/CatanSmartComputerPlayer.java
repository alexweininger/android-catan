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
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithBankAction;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;
import edu.up.cs.androidcatan.game.GameComputerPlayer;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;

public class CatanSmartComputerPlayer extends GameComputerPlayer  {
        private static final String TAG = "CatanSmartComputerPlayer";

        private int[] robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};
        private int hexId;
        int playerWithMostVPs;
        int playerWithMostVPsIntersection;
        boolean foundBuilding;

        CatanSmartComputerPlayer (String name) {
            super(name);
        }

        /**
         * callback method--game's state has changed
         *
         * @param info the information (presumably containing the game's state)
         */
        @Override
        protected void receiveInfo (GameInfo info) {
            Log.i(TAG, "receiveInfo() of player " + this.playerNum + " called.");

            if (!(info instanceof CatanGameState)) return;
            CatanGameState gs = (CatanGameState) info;
            Log.d(TAG, "receiveInfo: game state current player: " + gs.getCurrentPlayerId() + " this.playerNum: " + this.playerNum);

            Random random = new Random();

            Log.i(TAG, "receiveInfo() of player " + this.playerNum + " called.");

            /*------------------------------------CPUs Setup Phase Actions-----------------------------------------*/
            if (gs.isSetupPhase() && this.playerNum == gs.getCurrentPlayerId()) { //can take away this.playerNum
                Log.d(TAG, "receiveInfo: It is the setup phase. Computer player will now attempt to build a settlement and a road." + " " + this.playerNum);
                int randSettlementIntersection = random.nextInt(53);
                // generate random intersection until we find a valid location to build our settlement
                while (!(gs.getBoard().validBuildingLocation(this.playerNum, true, randSettlementIntersection) && checkIntersectionResource(randSettlementIntersection, gs))) {
                    sleep(1000); // sleep
                    Log.d(TAG, "receiveInfo: generating new building location" + " " + this.playerNum);
                    randSettlementIntersection = random.nextInt(53);
                }
                Log.e(TAG, "receiveInfo: exiting settlement look for " + this.playerNum);
                // send the build settlement action to the game
                Log.d(TAG, "receiveInfo: sending a CatanBuildSettlementAction to the game with intersection id: " +randSettlementIntersection + " " + this.playerNum);
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
                sleep(1000); // sleep
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
                sleep(300);
                game.sendAction(new CatanRollDiceAction(this));
                sleep(300);
                return;
            }
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
                        robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};
                        //3. Loop until computer has chosen enough cards to discard
                        for (int i = 0; i < gs.getPlayerList().get(playerNum).getResourceCards().length; i++) {
                            for (int j = 0; j < gs.getPlayerList().get(playerNum).getResourceCards()[i]; j++) {
                                robberResourcesDiscard[i]++;
                                Log.i(TAG, "receiveInfo: Player " + playerNum + " is discarding resources: Wanted- " + robberResourcesDiscard[i] + ", Actual- " + gs.getPlayerList().get(playerNum).getResourceCards()[i]);
                                if (gs.validDiscard(playerNum, robberResourcesDiscard)) {
                                    Log.i(TAG, "receiveInfo: Computer is now discarding resources");

                                    //4. Send discard action
                                    CatanRobberDiscardAction action = new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard);
                                    game.sendAction(action);
                                    break;
                                }
                            }
                            if (gs.getRobberPlayerListHasDiscarded()[playerNum]) {
                                Log.i(TAG, "receiveInfo: Computer discarded cards!");
                                break;
                            }
                        }
                    }
                    Log.i(TAG, "receiveInfo: Player is ending the discard phase!");
                    return;
                }//End of discard phase

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

                        //8.Check Player who has most victory points and get player ID (Cannot be this player)
                        playerWithMostVPs = gs.getPlayerWithMostVPsExcludingCurrentPlayer(playerNum);

                        //What intersection contains the building; also used as intersection to steal from
                        playerWithMostVPsIntersection = 0;

                        //A building has been found that contains the intersection of player with most VPs
                        foundBuilding = false;
                        sleep(2000);
                        //9. Iterate through each Hexagon and find one that has the playersId at one of the adjacent intersections
                        for (Hexagon hex : gs.getBoard().getHexagons()) {
                            Log.i(TAG, "receiveInfo: Checking hexagon " + hex.getHexagonId() + " for player " + playerWithMostVPs);
                            if (gs.getBoard().getRobber().getHexagonId() != hex.getHexagonId()) {
                                for (Integer intersection : gs.getBoard().getHexToIntIdMap().get(hex.getHexagonId())) {
                                    if (gs.getBoard().hasBuilding(intersection) && gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId() == playerWithMostVPs && tryMoveRobber(hex.getHexagonId(), gs)) {
                                        Log.i(TAG, "receiveInfo: Found player at hex " + hex.getHexagonId() + ".");
                                        hexId = hex.getHexagonId();
                                        playerWithMostVPsIntersection = intersection;
                                        foundBuilding = true;
                                    }
                                }
                            }

                            //We've found our hex and building, stop iteration of loop
                            if (foundBuilding) {
                                break;
                            }
                        }

                        //10. Send the action to move the robber; information has been saved to also steal with the robber

                        sleep(2000);
                        game.sendAction(new CatanRobberMoveAction(this, playerNum, hexId));
                        return;

                    }//End of Move Robber Phase

                    /*----------------Steal Resource Phase--------------*/

                    //11. Now Steal from the selected intersection
                    game.sendAction(new CatanRobberStealAction(this, playerNum, playerWithMostVPs));
                    return;
                }//End of Robber Phases after Discard phase
            }//End of Robber Phase

            // not setup phase if statement END

            /* ----------------------------------- CPUs Normal Action Phase ------------------------------------ */
//        if(!gs.isRobberPhase() && this.playerNum == gs.getCurrentPlayerId()){
//            Log.e(TAG, "receiveInfo: returning a CatanEndTurnAction");
//            game.sendAction(new CatanEndTurnAction(this));
//        }

        if (!gs.isSetupPhase() && gs.isActionPhase() && gs.getCurrentPlayerId() == this.playerNum && !gs.isRobberPhase()) {
            int settlementIntersection = getBuildingOfPlayer(gs);
            Log.d(TAG, "receiveInfo: settlementIntersection = " + settlementIntersection);
            if (settlementIntersection == -1) {
                Log.d(TAG, "receiveInfo: There is no settlementIntersection");
                return;
            }

            /*****Looks to trade so they can potentially build a road******/
            int brickCount = gs.getPlayerList().get(this.playerNum).getResourceCards()[0];

            int grainCount = gs.getPlayerList().get(this.playerNum).getResourceCards()[1];
            int oreCount = gs.getPlayerList().get(this.playerNum).getResourceCards()[3];
            int woolCount = gs.getPlayerList().get(this.playerNum).getResourceCards()[4];
            int tradeResourceId;
            if (brickCount == 0){
                tradeResourceId = 0;
            }
            else {
                tradeResourceId = 2;
            }
            if (grainCount >= 4) {
                if (!gs.getPlayerList().get(this.playerNum).hasResourceBundle(Road.resourceCost) && !gs.getPlayerList().get(this.playerNum).hasResourceBundle(Settlement.resourceCost) && !gs.getPlayerList().get(this.playerNum).hasResourceBundle(City.resourceCost)){
                    Log.d(TAG, "receiveInfo: Trade happening: player + " + this.playerNum + "traded grain for brick");
                    game.sendAction(new CatanTradeWithBankAction(this, 1,tradeResourceId));
                    Log.d(TAG, "receiveInfo: CatanTradeWithBankAction sent");
                    game.sendAction(new CatanEndTurnAction(this));
                    Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                    return;
                }
            }
            if (oreCount >= 4){
                if (!gs.getPlayerList().get(this.playerNum).hasResourceBundle(Road.resourceCost) && !gs.getPlayerList().get(this.playerNum).hasResourceBundle(Settlement.resourceCost) && !gs.getPlayerList().get(this.playerNum).hasResourceBundle(City.resourceCost)){
                    Log.d(TAG, "receiveInfo: Trade happening: ore for brick");
                    game.sendAction(new CatanTradeWithBankAction(this, 3,tradeResourceId));
                    Log.d(TAG, "receiveInfo: CatanTradeWithBankAction sent");
                    game.sendAction(new CatanEndTurnAction(this));
                    Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                    return;
                }
            }
            if (woolCount >= 4){
                if (!gs.getPlayerList().get(this.playerNum).hasResourceBundle(Road.resourceCost) && !gs.getPlayerList().get(this.playerNum).hasResourceBundle(Settlement.resourceCost) && !gs.getPlayerList().get(this.playerNum).hasResourceBundle(City.resourceCost)){
                    Log.d(TAG, "receiveInfo: Trade happening: wool for brick");
                    game.sendAction(new CatanTradeWithBankAction(this, 4,tradeResourceId));
                    Log.d(TAG, "receiveInfo: CatanTradeWithBankAction sent");
                    game.sendAction(new CatanEndTurnAction(this));
                    Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                    return;
                }
            }

            /******Looks to build a settlement****/
            if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(Settlement.resourceCost)){
                Log.d(TAG, "receiveInfo: Valid amount of resources to building");
                for (int n = 0; n < getPlayerRoadIntersection(getPlayerRoads(gs)).size(); n++){
                    //cycling through the amount, not the proper value at the intersection
                    if (gs.getBoard().validBuildingLocation(this.playerNum, false, getPlayerRoadIntersection(getPlayerRoads(gs)).get(n))){
                        Log.d(TAG, "receiveInfo: validBuildingLocation for a settlement");
                        game.sendAction(new CatanBuildSettlementAction(this, false, this.playerNum, getPlayerRoadIntersection(getPlayerRoads(gs)).get(n)));
                        Log.d(TAG, "receiveInfo: CatanBuildSettlementAction sent");
                        game.sendAction(new CatanEndTurnAction(this));
                        Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
                        return;
                    }
                }
            }

            /*****Looks to build a city from a settlement****/
            Building building = null;
            //Build a city if proper amount of resources
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

            /******Looks to build another road*****/
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
                for (int n = 0; n < intersectionsToChooseFrom.size(); n++){
                    if (gs.getBoard().validRoadPlacement(this.playerNum, false, roadCoordinate, intersectionsToChooseFrom.get(n))){
                        game.sendAction(new CatanBuildRoadAction(this, false, this.playerNum, roadCoordinate, intersectionsToChooseFrom.get(n))); //was random road intersection
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

            game.sendAction(new CatanEndTurnAction(this));
            Log.d(TAG, "receiveInfo: CatanEndTurnAction sent: Nothing was built");
            return;
            //not setup phase if statement END
        }
    }// receiveInfo() END

    private boolean tryMoveRobber(int hexId, CatanGameState gs){

        if(hexId == -1){
            Log.d(TAG, "tryMoveRobber: Invalid hex ID from CPU");
            return false;
        }

        if(hexId == gs.getBoard().getRobber().getHexagonId()){
            Log.d(TAG, "tryMoveRobber: Same hexId as robber");
            return false;
        }
        if(gs.getBoard().getHexagons().get(hexId).getResourceId() == 5){
            Log.d(TAG, "tryMoveRobber: Desert tile selected; invalid.");
            return false;
        }

        ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);

        for (Integer intersection : intersections) {
            if(gs.getBoard().getBuildings()[intersection] != null){
                if(gs.getBoard().getBuildings()[intersection].getOwnerId() != playerNum){
                    return true;
                }
            }
        }
        Log.d(TAG, "tryMoveRobber: ");
        return false;
    }

    /**
     * @param gs CatanGameState object to get the buildings on the board
     * @return gets the first building wth the owner's id and returns its intersection location
     */
    private int getBuildingOfPlayer(CatanGameState gs){
        for (int n = 0; n < gs.getBoard().getBuildings().length; n++){
            if (gs.getBoard().getBuildings()[n] != null && gs.getBoard().getBuildings()[n].getOwnerId() == this.playerNum){
                return n;
            }
        }
        return -1;
    }

    /**
     * @param gs CatanGameState object to get the roads on the board
     * @return ArrayList of roads that that player owns
     */
    private ArrayList<Road> getPlayerRoads(CatanGameState gs) {
        ArrayList<Road> playerRoads = new ArrayList<>();
        for (int n = 0; n < gs.getBoard().getRoads().size(); n++){
            if (gs.getBoard().getRoads().get(n).getOwnerId() == this.playerNum){
                playerRoads.add(gs.getBoard().getRoads().get(n));
            }
        }
        return playerRoads;
    }

    /**
     * @param playerRoads takes a list of a players Road
     * @return returns an arrayList of intersections along the player's roads
     */
    private ArrayList<Integer> getPlayerRoadIntersection(ArrayList<Road> playerRoads){
        ArrayList<Integer> intersections = new ArrayList<>();
        for (int n = 0; n < playerRoads.size(); n++) {
            intersections.add(playerRoads.get(n).getIntersectionAId());
            intersections.add(playerRoads.get(n).getIntersectionBId());
        }
//        ArrayList<Integer> noRepeatIntersections = new ArrayList<>();
//        for (int n = 0; n < intersections.size(); n++){
//            for (int j = n+1; j < intersections.size(); j++){
//                if (intersections.get(n) != intersections.get(j)){
//                    noRepeatIntersections.add(n);
//                }
//            }
            Log.d(TAG, "With repeat Intersections: " + intersections.toString());
            //Log.d(TAG, "No repeat Intersections: " + noRepeatIntersections.toString());
        //}
        //might need to change to return intersections
        return intersections;
    }

    /**
     * @param intersectionId intersection you are checking to see it's resource
     * @param gs
     * @return true if the id is for brick or lumber, false if it isn't
     */
    private boolean checkIntersectionResource(int intersectionId, CatanGameState gs){
        Log.d(TAG, "checkIntersectionResource() called with: intersectionId = [" + intersectionId + "], gs = [" + gs + "]");
        ArrayList<Integer> adjHexIds = gs.getBoard().getIntToHexIdMap().get(intersectionId);
        for (Integer adjHexId : adjHexIds) {
            //change back to 0 and 2 for building a road
            if(gs.getBoard().getHexagonFromId(adjHexId).getResourceId() == 0 || gs.getBoard().getHexagonFromId(adjHexId).getResourceId() == 2) {
                Log.d(TAG, "checkIntersectionResource() returned: " + true);
                return true;
            }
        }
        Log.d(TAG, "checkIntersectionResource() returned: " + false);
        return false;
    }
} // CatanDumbComputerPlayer class END


