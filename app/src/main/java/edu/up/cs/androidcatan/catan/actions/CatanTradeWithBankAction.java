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
public class CatanTradeWithBankAction extends GameAction implements Serializable {
    private static final long serialVersionUID = 5086307496325687953L;
    private int resourceIdGiving;
    private int resourceIdRec;

    public CatanTradeWithBankAction (GamePlayer player, int resourceIdGiving, int resourceIdRec) {
        super(player);
        this.resourceIdGiving = resourceIdGiving;
        this.resourceIdRec = resourceIdRec;
    }

    public int getResourceIdGiving () {
        return resourceIdGiving;
    }

    public int getResourceIdRec () {
        return resourceIdRec;
    }
}
