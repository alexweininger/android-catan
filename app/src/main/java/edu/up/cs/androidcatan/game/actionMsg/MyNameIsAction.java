package edu.up.cs.androidcatan.game.actionMsg;

import edu.up.cs.androidcatan.game.GamePlayer;

/**
 * An action by which the player tells the game its name
 * (typically the human's name, if it's a GameHumanPlayer).
 *
 * @author Steven R. Vegdahl 
 * @version July 2013
 */
public class MyNameIsAction extends GameAction {
	
	// to satisfy the Serializable interface
	private static final long serialVersionUID = -4574617895412648866L;
	
	// the player's name
	private String name;
	
	/** constructor
	 * 
	 * @param p
	 * 		the player who sent the action
	 * @param name
	 * 		the player's name
	 */
	public MyNameIsAction(GamePlayer p, String name) {
		super(p); // invoke superclass constructor
		this.name = name; // set the name
	}
	
	/**
	 * getter-method for the name
	 * 
	 * @return
	 * 		the player's name
	 */
	public String getName() {
		return name;
	}
	
}
