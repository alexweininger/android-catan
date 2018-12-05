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
public class CatanBuildCityAction extends GameAction {

    // instance variables
    private final String TAG = "CatanBuildCityAction";

    // instance variables
    private int intersectionId, ownerId;
    private boolean isSetupPhase;

    /**
     *  CatanBuildCityAction constructor
     * @param player player that is sending the action
     * @param isSetupPhase true or false for if it is the setUp phase
     * @param ownerId who owns the building
     * @param intersectionId the intersection on the board
     */
    public CatanBuildCityAction(GamePlayer player, boolean isSetupPhase, int ownerId, int intersectionId) {
        super(player);
        Log.d(TAG, "CatanBuildSettlementAction() called with: player = [" + player + "], ownerId = [" + ownerId + "], intersectionId = [" + intersectionId + "]");

        this.ownerId = ownerId;
        this.intersectionId = intersectionId;
    }

    /**
     * @return int of the intersection ID
     */
    public int getIntersectionId() {
        return intersectionId;
    }

    /**
     * @param intersectionId the new intersection ID
     */
    public void setIntersectionId(int intersectionId) {
        this.intersectionId = intersectionId;
    }

    /**
     * @return int the owner ID
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId who owns the building
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
