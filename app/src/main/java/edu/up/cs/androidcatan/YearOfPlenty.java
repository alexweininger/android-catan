package edu.up.cs.androidcatan;

/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 25th, 2018
 * https://github.com/alexweininger/game-state
 **/
public class YearOfPlenty extends DevelopmentCard {
    /*public YearOfPlenty() {
        super("Year Of plenty");
    }

    /**
     *
     * @param player player who is using the card
     */
    @Override
    public void useCard(Player player) {
        super.useCard(player);
        for (int i = 0; i < 2; i++) {
            int random = (int) Math.random() * 5;
            switch (random) {
                case 0:
                    player.addResources("Brick", 1);
                    break;
                case 1:
                    player.addResources("Ore", 1);
                    break;
                case 2:
                    player.addResources("Sheep", 1);
                    break;
                case 3:
                    player.addResources("Wheat", 1);
                    break;
                case 4:
                    player.addResources("Wood", 1);
                    break;
            }
        }
    }


    /**
     *
     * @return string representation of a YearOfPlenty
     */
    @Override
    public String toString() {

        return super.toString();
    }
}
