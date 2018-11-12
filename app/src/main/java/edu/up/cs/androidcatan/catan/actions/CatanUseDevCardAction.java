package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;
/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 1, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanUseDevCardAction extends GameAction {
    private int devCardId;

    public CatanUseDevCardAction(GamePlayer player, int devCardId){
        super(player);
        //Log.d(TAG, "CatanUseDevCardAction: ");
    }

}
