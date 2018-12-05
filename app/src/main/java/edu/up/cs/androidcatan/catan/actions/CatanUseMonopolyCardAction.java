package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanUseMonopolyCardAction extends GameAction {
    private static final String TAG = "CatanUseMonopolyCardAction";
    private int chosenResource;

    /**
     * CatanUseMonopolyCardAction constructor
     * @param player the player calling the action
     * @param chosenResource the resource that the player chose
     */
    public CatanUseMonopolyCardAction(GamePlayer player, int chosenResource){
        super(player);
        this.chosenResource = chosenResource;
        Log.d(TAG, "CatanUseMonopolyCardAction called");
    }

    public int getChosenResource() {
        return chosenResource;
    }
}
