package edu.up.cs.androidcatan.catan.actions;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanTradeWithCustomPortAction extends GameAction {

    private int resourceGiveId;
    private int resourceRecId;
    public CatanTradeWithCustomPortAction(GamePlayer player, int resourceGiveId, int resourceRecId) {
        super(player);
        this.resourceGiveId = resourceGiveId;
        this.resourceRecId = resourceRecId;
    }

    public int getResourceGiveId () { return resourceGiveId; }

    public int getResourceRecId () { return resourceRecId; }
}
