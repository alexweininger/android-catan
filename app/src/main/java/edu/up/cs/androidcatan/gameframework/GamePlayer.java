package edu.up.cs.androidcatan.gameframework;

import edu.up.cs.androidcatan.gameframework.infoMsg.GameInfo;

/**
 * A player who plays a (generic) game. Each class that implements a player for
 * a particular game should implement this interface.
 *
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @version July 2013
 */

public interface GamePlayer {

    // sets this player as the GUI player (implemented as final in the
    // major player classes)
    public abstract void gameSetAsGui(GameMainActivity activity);

    // sets this player as the GUI player (overrideable)
    public abstract void setAsGui(GameMainActivity activity);

    // sends a message to the player
    public abstract void sendInfo(GameInfo info);

    // start the player
    public abstract void start();

    // whether this player requires a GUI
    public boolean requiresGui();

    // whether this player supports a GUI
    public boolean supportsGui();

}// interface GamePlayer
