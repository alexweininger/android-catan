package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanUseMonopolyCardAction extends GameAction {
    private static final String TAG = "CatanUseMonopolyCardAction";
    public CatanUseMonopolyCardAction(GamePlayer player){
        super(player);
        Log.d(TAG, "CatanUseMonopolyCardAction called");
    }
}
