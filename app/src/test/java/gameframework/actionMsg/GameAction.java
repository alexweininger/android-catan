package gameframework.actionMsg;

import java.io.Serializable;

import gameframework.GamePlayer;

/**
 * An action for a generic game.  A game action is something that a player
 * tells the game that it wants to do (e.g., put an 'X' on the top-left
 * tic-tac-toe square).  The game will then decide whether the player is
 * allowed to perform that action before effecting the action on the
 * players behalf.  Most real games will subclass GameAction to define
 * actions that are relevant to the particular game.  A GameAction contains
 * the player as part of its state; this way the game always knows what
 * player sent it the action.
 * <P>
 * Several "generic" of GameAction classes are already defined.  These
 * include MyNameIsAction and GameOverAckAction.
 *
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @version July 2013
 */
public abstract class GameAction implements Serializable {

    // to support the Serializable interface
    private static final long serialVersionUID = 30672013L;

    // the player who generated the request
    private GamePlayer player;

    /**
     * constructor for GameAction
     *
     * @param player
     * 		the player who created the action
     */
    public GameAction(GamePlayer player) {
        this.player = player;
    }

    /**
     * tells the player who created the action
     *
     * @return the player who created the action
     *
     */
    public GamePlayer getPlayer() {
        return player;
    }

    /** Resets the source of the action. The intent is that it be used only
     *  by ProxyGame and ProxyPlayer.
     *
     * @param p
     * 		the new player to which the action is to be associated
     */
    public void setPlayer(GamePlayer p) {
        this.player = p;
    }
}
