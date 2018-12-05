package edu.up.cs.androidcatan.catan.actions;

import java.util.ArrayList;

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
public class CatanRobberDiscardAction extends GameAction {
    private int playerId;
    private int[] robberDiscardedResources;

    /**
     * CatanRobberDiscardAction
     * @param player player who is calling the action
     * @param playerId plauer ID for who is calling the action
     * @param robberDiscardedResources array of ints for how much of each resource to discard
     */
    public CatanRobberDiscardAction (GamePlayer player, int playerId, int[] robberDiscardedResources) {
        super(player);
        this.playerId = playerId;
        this.setRobberDiscardedResources(robberDiscardedResources);
    }

    public int getPlayerId () {
        return playerId;
    }

    public void setRobberDiscardedResources (int[] robberDiscardedResources) {
        this.robberDiscardedResources = robberDiscardedResources;
    }

    public int[] getRobberDiscardedResources () {
        return robberDiscardedResources;
    }
}
