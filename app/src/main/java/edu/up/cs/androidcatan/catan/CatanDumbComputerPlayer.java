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
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
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

    private int[] robberResourcesDiscard = new int[]{0,0,0,0,0};
    int hexId;

    /**
     * callback method--game's state has changed
     *
     * @param info the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo (GameInfo info) {
        Log.i(TAG, "receiveInfo() called with: info = [" + info + "]");

        if (!(info instanceof CatanGameState)) return;
        CatanGameState gs = (CatanGameState) info;

        Log.d(TAG, "receiveInfo: game state current player: " + gs.getCurrentPlayerId() + " this.playerNum: " + this.playerNum);
        if (gs.getCurrentPlayerId() != this.playerNum) return;

        Random random = new Random();
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

        /*------------------------------------CPUs Setup Phase Actions-----------------------------------------*/
        if (gs.isSetupPhase() && (roadCount < 2 || settlementCount < 2)) {

            Log.i(TAG, "receiveInfo: It is the setup phase. Computer player will now attempt to build a settlement and a road.");

            int randSettlementIntersection = random.nextInt(53);

            // generate random intersection until we find a valid location to build our settlement
            while (!gs.getBoard().validBuildingLocation(this.playerNum, true, randSettlementIntersection)) {
                randSettlementIntersection = random.nextInt(53);
            }

            if (gs.isSetupPhase()) { // need to build a settlement

                sleep(2000); // sleep

                Log.w(TAG, "receiveInfo: Attempting to build a settlement at intersection " + randSettlementIntersection);

                // add just enough resources for a settlement
                gs.getPlayerList().get(this.playerNum).addResourceCard(0, 1);
                gs.getPlayerList().get(this.playerNum).addResourceCard(1, 1);
                gs.getPlayerList().get(this.playerNum).addResourceCard(2, 1);
                gs.getPlayerList().get(this.playerNum).addResourceCard(4, 1);

                // send the build settlement action to the game
                Log.i(TAG, "receiveInfo: sending a CatanBuildSettlementAction to the game.");

                game.sendAction(new CatanBuildSettlementAction(this, true, this.playerNum, randSettlementIntersection)); // sending build settlement action

                Log.d(TAG, "receiveInfo() returned: void");

                // get adjacent intersections to what we just built
                ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getIntersectionGraph().get(randSettlementIntersection);

                Log.i(TAG, "receiveInfo: intersectionsToChooseFrom: " + intersectionsToChooseFrom);

                // choose a random intersection from those intersections
                int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());

                // generate random intersection until we find a valid location to build our settlement
                while (!gs.getBoard().validRoadPlacement(this.playerNum, true, randSettlementIntersection, intersectionsToChooseFrom.get(randomRoadIntersection))) {
                    randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
                }

                sleep(1000); // sleep

                Log.w(TAG, "receiveInfo: Attempting to build a road between " + intersectionsToChooseFrom.get(randomRoadIntersection) + " and " + randSettlementIntersection);

                // add just enough resources for a road
                gs.getPlayerList().get(this.playerNum).addResourceCard(0, 1);
                gs.getPlayerList().get(this.playerNum).addResourceCard(2, 1);

                // send the game a build road action
                Log.i(TAG, "receiveInfo: sending a CatanBuildRoadAction to the game.");
                game.sendAction(new CatanBuildRoadAction(this, true, this.playerNum, randSettlementIntersection, intersectionsToChooseFrom.get(randomRoadIntersection)));

                Log.d(TAG, "receiveInfo() returned: void");
                return;
            }
        } // setup phase if statement END

        if (gs.isSetupPhase() && settlementCount == 2 && roadCount == 2) {
            Log.e(TAG, "receiveInfo: returning a CatanEndTurnAction");
            game.sendAction(new CatanEndTurnAction(this));
        }

        /*-------------------------------CPUs Roll Dice Action--------------------------------------*/
        if (!gs.isSetupPhase() && !gs.isActionPhase()) {
            sleep(300);
            game.sendAction(new CatanRollDiceAction(this));
            sleep(300);
        }

        /*-------------------------------CPUs Robber Actions--------------------------------------*/
        if(gs.isRobberPhase()){

            /*--------------------Discard Phase--------------------*/
            if(gs.getPlayerList().get(playerNum).getTotalResourceCardCount() > 7 && !gs.getRobberPlayerListHasDiscarded()[playerNum]){
                for (int i = 0; i < gs.getPlayerList().get(playerNum).getResourceCards().length; i++) {
                    for(int j = 0; j < gs.getPlayerList().get(playerNum).getResourceCards()[i]; j++){
                        robberResourcesDiscard[i]++;
                        if(gs.validDiscard(playerNum, robberResourcesDiscard)){
                            CatanRobberDiscardAction action = new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard);
                            robberResourcesDiscard = gs.getRobberDiscardedResource();
                            game.sendAction(action);
                            break;
                        }
                    }
                    if(gs.getRobberPlayerListHasDiscarded()[playerNum]){
                        break;
                    }
                }
            }

            /*----------------------Move Robber Phase----------------*/
            if(gs.getCurrentPlayerId() == playerNum) {
                if(gs.isHasMovedRobber()) {
                    while (!gs.allPlayersHaveDiscarded()) {
                        sleep(2000);
                    }

                    for (Hexagon hex : gs.getBoard().getHexagons()) {
                        hexId = hex.getHexagonId();
                        if (tryMoveRobber(hexId, gs)) {
                            sleep(1000);
                            CatanRobberMoveAction action = new CatanRobberMoveAction(this, playerNum, hexId);
                            game.sendAction(action);
                            break;
                        }
                    }
                }

                /*----------------Steal Resource Phase--------------*/
                sleep(500);
                ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);
                for (Integer intersection : intersections){
                    if(gs.getBoard().hasBuilding(intersection) && gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId() != playerNum){
                        CatanRobberStealAction action = new CatanRobberStealAction(this, playerNum, gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId());
                        game.sendAction(action);
                    }
                }
            }

        }

        /* ----------------------------------- CPUs Normal Action Phase ------------------------------------ */
        Log.e(TAG, "receiveInfo: returning a CatanEndTurnAction");
        game.sendAction(new CatanEndTurnAction(this));
        // not setup phase if statement END


    }// receiveInfo() END

    CatanDumbComputerPlayer (String name) {
        super(name);
    }

    private boolean tryMoveRobber(int hexId, CatanGameState gs){

        if(hexId == -1){
            return false;
        }

        if(hexId == gs.getBoard().getRobber().getHexagonId()){
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
        return false;
    }

} // CatanDumbComputerPlayer class END
















