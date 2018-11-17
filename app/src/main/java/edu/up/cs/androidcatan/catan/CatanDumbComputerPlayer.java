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
        Log.i(TAG, "receiveInfo() of player  " + this.playerNum + " called.");

        if (!(info instanceof CatanGameState)) return;
        CatanGameState gs = (CatanGameState) info;
        Log.d(TAG, "receiveInfo: game state current player: " + gs.getCurrentPlayerId() + " this.playerNum: " + this.playerNum);

        if (this.playerNum != gs.getCurrentPlayerId()) {
            return;
        } else {
            Log.e(TAG, "receiveInfo: IT IS MY TURN I AM PLAYING");
        }

        sleep(1000);
        Random random = new Random();

        /*------------------------------------CPUs Setup Phase Actions-----------------------------------------*/
        if (gs.isSetupPhase()) {
            Log.d(TAG, "receiveInfo: It is the setup phase. Computer player will now attempt to build a settlement and a road.");

            int randSettlementIntersection = random.nextInt(53);
            // generate random intersection until we find a valid location to build our settlement
            while (!gs.getBoard().validBuildingLocation(this.playerNum, true, randSettlementIntersection)) {
                sleep(1000); // sleep
                Log.d(TAG, "receiveInfo: generating new building location");
                randSettlementIntersection = random.nextInt(53);
            }
            Log.e(TAG, "receiveInfo: exiting settlement look for " + this.playerNum);

            // send the build settlement action to the game
            Log.d(TAG, "receiveInfo: sending a CatanBuildSettlementAction to the game with intersection id: " +randSettlementIntersection);
            game.sendAction(new CatanBuildSettlementAction(this, true, this.playerNum, randSettlementIntersection)); // sending build settlement action

            // get adjacent intersections to what we just built
            ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getIntersectionGraph().get(randSettlementIntersection);

            Log.d(TAG, "receiveInfo: intersectionsToChooseFrom: " + intersectionsToChooseFrom);

            // choose a random intersection from those intersections
            int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
            int count = 0;
            // generate random intersection until we find a valid location to build our settlement
            while (!gs.getBoard().validRoadPlacement(this.playerNum, true, randSettlementIntersection, intersectionsToChooseFrom.get(randomRoadIntersection))) {
                if (count > 5) {
                    Log.e(TAG, "receiveInfo: CANNOT PLACE ROAD");
                    break;
                }
                Log.e(TAG, "receiveInfo: generating new road intersection");
                sleep(1000); // sleep
                randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
                count++;
            }

            sleep(1000); // sleep
            // send the game a build road action
            Log.i(TAG, "receiveInfo: sending a CatanBuildRoadAction to the game.");
            game.sendAction(new CatanBuildRoadAction(this, true, this.playerNum, randSettlementIntersection, intersectionsToChooseFrom.get(randomRoadIntersection)));
            Log.d(TAG, "receiveInfo() returned: void");

            Log.d(TAG, "receiveInfo: Ending turn during the setup phase after building 1 road and 1 settlement");
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

            // if the player has not discarded yet
            if (!gs.getRobberPlayerListHasDiscarded()[playerNum]) {
                Log.i(TAG, "receiveInfo: Computer player " + playerNum + " needs to discard!!!");

                // checks if they need to discard
                if (!gs.needsToDiscardHalf(playerNum)) {
                    // if they do not need to discard resources
                    Log.w(TAG, "receiveInfo: Player " + this.playerNum + " does not have 7+ cards. Sending CatanRobberDiscardAction to the game.");
                    game.sendAction(new CatanRobberDiscardAction(this, playerNum, new ArrayList<Integer>()));
                    return;
                }

                // at this point they need to discard
                int amountOfResourcesToDiscard = gs.getPlayerList().get(this.playerNum).getTotalResourceCardCount() / 2;
                Log.d(TAG, "receiveInfo: PLayer id: " + this.playerNum + " needs to discard " + amountOfResourcesToDiscard + ". G");

                ArrayList<Integer> resourcesToDiscardIds = new ArrayList<>();

                while (resourcesToDiscardIds.size() < amountOfResourcesToDiscard) {
                    int resId = random.nextInt(5);
                    if (gs.getPlayerList().get(this.playerNum).checkResourceCard(resId, 1)) {
                        Log.d(TAG, "receiveInfo: Added resource " + resId + " to the players discard attempt.");
                        resourcesToDiscardIds.add(resId);
                    }
                }

                Log.e(TAG, "receiveInfo: resourcesToDiscardIds:" + resourcesToDiscardIds.toString());

                for (int i = 0; i < resourcesToDiscardIds.size(); i++) {
                    gs.getPlayerList().get(this.playerNum).removeResourceCard(resourcesToDiscardIds.get(i), 1);
                }

                game.sendAction(new CatanRobberDiscardAction(this, this.playerNum, resourcesToDiscardIds));
                return;

                // logic for player to discard resource cards
                //                for (int i = 0; i < gs.getPlayerList().get(playerNum).getResourceCards().length; i++) {
                //                    for (int j = 0; j < gs.getPlayerList().get(playerNum).getResourceCards()[i]; j++) {
                //
                //                        // add to the resources they have discarded
                //                        robberResourcesDiscard[i]++;
                //
                //                        if (gs.validDiscard(playerNum, robberResourcesDiscard)) {
                //                            Log.i(TAG, "receiveInfo: Computer is now discarding resources");
                //                            CatanRobberDiscardAction action = new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard);
                //                            robberResourcesDiscard = gs.getRobberDiscardedResource();
                //                            game.sendAction(action);
                //                            break;
                //                        }
                //                    }
                //                    if (gs.getRobberPlayerListHasDiscarded()[playerNum]) {
                //                        Log.i(TAG, "receiveInfo: Computer discarded cards!");
                //                        break;
                //                    }
                //                }
                //                Log.i(TAG, "receiveInfo: Player is ending the discard phase!");
                //                return;
            }

            // if this player needs to move the robber, but all players have not discarded yet. Do nothing.
            if (!gs.allPlayersHaveDiscarded() && gs.getCurrentPlayerId() == playerNum) {
                Log.d(TAG, "receiveInfo: Player " + playerNum + " is waiting for all players to discard. Not moving robber. Yeet...");
                return;
            }

            /*----------------------Move Robber Phase----------------*/
            Log.d(TAG, "receiveInfo: Right before moving the robber: gs.getCurrentPlayerId()" + gs.getCurrentPlayerId() + " playerNum: " + playerNum);
            if (gs.getCurrentPlayerId() == playerNum) {
                Log.i(TAG, "receiveInfo: Computer is moving robber");
                if (!gs.getHasMovedRobber()) {
                    Log.i(TAG, "receiveInfo: Computer is moving the robber");
                    sleep(2000);

                    for (Hexagon hex : gs.getBoard().getHexagons()) {
                        hexId = hex.getHexagonId();
                        if (tryMoveRobber(hexId, gs)) {
                            Log.d(TAG, "receiveInfo: Computer is placing robber on hex " + hexId);
                            sleep(2000);
                            game.sendAction(new CatanRobberMoveAction(this, playerNum, hexId));
                            return;
                        }
                    }
                }
                Log.d(TAG, "receiveInfo: here at stealing");
                /*----------------Steal Resource Phase--------------*/
                sleep(500);
                ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);
                for (Integer intersection : intersections) {
                    if (gs.getBoard().hasBuilding(intersection) && gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId() != playerNum) {
                        Log.i(TAG, "receiveInfo: Computer is now stealing from player " + gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId());
                        CatanRobberStealAction action = new CatanRobberStealAction(this, playerNum, gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId());
                        game.sendAction(action);
                    }
                }
            }
            Log.e(TAG, "receiveInfo: here boiiiiiiiiiiiiiiii");
            return;
            //                        if(!gs.isRobberPhase()) {
            //                            game.sendAction(new CatanEndTurnAction(this));
            //                        }
            //                        else{
            //                            Log.e(TAG, "receiveInfo: Got to the end of CPU robber phase without ending robber phase", new Exception());
            //                        }
        }

        /* ----------------------------------- CPUs Normal Action Phase ------------------------------------ */

        Log.e(TAG, "receiveInfo: returning a CatanEndTurnAction");
        if (!gs.isRobberPhase() && gs.getCurrentPlayerId() == this.playerNum) {
            game.sendAction(new CatanEndTurnAction(this));
        }

        // not setup phase if statement END

    }// receiveInfo() END

    CatanDumbComputerPlayer (String name) {
        super(name);
    }

    private boolean tryMoveRobber (int hexId, CatanGameState gs) {
        Log.d(TAG, "tryMoveRobber() called with: hexId = [" + hexId + "], gs = [" + gs + "]");

        if (hexId == -1) {
            Log.d(TAG, "tryMoveRobber: Invalid hex ID from CPU");
            return false;
        }

        if (hexId == gs.getBoard().getRobber().getHexagonId()) {
            Log.d(TAG, "tryMoveRobber: Same hexId as robber");
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

} // CatanDumbComputerPlayer class END
