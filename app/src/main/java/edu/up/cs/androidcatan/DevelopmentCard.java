package edu.up.cs.androidcatan;
/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 25th, 2018
 * https://github.com/alexweininger/game-state
 **/

import java.util.ArrayList;
import java.util.Random;

public class DevelopmentCard {

    public static int[] resourceCost = {0, 0, 1, 1, 1};

    //default instance variable
    private int devCardId;
    private boolean isPlayable;

    public DevelopmentCard(int devCardId) {
        this.devCardId = devCardId;
        this.isPlayable = false;
    }

    //default use method
    public void useCard(int devCardId) {
        // todo
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
     * @param player - player playing development card
     */
    public void useYearofPlentyCard(Player player) {
        Random random = new Random();
        player.addResourceCard(random.nextInt(4), 1);
        player.addResourceCard(random.nextInt(4), 1);
    }

    /**
     * @param playerList - copy of GameState.playerList
     * @param resourceId - resource id of what the player wants to steal from all other players
     */
    public void useMonopolyCard(int playerId, ArrayList<Player> playerList, int resourceId) {
        int totalCollected = 0;

        // go through each player
        for (Player player : playerList) {

            // if player isn't the player who is playing the dev card
            if (player.getPlayerId() != playerId) {
                // add the number of resource of this type the player has
                totalCollected += player.getResourceCards()[resourceId];
                // remove these resource cards from players inventory
                player.removeResourceCard(resourceId, player.getResourceCards()[resourceId]);
                playerList.get(playerId).addResourceCard(resourceId, player.getResourceCards()[resourceId]);
            }
        }
    }

    /**
     * @param playable allows the player to play the card or not
     */
    public void setPlayable(boolean playable) {
        isPlayable = playable;
    }

    @Override
    public String toString() {
        return "DevelopmentCard{" +
                "devCardId=" + devCardId +
                ", isPlayable=" + isPlayable +
                '}';
    }
}
