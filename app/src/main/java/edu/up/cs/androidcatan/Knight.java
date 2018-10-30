package edu.up.cs.androidcatan;

/**
 * @author Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version October 25th, 2018
 * https://github.com/alexweininger/game-state
 **/

public class Knight extends DevelopmentCard {

    public Knight() {
        super("Knight");
    }

    /**
     *
     * @param robber - instance of the the robber object
     * @param player - player whose turn it is
     */
    public void useCard(Robber robber, Player player) {
        super.useCard(player);
        //TODO:need to get input of tile user pressed on screen to move robber to
        int hexNumber = 5; //5 is only a placeholder for now
        robber.setHexagonId(hexNumber);
        player.setArmySize(player.getArmySize() + 1);
    }

    /**
     *
     * @return string representation of a Knight
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
