package edu.up.cs.androidcatan.catan.gamestate;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.Player;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class DevelopmentCard {

    public static int[] resourceCost = {0, 0, 1, 1, 1};

    //default instance variable
    private int devCardId;
    private boolean isPlayable;
    private String name;
    private String description;

    public DevelopmentCard(int devCardId) {
        this.devCardId = devCardId;
        this.isPlayable = false;
    }

    public DevelopmentCard(DevelopmentCard dc) {
        this.setDevCardId(dc.getDevCardId());
        this.setPlayable(dc.isPlayable());
    }

    //default use method
    public void useCard(Player player, int devCardId) {
        switch (devCardId){
            case 0:
                useKnightCard(null, player);
                break;
            case 1:
                useVictoryPointsCard(player);
                break;
            case 2:
                useYearofPlentyCard(player);
                break;
            case 3:
                useMonopolyCard(player.getPlayerId(), null, 1);
                break;
            case 4:
                useRoadDevCard(player);
                break;
        }
    }

    public void useKnightCard(Robber robber, Player player) {
        //TODO: need to get input of tile user pressed on screen to move robber to
        int hexNumber = 5; //placeholder
        robber.setHexagonId(hexNumber);
        player.setArmySize(player.getArmySize() + 1);
    }

    public void useVictoryPointsCard(Player player) {
        player.useDevCard(1);
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
     * @param playerList - copy of CatanGameState.playerList
     * @param resourceId - resource id of what the player wants to steal from all other players
     */
    public void useMonopolyCard(int playerId, ArrayList<Player> playerList, int resourceId) {

        // go through each player
        for (Player player : playerList) {

            // if player isn't the player who is playing the dev card
            if (player.getPlayerId() != playerId) {
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
        this.isPlayable = playable;
    }

    public static int[] getResourceCost() {
        return resourceCost;
    }

    public static void setResourceCost(int[] resourceCost) {
        DevelopmentCard.resourceCost = resourceCost;
    }

    public int getDevCardId() {
        return devCardId;
    }

    public void setDevCardId(int devCardId) {
        this.devCardId = devCardId;
    }

    public boolean isPlayable() {
        return isPlayable;
    }

    @Override
    public String toString() {
        return "DevelopmentCard{" +
                "devCardId=" + devCardId +
                ", isPlayable=" + isPlayable +
                '}';
    }
}
