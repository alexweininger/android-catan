package edu.up.cs.androidcatan.catan.actions;

import java.io.Serializable;

import edu.up.cs.androidcatan.catan.gamestate.Port;
import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class CatanTradeWithPortAction extends GameAction implements Serializable {

    private static final long serialVersionUID = -5933779297120945954L;
    private Port port;
    private int resourceRecId;

    /**
     * CatanTraeWithPortAction
     *
     * @param player        the player who is calling the action
     * @param port          the port being traded with
     * @param resourceRecId the resource that is being received
     */
    public CatanTradeWithPortAction(GamePlayer player, Port port, int resourceRecId) {
        super(player);
        this.port = new Port(port);
        this.resourceRecId = resourceRecId;
    }

    public int getResourceRecId() {
        return resourceRecId;
    }

    public Port getPort() {
        return port;
    }
}
