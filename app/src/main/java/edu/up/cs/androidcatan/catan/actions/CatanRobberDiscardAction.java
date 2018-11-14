package edu.up.cs.androidcatan.catan.actions;

import java.util.ArrayList;

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
    private int playerId;
    private ArrayList<Integer> robberDiscardedResources;

    public CatanRobberDiscardAction (GamePlayer player, int playerId, ArrayList<Integer> robberDiscardedResources) {
        super(player);
        this.playerId = playerId;
        this.setRobberDiscardedResources(robberDiscardedResources);
    }

    public int getPlayerId () {
        return playerId;
    }

    public void setRobberDiscardedResources (ArrayList<Integer> robberDiscardedResources) {
        this.robberDiscardedResources = robberDiscardedResources;
    }

    public ArrayList<Integer> getRobberDiscardedResources () {
        return robberDiscardedResources;
    }
}
