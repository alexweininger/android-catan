package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import java.io.Serializable;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanUseKnightCardAction extends GameAction implements Serializable {
    private static final String TAG = "CatanUseKnightCardAction";
    private static final long serialVersionUID = -1028980973911587150L;

    public CatanUseKnightCardAction(GamePlayer player){
        super(player);
        Log.d(TAG, "CatanUseKnightCardAction called");
    }
}
