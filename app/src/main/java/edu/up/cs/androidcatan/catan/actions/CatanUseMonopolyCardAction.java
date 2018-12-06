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

public class CatanUseMonopolyCardAction extends GameAction implements Serializable {
    private static final String TAG = "CatanUseMonopolyCardAction";
    private static final long serialVersionUID = 6395440562725742066L;
    private int chosenResource;

    /**
     * CatanUseMonopolyCardAction constructor
     *
     * @param player         the player calling the action
     * @param chosenResource the resource that the player chose
     */
    public CatanUseMonopolyCardAction(GamePlayer player, int chosenResource) {
        super(player);
        this.chosenResource = chosenResource;
        Log.d(TAG, "CatanUseMonopolyCardAction called");
    }

    public int getChosenResource() {
        return chosenResource;
    }
}
