package edu.up.cs.androidcatan.game.util;

/**
 * An inteface that defines an object that can respond to a GameTimer "tick".
 * 
 * @author Steven R. Vegdahl
 * @version July 2013
 *
 */
public interface Tickable {
	/**
	 * callback method that is invoked when the timer "ticks"
	 * 
	 * @param timer
	 * 		the timer that is associated with the "tick"
	 */
	public abstract void tick(GameTimer timer);
}
