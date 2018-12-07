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

public class CatanUseKnightCardAction extends GameAction implements Serializable {
    private static final String TAG = "CatanUseKnightCardAction";
    private static final long serialVersionUID = -1028980973911587150L;

    /**
     * CatanUseKnightCardAction
     *
     * @param player the player calling the action
     */
    public CatanUseKnightCardAction(GamePlayer player) {
        super(player);
        Log.d(TAG, "CatanUseKnightCardAction called");
    }
}
