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
public class CatanRollDiceAction extends GameAction implements Serializable {
    private static final long serialVersionUID = -9111895641729479393L;

    /**
     * CatanRollDiceAction
     * @param player the player who is calling the action
     */
    public CatanRollDiceAction (GamePlayer player) { super(player); }
}
