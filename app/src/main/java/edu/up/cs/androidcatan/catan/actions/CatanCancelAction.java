package edu.up.cs.androidcatan.catan.actions;

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
public class CatanCancelAction extends GameAction {

    /**
     * CaatanCancelAcation constructor
     * @param player player calling the action
     */
    public CatanCancelAction(GamePlayer player){
        super(player);
    }
}
