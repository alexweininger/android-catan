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
public class CatanRobberStealAction extends GameAction {
    int playerId;
    int stealId;

    public CatanRobberStealAction(GamePlayer player, int playerId, int stealId) {
        super(player);
        this.playerId = playerId;
        this.stealId = stealId;
    }
}
