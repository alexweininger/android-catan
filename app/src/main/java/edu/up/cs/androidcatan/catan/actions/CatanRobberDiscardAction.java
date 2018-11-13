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
public class CatanRobberDiscardAction extends GameAction {
    int playerId;
    int[] robberDiscardedResources;
    public CatanRobberDiscardAction(GamePlayer player, int playerId, int[] robberDiscardedResources)
    {
        super(player);
        this.playerId = playerId;
        this.robberDiscardedResources = robberDiscardedResources;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int[] getRobberDiscardedResources() {
        return robberDiscardedResources;
    }
}
