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
        Log.i(TAG, "receiveInfo() of player " + this.playerNum + " called.");

        if (!(info instanceof CatanGameState)) return;
        CatanGameState gs = (CatanGameState) info;
        Log.d(TAG, "receiveInfo: game state current player: " + gs.getCurrentPlayerId() + " this.playerNum: " + this.playerNum);

        Random random = new Random();

        /*------------------------------------CPUs Setup Phase Actions-----------------------------------------*/
        if (gs.isSetupPhase() && this.playerNum == gs.getCurrentPlayerId()) {
            Log.d(TAG, "receiveInfo: It is the setup phase. Computer player will now attempt to build a settlement and a road." + " " + this.playerNum);

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
            Log.i(TAG, "receiveInfo: RollDiceAction by DumbComputerPlayer " + this.playerNum);
            game.sendAction(new CatanRollDiceAction(this));
            sleep(300);
            return;
        }
        /*-------------------------------CPUs Robber Actions--------------------------------------*/
        if (gs.isRobberPhase()) {
            Log.i(TAG, "receiveInfo: Computer has reached the Robber Phase");
            sleep(500);
            /*--------------------Discard Phase--------------------*/

            if (!gs.getRobberPlayerListHasDiscarded()[playerNum]) {
                Log.i(TAG, "receiveInfo: Computer player " + playerNum + " needs to discard!!!");
                if (!gs.checkPlayerResources(playerNum)) {
                    Log.i(TAG, "receiveInfo: Computer " + playerNum + " does not need to discard, but still needs to send action.");
                    game.sendAction(new CatanRobberDiscardAction(this, playerNum, robberResourcesDiscard));
                    return;
                } else {
                    robberResourcesDiscard = new int[]{0, 0, 0, 0, 0};
                    for (int i = 0; i < gs.getPlayerList().get(playerNum).getResourceCards().length; i++) {
                        for (int j = 0; j < gs.getPlayerList().get(playerNum).getResourceCards()[i]; j++) {
                            robberResourcesDiscard[i]++;
                            Log.i(TAG, "receiveInfo: Player " + playerNum + " is discarding resources: Wanted- " + robberResourcesDiscard[i] + ", Actual- " + gs.getPlayerList().get(playerNum).getResourceCards()[i]);
                            if (gs.validDiscard(playerNum, robberResourcesDiscard)) {
                                Log.i(TAG, "receiveInfo: Computer is now discarding resources");
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
            }

            if (!gs.allPlayersHaveDiscarded() && gs.getCurrentPlayerId() == playerNum) {
                Log.d(TAG, "receiveInfo: Not all players have discarded!!!!");
                return;
            }

            Log.i(TAG, "receiveInfo: Robber Phase --> Move Robber Phase");

            /*----------------------Move Robber Phase----------------*/
            if (gs.getCurrentPlayerId() == playerNum) {
                Log.i(TAG, "receiveInfo: Computer is moving robber");
                if (!gs.getHasMovedRobber()) {
                    Log.i(TAG, "receiveInfo: Computer Player hasMovedRobber: " + gs.getHasMovedRobber());
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
                // get adjacent intersections around the hexagon
                ArrayList<Integer> intersections = gs.getBoard().getHexToIntIdMap().get(hexId);
                // for each adjacent intersection
                for (Integer intersection : intersections) {
                    // if intersection has a building AND building isn't owned by the current player
                    if (gs.getBoard().hasBuilding(intersection) && gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId() != playerNum) {
                        Log.i(TAG, "receiveInfo: Computer is now stealing from player " + gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId());

                        // send CatanRobberStealAction to the game
                        game.sendAction(new CatanRobberStealAction(this, this.playerNum, gs.getBoard().getBuildingAtIntersection(intersection).getOwnerId()));
                        return;
                    }
                }
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
