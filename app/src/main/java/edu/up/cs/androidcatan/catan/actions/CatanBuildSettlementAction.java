package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import java.io.Serializable;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class CatanBuildSettlementAction extends GameAction implements Serializable {
    private static final long serialVersionUID = -6528636163217483199L;
    private final String TAG = "CatanBuildSettlementAction";

    private int intersectionId, ownerId;
    private boolean isSetupPhase;

    /**
     * CantanBuildSettlementAcation constructor
     *
     * @param player         the player calling the action
     * @param isSetupPhase   true or false for if it is the setUp phase
     * @param ownerId        id of who owns the building
     * @param intersectionId the intersection ID of where the settlement is located on the board
     */
    public CatanBuildSettlementAction(GamePlayer player, boolean isSetupPhase, int ownerId, int intersectionId) {
        super(player);
        Log.d(TAG, "CatanBuildSettlementAction() called with: player = [" + player + "], ownerId = [" + ownerId + "], intersectionId = [" + intersectionId + "]");
        this.isSetupPhase = isSetupPhase;
        this.ownerId = ownerId;
        this.intersectionId = intersectionId;
    }

    public int getIntersectionId() {
        return intersectionId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public boolean isSetupPhase() {
        return isSetupPhase;
    }
}
