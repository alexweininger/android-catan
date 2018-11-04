package edu.up.cs.androidcatan.catan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.game.GameComputerPlayer;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanDumbComputerPlayer extends GameComputerPlayer {
    private static final String TAG = "CatanDumbComputerPlayer";
    /**
     * ctor does nothing extra
     */
    public CatanDumbComputerPlayer(String name) {
        super(name);
    }

    /**
     * callback method--game's state has changed
     *
     * @param info the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        Log.i(TAG, "receiveInfo() called with: info = [" + info + "]");
        if (!(info instanceof CatanGameState)) return;

        CatanGameState gs = (CatanGameState) info;

        Log.d(TAG, "receiveInfo: game state current player: " + gs.getCurrentPlayerId() + " this.playerNum: " + this.playerNum);
        if (gs.getCurrentPlayerId() != this.playerNum) return;

        sleep(200);

        Random random = new Random();

        if (gs.isSetupPhase()) {
            Log.i(TAG, "receiveInfo: It is the setup phase. Computer player will now attempt to build a settlement and a road.");

            int randomIntersectionId = random.nextInt(53);

            // generate random intersection until we find a valid location to build our settlement
            while (!gs.getBoard().validBuildingLocation(this.playerNum, true, randomIntersectionId)) {
                randomIntersectionId = random.nextInt(53);
            }
            // add just enough resources for a settlement
            gs.getPlayerList().get(this.playerNum).addResourceCard(0, 1);
            gs.getPlayerList().get(this.playerNum).addResourceCard(1, 1);
            gs.getPlayerList().get(this.playerNum).addResourceCard(2, 1);
            gs.getPlayerList().get(this.playerNum).addResourceCard(4, 1);
            Log.d(TAG, "receiveInfo: inventory: " + gs.getPlayerList().get(this.playerNum).printResourceCards());
            game.sendAction(new CatanBuildSettlementAction(this, true, this.playerNum, randomIntersectionId));

            // get adjacent intersections to what we just built
            ArrayList<Integer> intersectionsToChooseFrom = gs.getBoard().getAdjacentIntersectionsToIntersection(randomIntersectionId);

            Log.i(TAG, "receiveInfo: intersectionsToChooseFrom: " + intersectionsToChooseFrom);
            sleep(5000);
            // choose a random intersection from those intersections
            int randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
            // generate random intersection until we find a valid location to build our settlement
            while (!gs.getBoard().validRoadPlacement(this.playerNum, true, randomIntersectionId, intersectionsToChooseFrom.get(randomRoadIntersection))) {
                randomRoadIntersection = random.nextInt(intersectionsToChooseFrom.size());
                sleep(1000);
            }
            // add just enough resources for a road
            gs.getPlayerList().get(this.playerNum).addResourceCard(0, 1);
            gs.getPlayerList().get(this.playerNum).addResourceCard(1, 1);
            game.sendAction(new CatanBuildRoadAction(this, this.playerNum, randomIntersectionId, intersectionsToChooseFrom.get(randomRoadIntersection)));
        } else {
            Log.i(TAG, "receiveInfo: Not setup phase.");
        }

        game.sendAction(new CatanEndTurnAction(this));
    }//receiveInfo
}
