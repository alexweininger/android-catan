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

public class CatanRobberMoveAction extends GameAction implements Serializable {
    private static final long serialVersionUID = -3677845305432070761L;
    private int hexagonId;
    private int playerId;

    /**
     * CatanRobberMoveAction constructor
     *
     * @param player    the player who is calling the action
     * @param playerId  the ID for the player who is calling the action
     * @param hexagonId the ID of the tile to move the robber to
     */
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
