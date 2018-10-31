package gameframework.infoMsg;;

/**
 * A message from the game to a player that tells the player that
 * the game is over.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class GameOverInfo extends GameInfo {

    // to satisfy the Serializable interface
    private static final long serialVersionUID = -8005304466588509849L;

    // the message that gives the game's result
    private String message;

    /**
     * constructor
     *
     * @param msg
     * 		a message that tells the result of the game
     */
    public GameOverInfo(String msg) {
        this.message = msg;
    }

    /**
     * getter method for the message
     *
     * @return
     * 		the message, telling the result of the game
     */
    public String getMessage() {
        return message;
    }
}
