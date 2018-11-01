package edu.up.cs.androidcatan.game.actionMsg;

import edu.up.cs.androidcatan.game.GamePlayer;

/**
 * An action by which the player tells the game its name
 * (typically the human's name, if it's a GameHumanPlayer).
 *
 * @author Steven R. Vegdahl 
 * @version July 2013
 */
public class ReadyAction extends GameAction {
	
	// to satisfy the Serializable interface
	private static final long serialVersionUID = -5286032209480788772L;

	/** constructor
	 * 
	 * @param p
	 * 		the player who sent the action
	 */
	public ReadyAction(GamePlayer p) {
		super(p);
	}
}
