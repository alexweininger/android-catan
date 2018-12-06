package edu.up.cs.androidcatan.catan.actions;

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

public class CatanConfirmAction extends GameAction implements Serializable {
    private static final long serialVersionUID = -6209535461622400857L;

    /**
     * CatanConfirmAction constructor
     *
     * @param player the player who is calling the action
     */
    public CatanConfirmAction(GamePlayer player) {
        super(player);
    }
}
