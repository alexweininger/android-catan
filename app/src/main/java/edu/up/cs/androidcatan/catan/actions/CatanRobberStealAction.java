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
public class CatanRobberStealAction extends GameAction implements Serializable {
    private static final long serialVersionUID = 9193553751976296741L;
    private int playerId;
    private int stealingFromPlayerId;
    public CatanRobberStealAction(GamePlayer player, int playerId, int stealingFromPlayerId) {
        super(player);
        this.playerId = playerId;
        this.stealingFromPlayerId = stealingFromPlayerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getStealingFromPlayerId() {
        return stealingFromPlayerId;
    }
}
