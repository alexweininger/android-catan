package edu.up.cs.androidcatan;
/**
 * @author: Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version: October 25th, 2018
 * https://github.com/alexweininger/game-state
 **/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DevelopmentCard {

    private ArrayList<Integer> developmentCards = new ArrayList<Integer>(); // ArrayList of the development card in the deck
    private HashMap<String, Integer> resourceCost = new HashMap<>();

    //default instance variable
    private String name;
    private boolean isPlayable;

    public DevelopmentCard(String name) {
        this.name = name;
        this.isPlayable = false;
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
                return new Knight();
            case 1:
                return new VictoryPoints();
            case 2:
                return new RoadDevCard();
            case 3:
                return new Monopoly();
            case 4:
                return new YearOfPlenty();
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
        sb.append(name);
        sb.append(", isPlayable=");
        sb.append(isPlayable);

        return sb.toString();
    }
}
