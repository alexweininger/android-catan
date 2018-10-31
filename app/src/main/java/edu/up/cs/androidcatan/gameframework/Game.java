package edu.up.cs.androidcatan.gameframework;

import edu.up.cs.androidcatan.gameframework.actionMsg.GameAction;

/**
 * To support remote play, this game framework has two types of Games: local
 * games and remote games that are represented by a proxy. Both types adhere to
 * this common interface.
 *
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @version July 2013
 * @see LocalGame
 * @see ProxyGame
 */

public interface Game {

    /**
     * starts the game
     *
     * @param players
     * 			the players who are in the game
     */
    public abstract void start(GamePlayer[] players);

    /**
     * sends the given action to the Game object.
     *
     * @param action
     *            the action to send
     */
    public abstract void sendAction(GameAction action);

}
