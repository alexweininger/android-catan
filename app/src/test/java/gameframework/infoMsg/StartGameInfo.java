package gameframework.infoMsg;

/**
 * The a message from the game to a player that game is ready to start.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class StartGameInfo extends GameInfo {

    // to satisfy the Serializable interface
    private static final long serialVersionUID = -6396033535388393791L;

    // an array, in order of the names of all the players playing the game
    private String[] allPlayerNames;

    /**
     * constructor
     *
     * @param allPlayerNames
     * 		an array containing the names of all the players that are playing
     * 		the game, in player-ID order.
     */
    public StartGameInfo(String[] allPlayerNames) {
        this.allPlayerNames = allPlayerNames;
    }

    /**
     * getter-method for the array of player names
     *
     * @return
     * 		the array of player names
     */
    public String[] getPlayerNames() {
        return allPlayerNames;
    }
}
