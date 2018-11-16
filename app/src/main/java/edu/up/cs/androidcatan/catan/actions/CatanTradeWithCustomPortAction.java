package edu.up.cs.androidcatan.catan.actions;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanTradeWithCustomPortAction extends GameAction {
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    private int resourceRecId;
    public CatanTradeWithCustomPortAction(GamePlayer player, int resourceRecId) {
        super(player);
        this.resourceRecId = resourceRecId;
    }
}
