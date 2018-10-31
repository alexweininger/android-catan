package edu.up.cs.androidcatan.catan.devcards;
/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 25th, 2018
 * https://github.com/alexweininger/game-state
 **/

import java.util.Random;

import edu.up.cs.androidcatan.Player;
import edu.up.cs.androidcatan.Robber;
import edu.up.cs.androidcatan.catan.buildings.Road;

public class DevelopmentCard {

    public static int[] resourceCost = {0, 0, 1, 1, 1};

    //default instance variable
    private int devCardId;
    private boolean isPlayable;

    public DevelopmentCard (int devCardId) {
        this.devCardId = devCardId;
        this.isPlayable = false;
    }


    //default use method
    public void useCard(Player player) {
        player.useDevCard(this);
    }

    public void useKnightCard(Robber robber, Player player) {
        //TODO: need to get input of tile user pressed on screen to move robber to
        int hexNumber = 5; //placeholder
        robber.setHexagonId(hexNumber);
        player.setArmySize(player.getArmySize() + 1);
    }

    public void useVictoryPointsCard(Player player) {
        player.useDevCard(this);
    }

    public void useRoadDevCard(Player player) {
        for (int n = 0; n < 2; n++) {
            int startIntersection = 5;//TODO: get the start and end intersection id from users tap
            int endIntersection = 2; //placeholder
            //TODO: need to check if valid intersection
            Road road = new Road(startIntersection, endIntersection, player.getPlayerId());
        }
    }

    /**
     *
     * @param player
     */
    public void useYearofPlentyCard(Player player) {
        Random random = new Random();
        player.addResourceCard(random.nextInt(4), 1);
        player.addResourceCard(random.nextInt(4), 1);
    }

    /**
     *
     * @param player1
     * @param player2
     * @param player3
     * @param player4
     * @param resource
     */
    public void useMonopolyCard(Player player1, Player player2, Player player3, Player player4, String resource) {
        int totalCollected;
        totalCollected = player2.getResources().get(resource);
        totalCollected = player3.getResources().get(resource);
        totalCollected = player4.getResources().get(resource);

        //TODO: Don't know whether this removes all the cards or just one

        player2.setResources(resource, player2.getResources().remove(resource));
        player3.setResources(resource, player3.getResources().remove(resource));
        player4.setResources(resource, player4.getResources().remove(resource));

        player1.setResources(resource, totalCollected);
    }

    /**
     * @param player player who is building a dev card
     */
    public void build(Player player) {
        player.removeResourceCard(1, 1);
        player.removeResourceCard(2, 1);
        player.removeResourceCard(3, 1);

        //adds the building to the player's array list of built buildings TODO
    }



    /**
     * @param playable allows the player to play the card or not
     */
    public void setPlayable(boolean playable) {
        isPlayable = playable;
    }

    // play card based on given dev card id

    //Step 1: Create developmentCards object somewhere
    //Step 2: Call toString on the object
    //Step 3: For one of the players, have this toString printed to the console
    //as if they selected to play this card (may need a boolean to see if they have
    //the card)


    @Override
    public String toString() {
        return "DevelopmentCard{" +
                "devCardId=" + devCardId +
                ", isPlayable=" + isPlayable +
                '}';
    }
}
