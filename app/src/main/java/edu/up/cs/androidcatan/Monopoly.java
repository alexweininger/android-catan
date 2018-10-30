package edu.up.cs.androidcatan;

public class Monopoly extends DevelopmentCard {

    public Monopoly() {
        super("Monopoly");
    }

    /**
     *
     * @param player player useing the card
     * @param p2 other player to lose resources
     * @param p3 other player to lose resources
     * @param p4 other player to lose resources
     */
    public void useCard(Player player, Player p2, Player p3, Player p4) {
        super.useCard(player);

        //TODO: figure which resourse the play picked
        int totalCollected;
        totalCollected = p2.getResources().get("Ore");
        totalCollected += p3.getResources().get("Ore");
        totalCollected += p4.getResources().get("Ore");

        p2.setResources("Ore", 0);
        p3.setResources("Ore", 0);
        p4.setResources("Ore", 0);

        player.setResources("Ore", totalCollected);

    } // end useCard method

    /**
     *
     * @return string representation of a Monopoly
     */
    @Override
    public String toString() {
        return super.toString();
    }
}