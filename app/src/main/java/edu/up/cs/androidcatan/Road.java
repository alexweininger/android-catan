package edu.up.cs.androidcatan;

import java.util.HashMap;

/**
 * @author Alex Weininger, Andrew Lang, Daniel Borg, Niraj Mali
 * @version October 24th, 2018
 * https://github.com/alexweininger/game-state
 **/
public class Road extends Building {

    private int startIntersectionID, endIntersectionID;
    private static HashMap<String, Integer> checkResources = new HashMap<>();
    /**
     *
     * @param startIntersectionID -
     * @param endIntersectionID -
     */
    public Road(int startIntersectionID, int endIntersectionID, int ownerId) {
        super("Road", 0, ownerId);

        this.startIntersectionID = startIntersectionID;
        this.endIntersectionID = endIntersectionID;

    }

    public int getStartIntersectionID() { return startIntersectionID; }
    public int getEndIntersectionID() { return endIntersectionID; }

    /**
     *
     * @return string representation of a Road
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("Road{");
        sb.append("startIntersectionID=");
        sb.append(startIntersectionID);
        sb.append(", endIntersectionID=");
        sb.append(endIntersectionID);
        sb.append('}');

        return sb.toString();
    }

    public static void roadResourcePriceMake(){
        checkResources.put("brick", 1);
        checkResources.put("log", 1);
    }
}
