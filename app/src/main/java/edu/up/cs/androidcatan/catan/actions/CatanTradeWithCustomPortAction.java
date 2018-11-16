package edu.up.cs.androidcatan.catan.actions;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanTradeWithCustomPortAction extends GameAction {
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    private int resourceGiveId;
    private int resourceRecId;
    public CatanTradeWithCustomPortAction(GamePlayer player, int resourceGiveId, int resourceRecId) {
        super(player);
        this.resourceGiveId = resourceGiveId;
        this.resourceRecId = resourceRecId;
    }

    public int getResourceGiveId () {
        return resourceGiveId;
    }

    public void setResourceGiveId (int resourceGiveId) {
        this.resourceGiveId = resourceGiveId;
    }

    public int getResourceRecId () {
        return resourceRecId;
    }

    public void setResourceRecId (int resourceRecId) {
        this.resourceRecId = resourceRecId;
    }
}
