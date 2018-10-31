package edu.up.cs.androidcatan;
/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 25th, 2018
 * https://github.com/alexweininger/game-state
 **/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.print.DocFlavor;

public class DevelopmentCard {

    private ArrayList<Integer> developmentCards = new ArrayList<Integer>(); // ArrayList of the development card in the deck
    private HashMap<String, Integer> resourceCost = new HashMap<>();
    private String[] resources = {"Brick", "Ore", "Sheep", "Wheat", "Wood"};

    //private String[] cards = {"Knight","Monopoly", "Year of Plenty", "Road Building", "Victory Points"};
    //default instance variable
    private String cardName;
    private boolean isPlayable;
    private DevelopmentCard Knight, Monopoly, YearofPlenty, RoadBuilding, VictoryPoints;

    public DevelopmentCard(String cardName, DevelopmentCard Knight, DevelopmentCard Monopoly, DevelopmentCard YearofPlenty, DevelopmentCard RoadBuilding, DevelopmentCard VictoryPoints) {
        this.cardName = cardName;
        this.isPlayable = false;
        this.Knight = Knight;
        this.Monopoly = Monopoly;
        this.YearofPlenty = YearofPlenty;
        this.RoadBuilding = RoadBuilding;
        this.VictoryPoints = VictoryPoints;
    }

    public DevelopmentCard(){
    }

    /**
     * creates a deck of int representing the exact number each type of card
     */
    public void generateDevCardDeck() {
        resourceCost.put("Ore", 1);
        resourceCost.put("Wheat", 1);
        resourceCost.put("Sheep", 1);

        int[] devCardCounts = {14, 5, 2, 2, 2};
        for (int i = 0; i < devCardCounts.length; i++) {
            for (int j = 0; j < devCardCounts[i]; j++) {
                developmentCards.add(i);
            }
        }
    }

    //default use method
    public void useCard(Player player) {
        player.useDevCard(this);
    }

    public void useKnightCard(Robber robber, Player player){
        //TODO: need to get input of tile user pressed on screen to move robber to
        int hexNumber = 5; //placeholder
        robber.setHexagonId(hexNumber);
        player.setArmySize(player.getArmySize() + 1);
    }

    public void useVictoryPointsCard(Player player){
        player.useDevCard(this);
    }

    public void useRoadDevCard(Player player){
        for(int n = 0; n < 2; n++){
            int startIntersection = 5;//TODO: get the start and end intersection id from users tap
            int endIntersection = 2; //placeholder
            //TODO: need to check if valid intersection
            Road road = new Road(startIntersection, endIntersection, player.getPlayerId());
        }
    }

    public void useYearofPlentyCard(Player player){
        for (int n = 0; n < 2; n++){
            int rand = (int) (Math.random() * 5);
            player.addResources(resources[n], 1);
        }
    }

    public void useMonopolyCard(Player player1, Player player2, Player player3, Player player4, String resource){
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
        player.addDevCard(getRandomCard());
    }

    /**
     * @return the random dev card the player drew
     */
    public DevelopmentCard getRandomCard() {
        Random random = new Random();
        int randomDevCard = random.nextInt(developmentCards.size() - 1);
        int drawnDevCard = developmentCards.get(randomDevCard);
        developmentCards.remove(randomDevCard);
        switch (drawnDevCard) { // switch to create new dev card
            case 0:
                return Knight;
            case 1:
                return VictoryPoints;
            case 2:
                return RoadBuilding;
            case 3:
                return Monopoly;
            case 4:
                return YearofPlenty;
            default:
                return null;
        }
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



    /**
     * @return string representation of a DevelopmnentCard
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("DevelopmentCard{");
        sb.append("name=");
        sb.append(cardName);
        sb.append(", isPlayable=");
        sb.append(isPlayable);

        return sb.toString();
    }
}
