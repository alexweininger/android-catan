package edu.up.cs.androidcatan;
/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 24th, 2018
 * https://github.com/alexweininger/game-state
 **/

public class VictoryPoints extends DevelopmentCard {

    public VictoryPoints() {
        super("Victory Points");
    }


    /**
     *
     * @param player player who is using the card
     */
    @Override
    public void useCard(Player player) {
        super.useCard(player);
        // TODO
    }


    /**
     *
     * @return string representation of a VictoryPoints
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
