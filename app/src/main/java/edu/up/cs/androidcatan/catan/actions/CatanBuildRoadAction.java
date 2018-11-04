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
public class CatanBuildRoadAction extends GameAction {
    private int intersectionAId, intersectionBid, ownerId;

    public CatanBuildRoadAction(GamePlayer player, int intersectionAId, int intersectionBid, int ownerId) {
        super(player);
        this.intersectionAId = intersectionAId;
        this.intersectionBid = intersectionBid;
        this.ownerId = ownerId;
    }

    public int getIntersectionAId() {
        return intersectionAId;
    }

    public void setIntersectionAId(int intersectionAId) {
        this.intersectionAId = intersectionAId;
    }

    public int getIntersectionBid() {
        return intersectionBid;
    }

    public void setIntersectionBid(int intersectionBid) {
        this.intersectionBid = intersectionBid;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
