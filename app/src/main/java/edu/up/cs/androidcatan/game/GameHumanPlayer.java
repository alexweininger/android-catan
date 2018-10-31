package edu.up.cs.androidcatan.game;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import edu.up.cs.androidcatan.game.actionMsg.GameOverAckAction;
import edu.up.cs.androidcatan.game.actionMsg.MyNameIsAction;
import edu.up.cs.androidcatan.game.actionMsg.ReadyAction;
import edu.up.cs.androidcatan.game.infoMsg.BindGameInfo;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;
import edu.up.cs.androidcatan.game.infoMsg.GameOverInfo;
import edu.up.cs.androidcatan.game.infoMsg.StartGameInfo;
import edu.up.cs.androidcatan.game.infoMsg.TimerInfo;
import edu.up.cs.androidcatan.game.util.GameTimer;
import edu.up.cs.androidcatan.game.util.MessageBox;
import edu.up.cs.androidcatan.game.util.Tickable;

/**
 * class GameHumanPlayer
 * 
 * is an abstract base class for a player that is controlled by a human. For any
 * particular game, a subclass should be created that can display the current
 * game state and responds to user commands.
 * 
 * @author Steven R. Vegdahl
 * @author Andrew Nuxoll
 * @version July 2013
 * 
 */
public abstract class GameHumanPlayer implements GamePlayer, Tickable {
	/**
	 * instance variables
	 */
	protected Game game; // the game
	protected int playerNum; // my player ID
	protected String name; // my player's name
	protected String[] allPlayerNames; // the names of all the player
	private Handler myHandler; // my thread's handler
	private GameMainActivity myActivity; // the current activity
	private GameTimer myTimer = new GameTimer(this); // my player's timer
	private boolean gameOver; // whether the game is over

	/**
	 * constructor
	 * 
	 * @param name the name of the player
	 */

	public GameHumanPlayer(String name) {
		// set the name via the argument

			this.name = name;

		
		// mark game as not being over
		this.gameOver = false;
		
		// get new handler for this thread
		this.myHandler = new Handler();
	}
	
	/**
	 * Returns this object's game timer
	 * 
	 * @return this object's game timer.
	 */
	protected final GameTimer getTimer() {
		return myTimer;
	}

	/**
	 * "tick" call-back method, called when a timer message is received.
	 */
	public final void tick(GameTimer timer) {
		// send the message to the player
		sendInfo(new TimerInfo(timer));
	}
	
	/**
	 * Returns the GUI's top object; used for flashing.
	 * 
	 * @return the GUI's top object.
	 */
	public abstract View getTopView();
	
	/**
	 * Start's the GUI's thread, setting up handler.
	 */
	public void start() {
		// Don't need to do anything since I'm already looping
		// and have a handler.
	}
	
	/**
	 * perform any initialization that needs to be done after the player
	 * knows what their game-position and opponents' names are.
	 */
	protected void initAfterReady() {
		// by default, we do nothing
	}
	
	/**
	 * Sets this player as the one attached to the GUI. Saves the
	 * activity, then invokes subclass-specific method.
	 */
	public final void gameSetAsGui(GameMainActivity a) {

			myActivity = a;
			setAsGui(a);

	}

	/*
	 * ====================================================================
	 * Abstract Methods
	 * 
	 * Create the game specific functionality for this human player by
	 * sub-classing this class and implementing the following methods.
	 * --------------------------------------------------------------------
	 */

	/*
	 * ====================================================================
	 * Public Methods
	 * --------------------------------------------------------------------
	 */
	
	/**
	 * Flashes the background of the GUI--typically indicating that some kind
	 * of error occurred. Caveat: if multiple flash calls overlap, the prior one
	 * will take precedence. 
	 * 
	 * @param color
	 * 			the color to flash
	 * @param duration
	 * 			the number of milliseconds the flash should last
	 */
	protected void flash(int color, int duration) {
		// get the top view, ignoring if null
		View top = this.getTopView();
		if (top == null) return;
		
		// save the original background color; set the new background
		// color
		int savedColor = getBackgroundColor(top);
		top.setBackgroundColor(color);
		
		// set up a timer event to set the background color back to
		// the original.
		myHandler.postDelayed(new Unflasher(savedColor), duration);
	}
	
	/**
	 * helper-class to finish a "flash.
	 * 
	 */
	private class Unflasher implements Runnable {
		
		// the original color
		private int oldColor;
		
		// constructor
		public Unflasher(int oldColor) {
			this.oldColor = oldColor;
		}
		
		// method to run at the appropriate time: sets background color
		// back to the original
		public void run() {
			View top = GameHumanPlayer.this.getTopView();
			if (top == null) return;
			top.setBackgroundColor(oldColor);
		}
	}
	
	/**
	 * helper-method to get the background color of a view
	 * @param v
	 * 			the view
	 * @return
	 * 			the (int representation) of the background color,
	 * 			or "transparent" if the color could not be deduced
	 */
	private static int getBackgroundColor(View v) {
		 int color = Color.TRANSPARENT;
         Drawable background = v.getBackground();
         if (background instanceof ColorDrawable) {
             color = ((ColorDrawable) background).getColor();
         }
         return color;
	}

	/**
	 * Sends a 'state' object to the game's thread.
	 * 
	 * @param info
	 * 		the information object to send
	 */
	public void sendInfo(GameInfo info) {
		// wait until handler is there
		while (myHandler == null) Thread.yield();

		// post message to the handler
		Log.d("sendInfo", "about to post");
		myHandler.post(new MyRunnable(info));
		Log.d("sendInfo", "done with post");
	}

	/**
	 * Callback method, called when player gets a message
	 * 
	 * @param info
	 * 		the message
	 */
	public abstract void receiveInfo(GameInfo info);

	
	/**
	 * Helper-class that runs the on the GUI's main thread when
	 * there is a message to the player.
	 */
	private class MyRunnable implements Runnable {
		// the message to send to the player
		private GameInfo myInfo;
		
		// constructor
		public MyRunnable(GameInfo info) {
			myInfo = info;
		}
		
		// the run method, which is run in the main GUI thread
		public void run() {
			
			// if the game is over, just tell the activity that the game is over
			if (gameOver) {
				myActivity.setGameOver(true);
				return;
			}
			
			if (game == null) {
				// game has not been bound: the only thing we're looking for is
				// BindGameInfo object; ignore everything else
				if (myInfo instanceof BindGameInfo) {
					Log.i("GameHumanPlayer", "binding game");
					BindGameInfo bgs = (BindGameInfo)myInfo;
					game = bgs.getGame(); // set the game
					playerNum = bgs.getPlayerNum(); // set our player id
					
					// respond to the game, telling it our name
					game.sendAction(new MyNameIsAction(GameHumanPlayer.this, name));
				}
			}
			else if (allPlayerNames == null) {
				// here, the only thing we're looking for is a StartGameInfo object;
				// ignore everything else
				if (myInfo instanceof StartGameInfo) {
					Log.i("GameHumanPlayer", "notification to start game");
					
					// update our player-name array
					allPlayerNames = ((StartGameInfo)myInfo).getPlayerNames();

					// perform game-specific initialization
					initAfterReady();
					
					// tell the game we're ready to play the game
					game.sendAction(new ReadyAction(GameHumanPlayer.this));
				}
			}
			else if (myInfo instanceof GameOverInfo) {
				// if we're being notified the game is over, finish up
				
				// perform the "gave over" behavior--by default, to show pop-up message
				gameIsOver(((GameOverInfo)myInfo).getMessage());
				
				// if our activity is non-null (which it should be), mark the activity as over
				if (myActivity != null) myActivity.setGameOver(true);
				
				// acknowledge to the game that the game is over
				game.sendAction(new GameOverAckAction(GameHumanPlayer.this));
				
				// set our instance variable, to indicate the game as over
				gameOver = true;
			}
			else if (myInfo instanceof TimerInfo) {
				// if we have a timer-tick, and it's our timer object,
				// directly invoke the subclass method; otherwise, pass
				// it on as a message
				if (((TimerInfo)myInfo).getTimer() == myTimer) {
					// checking that it's from our timer
					timerTicked();
				}
				else {
					receiveInfo(myInfo);
				}
			}
			else {
				// pass the state on to the subclass
				receiveInfo(myInfo);
			}
		}
	}
	
	/**
	 * callback method--called when we are notified that the game is over
	 * 
	 * @param msg
	 * 		the "game over" message sent by the game
	 */
	protected void gameIsOver(String msg) {
		// the default behavior is to put a pop-up for the user to see that tells
		// the game's result
		MessageBox.popUpMessage(msg, myActivity);
	}
	
	/**
	 * Tells whether this class requires a GUI to run
	 * 
	 * @return true, since this player needs to be running as a GUI
	 */
	public final boolean requiresGui() {
		return true;
	}
	
	/**
	 * Tells whether this class supports the running in a GUI
	 * 
	 * @return true, since this player actually needs to be running as a GUI
	 */	
	public final boolean supportsGui() {
		return true;
	}

	/**
	 * Invoked whenever the player's timer has ticked. It is expected
	 * that this will be overridden in many games.
	 */
	protected void timerTicked() {
		// by default, do nothing
	}

}// class GameHumanPlayer

