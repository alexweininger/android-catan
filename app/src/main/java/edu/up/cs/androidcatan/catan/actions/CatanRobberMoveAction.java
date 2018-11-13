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
public class CatanRobberMoveAction extends GameAction {
    private int hexagonId;
    private int playerId;

    public CatanRobberMoveAction(GamePlayer player, int playerId, int hexagonId) {
        super(player);
        this.hexagonId = hexagonId;
        this.playerId = playerId;
    }

    public int getHexagonId() {
        return hexagonId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
