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

public class CatanUseYearOfPlentyCardAction extends GameAction implements Serializable {
    private static final String TAG = "CatanUseYearOfPlentyCardAction";
    private static final long serialVersionUID = 6394504060565561132L;
    private int chosenResource;

    /**
     * CatanUseYearOfPlentyCardAction constuctor
     *
     * @param player         player who is calling the action
     * @param chosenResource
     */
    public CatanUseYearOfPlentyCardAction(GamePlayer player, int chosenResource) {
        super(player);
        this.chosenResource = chosenResource;
        Log.d(TAG, "CatanUseYearOfPlentyCardAction called");
    }

    public int getChosenResource() {
        return chosenResource;
    }
}
