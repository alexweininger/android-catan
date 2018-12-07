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

public class CatanUseDevCardAction extends GameAction implements Serializable {
    private static final String TAG = "CatanUseDevCardAction";
    private static final long serialVersionUID = 6552057080959641246L;
    private int devCardId;

    /**
     * CatanUseDevCardAction constructor
     *
     * @param player    the player calling the action
     * @param devCardId the id of the dev card being used
     */
    public CatanUseDevCardAction(GamePlayer player, int devCardId) {
        super(player);
        Log.d(TAG, "CatanUseDevCardAction: called with player = [" + player + "], devCardId = [" + devCardId + "]");
        this.devCardId = devCardId;
    }
}
