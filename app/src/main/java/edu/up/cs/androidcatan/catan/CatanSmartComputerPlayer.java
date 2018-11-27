package edu.up.cs.androidcatan.catan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberDiscardAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberMoveAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberStealAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.game.GameComputerPlayer;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;

public class CatanSmartComputerPlayer extends GameComputerPlayer{
        private static final String TAG = "CatanSmartComputerPlayer";

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
            if(gs.isRobberPhase()){
                Log.i(TAG, "receiveInfo: Computer has reached the Robber Phase");
                sleep(500);
                /*--------------------Discard Phase--------------------*/

                if(!gs.getRobberPlayerListHasDiscarded()[playerNum]){
                    Log.i(TAG, "receiveInfo: Computer player " + playerNum + " needs to discard!!!");
                    if(!gs.checkPlayerResources(playerNum)){
                        game.sendAction(new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard));
                        return;
                    }
                    for (int i = 0; i < gs.getPlayerList().get(playerNum).getResourceCards().length; i++) {
                        for(int j = 0; j < gs.getPlayerList().get(playerNum).getResourceCards()[i]; j++){
                            robberResourcesDiscard[i]++;
                            if(gs.validDiscard(playerNum, robberResourcesDiscard)){
                                Log.i(TAG, "receiveInfo: Computer is now discarding resources");
                                CatanRobberDiscardAction action = new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard);
                                robberResourcesDiscard = gs.getRobberDiscardedResources();
                                game.sendAction(action);
                                break;
                            }
                        }
                        if(gs.getRobberPlayerListHasDiscarded()[playerNum]){
                            Log.i(TAG, "receiveInfo: Computer discarded cards!");
                            break;
                        }
                    }
                    Log.i(TAG, "receiveInfo: Player is ending the discard phase!");
                    return;
                }

                if(!gs.allPlayersHaveDiscarded() && gs.getCurrentPlayerId() == playerNum){
                    Log.d(TAG, "receiveInfo: Not all players have discarded!!!!");
                    return;
                }

                Log.i(TAG, "receiveInfo: Robber Phase --> Move Robber Phase");

                /*----------------------Move Robber Phase----------------*/
                if(gs.getCurrentPlayerId() == playerNum) {
                    Log.i(TAG, "receiveInfo: Computer is moving robber");
                    if(!gs.getHasMovedRobber()) {
                        Log.i(TAG, "receiveInfo: Computer is moving the robber");
                        sleep(2000);

                        for (Hexagon hex : gs.getBoard().getHexagons()) {
                            hexId = hex.getHexagonId();
                            if (tryMoveRobber(hexId, gs)) {
                                Log.d(TAG, "receiveInfo: Computer is placing robber on hex " + hexId);
                                sleep(2000);
                                CatanRobberMoveAction action = new CatanRobberMoveAction(this, playerNum, hexId);
                                game.sendAction(action);
                                return;
                            }
                        }
                    }

                    /*----------------Steal Resource Phase--------------*/
                    sleep(500);
                    ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);
                    for (Integer intersection : intersections){
                        if(gs.getBoard().hasBuilding(intersection) && gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId() != playerNum){
                            Log.i(TAG, "receiveInfo: Computer is now stealing from player " + gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId() );
                            CatanRobberStealAction action = new CatanRobberStealAction(this, playerNum, gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId());
                            game.sendAction(action);
                        }
                    }
                }
                return;
//            if(!gs.isRobberPhase()) {
//                game.sendAction(new CatanEndTurnAction(this));
//            }
//            else{
//                Log.e(TAG, "receiveInfo: Got to the end of CPU robber phase without ending robber phase", new Exception());
//            }
            }

            /* ----------------------------------- CPUs Normal Action Phase ------------------------------------ */
            Log.e(TAG, "receiveInfo: returning a CatanEndTurnAction");
            if(!gs.isRobberPhase() && this.playerNum == gs.getCurrentPlayerId()){
                game.sendAction(new CatanEndTurnAction(this));
            }

            // not setup phase if statement END


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
        private int getBuildingOfPlayer(CatanGameState gs){
            for (int n = 0; n < gs.getBoard().getBuildings().length; n++){
                if (gs.getBoard().getBuildings()[n] != null && gs.getBoard().getBuildings()[n].getOwnerId() == this.playerNum){
                    return n;
                }
            }
            return -1;
        }
        private ArrayList<Road> getPlayerRoads(CatanGameState gs) {
            ArrayList<Road> playerRoads = new ArrayList<>();
            for (int n = 0; n < gs.getBoard().getRoads().size(); n++){
                if (gs.getBoard().getRoads().get(n).getOwnerId() == this.playerNum){
                    playerRoads.add(gs.getBoard().getRoads().get(n));
                }
            }
            return playerRoads;
        }
        private ArrayList<Integer> getPlayerRoadIntersection(ArrayList<Road> playerRoads){
            ArrayList<Integer> intersections = new ArrayList<>();
            for (int n = 0; n < playerRoads.size(); n++) {
                intersections.add(playerRoads.get(n).getIntersectionAId());
                intersections.add(playerRoads.get(n).getIntersectionBId());
            }
            ArrayList<Integer> noRepeatIntersections = new ArrayList<>();
            for (int n = 0; n < intersections.size(); n++){
                for (int j = n+1; j < intersections.size(); j++){
                    if (intersections.get(n) != intersections.get(j)){
                        noRepeatIntersections.add(n);
                    }
                }
                Log.d(TAG, "With repeat Intersections: " + intersections.toString());
                Log.d(TAG, "No repeat Intersections: " + noRepeatIntersections.toString());
            }
            return intersections;
        }
        private boolean checkIntersectionResource(int intersectionId, CatanGameState gs){
            Log.d(TAG, "checkIntersectionResource() called with: intersectionId = [" + intersectionId + "], gs = [" + gs + "]");
            ArrayList<Integer> adjHexIds = gs.getBoard().getIntToHexIdMap().get(intersectionId);
            for (Integer adjHexId : adjHexIds) {
                if(gs.getBoard().getHexagonFromId(adjHexId).getResourceId() == 0 || gs.getBoard().getHexagonFromId(adjHexId).getResourceId() == 2) {
                    Log.d(TAG, "checkIntersectionResource() returned: " + true);
                    return true;
                }
            }
            Log.d(TAG, "checkIntersectionResource() returned: " + false);
            return false;
        }
} // CatanDumbComputerPlayer class END


//-------------------------------------ANDREW'S SMART AI CODE-------------------------------------//
/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 9th, 2018
 * https://github.com/alexweininger/android-catan
 **/
//public class CatanSmartComputerPlayer extends GameComputerPlayer {
//    private static final String TAG = "CatanSmartComputerPlayer";
//    private int[] robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};
//    int hexId;
//    /**
//     * callback method--game's state has changed
//     *
//     * @param info the information (presumably containing the game's state)
//     */
//    @Override
//    protected void receiveInfo (GameInfo info) {
//        Log.i(TAG, "receiveInfo() of player " + this.playerNum + " called.");
//        if (!(info instanceof CatanGameState)) return;
//        CatanGameState gs = (CatanGameState) info;
//        Log.d(TAG, "receiveInfo: game state current player: " + gs.getCurrentPlayerId() + " this.playerNum: " + this.playerNum);
//        if (this.playerNum != gs.getCurrentPlayerId()) {
//            return;
//        } else {
//            Log.e(TAG, "receiveInfo: IT IS MY TURN I AM PLAYING" + " " + this.playerNum);
//        }
//        sleep(1000);
//        Random random = new Random();
//        /*------------------------------------CPUs Setup Phase Actions-----------------------------------------*/
//        if (gs.isSetupPhase() && this.playerNum == gs.getCurrentPlayerId()) { //can take away this.playerNum
//            Log.d(TAG, "receiveInfo: It is the setup phase. Computer player will now attempt to build a settlement and a road." + " " + this.playerNum);
//            int randSettlementIntersection = random.nextInt(53);
//            // generate random intersection until we find a valid location to build our settlement
//            while (!(gs.getBoard().validBuildingLocation(this.playerNum, true, randSettlementIntersection) && checkIntersectionResource(randSettlementIntersection, gs))) {
//                sleep(1000); // sleep
//                Log.d(TAG, "receiveInfo: generating new building location" + " " + this.playerNum);
//                randSettlementIntersection = random.nextInt(53);
//            }
//            Log.e(TAG, "receiveInfo: exiting settlement look for " + this.playerNum);
//            // send the build settlement action to the game
//            Log.d(TAG, "receiveInfo: sending a CatanBuildSettlementAction to the game with intersection id: " +randSettlementIntersection + " " + this.playerNum);
//            game.sendAction(new CatanBuildSettlementAction(this, true, this.playerNum, randSettlementIntersection)); // sending build settlement action
//            // get adjacent intersections to what we just built
//            ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getIntersectionGraph().get(randSettlementIntersection);
//            Log.d(TAG, "receiveInfo: intersectionsToChooseFrom: " + intersectionsToChooseFrom + " " + this.playerNum);
//            // choose a random intersection from those intersections
//            int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
//            int count = 0;
//            // generate random intersection until we find a valid location to build our settlement
//            while (!gs.getBoard().validRoadPlacement(this.playerNum, true, randSettlementIntersection, intersectionsToChooseFrom.get(randomRoadIntersection))) {
//                if (count > 5) {
//                    Log.e(TAG, "receiveInfo: CANNOT PLACE ROAD" + " " + this.playerNum);
//                    break;
//                }
//                Log.e(TAG, "receiveInfo: generating new road intersection" + " " + this.playerNum);
//                sleep(1000); // sleep
//                randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
//                count++;
//            }
//            sleep(1000); // sleep
//            // send the game a build road action
//            Log.i(TAG, "receiveInfo: sending a CatanBuildRoadAction to the game." + " " + this.playerNum);
//            game.sendAction(new CatanBuildRoadAction(this, true, this.playerNum, randSettlementIntersection, intersectionsToChooseFrom.get(randomRoadIntersection)));
//            Log.d(TAG, "receiveInfo() returned: void");
//            Log.d(TAG, "receiveInfo: Ending turn during the setup phase after building 1 road and 1 settlement" + " " + this.playerNum);
//            game.sendAction(new CatanEndTurnAction(this));
//            return;
//        } // setup phase if statement END
//        /*------------------------------Setup Phase End------------------------------------------*/
//        /*-------------------------------CPUs Roll Dice Action--------------------------------------*/
//        if (!gs.isSetupPhase() && !gs.isActionPhase() && gs.getCurrentPlayerId() == this.playerNum) {
//            sleep(300);
//            game.sendAction(new CatanRollDiceAction(this));
//            Log.d(TAG, "receiveInfo: Roll action happened for cpu");
//            sleep(300);
//            return;
//        }
//        /*-------------------------------CPUs Robber Actions--------------------------------------*/
//        if(gs.isRobberPhase()){
//            Log.i(TAG, "receiveInfo: Computer has reached the Robber Phase");
//            sleep(500);
//            /*--------------------Discard Phase--------------------*/
//            if(!gs.getRobberPlayerListHasDiscarded()[playerNum]){
//                Log.i(TAG, "receiveInfo: Computer player " + playerNum + " needs to discard!!!");
//                if(!gs.checkPlayerResources(playerNum)){
//                    Log.i(TAG, "receiveInfo: Computer " + playerNum + " does not need to discard, but still needs to send action.");
//                    game.sendAction(new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard));
//                    return;
//                }
//                else{
//                    robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};
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
//                }
//                Log.i(TAG, "receiveInfo: Player is ending the discard phase!");
//                return;
//            }
//            if(!gs.allPlayersHaveDiscarded() && gs.getCurrentPlayerId() == playerNum){
//                Log.d(TAG, "receiveInfo: Not all players have discarded!!!!");
//                return;
//            }
//            Log.i(TAG, "receiveInfo: Robber Phase --> Move Robber Phase");
//            /*----------------------Move Robber Phase----------------*/
//            if(gs.getCurrentPlayerId() == playerNum) {
//                Log.i(TAG, "receiveInfo: Computer is moving robber");
//                if(!gs.getHasMovedRobber()) {
//                    Log.i(TAG, "receiveInfo: Computer Player hasMovedRobber: " + gs.getHasMovedRobber());
//                    Log.i(TAG, "receiveInfo: Computer is moving the robber");
//                    sleep(2000);
//                    for (Hexagon hex : gs.getBoard().getHexagons()) {
//                        hexId = hex.getHexagonId();
//                        if (tryMoveRobber(hexId, gs)) {
//                            Log.d(TAG, "receiveInfo: Computer is placing robber on hex " + hexId);
//                            sleep(2000);
//                            CatanRobberMoveAction action = new CatanRobberMoveAction(this, playerNum, hexId);
//                            game.sendAction(action);
//                            return;
//                        }
//                    }
//                }
//                /*----------------Steal Resource Phase--------------*/
//                sleep(500);
//                ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);
//                for (Integer intersection : intersections){
//                    if(gs.getBoard().hasBuilding(intersection) && gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId() != playerNum){
//                        Log.i(TAG, "receiveInfo: Computer is now stealing from player " + gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId() );
//                        CatanRobberStealAction action = new CatanRobberStealAction(this, playerNum, gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId());
//                        game.sendAction(action);
//                        return;
//                    }
//                }
//            }
//        }
//        /* ----------------------------------- CPUs Normal Action Phase ------------------------------------ */
//        if(!gs.isRobberPhase() && this.playerNum == gs.getCurrentPlayerId()){
//            Log.e(TAG, "receiveInfo: returning a CatanEndTurnAction");
//            game.sendAction(new CatanEndTurnAction(this));
//        }
////
////        /******Looks to build another road*****/
////        if (!gs.isSetupPhase() && gs.isActionPhase() && gs.getCurrentPlayerId() == this.playerNum && !gs.isRobberPhase()) {
////            int settlementIntersection = getBuildingOfPlayer(gs);
////            Log.d(TAG, "receiveInfo: settlementIntersection = " + settlementIntersection);
////            if (settlementIntersection == -1) {
////                Log.d(TAG, "receiveInfo: There is no settlementIntersection");
////                return;
////            }
////
////            /*****Looks to build a city from a settlement****/
////            Building building = null;
////            //Build a city if proper amount of resources
////            if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(City.resourceCost)) {
////                for (int n = 0; n < gs.getBoard().getBuildings().length; n++) {
////                    if (gs.getBoard().getBuildings()[n] == null){
////                        break;
////                    }
////                    if (gs.getBoard().getBuildings()[n].getOwnerId() == this.playerNum) {
////                        building = gs.getBoard().getBuildings()[n];
////                        if (building instanceof Settlement) {
////                            game.sendAction(new CatanBuildCityAction(this, false, this.playerNum, n));
////                            Log.d(TAG, "receiveInfo: CatanBuildCityAction sent");
////                            game.sendAction(new CatanEndTurnAction(this));
////                            Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
////                            return;
////                        }
////                    }
////                }
////            }
////
////            if (gs.getPlayerList().get(this.playerNum).hasResourceBundle(Road.resourceCost)) {
////
////                // get road endpoints for players roads
////                ArrayList<Integer> individualRoads = getPlayerRoadIntersection(getPlayerRoads(gs));
////                int randIntersection = random.nextInt(individualRoads.size());
////
////                // get random intersection from those road intersections
////                int roadCoordinate = individualRoads.get(randIntersection);
////
////                // get all adjacent intersections
////                ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getIntersectionGraph().get(roadCoordinate);
////
////                int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
////
////                while (!gs.getBoard().validRoadPlacement(this.playerNum, true, roadCoordinate, intersectionsToChooseFrom.get(randomRoadIntersection))) {
////                    Log.d(TAG, "receiveInfo: validRoadPlacement while loop executed");
////                    randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
////                }
////
////                // roadCoordinate should be valid at this point
////
////                game.sendAction(new CatanBuildRoadAction(this, false, this.playerNum, roadCoordinate, intersectionsToChooseFrom.get(randomRoadIntersection)));
////                Log.d(TAG, "receiveInfo: CatanBuildRoadAction sent");
////
////                game.sendAction(new CatanEndTurnAction(this));
////
////                Log.d(TAG, "receiveInfo: CatanEndTurnAction sent");
////                return;
////            }
////
////            game.sendAction(new CatanEndTurnAction(this));
////            Log.d(TAG, "receiveInfo: CatanEndTurnAction sent: Nothing was built");
////            return;
////        }
//        // not setup phase if statement END
//    }// receiveInfo() END
//    CatanSmartComputerPlayer (String name) {
//        super(name);
//    }
//    private boolean tryMoveRobber(int hexId, CatanGameState gs){
//        if(hexId == -1){
//            Log.d(TAG, "tryMoveRobber: Invalid hex ID from CPU");
//            return false;
//        }
//        if(hexId == gs.getBoard().getRobber().getHexagonId()){
//            Log.d(TAG, "tryMoveRobber: Same hexId as robber");
//            return false;
//        }
//        ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);
//        for (Integer intersection : intersections) {
//            if(gs.getBoard().getBuildings()[intersection] != null){
//                if(gs.getBoard().getBuildings()[intersection].getOwnerId() != playerNum){
//                    return true;
//                }
//            }
//        }
//        Log.d(TAG, "tryMoveRobber: ");
//        return false;
//    }

//}
//} // CatanDumbComputerPlayer class END
