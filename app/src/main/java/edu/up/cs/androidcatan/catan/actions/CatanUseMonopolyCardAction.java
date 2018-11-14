package edu.up.cs.androidcatan.catan.actions;

import android.util.Log;

import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

public class CatanUseMonopolyCardAction extends GameAction {
    private static final String TAG = "CatanUseMonopolyCardAction";
    private int chosenResource;
    private int player2Resource;
    private int player3Resource;
    private int player4Resource;


    public CatanUseMonopolyCardAction(GamePlayer player, int chosenResource, int player2Resource, int player3Resource, int player4Resource){
        super(player);
        this.chosenResource = chosenResource;
        this.player2Resource = player2Resource;
        this.player3Resource = player3Resource;
        this.player4Resource = player4Resource;

        Log.d(TAG, "CatanUseMonopolyCardAction called");
    }

    public int getChosenResource() {
        return chosenResource;
    }

    public int getPlayer2Resource() {
        return player2Resource;
    }

    public int getPlayer3Resource() {
        return player3Resource;
    }

    public int getPlayer4Resource() {
        return player4Resource;
    }

}
