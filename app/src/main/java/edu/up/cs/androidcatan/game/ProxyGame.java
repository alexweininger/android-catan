package edu.up.cs.androidcatan.game;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import edu.up.cs.androidcatan.game.actionMsg.GameAction;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;
import edu.up.cs.androidcatan.game.util.IPCoder;
import edu.up.cs.androidcatan.game.util.NetworkObjectPasser;

/**
 * A Game object that is used as a proxy for the real game that is on another
 * machine on the network.  Each ProxyGame is associated with exactly one
 * Player object.  Whenever a message is sent to the ProxyGame object,
 * it serializes the message and sends it across the network; when
 * the ProxyGame object receives a message from the network, it
 * unserializes the message and sends it to its player.
 * 
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class ProxyGame implements Game {

    // the player associated with this game
    private GamePlayer player;
    
	// a queue of objects that are collected, which might have been sent over the
    // network before we are connected to a player
    private Queue<GameInfo> queuedObjectsForPlayer = new LinkedList<GameInfo>();
    
    // the network-connection object
    private NetworkObjectPasser networkPasser;

    /**
     * Static method used instead of a constructor, so that null can be returned if
     * the creation was unsuccessful.
     * 
     * @param portNum
     * 			the port number for connecting to the host
     * @param ipCode
     * 			the IP code of the server where the game is hosted
     * @return
     */
    public static ProxyGame create(int portNum, String ipCode) {
    	// create the game object
    	ProxyGame rtnVal = new ProxyGame(portNum, ipCode);
    	
    	// see if a connection becomes established; if so, return
    	// the object, otherwise null
    	boolean isReady = rtnVal.networkPasser.isReady();
    	if (isReady) {
    		return rtnVal;
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * ProxyGame constructor (private)
     *
     * @param portNum
     * 		the port number on the server to connect to
     * @param ipCode
     * 		the IP code of the remote site to where the actual
     *  	game is running
     */
    private ProxyGame(int portNum, String ipCode) {

        // set instance variables to their initial values
        player = null;
        ipCode = IPCoder.decodeIp(ipCode); // convert to IP address
        
        // create the network-connector object
        networkPasser = new NetworkObjectPasser(ipCode, portNum) {
        	
        	// callback method, called whenever an object is sent to us from
        	// across the network
        	public void onReceiveObject(Object obj) {
        		Log.i("ProxyGame", "received object ("+obj.getClass()+")");
        		try {
        			boolean b = obj instanceof GameInfo;
        			if (b) {
        				// object is a GameStae object
        				GameInfo gs = (GameInfo)obj;
        				gs.setGame(ProxyGame.this);
        				synchronized(this) {
        					if (player == null) {
        						// if the player has not been connected, save the
        						// object in a queue
        						Log.i("ProxyGame", "adding object to queue");
        						queuedObjectsForPlayer.add(gs);
        					}
        					else {
        						// if the player has been connected, send the object
        						// directly to the player
                				Log.i("ProxyGame", "about to send state to player");
                				player.sendInfo(gs);
                				Log.i("ProxyGame", "... done sending state");
        					}
        				}
        			}
        			else {
        				// ignore if the object is not a GameInfo object
        				Log.i("ProxyGame", "object NOT being sent to player");
        			}
        		}
        		catch (Exception x) {
        			// if any other exception occurs, log it
        			Log.i(x.getClass().toString(), x.getMessage());
        		}
        	}
        };
    }

    /**
     * Method used by player to send an action to this Game object.
     *
     * @param action  the action object to apply
     */
	public final void sendAction(GameAction action) {
    	// Send the action across the socket, nulling out the player in
		// the action so that the entire player is not serialized.
    	if (action != null) {
    		action.setPlayer(null);
    		networkPasser.sendObject(action);
    	}
    }

	/**
	 * Starts the game. In this context, we know that the array will
	 * contain exactly one player.
	 */
	public void start(GamePlayer[] players) {
		Log.i("ProxyGame", "start() called");
		
		// if player has already been bound, ignore
		if (player != null) return;
		
		// if the player array somehow something other than
		// a single element, ignore
		if (players.length != 1) return;
		
		// start the player
		if (players[0] != null) {
			players[0].start(); // start our player
		}
		
		// loop through and empty (and send) the objects that might have
		// accumulated in the queue before the player was bound
		for (;;) {
			GameInfo unqueuedObject;
			synchronized (this) {
				if (queuedObjectsForPlayer.isEmpty()) {
					// queue is finally empty: bind player and return
					player = players[0];
					return;
				}
				else {
					// queue not empty, so remove an object from the queue
					unqueuedObject = queuedObjectsForPlayer.remove();
				}
			}
			
			// send the just=unqueued object to the player
			Log.i("ProxyGame", "sending queued object to player ("+unqueuedObject.getClass()+")");
			players[0].sendInfo(unqueuedObject);
		}
	}
}

