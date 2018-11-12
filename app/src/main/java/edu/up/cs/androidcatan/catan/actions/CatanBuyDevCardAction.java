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
public class CatanBuyDevCardAction extends GameAction {
    private int developmentCardIdToBuy;

    public CatanBuyDevCardAction (GamePlayer player, int developmentCardIdToBuy) {
        super(player);
        this.developmentCardIdToBuy = developmentCardIdToBuy;
    }

    public int getDevelopmentCardIdToBuy () {
        return developmentCardIdToBuy;
    }

    public void setDevelopmentCardIdToBuy (int developmentCardIdToBuy) {
        this.developmentCardIdToBuy = developmentCardIdToBuy;
    }
}
