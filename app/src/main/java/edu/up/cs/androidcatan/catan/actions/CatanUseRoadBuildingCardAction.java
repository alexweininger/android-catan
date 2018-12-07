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

public class CatanUseRoadBuildingCardAction extends GameAction implements Serializable {
    private static final String TAG = "CatanUseRoadBuildingCardAction";
    private static final long serialVersionUID = 999657364598380655L;

    /**
     * CatanUSeRoaduildingCardAction constructor
     *
     * @param player player who is calling the action
     */
    public CatanUseRoadBuildingCardAction(GamePlayer player) {
        super(player);
        Log.d(TAG, "CatanUseRoadBuildingCardAction called");
    }
}
