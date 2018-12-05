package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanUseKnightCardAction extends GameAction {
    private static final String TAG = "CatanUseKnightCardAction";

    /**
     * CatanUseKnightCardAction
     * @param player the player calling the action
     */
    public CatanUseKnightCardAction(GamePlayer player){
        super(player);
        Log.d(TAG, "CatanUseKnightCardAction called");
    }
}
