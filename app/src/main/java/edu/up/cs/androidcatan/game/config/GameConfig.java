package edu.up.cs.androidcatan.game.config;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.up.cs.androidcatan.game.GameMainActivity;
import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.ProxyPlayer;

/**
 * GameConfig class
 * <P>
 * This class describes a user-specified configuration for playing a game. It
 * includes information about the number and type of players in the game and
 * other related meta-data.
 * 
 * A game initializes this class with some information and additional data is
 * provided by the user or loaded from a previously-saved configuration.
 * 
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @version July 2013
 * @see GameMainActivity
 */
public class GameConfig {

	/** a list of all valid player types that the user chooses */
	private GamePlayerType[] availTypes;

	/** a list of the names of each player */
	private ArrayList<String> selNames = new ArrayList<String>();

	/**
	 * a list of the type of each player. Each type in the list is associated
	 * with the player whose name is at the same index in selNames. Thus, this
	 * ArrayList and selNames must always be the same length. This is managed
	 * automatically via the {@link #addPlayer} and {@link #removePlayer}
	 * methods.
	 */
	private ArrayList<GamePlayerType> selTypes = new ArrayList<GamePlayerType>();
	
	/**
	 * The player type selected in the remote player tab.
	 */
	private GamePlayerType remoteSelType;

	/**
	 * if set to true, indicates the game will be run on the local computer
	 * rather than connecting to a remote server
	 */
	private boolean isLocal;

	/**
	 * if the player is connecting to a game running on another device, this
	 * string is used to store the player's name
	 */
	private String remoteName;

	/**
	 * if the player is connecting to a game running on a another device, this
	 * string contains the IP address of that device
	 */
	private String ipCode;
	
	/**
	 * the port number for connecting to another device over the network
	 */
	private int portNum;

	/**
	 * this specifies the minimum number of players required for a legal game.
	 * For example, tic-tac-toe would have a minimum (and maximum, see below) of
	 * two players.
	 */
	private int minPlayers;

	/** this specifies the maximum number of players that the game can handle */
	private int maxPlayers;

	/**
	 * This is the name of the game. This value is used to identify the game
	 * to the user.
	 */
	private String gameName;

	/**
	 * if this boolean is set to false, then this configuration can not be
	 * modified by the user and the configuration activity will be skipped
	 * (taking the players straight to the game). This can be useful when
	 * debugging.
	 */
	private boolean userModifiable;
	
	/**
	 * to create an instance of this class initial values for some instance
	 * variables must be supplied. The constructor makes a cursory effort to
	 * catch and fix bad input but, ultimately, the caller is responsible for
	 * creating a proper game definition. In particular, you must call addPlayer
	 * sufficient times to make sure that the minimum number of players is met.
	 * <P>
	 * This is the version of the constructor that is expected to be called
	 * when the game activity starts. It therefore automatically adds a "Network
	 * Player" player-type option, which always becomes the last element in the
	 * available-player list.
	 * <P>
	 * This constructor leave the list of local players empty, and sets the
	 * remote player to have a type that corresponds to the first player in
	 * the available-player list. These defaults can (and probably should) be
	 * changed by calling 'addPlayer' and 'setRemoteData'.
	 * 
	 * @param availTypes
	 * 		the list of available player types (excluding the network player, which
	 * 		is added by the constructor
	 * @param minPlayers
	 * 		the minimum number of players allowed in this game
	 * @param maxPlayers
	 * 		the maximum number of players allowed in this game
	 * @param gameName
	 * 		the name of the game
	 * @param portNum
	 * 		the port number used by this game for connecting over the network
	 */
	public GameConfig(ArrayList<GamePlayerType> availTypes, int minPlayers,
                      int maxPlayers, String gameName, int portNum) {
		
		// create an array to hold the available player types, including
		// the "Network Player" that will be added
		int arrayLength = availTypes.size()+1;
		GamePlayerType[] availArray = new GamePlayerType[arrayLength];
		
		// add the player types passed in to the constructor
		availTypes.toArray(availArray);
		
		// add the network player
		availArray[arrayLength-1] = new GamePlayerType("Network Player") {
			public GamePlayer createPlayer(String name) {
				int portNum = getPortNum();
				return new ProxyPlayer(portNum);
			}
		};
		
		// perform the initialization of the object
		initGameConfig(availArray, minPlayers, maxPlayers, gameName, portNum);
		
	}// constructor
	
	/**
	 * makes a copy of a config, but without the player information
	 * @return
	 * 		the copy of the config
	 */
	public GameConfig copyWithoutPlayers() {
		return new GameConfig(availTypes, minPlayers, maxPlayers, gameName, portNum);
	}// copyWithoutPlayers
	
	/**
	 * private version of the constructor, used to support the 'copyWithoutPlayers'
	 * method
	 * 
	 * @param availTypes
	 * 		the list of available player types (excluding the network player, which
	 * 		is added by the constructor
	 * @param minPlayers
	 * 		the minimum number of players allowed in this game
	 * @param maxPlayers
	 * 		the maximum number of players allowed in this game
	 * @param gameName
	 * 		the name of the game
	 * @param portNum
	 * 		the port number used by this game for connecting over the network
	 */
	private GameConfig(GamePlayerType[] availTypes, int minPlayers,
                       int maxPlayers, String gameName, int portNum) {
		
		// perform the initialization of the object
		initGameConfig(availTypes, minPlayers, maxPlayers, gameName, portNum);
	}
	
	private void initGameConfig(GamePlayerType[] availTypes, int minPlayers,
                                int maxPlayers, String gameName, int portNum) {
		
		// initialize the instance variables from the parameters
		this.availTypes = availTypes;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.gameName = gameName;
		this.portNum = portNum;

		// default to a local game
		this.isLocal = true;

		// set the defaults for remote-player data:
		// - name: "Guest"
		// - IP code: empty string
		// - player type: the first one in the available-player list
		setRemoteData("Guest", "", 0);

		// by default, allow the user to modify the configuration
		this.userModifiable = true;
		
	}// setRemoteData
	
	/**
	 * set the remove-player data
	 * 
	 * @param playerName
	 * 		the remote player's name
	 * @param ipCode
	 * 		the IP code used by the remote player
	 * @param menuIndex
	 * 		the index in the available-player array that denotes this
	 * 		player's type
	 */
	public void setRemoteData(String playerName, String ipCode, int menuIndex) {
		this.ipCode = ipCode;
		this.remoteName = playerName;
		this.remoteSelType = availTypes[menuIndex];
	}// setRemoteData
	
	/**
	 * Saves this configuration data in a file so that it can be later reused. The
	 * format used is a sequence of serialized objects:
	 * - a Boolean that denotes whether the "Local Game" tab was selected
	 * - the name of the remote player (String)
	 * - the type of the remote player (String, which is the menu-text that denotes the type)
	 * - the IP code for the remote player (String)
	 * - the list of player names (ArrayList<String>)
	 * - a sequence of N Strings that denote the (menu-text) for the respective
	 *   player types, where N is the number of elements in the list of player names
	 * 
	 * @param fileName
	 * 		the name of the file for storing
	 * @param activity
	 * 		the current activity
	 * @return
	 * 		a boolean that denotes whether the operation was successful
	 */
	public boolean saveConfig(String fileName, GameMainActivity activity) {
		
		// if the player-name and player-type arrays are of different sizes, something
		// is terribly wrong: give up
		if (selNames.size() != selTypes.size()) {
			return false;
		}
		
		try {
			// open the output file and connect it a stream for writing serialized objects
			FileOutputStream fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			// write out whether the local or remote tab is selected
			oos.writeObject(isLocal);

			// write out player name, player type and IP code
			oos.writeObject(remoteName);
			oos.writeObject(remoteSelType.getTypeName());
			oos.writeObject(ipCode);
			
			// write out the players' names
			oos.writeObject(selNames);
			
			// write out the names of the players' types
			for (GamePlayerType gpt : selTypes) {
				oos.writeObject(gpt.getTypeName());
			}
			
			// close the stream
			oos.close();
		} catch (IOException e) {
			// return false if there was a problem
			Log.i("MainActivity", "File writing problem.");
			return false;
		}
		
		// there was no problem: return true
		return true;
	}
	
	/**
	 * Saves this configuration data in a file so that it can be later reused. The
	 * format used listed in the method-header comment for the 'saveConfig' method.
	 * 
	 * @param fileName
	 * 		the name of the file that contains the configuration information
	 * @param activity
	 * 		the current activity
	 * @return
	 * 		a boolean that denotes whether the operation was successful
	 */
	public boolean restoreSavedConfig(String fileName, GameMainActivity activity) {
		
		// the input stream from which we will read the objects
		ObjectInputStream ois = null;
		
		try {
			// attempt to open the input file; set it up to read serialized objects
			FileInputStream fis = activity.openFileInput(fileName);
			ois = new ObjectInputStream(fis);
			
			// read in the remote player name and type, and the IP code
			boolean isLocalTemp = (Boolean)ois.readObject();
			String nameTemp = ois.readObject().toString();
			String typeNameTemp = ois.readObject().toString();
			String ipTemp = ois.readObject().toString();
			
			// map the player type name to the corresponding player type
			GamePlayerType gpt = findPlayerType(typeNameTemp);
			if (gpt == null) {
				// if could not map the name to a player type, there is an
				// inconsistency; abort operation
				return false;
			}
			
			// read in the players' names as an ArrayList<String>
			Object obj = ois.readObject();
			if (!(obj instanceof ArrayList<?>)) {
				// if not an ArrayList, abort
				return false;
			}
			ArrayList<String> selNamesTemp = new ArrayList<String>();
			for (Object o : (ArrayList<?>)obj) {
				// force elements to be strings
				selNamesTemp.add(o.toString());
			}
			
			// if the number of players is out of the legal range for the game, abort
			int size = selNamesTemp.size();
			if (size < minPlayers || size > maxPlayers) {
				return false;
			}
			
			// read in the names of the players' types, converting to string
			ArrayList<String> typeNames = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				typeNames.add(ois.readObject().toString());
			}
			
			// convert player type names to player types; abort if there is a
			// mapping failure
			ArrayList<GamePlayerType> selTypesTemp = new ArrayList<GamePlayerType>();
			for (String typeName : typeNames) {
				GamePlayerType gpt2 = findPlayerType(typeName);
				if (gpt2 == null) {
					// mapping failure: abort
					return false;
				}
				selTypesTemp.add(gpt2);
			}

			// everything was successful, so modify the configuration by replacing the
			// old information with the new
			this.isLocal = isLocalTemp;
			this.remoteName = nameTemp;
			this.remoteSelType = gpt;
			this.ipCode = ipTemp;
			this.selNames = selNamesTemp;
			this.selTypes = selTypesTemp;
			
			// return "success"
			return true;
		}
		catch (FileNotFoundException fnfx) {
			// if the file was not there, that's OK, we just leave the current information
			// alone; this is therefore considered a "success"
			return true;
		}
		catch (ClassCastException ccx) {
			// if there was a class cast problem anywhere, there is inconsistent data in
			// the file: abort
			return false;
		}
		catch (IOException e) {
			// abort if I/O exception
			Log.i("MainActivity", "File reading problem.");
			return false;
		}
		catch (ClassNotFoundException cnfx) {
			// abort of there if one of the serialized objects somehow was (or contained)
			// an object in a class that we do not know about.
			Log.i("MainActivity", "Object/class reading problem.");
			return false;
		}
		finally {
			// close the stream
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					// if error closing stream, well, at least we tried
				}
			}
		}
	}//restoreSavedConfig
	
	/**
	 * helper-method to convert a menu-item string to a GamePlayerType object in the
	 * available player list.
	 * 
	 * @param menuString
	 * 		the string in the menu that corrsponds to the given player type
	 * @return
	 * 		the GamePlayerType object that corresponds to that string, or null
	 * 		if no such GamePlayerType object exists
	 */
	private GamePlayerType findPlayerType(String menuString) {
		// search/match the available-types array, returning the
		// corresponding GamePlayerType object
		for (GamePlayerType gpt : availTypes) {
			if (menuString.equals(gpt.getTypeName())) {
				return gpt;
			}
		}
		
		// if we get here, there was no match, so return null
		return null;
	}// findPlayerTypes


	/**
	 * @return the available player types
	 */
	public GamePlayerType[] getAvailTypes() {
		return availTypes;
	}// getAvailTypes
	
	/**
	 * 
	 * @return
	 * 		the port number used by this game for internet connections
	 */
	public int getPortNum() {
		return portNum;
	}// getPortNum

	/**
	 * addPlayer
	 * 
	 * adds a new player to the configuration
	 * 
	 * @param name
	 *            the player's name
	 * @param typeIndex
	 *            valid index of the player's type in availTypes
	 */
	public void addPlayer(String name, int typeIndex) {
		// adjust illegal input
		if (name == null) {
			// treat null name as empty string
			name = "";
		}
		
		// treat bad index as having value 0
		if (typeIndex < 0 || typeIndex >= availTypes.length) {
			typeIndex = 0;
		}

		// don't go over the maximum
		if (selNames.size() >= this.maxPlayers)
			return;

		// append the new values
		selNames.add(name);
		selTypes.add(this.availTypes[typeIndex]);
		
	}// addPlayer

	/**
	 * removePlayer
	 * 
	 * removes a player from the configuration
	 * 
	 * @param index
	 *            of the player to remove
	 */
	public void removePlayer(int index) {
		// catch and ignore invalid index
		if ((index < 0) || (index >= selNames.size()))
			return;

		this.selNames.remove(index);
	}// removePlayer

	/**
	 * @return an array of the player names
	 */
	public String[] getSelNames() {
		if (isLocal) {
			return (String[]) this.selNames.toArray();
		}
		else {
			return new String[]{remoteName};
		}
	}// getSelNames

	/**
	 * get the name of the player at a given index
	 * 
	 * @param index
	 *            of the player whose name is wanted
	 * @return the player's name or null if index is invalid
	 */
	public String getSelName(int index) {
		if (isLocal) {
			// if we're local, catch and ignore an invalid index;
			// otherwise, return the appropriate player's name
			if ((index < 0) || (index >= selNames.size())) {
				return null;
			}
			else {
				return this.selNames.get(index);
			}
		}
		else { // we're remote: the only valid index is zero, so
			// return the remote name or null, depending on whether the
			// index is zero
			if (index == 0) {
				return remoteName;
			}
			else {
				return null;
			}
		}
	}// getSelName

	/**
	 * @return
	 * 		an array of GamePlayerType objects that correspond to whether
	 * 		a local or remote game was selected
	 * 
	 */
	public GamePlayerType[] getSelTypes() {
		if (isLocal) {
			// local game: fill an array with copies of the objects in this.selTypes
			GamePlayerType[] retVal = new GamePlayerType[selTypes.size()];
			int index = 0;
			for (GamePlayerType gpt : this.selTypes) {
				retVal[index] = (GamePlayerType) gpt.clone();
				++index;
			}
			return retVal;
		}
		else {
			// remote game: create a one-element array with a clone of the remote
			// player type
			return new GamePlayerType[]{(GamePlayerType)remoteSelType.clone()};
		}
	}// getSelTypes

	/**
	 * get the type of the player at a given index
	 * 
	 * @param index
	 *            of the player whose type is wanted
	 * @return the player's name or null if index is invalid
	 */
	public GamePlayerType getSelType(int index) {
		if (isLocal) {
			// local game: catch and ignore invalid index;
			// if OK, then return the appropriate object
			if ((index < 0) || (index >= selNames.size())) {
				return null;
			}
			else {
				return this.selTypes.get(index);
			}
		}
		else {
			// remote game: only legal index is 0; return element
			// 0 or null, depending on index
			if (index == 0) {
				return(remoteSelType);
			}
			else {
				return null;
			}
		}
	}// getSelType

	/**
	 * @return whether the current configuration denotes a local game
	 */
	public boolean isLocal() {
		return isLocal;
	}// isLocal

	/**
	 * sets the attribute that tells whether the current configuration denotes
	 * a local game
	 * 
	 * @param isLocal
	 *            whether the game is to be set to "local game"
	 */
	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}// setLocal

	/**
	 * @return the name of the remote player
	 */
	public String getRemoteName() {
		return remoteName;
	}// getRemoteName
	
	/**
	 * @return
	 * 		the type of the remote player
	 */
	public GamePlayerType getRemoteSelType() {
		return remoteSelType;
	}// getRemotePlayerType

	/**
	 * sets the remote player name
	 * 
	 * @param remoteName
	 *            the name
	 */
	public void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}// setRemoteName
	
	/**
	 * sets the remote player type
	 * 
	 * @param
	 * 		idx index in the list of available types that
	 * 		corresponds to the player type to which the
	 * 		remote player is to be set
	 */
	public void setRemoteSelType(int idx) {
		// check invalid indices; then set type
		if (idx < 0 || idx >= availTypes.length) return; 
		remoteSelType = this.availTypes[idx];
	}// setRemoteSelType

	/**
	 * @return the IP code
	 */
	public String getIpCode() {
		return ipCode;
	}// getIpCode

	/**
	 * sets the config's IP code
	 * 
	 * @param ipCode
	 *            the code to set
	 */
	public void setIpCode(String ipCode) {
		this.ipCode = ipCode;
	}// setIpCode

	/**
	 * @return the minimum number of players allowed in the game
	 */
	public int getMinPlayers() {
		return minPlayers;
	}// getMinPlayers

	/**
	 * @return the maximum number of players allowed in the game
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * @return
	 * 		the current number of players specified in the game, corresponding
	 * 		to whether the configuration is for a local or remote game
	 * 
	 */
	public int getNumPlayers() {
		return isLocal ? selNames.size() : 1;
	}// getNumPlayers

	/**
	 * @return the current number of player types
	 */
	public int getNumTypes() {
		return isLocal ? selTypes.size() : 1;
	}// getNumTypes

	/**
	 * @return the game name
	 */
	public String getGameName() {
		return gameName;
	}// getGameName

	/**
	 * @return the whether the configuration is modifiable
	 */
	public boolean isUserModifiable() {
		return userModifiable;
	}// isUserModifiable

	/**
	 * sets whether the configuration is modifiable
	 * 
	 * @param userModifiable
	 *       whether the configuration is to be made modifiable
	 */
	public void setUserModifiable(boolean userModifiable) {
		this.userModifiable = userModifiable;
	}// setUserModifiable

}// class GameConfig

