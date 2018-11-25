package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 1, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanBuildSettlementAction extends GameAction {
    private final String TAG = "CatanBuildSettlementAction";

    private int intersectionId, ownerId;
    private boolean isSetupPhase;

    public CatanBuildSettlementAction (GamePlayer player, boolean isSetupPhase, int ownerId, int intersectionId) {
        super(player);
        Log.d(TAG, "CatanBuildSettlementAction() called with: player = [" + player + "], ownerId = [" + ownerId + "], intersectionId = [" + intersectionId + "]");
        this.isSetupPhase = isSetupPhase;
        this.ownerId = ownerId;
        this.intersectionId = intersectionId;
    }

    public int getIntersectionId () { return intersectionId; }

    public int getOwnerId () { return ownerId; }

    public boolean isSetupPhase () { return isSetupPhase; }
}
