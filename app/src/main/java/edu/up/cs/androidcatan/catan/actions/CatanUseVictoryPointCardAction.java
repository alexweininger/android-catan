package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanUseVictoryPointCardAction extends GameAction {
    private static final String TAG = "CatanUseVictoryPointCardAction";

    /**
     * CatanUseVictoryPointCardAction constructor
     * @param player player who is calling the action
     */
    public CatanUseVictoryPointCardAction(GamePlayer player){
        super(player);
        Log.d(TAG, "CatanUseVictoryPointCardAction called");
    }
}
