package edu.up.cs.androidcatan.game;

import android.util.Log;

import edu.up.cs.androidcatan.game.actionMsg.GameAction;
import edu.up.cs.androidcatan.game.infoMsg.BindGameInfo;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;
import edu.up.cs.androidcatan.game.util.NetworkObjectPasser;

/**
 * A Player object that is used as a proxy for the real player that is on another
 * machine on the network. Whenever a message is sent to the ProxyPlayer object,
 * it serializes the message and sends it across the network; when
 * the ProxyPlayer object receives a message from the network, it
 * unserializes the message and sends it to its game.
 * 
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class ProxyPlayer implements GamePlayer {


    // the game object
    private Game game;
    
    // the object that connects us to the network
    private NetworkObjectPasser networkPasser;

    // whether this player is ready to play the game.  In this case, being
    // ready essentially means that the a connection with a remote player
    // has been established
    private boolean isReady;

    /**
     * ProxyPlayer constructor.
     * 
     * @param portNum
     * 		the port number through which we connect to our client
     */
    public ProxyPlayer(int portNum) {
    	
    	Log.i("ProxyPlayer", "creating Proxy Player");
        
        // set instance variables to their initial values
        game = null; // the game
        isReady = false; // whether we are ready

        // create our network-connection object, connecting as a server
        networkPasser =
        		new NetworkObjectPasser(null, portNum) {
        	
        	// callback method, called whenever we receive an object
        	// that has come across the network
        	public void onReceiveObject(Object obj) {
        		if (obj instanceof GameAction) {
        			// if it's a game action (which it should be), send
        			// the action to the game        			
        			GameAction action = (GameAction)obj;
        			action.setPlayer(ProxyPlayer.this);
        			game.sendAction(action);
        		}
        	}
        };
    }

    /**
     * Tells whether the player is ready to play the game.
     *
     * @return a boolean value indicating whether the player is ready
     *   to play.
     */
    public boolean isReady() {
        // return value based on whether we are marked as being ready
        return isReady;
    }

    
    /**
     * Starts the game. (In this case the constructor has already done
     * all the work.)
     */
    public void start() {
    }

    /**
     * Used by the game to send a GameInfo object to this player
     * 
     * @param state
     * 		The state to send
     */
    public void sendInfo(GameInfo state) {
    	if (game == null && state instanceof BindGameInfo) {
    		// If we're just starting (so we don't know who
    		// game is), then it had better be a BindGameInfo
    		// message. Get the game from the BindGameInfo
    		// object so that we have the connection for
    		// future messages.
    		game = ((BindGameInfo)state).getGame();
    	}
    	
    	// Null out the game from the GameInfo object (if present),
    	// so that the entire game does not get passed across the
    	// network
    	state.setGame(null);
    	
    	// send the state across the network
    	networkPasser.sendObject(state);
	}
    
    
    /**
     * Set this game as a GUI. (Should never be called because the
     * 'supportsGui' method returns false.)
     */
    public final void gameSetAsGui(GameMainActivity a) {
    }
    
    /**
     * Set this game as a GUI. (Should never be called because the
     * 'supportsGui' method returns false.)
     */
    public void setAsGui(GameMainActivity a) {
    }
    
    /**
     * Tells whether the this player requires a GUI.
     * 
     * @return
     * 		false, since this player does not require a GUI
     */
    public boolean requiresGui() {
    	return false;
    }
    
    /**
     * Tells whether the this player support a GUI.
     * 
     * @return
     * 		false, since this player does not support a GUI
     */
    public boolean supportsGui() {
    	return false;
    }
}

