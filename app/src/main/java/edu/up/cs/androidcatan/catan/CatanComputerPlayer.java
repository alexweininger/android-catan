package edu.up.cs.androidcatan.catan;

import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.game.GameComputerPlayer;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanComputerPlayer extends GameComputerPlayer {

    /**
     * ctor does nothing extra
     */
    public CatanComputerPlayer(String name) {
        super(name);
    }

    /**
     * callback method--game's state has changed
     *
     * @param info
     * 		the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        if (!(info instanceof CatanGameState)) return;

        CatanGameState gs = (CatanGameState) info;

        if(gs.getCurrentPlayerId() != this.playerNum) return;

        sleep(200);

        game.sendAction(new CatanEndTurnAction(this));
    }//receiveInfo

}
