package edu.up.cs.androidcatan.game.infoMsg;

import edu.up.cs.androidcatan.game.util.GameTimer;

/**
 * The a message from to a player (typically sent by a timer) that the timer's
 * clock has "ticked".
 *
 * @author Steven R. Vegdahl 
 * @version July 2013
 */

public class TimerInfo extends GameInfo {
	
	// to satisfy the Serializable interface
	private static final long serialVersionUID = -7138064704052644451L;

	// the timer that generated this message
	private GameTimer myTimer;
	
	/**
	 * constructor
	 * 
	 * @param timer
	 * 		the timer that generated this "tick"
	 */
	public TimerInfo(GameTimer timer) {
		myTimer = timer;
	}
	
	/**
	 * getter method for the timer
	 * 
	 * @return
	 * 		the timer that generated the "tick"
	 */
	public GameTimer getTimer() {
		return myTimer;
	}
	
}

