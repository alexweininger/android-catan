package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanUseYearOfPlentyCardAction extends GameAction {
    private static final String TAG = "CatanUseYearOfPlentyCardAction";
    private int chosenResource;

    public CatanUseYearOfPlentyCardAction(GamePlayer player, int chosenResource){
        super(player);
        this.chosenResource = chosenResource;
        Log.d(TAG, "CatanUseYearOfPlentyCardAction called");
    }
    public int getChosenResource() { return chosenResource; }
}
