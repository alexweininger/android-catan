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
public class CatanRobberDiscardAction extends GameAction implements Serializable {
    private static final long serialVersionUID = -6036756557172908598L;
    private int playerId;
    private int[] robberDiscardedResources;

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
