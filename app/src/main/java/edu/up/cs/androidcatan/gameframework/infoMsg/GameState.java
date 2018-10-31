package edu.up.cs.androidcatan.gameframework.infoMsg;

/**
 * The state of the game. This class should be subclassed so that it holds
 * all state information for the particular game being implemented. For
 * example, if the game were chess, it would contain the contents of each
 * square on the board, which player's turn it was, etc.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public abstract class GameState extends GameInfo {

    // to satisfy the Serializable interface
    private static final long serialVersionUID = -5109179064333136954L;

}
