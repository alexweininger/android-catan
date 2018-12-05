package edu.up.cs.androidcatan.catan.actions;

import java.io.Serializable;

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
public class CatanEndTurnAction extends GameAction implements Serializable {
    private static final long serialVersionUID = -7515825710272678466L;

    /**
     * CstanEndTurnAction constuctor
     * @param player player who is calling the action
     */
    public CatanEndTurnAction(GamePlayer player){
        super(player);
    }
}
