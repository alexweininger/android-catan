package edu.up.cs.androidcatan.catan.gamestate;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.up.cs.androidcatan.catan.Player;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Building;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Settlement;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 8th, 2018
 * https://github.com/alexweininger/android-catan
 **/

public class Board {
    /**
     * External Citation
     * Date: 8 October 2018
     * Problem: Struggling to represent board and tiles.
     * Resource:
     * https://www.academia.edu/9699475/Settlers_of_Catan_Developing_an_Implementation_
     * of_an_Emerging_Classic_Board_Game_in_Java
     * Solution: We used the concepts and ideas from this research paper to help us represent the board
     * information and the hexagons.
     */

    /*
     * 'Rings' are used to organize the following ID 2D-ArrayLists. Rings in context mean ring of hexagons or intersections
     * on the board. So for hexagons, the first ring contains the very middle hexagon. Ring 2 are the hexagons around that one.
     * Hexagon 0 is the center, and hex 1 is directly right of hex 0, and then they are numbered by ring. So ring 0 has 1
     * hexagon. Ring 2 has 6, and ring 3 (outer ring) has 12 hexagons.
     */

    private static final String TAG = "Board";
    // Robber object.
    private Robber robber;

    // hexagonIdRings holds the IDs of each hexagon on the board, organized into rings.
    private ArrayList<ArrayList<Integer>> hexagonIdRings = new ArrayList<>();
    // intersectionIdRings holds the IDs of each intersection on the board, organized into rings.
    private ArrayList<ArrayList<Integer>> intersectionIdRings = new ArrayList<>();

    /*  hGraph and iGraph are 2d arrays that hold adjacency information for hexagons and intersections. */
    private boolean[][] hGraph = new boolean[19][19];
    private boolean[][] iGraph = new boolean[54][54];

    // Maps relating hex to intersection and intersection to hex ids
    private ArrayList<ArrayList<Integer>> hexToIntIdMap = new ArrayList<>(); // rows: hex id - col: int ids
    private ArrayList<ArrayList<Integer>> intToHexIdMap = new ArrayList<>(); // rows: int id - col: hex id

    // Array of buildings on the board - indexed by intersection.
    private Building[] buildings = new Building[54];

    // List of all roads on board, in no particular order.
    private ArrayList<Road> roads = new ArrayList<>();

    // Adjacency graph identical to iGraph, however only contains Road objects and null.
    private Road[][] roadGraph;

    // List of all hexagons on board.
    private ArrayList<Hexagon> hexagons = new ArrayList<>(); // list of resource tiles

    private ArrayList<Port> portList = new ArrayList<>();

    private ArrayList<ArrayList<Integer>> intersectionGraph = new ArrayList<>();

    // new
    private int highlightedHexagonId = -1;
    private int highlightedIntersectionId = -1;

    public Board () {
        Log.d(TAG, "Board() called");
        this.roadGraph = new Road[54][54];
        robber = new Robber(0);

        populateHexagonIds(); // populate ids
        populateIntersectionIds();

        generateHexagonGraph(); // generate adj. graphs
        generateIntersectionGraph();
        generateNewIntersectionGraphManually(); // new intersection graph
        generateRoadMatrix();

        generateIntToHexMap(); // generate maps
        generateHexToIntMap();

        do {
            this.hexagons.clear();
            generateHexagonTiles(); // generate hex tiles
        } while (!checkChitRule());

        designatePorts();
    } // end Board constructor

    /**
     * @param b - board to copy
     */
    public Board (Board b) {
        this.setHexagonIdRings(b.getHexagonIdRings());
        this.setIntersectionIdRings(b.getIntersectionIdRings());
        this.sethGraph(b.getHGraph());
        this.setiGraph(b.getIGraph());
        this.setHexToIntIdMap(b.getHexToIntIdMap());
        this.setIntToHexIdMap(b.getIntToHexIdMap());
        this.setBuildings(b.getBuildings());
        this.setRoads(b.getRoads());
        this.setRobber(new Robber(b.getRobber())); // class
        this.setRoadGraph(b.getRoadGraph());
        this.setRoadGraph(b.getRoadGraph());
        this.setPortList(b.getPortList());
        this.setIntersectionGraph(b.getIntersectionGraph());
        this.setHighlightedHexagonId(b.getHighlightedHexagonId());
        this.setHighlightedIntersectionId(b.getHighlightedIntersectionId());

        for (int i = 0; i < b.getBuildings().length; i++) {
            if (b.getBuildings()[i] instanceof Settlement) {
                this.buildings[i] = new Settlement(b.getBuildings()[i].getOwnerId());
            } else if (b.getBuildings()[i] instanceof City) {
                this.buildings[i] = new City(i, b.getBuildings()[i].getOwnerId());
            }
        }
        for (Hexagon hexagon : b.getHexagons()) {
            this.hexagons.add(new Hexagon(hexagon));
        }
    } // end Board deep copy constructor

    /* ----- helper / checking methods ----- */

    /**
     * @param playerId - player to test if the intersection is connected
     * @param intersectionId - intersection to test
     * @return - is the intersection connected to the players buildings or roads?
     */
    private boolean isConnected (int playerId, int intersectionId) {
        Log.d(TAG, "isConnected() called with: playerId = [" + playerId + "], intersectionId = [" + intersectionId + "]");
        // check if intersection has no building and no road
        if (!hasRoad(intersectionId) && this.buildings[intersectionId] == null) {
            Log.e(TAG, "isConnected: Not connected. Returned: " + false);
            return false;
        }
        // check if player is an owner of intersection
        Log.d(TAG, "isConnected() returned: " + getIntersectionOwners(intersectionId).contains(playerId));
        return getIntersectionOwners(intersectionId).contains(playerId);
    }

    /* ----- road methods ----- */

    /**
     * @param playerId - player building the road
     * @param a - intersection
     * @param b - intersection
     * @return - if road can be placed
     */
    public boolean validRoadPlacement (int playerId, boolean isSetupPhase, int a, int b) {
        Log.d(TAG, "validRoadPlacement() called with: playerId = [" + playerId + "], isSetupPhase = [" + isSetupPhase + "], a = [" + a + "], b = [" + b + "]");
        // check if intersections are adjacent

        if (!this.intersectionGraph.get(a).contains(b)) {
            Log.e(TAG, "validRoadPlacement: Invalid road placement. Intersections are not adjacent.");
            Log.i(TAG, "validRoadPlacement: intersectionGraph: " + this.intersectionGraph.toString());
            return false;
        }

        // check if it is not the setup phase
        if (!isSetupPhase) {
            // check if road is connected to players roads / buildings at either intersection
            if (!isConnected(playerId, a) && !isConnected(playerId, b)) {
                Log.e(TAG, "validRoadPlacement: Invalid road placement. IntersectionDrawable(s) are not connected to players buildings or roads.");
                return false;
            }
        }

        // check if 3 roads at either intersection
        if (getRoadsAtIntersection(a).size() > 2 || getRoadsAtIntersection(b).size() > 2) {
            Log.e(TAG, "validRoadPlacement: Invalid road placement. Roads are already built at this intersection.");
            return false;
        }

        // check if road is already built
        Log.i(TAG, "validRoadPlacement: this.roadGraph.getOwnerId: " + this.roadGraph[a][b].getOwnerId());
        if (this.roadGraph[a][b].getOwnerId() != -1) {
            Log.e(TAG, "validRoadPlacement: Invalid road placement. A road is already built here. Returning false.");
            return false;
        }

        Log.d(TAG, "validRoadPlacement: Valid road placement.");
        return true;
    }

    /**
     * @param playerId
     * @param intersectionA
     * @param intersectionB
     */
    public void addRoad (int playerId, int intersectionA, int intersectionB) {
        Log.d(TAG, "addRoad() called with: playerId = [" + playerId + "], intersectionA = [" + intersectionA + "], intersectionB = [" + intersectionB + "]");
        Road road = new Road(playerId, intersectionA, intersectionB);
        this.roads.add(road);
        this.roadGraph[road.getIntersectionAId()][road.getIntersectionBId()].setOwnerId(road.getOwnerId());
        this.roadGraph[road.getIntersectionBId()][road.getIntersectionAId()] = road;
    }

    /**
     * @param i - intersection to check
     * @return returns if road is connected to given intersection
     */
    public boolean hasRoad (int i) {
        Log.d(TAG, "hasRoad() called with: i = [" + i + "]");
        for (Road road : roadGraph[i]) {
            if (road.getOwnerId() != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Main method to calculate the longest road trophy holder. - AL
     *
     * @param playerList list of player objects
     * @return returns the playerid with the longest road for now (may need to change so that it returns the value instead)
     */
    //TODO properly implement this method and fix logic
    public int getPlayerWithLongestRoad (ArrayList<Player> playerList) {
        Log.i(TAG, "getPlayerWithLongestRoad() called with: playerList = [" + playerList + "]");
        ArrayList<Integer> longestRoadPerPlayer = new ArrayList<>();
        for (Player player : playerList) {
            //for each player there is an adjacency map as well as a list
            ArrayList<Road> playerRoads = new ArrayList<>();
            Road[][] playerRoadList = new Road[54][54];
            ArrayList<Integer> currentPlayerRoadLength = new ArrayList<>();
            for (Road road : roads) {
                if (road.getOwnerId() == player.getPlayerId()) {
                    playerRoads.add(road);
                    //check line below
                    playerRoadList[road.getIntersectionAId()][road.getIntersectionBId()] = road;
                }
            }
            for (int n = 0; n < playerRoads.size(); n++) {
                currentPlayerRoadLength.add(traverseRoads(roads.get(n).getIntersectionAId(), player.getPlayerId(), playerRoadList, 0));
            }
            int max = 0;
            for (int n = 0; n < currentPlayerRoadLength.size(); n++) {
                max = currentPlayerRoadLength.get(0);
                if (currentPlayerRoadLength.get(n) >= max) {
                    max = currentPlayerRoadLength.get(n);
                }
            }
            longestRoadPerPlayer.add(player.getPlayerId(), max);
        }
        int playerIdLongestRoad = -1;
        int currLongestRoad = 0;
        //currently gives the longest road trophy to the most recent player checked within the array if
        //it shares the longest road with a prior player
        for (int n = 0; n < longestRoadPerPlayer.size(); n++) {
            if (longestRoadPerPlayer.get(n) > currLongestRoad) {
                currLongestRoad = longestRoadPerPlayer.get(n);
                playerIdLongestRoad = n;
            }
        }
        Log.d(TAG, "getPlayerWithLongestRoad() returned: " + playerIdLongestRoad);
        return playerIdLongestRoad;
    }

    /**
     * @param intersectionId intersection to check for a break
     * @param playerId player we're checking for
     * @return if there is a break
     */
    public boolean isBreakAtIntersection (int intersectionId, int playerId) {
        Log.d(TAG, "isBreakAtIntersection() called with: intersectionId = [" + intersectionId + "], playerId = [" + playerId + "]");

        // if null (means no building) return false
        if (this.buildings[intersectionId] == null) return false;

        // (if not null) check if the player owns the building
        return this.buildings[intersectionId].getOwnerId() != playerId;
    }

    /**
     * @param intersectionId IntersectionId to check (0-53).
     * @param road Road adjacency matrix.
     * @return If intersection is a dead end for the given road object.
     */
    public boolean checkDeadEnd (int intersectionId, Road[][] road) {
        Log.d(TAG, "checkDeadEnd() called with: intersectionId = [" + intersectionId + "], road = [" + road + "]");
        for (Integer intersection : this.intersectionGraph.get(intersectionId)) {
            if (road[intersectionId][intersection] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recursive method that will call other helper methods within board
     *
     * @param intersectionId IntersectionId to start traversing.
     * @param playerId Which players roads to traverse.
     * @param road Adjacency matrix for roads ONLY the given player owns.
     * @return Number of roads in a given section of continuous roads.
     */
    public int traverseRoads (int intersectionId, int playerId, Road[][] road, int stackCount) {
        if (stackCount > 200) {
            Log.e(TAG, "traverseRoads: reached a stackCount of 200. Returning 0.");
            return 0;
        }
        Log.d(TAG, "traverseRoads() called with: intersectionId = [" + intersectionId + "], playerId = [" + playerId + "], road = [" + road + "]");
        if (isBreakAtIntersection(intersectionId, playerId)) {
            return 0;
        }
        if (checkDeadEnd(intersectionId, road)) {
            return 0;
        }
        for (Integer intersection : this.intersectionGraph.get(intersectionId)) {
            return 1 + traverseRoads(intersection, playerId, road, stackCount + 1);
        }
        return 0;
    }

    /* ----- validate building methods ----- */

    /**
     * @param playerId - player building the building
     * @param intersectionId - intersection of building
     * @return - is the building location valid
     */
    public boolean validBuildingLocation (int playerId, boolean isSetupPhase, int intersectionId) {
        Log.d(TAG, "validBuildingLocation() called with: playerId = [" + playerId + "], isSetupPhase = [" + isSetupPhase + "], intersectionId = [" + intersectionId + "]");

        // check if the player id is within bounds
        if (playerId < 0 || playerId > 3) {
            Log.e(TAG, "validBuildingLocation: returned " + false + " because playerId is not in range(" + playerId + ")");
            return false;
        }

        // check if intersection is within bounds
        if (intersectionId < 0 || intersectionId > buildings.length - 1) {
            Log.e(TAG, "validBuildingLocation() returned: " + false + " because intersection does not exist.");
            return false;
        }

        if (!isSetupPhase) {
            // it is not the setup phase of the game
            // check if the intersection is connected to players' roads/buildings
            if (!isConnected(playerId, intersectionId)) {
                Log.i(TAG, "validBuildingLocation: invalid location because intersection " + intersectionId + " is not connected.");
                return false;
            }
        }

        // check if intersection already has a building on it
        if (this.buildings[intersectionId] != null) {
            Log.i(TAG, "validBuildingLocation: invalid location because intersection " + intersectionId + " already has a building on it.");
            return false;
        }

        // check if adjacent intersections do not have buildings for the distance rule
        for (int intersection : this.intersectionGraph.get(intersectionId)) { // for each adj. intersection
            Log.d(TAG, "validBuildingLocation: DISTANCE RULE - Checking intersection " + intersection + " for a building.");
            if (this.buildings[intersection] != null) { // check if building exists there
                Log.i(TAG, "validBuildingLocation: invalid - building at intersection " + intersectionId + " violates the distance rule (" + intersection + " is adj. and has a building).");
                return false;
            }
        }
        Log.d(TAG, "validBuildingLocation() returned: " + true);
        return true;
    }

    public boolean validCityLocation (int playerId, int intersectionId) {
        Log.d(TAG, "validCityLocation() called with: playerId = [" + playerId + "], intersectionId = [" + intersectionId + "]");

        // check if intersection is within bounds
        if (intersectionId < 0 || intersectionId > buildings.length - 1) {
            Log.e(TAG, "validCityLocation() returned: " + false + " because intersection does not exist.");
            return false;
        }

        // check if the player id is within bounds
        if (playerId < 0 || playerId > 3) {
            Log.e(TAG, "validCityLocation: returned " + false + " because playerId is not in range(" + playerId + ")");
            return false;
        }

        // check for an already existing building
        if (!hasBuilding(intersectionId)) {
            Log.w(TAG, "validCityLocation: Cannot build a city where there is not already a building. Returning false.");
            return false;
        }

        // check if city already exists at intersection
        if (!(this.buildings[intersectionId] instanceof City)) {
            Log.w(TAG, "validCityLocation: Cannot build a city if there is already a city built at the intersection. Returning false.");
            return false;
        }

        // check if the building is a settlement or not
        if (!(this.buildings[intersectionId] instanceof Settlement)) {
            Log.w(TAG, "validCityLocation: Cannot build a city if there is not a settlement already at intersection. Returning false.");
            return false;
        }

        // check if the settlement at the intersection is owned by the player trying to build the city
        if (this.buildings[intersectionId].getOwnerId() != playerId) {
            Log.e(TAG, "validCityLocation: Cannot built a city on top of another players settlements. Returning false.");
            return false;
        }

        Log.d(TAG, "validCityLocation() returned: " + true);
        return true;
    }

    private void swapChitValues(int hexagonId) {
        Random random = new Random();
        int randomHexId = random.nextInt(18);
        Hexagon hex = this.hexagons.get(randomHexId);
        int chitVal = this.hexagons.get(hexagonId).getChitValue();
        this.hexagons.get(hexagonId).setChitValue(hex.getChitValue());
        hex.setChitValue(chitVal);
    }

    /**
     * Method puts the hexagons in a new order starting from the top left to bottom right (line by line)
     *
     * @return An array list of reorganized hexagons
     */
    public ArrayList<Hexagon> getHexagonListForDrawing () {
        ArrayList<Hexagon> result = new ArrayList<>();

        // row 1
        result.add(hexagons.get(11));
        result.add(hexagons.get(10));
        result.add(hexagons.get(9));

        // row 2
        result.add(hexagons.get(12));
        result.add(hexagons.get(3));
        result.add(hexagons.get(2));
        result.add(hexagons.get(8));

        // row 2
        result.add(hexagons.get(13));
        result.add(hexagons.get(4));
        result.add(hexagons.get(0));
        result.add(hexagons.get(1));
        result.add(hexagons.get(7));

        // row 2
        result.add(hexagons.get(14));
        result.add(hexagons.get(5));
        result.add(hexagons.get(6));
        result.add(hexagons.get(18));

        // row 2
        result.add(hexagons.get(15));
        result.add(hexagons.get(16));
        result.add(hexagons.get(17));

        return result;
    }

    /**
     * @param intersectionId - to check for owners
     * @return - ArrayList of playerIds who either own a road that is connected to intersection, or have a building that is on this intersection
     */
    public ArrayList<Integer> getIntersectionOwners (int intersectionId) {
        Log.d(TAG, "getIntersectionOwners() called with: intersectionId = [" + intersectionId + "]");
        ArrayList<Integer> result = new ArrayList<>();

        if (!this.hasBuilding(intersectionId)) {
            if (this.hasRoad(intersectionId)) {
                for (Road r : this.getRoadsAtIntersection(intersectionId)) {
                    result.add(r.getOwnerId());
                }
            } else {
                return result;
            }
        } else {
            result.add(this.buildings[intersectionId].getOwnerId());
        }
        Log.d(TAG, "getIntersectionOwners() returned: " + result);
        return result;
    }

    /**
     * @param i - intersection id
     * @return ArrayList of roads connected to that intersection
     */
    public ArrayList<Road> getRoadsAtIntersection (int i) {
        Log.d(TAG, "getRoadsAtIntersection() called with: i = [" + i + "]");
        ArrayList<Road> result = new ArrayList<>();

        for (Road r : this.roads) {
            if (r.getIntersectionAId() == i || r.getIntersectionBId() == i) {
                result.add(r);
            }
        }
        Log.d(TAG, "getRoadsAtIntersection() returned: " + result);
        return result;
    }

    /**
     * @param chitValue - value of dice sum and tile chit value that will produce resources
     * @return list of hexagons with chitValue AND DO NOT HAVE ROBBER - AW
     */
    public ArrayList<Integer> getHexagonsFromChitValue (int chitValue) {
        Log.d(TAG, "getHexagonsFromChitValue() called with: chitValue = [" + chitValue + "]");
        ArrayList<Integer> hexagonIdList = new ArrayList<>();
        for (int i = 0; i < this.hexagons.size(); i++) {
            // check for chit value
            if (this.hexagons.get(i).getChitValue() == chitValue) {
                // check if robber is on hexagon
                if (this.robber.getHexagonId() != i) {
                    hexagonIdList.add(i);
                } else {
                    Log.i(TAG, "getHexagonsFromChitValue: robber was detected on hexagon, hexagon with id: " + i + " not producing resources chit values: " + chitValue);
                }
            }
        }
        if (hexagonIdList.size() > 2) { // error checking
            Log.e(TAG, "getHexagonsFromChitValue: returning a list with more than 2 hexagons with chit values of: " + chitValue);
        }
        Log.d(TAG, "getHexagonsFromChitValue() returned: " + hexagonIdList);
        return hexagonIdList;
    }

    /**
     * @param hexagonId - hexagonId to move the robber to
     * @return - true robber is moved, false if robber cannot be moved (trying to move to same hex) - AW
     */
    public boolean moveRobber (int hexagonId) {
        Log.d(TAG, "moveRobber() called with: hexagonId = [" + hexagonId + "]");
        // check if moving to same hexagon
        if (hexagonId == this.robber.getHexagonId()) return false;

        // change robber position
        this.robber.setHexagonId(hexagonId);
        return true;
    }

    /**
     * ! Error checking is not done here, this method assumes error checking has already been done.
     * adds the building to the building array - AW
     *
     * @param intersectionId - intersection id of the building location
     * @param building - building object
     */
    public boolean addBuilding (int intersectionId, Building building) {
        Log.d(TAG, "addBuilding() called with: intersectionId = [" + intersectionId + "], building = [" + building + "]");
        if (this.buildings[intersectionId] != null) {
            Log.e(TAG, "addBuilding: Cannot add building, building already exists at intersection id: " + intersectionId);
            return false;
        }
        building.setOwnerId(building.getOwnerId());
        this.buildings[intersectionId] = building;
        return true;
    }

    /**
     * Checks to see if there is a building already at the given intersection or not
     *
     * @param intersectionId - intersection id
     * @return whether there is a building at that given intersection
     */
    public boolean hasBuilding (int intersectionId) {
        return this.buildings[intersectionId] != null;
    }

    /**
     * @param intersectionId - intersection id
     * @return - the building located at given intersection
     */
    public Building getBuildingAtIntersection (int intersectionId) {
        return this.buildings[intersectionId];
    }

    /* ----- adjacency checking methods -----*/

    /**
     * TODO TEST
     * getAdjacentIntersectionsToIntersection
     *
     * @param intersectionId - given intersection i (0-53)
     * @return - ArrayList of intersection ids that are adjacent to the given intersection id
     */
    public ArrayList<Integer> getAdjacentIntersectionsToIntersectionOld (int intersectionId) {
        Log.d(TAG, "getAdjacentIntersectionsToIntersectionOld() called with: intersectionId = [" + intersectionId + "]");

        ArrayList<Integer> adjacentIntersections = new ArrayList<>(3);
        for (int i = 0; i < 54; i++) {
            if (areIntersectionsAdjacent(i, intersectionId)) {
                adjacentIntersections.add(i);
            }
        }

        if (adjacentIntersections.size() > 3) {
            Log.e(TAG, "getAdjacentIntersectionsToIntersectionOld: Received more than 3 adjacent intersections. That makes no sense.");
        }

        // check if we have a bad error
        if (adjacentIntersections.size() < 2) {
            Log.e(TAG, "getAdjacentIntersectionsToIntersectionOld: Did not find 2 adjacent intersections. intersectionId = [\" + intersectionId + \"]. This is not good.", new Exception("IntersectionDrawable adjacency error."));
        }

        Log.d(TAG, "getAdjacentIntersectionsToIntersectionOld() returned: " + adjacentIntersections);
        return adjacentIntersections;
    }

    /**
     * TODO TEST
     *
     * @param hexagonId - hexagon id that you want to get adjacency of
     * @return ArrayList<Integer> - list of adj. hex id's
     */
    public ArrayList<Integer> getAdjacentHexagons (int hexagonId) {
        Log.d(TAG, "getAdjacentHexagons() called with: hexagonId = [" + hexagonId + "]");
        ArrayList<Integer> adjacentHexagons = new ArrayList<>(6);
        for (int i = 0; i < 19; i++) {
            if (adjacentHexagons.size() > 6) {
                Log.d(TAG, "getAdjacentHexagons: ERROR got more than 6 adjacent hexagons");
                break;
            }
            if (hGraph[hexagonId][i] || hGraph[i][hexagonId]) {
                adjacentHexagons.add(i);
            }
        }
        Log.d(TAG, "getAdjacentHexagons() returned: " + adjacentHexagons);
        return adjacentHexagons;
    }

    /**
     * @param ring - ring of intersection
     * @param col - column within ring of intersection
     * @return - int intersection id
     */
    private int getIntersectionId (int ring, int col) {
        if (ring < 0 || ring > 2) {
            Log.e(TAG, "getIntersectionId: Invalid ring value received: " + ring);
            return -1;
        }
        if (col < 0 || col > intersectionIdRings.get(ring).size() - 1) {
            Log.e(TAG, "getIntersectionId: Invalid col value received: " + col);
            return -1;
        }

        return intersectionIdRings.get(ring).get(col);
    }

    /**
     * @param ring - hexagon ring (0-2)
     * @param col - column within hexagon ring
     * @return - int hexagon id
     */
    public int getHexagonId (int ring, int col) {
        return hexagonIdRings.get(ring).get(col);
    }

    /**
     * @param hexagonId - hexagon id - AW
     * @return Hexagon
     */
    public Hexagon getHexagonFromId (int hexagonId) {
        if (hexagonId < 0 || hexagonId >= this.hexagons.size()) { // error checking
            Log.d(TAG, "getHexagonFromId: ERROR cannot get hexagon with id: " + hexagonId + ". Does not exists in ArrayList hexagons.");
            return null;
        }
        return this.hexagons.get(hexagonId);
    }

    public boolean areIntersectionsAdjacent (int intA, int intB) {
        if (intA < 0 || intB < 0 || intA > 53 || intB > 53) {
            Log.e(TAG, "areIntersectionsAdjacent: Index out of bounds for checking intersection adjacency.");
            return false;
        }

        return this.intersectionGraph.get(intA).contains(intB);
    }

    /*----- board helper methods for setting up board and populating data structures -----*/

    private ArrayList<Integer> generateChitList () {
        Log.d(TAG, "generateChitList() called");
        ArrayList<Integer> chitList = new ArrayList<>();

        chitList.add(2);
        chitList.add(2);
        chitList.add(3);
        chitList.add(3);
        chitList.add(4);
        chitList.add(4);
        chitList.add(5);
        chitList.add(5);
        chitList.add(6);
        chitList.add(6);
        chitList.add(8);
        chitList.add(8);
        chitList.add(9);
        chitList.add(9);
        chitList.add(10);
        chitList.add(10);
        chitList.add(11);
        chitList.add(11);
        chitList.add(12);

        Collections.shuffle(chitList);

        return chitList;
    }

    /**
     * @return If hexagon tiles follow the rule stating that no 6/8 chit can be adjacent to one another.
     */
    private boolean checkChitRule () {
        Log.d(TAG, "checkChitRule() called");
        // checks if any 8's or 6's are adjacent to one another

        // go through all hexagons
        for (int i = 0; i < this.hexagons.size(); i++) {
            if (hexagons.get(i).getChitValue() == 8) {
                for (Integer integer : getAdjacentHexagons(i)) {
                    if (integer != i) {
                        if (hexagons.get(integer).getChitValue() == 8) {
                            Log.e(TAG, "generateHexagonTiles: Chits 8 adjacent, reshuffling the hexagon tiles...");
                            return false;
                        }
                    }
                }
            }
            if (hexagons.get(i).getChitValue() == 6) {
                for (Integer integer : getAdjacentHexagons(i)) {
                    if (integer != i) {
                        if (hexagons.get(integer).getChitValue() == 6) {
                            Log.e(TAG, "generateHexagonTiles: Chits 6 adjacent, reshuffling the hexagon tiles...");
                            return false;
                        }

                        if (hexagons.get(integer).getChitValue() == 8) {
                            Log.e(TAG, "generateHexagonTiles: Chits 6 and 8 adjacent, reshuffling the hexagon tiles...");
                            return false;
                        }
                    }
                }
            }
        }
        Log.e(TAG, "checkChitRule() returned: " + true);
        return true;
    }

    /**
     * builds the ArrayList of Hexagon objects, creating the correct amount of each resource tile,
     * randomly assigning them to locations. Also randomly gives Hexagon a chit value.
     */
    private void generateHexagonTiles () {
        Log.i(TAG, "generateHexagonTiles() called");

        //arrays that contain information regarding what each hexagon will contain
        int[] resourceTypeCount = {3, 4, 4, 3, 4, 1};

        int[] resources = {0, 1, 2, 3, 4, 5};

        Random random = new Random();

        ArrayList<Integer> chitList = generateChitList();

        //iterates through the hexagons and assigns each individual one the information required
        while (this.hexagons.size() < 19) {

            int randomChitValue = chitList.get(this.hexagons.size());

            int randomResourceType;
            do {
                randomResourceType = random.nextInt(resourceTypeCount.length);
            } while (resourceTypeCount[randomResourceType] < 1);

            if (randomResourceType == 5) {
                Log.w(TAG, "generateHexagonTiles: randomResourceType = 5. Desert tile id = " + (hexagons.size()));
                randomChitValue = 0;
            }

            Log.e(TAG, "generateHexagonTiles: size(): " + hexagons.size());
            hexagons.add(new Hexagon(resources[randomResourceType], randomChitValue, hexagons.size()));
            resourceTypeCount[randomResourceType]--;

            if (resources[randomResourceType] == 5) {
                robber.setHexagonId(this.hexagons.size() - 1);
            }

            Log.i(TAG, "generateHexagonTiles: hexagonsSize: " + this.hexagons.size());
        }

        Log.i(TAG, "generateHexagonTiles: exited loop");

        Log.i(TAG, "generateHexagonTiles: hexagon list:");

        if (hexagons.size() < 19) {
            Log.e(TAG, "generateHexagonTiles: hexagons size less than 19");
        }

        for (Hexagon hexagon : this.hexagons) {
            Log.i(TAG, "| " + hexagon);
        }

        // the rest of the code checks the method for error
        int resourceCountChecks[] = new int[6];
        for (Hexagon hexagon : this.hexagons) {
            resourceCountChecks[hexagon.getResourceId()]++;
        }

        for (int i = 0; i < resourceCountChecks.length; i++) {
            if (resourceTypeCount[i] < resourceCountChecks[i]) {
                Log.e(TAG, "generateHexagonTiles: Resource tile count check failed for resource " + i + ". There are " + resourceCountChecks[i] + " of this resources when there should only be " + resourceTypeCount[i] + ".");
                //generateHexagonTiles();
            }
        }
    }

    /**
     * populating hexagonIdRings with hex IDs (0-18, 19 hexagons)
     */
    private void populateHexagonIds () {
        int id = 0;
        for (int i = 0; i < 3; i++) {
            this.hexagonIdRings.add(new ArrayList<Integer>());
            if (0 == i) {
                this.hexagonIdRings.get(i).add(0);
                id++;
            } else {
                for (int j = 0; j < i * 6; j++) {
                    this.hexagonIdRings.get(i).add(id);
                    id++;
                }
            }
        }
    }

    /**
     * populating intersectionIdRings with intersection IDs (0-53, 54 intersections)
     */
    private void populateIntersectionIds () {
        int id = 0;
        for (int i = 0; i < 3; i++) {
            this.intersectionIdRings.add(new ArrayList<Integer>());
            for (int j = 0; j < ((2 * i) + 1) * 6; j++) {
                this.intersectionIdRings.get(i).add(id);
                id++;
            }
        }
    }

    /**
     * generateHexagonGraph
     */
    private void generateHexagonGraph () {
        // set all values in the 2d array to false
        for (int i = 0; i < 2; i++) { // rings
            for (int j = 0; j < this.hexagonIdRings.get(i).size(); j++) { // cols
                this.hGraph[i][hexagonIdRings.get(i).get(j)] = false;
            }
        }
        for (int col = 0; col < 6; col++) {
            hGraph[0][col] = true;
        }
        for (int i = 0; i < 2; i++) { // rings (rows)
            for (int j = 0; j < this.hexagonIdRings.get(i).size(); j++) { // cols
                // i and j are only 0 once and never are 0 again 0, 0 = center
                /*
                 *  for each hexagon in hexagonIdRings:
                 *   1. check the next hexagon in the same ring
                 *     a. make sure that this 'wraps' around at the end using %
                 *   2 look at the two adjacent hexagons in the next ring
                 *     a. corner vs. non-corner hexagons = if j % i == 0
                 *     b. sextants (0-5), calculated with sextant = j / i;
                 */
                int sextant = j;
                boolean corner = false;
                if (i != 0) {
                    sextant = j / i;
                    corner = true;
                }
                this.hGraph[getHexagonId(i, j)][getHexagonId(i + 1, j + sextant)] = true;
                this.hGraph[getHexagonId(i, j)][getHexagonId(i + 1, j + sextant + 1)] = true;

                if (corner) {
                    int size = hexagonIdRings.get(i + 1).size();
                    int nextIndex = ((j - 1 + sextant) % size);
                    if (nextIndex < 12 && nextIndex >= 0) {
                        hGraph[getHexagonId(i, j)][getHexagonId(i + 1, nextIndex)] = true;
                    } else {
                        hGraph[getHexagonId(i, j)][getHexagonId(i + 1, size - Math.abs(j - 1 + sextant) % size)] = true;
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < hexagonIdRings.get(i).size(); j++) {
                hGraph[getHexagonId(i, j)][getHexagonId(i, j)] = true;
                int newIndex = j + 1;
                int newIndexBack = j - 1;

                if (newIndex >= hexagonIdRings.get(i).size()) {
                    newIndex = newIndex % hexagonIdRings.get(i).size();
                }
                if (newIndexBack < 0) {
                    newIndexBack = hexagonIdRings.get(i).size() - Math.abs(newIndexBack);
                }
                hGraph[getHexagonId(i, j)][getHexagonId(i, newIndex)] = true;
                hGraph[getHexagonId(i, j)][getHexagonId(i, newIndexBack)] = true;
            }
        }

        for (int i = 0; i < hGraph.length; i++) {
            for (int j = 0; j < hGraph[i].length; j++) {
                hGraph[j][i] = hGraph[i][j];
            }
        }
    } // end generateHexagonGraph

    public void generateNewIntersectionGraphManually () {

        for (int i = 0; i < 54; i++) {
            this.intersectionGraph.add(new ArrayList<Integer>());
        }

        intersectionGraph.get(0).add(1); // ring 0 start
        intersectionGraph.get(0).add(6);
        intersectionGraph.get(0).add(5);

        intersectionGraph.get(1).add(0);
        intersectionGraph.get(1).add(9);
        intersectionGraph.get(1).add(2);

        intersectionGraph.get(2).add(1);
        intersectionGraph.get(2).add(12);
        intersectionGraph.get(2).add(13);

        intersectionGraph.get(3).add(2);
        intersectionGraph.get(3).add(4);
        intersectionGraph.get(3).add(15);

        intersectionGraph.get(4).add(3);
        intersectionGraph.get(4).add(5);
        intersectionGraph.get(4).add(18);

        intersectionGraph.get(5).add(0);
        intersectionGraph.get(5).add(4);
        intersectionGraph.get(5).add(21);

        intersectionGraph.get(6).add(7); // ring 1 start
        intersectionGraph.get(6).add(0);
        intersectionGraph.get(6).add(23);

        intersectionGraph.get(7).add(6);
        intersectionGraph.get(7).add(8);
        intersectionGraph.get(7).add(26);

        intersectionGraph.get(8).add(7);
        intersectionGraph.get(8).add(9);
        intersectionGraph.get(8).add(29);

        intersectionGraph.get(9).add(8);
        intersectionGraph.get(9).add(10);
        intersectionGraph.get(9).add(1);

        intersectionGraph.get(10).add(9);
        intersectionGraph.get(10).add(11);
        intersectionGraph.get(10).add(31);

        intersectionGraph.get(11).add(10);
        intersectionGraph.get(11).add(12);
        intersectionGraph.get(11).add(34);

        intersectionGraph.get(12).add(2);
        intersectionGraph.get(12).add(11);
        intersectionGraph.get(12).add(13);

        intersectionGraph.get(13).add(12);
        intersectionGraph.get(13).add(14);
        intersectionGraph.get(13).add(36);

        intersectionGraph.get(14).add(13);
        intersectionGraph.get(14).add(15);
        intersectionGraph.get(14).add(39);

        intersectionGraph.get(15).add(3);
        intersectionGraph.get(15).add(14);
        intersectionGraph.get(15).add(16);

        intersectionGraph.get(16).add(15);
        intersectionGraph.get(16).add(17);
        intersectionGraph.get(16).add(41);

        intersectionGraph.get(17).add(16);
        intersectionGraph.get(17).add(18);
        intersectionGraph.get(17).add(44);

        intersectionGraph.get(18).add(17);
        intersectionGraph.get(18).add(4);
        intersectionGraph.get(18).add(19);

        intersectionGraph.get(19).add(18);
        intersectionGraph.get(19).add(20);
        intersectionGraph.get(19).add(46);

        intersectionGraph.get(20).add(19);
        intersectionGraph.get(20).add(21);
        intersectionGraph.get(20).add(49);

        intersectionGraph.get(21).add(5);
        intersectionGraph.get(21).add(20);
        intersectionGraph.get(21).add(22);

        intersectionGraph.get(22).add(21);
        intersectionGraph.get(22).add(23);
        intersectionGraph.get(22).add(51);

        intersectionGraph.get(23).add(6);
        intersectionGraph.get(23).add(22);
        intersectionGraph.get(23).add(24);

        intersectionGraph.get(24).add(23); // ring 2 start
        intersectionGraph.get(24).add(25);
        intersectionGraph.get(24).add(53);

        intersectionGraph.get(25).add(24);
        intersectionGraph.get(25).add(26);

        intersectionGraph.get(26).add(7);
        intersectionGraph.get(26).add(25);
        intersectionGraph.get(26).add(27);

        intersectionGraph.get(27).add(26);
        intersectionGraph.get(27).add(28);

        intersectionGraph.get(28).add(27);
        intersectionGraph.get(28).add(29);

        intersectionGraph.get(29).add(8);
        intersectionGraph.get(29).add(28);
        intersectionGraph.get(29).add(30);

        intersectionGraph.get(30).add(29);
        intersectionGraph.get(30).add(31);

        intersectionGraph.get(31).add(10);
        intersectionGraph.get(31).add(30);
        intersectionGraph.get(31).add(32);

        intersectionGraph.get(32).add(31);
        intersectionGraph.get(32).add(33);

        intersectionGraph.get(33).add(32);
        intersectionGraph.get(33).add(34);

        intersectionGraph.get(34).add(11);
        intersectionGraph.get(34).add(33);
        intersectionGraph.get(34).add(35);

        intersectionGraph.get(35).add(34);
        intersectionGraph.get(35).add(36);

        intersectionGraph.get(36).add(13);
        intersectionGraph.get(36).add(35);
        intersectionGraph.get(36).add(37);

        intersectionGraph.get(37).add(36);
        intersectionGraph.get(37).add(38);

        intersectionGraph.get(38).add(37);
        intersectionGraph.get(38).add(39);

        intersectionGraph.get(39).add(38);
        intersectionGraph.get(39).add(14);
        intersectionGraph.get(39).add(40);

        intersectionGraph.get(40).add(39);
        intersectionGraph.get(40).add(41);

        intersectionGraph.get(41).add(16);
        intersectionGraph.get(41).add(40);
        intersectionGraph.get(41).add(42);

        intersectionGraph.get(42).add(41);
        intersectionGraph.get(42).add(43);

        intersectionGraph.get(43).add(42);
        intersectionGraph.get(43).add(44);

        intersectionGraph.get(44).add(17);
        intersectionGraph.get(44).add(43);
        intersectionGraph.get(44).add(45);

        intersectionGraph.get(45).add(44);
        intersectionGraph.get(45).add(46);

        intersectionGraph.get(46).add(19);
        intersectionGraph.get(46).add(47);
        intersectionGraph.get(46).add(45);

        intersectionGraph.get(47).add(46);
        intersectionGraph.get(47).add(48);

        intersectionGraph.get(48).add(47);
        intersectionGraph.get(48).add(49);

        intersectionGraph.get(49).add(20);
        intersectionGraph.get(49).add(48);
        intersectionGraph.get(49).add(50);

        intersectionGraph.get(50).add(49);
        intersectionGraph.get(50).add(51);

        intersectionGraph.get(51).add(22);
        intersectionGraph.get(51).add(50);
        intersectionGraph.get(51).add(52);

        intersectionGraph.get(52).add(51);
        intersectionGraph.get(52).add(53);

        intersectionGraph.get(53).add(52);
        intersectionGraph.get(53).add(24);
    }

    /**
     * generates the intersection adjacency graph
     */
    private void generateIntersectionGraph () {
        // set all values in the 2d array to false
        for (int i = 0; i < 2; i++) { // rings
            for (int j = 0; j < this.intersectionIdRings.get(i).size(); j++) { // ids
                this.iGraph[i][intersectionIdRings.get(i).get(j)] = false;
            }
        }
        for (int i = 0; i < 3; i++) { //rings 0-2
            boolean hasNextLink = true; // is it looking to the next ring or prev ring
            int skipCount = 2; // # of intersections to skip to switch hasNext
            for (int j = 0; j < intersectionIdRings.get(i).size(); j++) { //columns starts at 1 and ends at 0 (wrapped by 1)

                int size = intersectionIdRings.get(i).size();
                int col = j % size; // wrap if needs to be 0
                int ringIndexDiff = -1;

                if (i == 1) {
                    if (skipCount == 0) {
                        hasNextLink = false;
                        skipCount = 2;
                    } else {
                        hasNextLink = true;
                        skipCount--;
                    }
                    col = (j + 1) % size;
                }

                if (i == 2) hasNextLink = false;

                int nextIntersection = (col + 1) % size;
                iGraph[getIntersectionId(i, col)][getIntersectionId(i, nextIntersection)] = true;

                //Log.d(TAG, "skip: " + skipCount);
                if (hasNextLink) {
                    //hLog.d(TAG, "nextLink: i: " + i + " col: " + col + " skip: " + skipCount);
                    if (col + ringIndexDiff == -1) {
                        iGraph[getIntersectionId(i, col)][getIntersectionId(i + 1, 15)] = true;
                    } else {
                        iGraph[getIntersectionId(i, col)][getIntersectionId(i + 1, col + ringIndexDiff)] = true;
                    }
                }
            }
        }
        //        StringBuilder str = new StringBuilder();
        //        str.append("\n\n----------------\n");
        //        for (int i = 0; i < iGraph.length; i++) {
        //            StringBuilder strRow = new StringBuilder();
        //            for (int j = 0; j < iGraph[i].length; j++) {
        //                strRow.append(i).append("-").append(j).append("=");
        //                if (iGraph[i][j]) strRow.append("t ");
        //                else strRow.append("f ");
        //            }
        //            //str.append("\n");
        //            Log.d("dev", "" + strRow.toString());
        //        }
        //        Log.d("dev", "" + str.toString());
    } // end generateIntersectionGraph method

    /**
     * generates the hexagon to integer map from the integer to hexagon map
     */
    private void generateHexToIntMap () {
        for (int i = 0; i < 19; i++) {
            this.hexToIntIdMap.add(new ArrayList<Integer>(6));
        }
        for (int i = 0; i < intToHexIdMap.size(); i++) {
            for (int j = 0; j < intToHexIdMap.get(i).size(); j++) {
                this.hexToIntIdMap.get(this.intToHexIdMap.get(i).get(j)).add(i);
            }
        }
    }

    /**
     * manually generates the integer to hexagon map
     */
    private void generateIntToHexMap () {
        for (int i = 0; i < 54; i++) {
            intToHexIdMap.add(new ArrayList<Integer>(3));
        }
        intToHexIdMap.get(0).add(0); // inner ring of intersections (ring 0)
        intToHexIdMap.get(0).add(1);
        intToHexIdMap.get(0).add(2);

        intToHexIdMap.get(1).add(0);
        intToHexIdMap.get(1).add(2);
        intToHexIdMap.get(1).add(3);

        intToHexIdMap.get(2).add(0);
        intToHexIdMap.get(2).add(3);
        intToHexIdMap.get(2).add(4);

        intToHexIdMap.get(3).add(0);
        intToHexIdMap.get(3).add(4);
        intToHexIdMap.get(3).add(5);

        intToHexIdMap.get(4).add(0);
        intToHexIdMap.get(4).add(5);
        intToHexIdMap.get(4).add(6);

        intToHexIdMap.get(5).add(0);
        intToHexIdMap.get(5).add(1);
        intToHexIdMap.get(5).add(6);

        intToHexIdMap.get(6).add(1); // middle ring of intersections (ring 1)
        intToHexIdMap.get(6).add(2);
        intToHexIdMap.get(6).add(8);

        intToHexIdMap.get(7).add(2);
        intToHexIdMap.get(7).add(8);
        intToHexIdMap.get(7).add(9);

        intToHexIdMap.get(8).add(2);
        intToHexIdMap.get(8).add(9);
        intToHexIdMap.get(8).add(10);

        intToHexIdMap.get(9).add(2);
        intToHexIdMap.get(9).add(3);
        intToHexIdMap.get(9).add(10);

        intToHexIdMap.get(10).add(3);
        intToHexIdMap.get(10).add(10);
        intToHexIdMap.get(10).add(11);

        intToHexIdMap.get(11).add(3);
        intToHexIdMap.get(11).add(11);
        intToHexIdMap.get(11).add(12);

        intToHexIdMap.get(12).add(3);
        intToHexIdMap.get(12).add(4);
        intToHexIdMap.get(12).add(12);

        intToHexIdMap.get(13).add(4);
        intToHexIdMap.get(13).add(12);
        intToHexIdMap.get(13).add(13);

        intToHexIdMap.get(14).add(4);
        intToHexIdMap.get(14).add(13);
        intToHexIdMap.get(14).add(14);

        intToHexIdMap.get(15).add(4);
        intToHexIdMap.get(15).add(5);
        intToHexIdMap.get(15).add(14);

        intToHexIdMap.get(16).add(5);
        intToHexIdMap.get(16).add(14);
        intToHexIdMap.get(16).add(15);

        intToHexIdMap.get(17).add(5);
        intToHexIdMap.get(17).add(15);
        intToHexIdMap.get(17).add(16);

        intToHexIdMap.get(18).add(5);
        intToHexIdMap.get(18).add(6);
        intToHexIdMap.get(18).add(16);

        intToHexIdMap.get(19).add(6);
        intToHexIdMap.get(19).add(16);
        intToHexIdMap.get(19).add(17);

        intToHexIdMap.get(20).add(6);
        intToHexIdMap.get(20).add(17);
        intToHexIdMap.get(20).add(18);

        intToHexIdMap.get(21).add(6);
        intToHexIdMap.get(21).add(1);
        intToHexIdMap.get(21).add(18);

        intToHexIdMap.get(22).add(1);
        intToHexIdMap.get(22).add(18);
        intToHexIdMap.get(22).add(7);

        intToHexIdMap.get(23).add(1);
        intToHexIdMap.get(23).add(7);
        intToHexIdMap.get(23).add(8);

        intToHexIdMap.get(24).add(7); // outer ring of intersections (ring 2)
        intToHexIdMap.get(24).add(8); // side 1

        intToHexIdMap.get(25).add(8);

        intToHexIdMap.get(26).add(8);
        intToHexIdMap.get(26).add(9);

        intToHexIdMap.get(27).add(9);

        intToHexIdMap.get(28).add(9);

        intToHexIdMap.get(29).add(9); // side 2
        intToHexIdMap.get(29).add(10);

        intToHexIdMap.get(30).add(10);

        intToHexIdMap.get(31).add(10);
        intToHexIdMap.get(31).add(11);

        intToHexIdMap.get(32).add(11);

        intToHexIdMap.get(33).add(11);

        intToHexIdMap.get(34).add(11); // side 3
        intToHexIdMap.get(34).add(12);

        intToHexIdMap.get(35).add(12);

        intToHexIdMap.get(36).add(12);
        intToHexIdMap.get(36).add(13);

        intToHexIdMap.get(37).add(13);

        intToHexIdMap.get(38).add(13);

        intToHexIdMap.get(39).add(13); // side 4
        intToHexIdMap.get(39).add(14);

        intToHexIdMap.get(40).add(14);

        intToHexIdMap.get(41).add(14);
        intToHexIdMap.get(41).add(15);

        intToHexIdMap.get(42).add(15);

        intToHexIdMap.get(43).add(15);

        intToHexIdMap.get(44).add(15); // side 5
        intToHexIdMap.get(44).add(16);

        intToHexIdMap.get(45).add(16);

        intToHexIdMap.get(46).add(16);
        intToHexIdMap.get(46).add(17);

        intToHexIdMap.get(47).add(17);

        intToHexIdMap.get(48).add(17);

        intToHexIdMap.get(49).add(17); // side 6
        intToHexIdMap.get(49).add(18);

        intToHexIdMap.get(50).add(18);

        intToHexIdMap.get(51).add(18);
        intToHexIdMap.get(51).add(7);

        intToHexIdMap.get(52).add(7);

        intToHexIdMap.get(53).add(7);
    }

    /**
     * generates an intersection adjacency matrix for the roads
     */
    private void generateRoadMatrix () {
        for (int i = 0; i < iGraph.length; i++) {
            for (int j = 0; j < iGraph[i].length; j++) {
                roadGraph[i][j] = new Road(-1, j, j);
            }
        }
        for (int i = 0; i < roadGraph.length; i++) {
            for (int j = 0; j < roadGraph[i].length; j++) {
                roadGraph[j][i] = roadGraph[i][j];
            }
        }
    }

    /**
     * Creates ports along the given intersection, and assigns them proper trade values
     */
    private void designatePorts () {
        portList.add(new Port(25, 3, 3)); //Ore
        portList.add(new Port(26, 3, 3));

        portList.add(new Port(29, 2, 1)); //Grain
        portList.add(new Port(30, 2, 1));

        portList.add(new Port(32, 3, -1)); //Anything
        portList.add(new Port(33, 3, -1));

        portList.add(new Port(35, 2, 2)); //Lumber
        portList.add(new Port(36, 2, 2));

        portList.add(new Port(39, 2, 0)); //Brick
        portList.add(new Port(40, 2, 0));

        portList.add(new Port(42, 3, -1)); //anything
        portList.add(new Port(43, 3, -1));

        portList.add(new Port(45, 3, -1)); //anything
        portList.add(new Port(46, 3, -1));

        portList.add(new Port(52, 3, -1)); //anything
        portList.add(new Port(53, 3, -1));

        portList.add(new Port(49, 2, 4));  //Wool
        portList.add(new Port(50, 2, 4));
    }

    /* ----- generic getter methods ----- */

    public ArrayList<ArrayList<Integer>> getIntersectionGraph () {
        return intersectionGraph;
    }

    /**
     * @return Hexagons organized into 2D ArrayList by rings.
     */
    private ArrayList<ArrayList<Integer>> getHexagonIdRings () {
        return hexagonIdRings;
    }

    /**
     * @return Intersections organized into a 2D ArrayList by rings.
     */
    private ArrayList<ArrayList<Integer>> getIntersectionIdRings () {
        return intersectionIdRings;
    }

    /**
     * @return Hexagon adjacency graph.
     */
    public boolean[][] getHGraph () {
        return hGraph;
    }

    /**
     * @return IntersectionDrawable adjacency graph.
     */
    public boolean[][] getIGraph () {
        return iGraph;
    }

    /**
     * @return Map of hexagons to intersections.
     */
    public ArrayList<ArrayList<Integer>> getHexToIntIdMap () {
        return hexToIntIdMap;
    }

    /**
     * @return Map of intersections to hexagons.
     */
    public ArrayList<ArrayList<Integer>> getIntToHexIdMap () {
        return intToHexIdMap;
    }

    /**
     * @return Array List of Road objects.
     */
    public ArrayList<Road> getRoads () {
        return this.roads;
    }

    /**
     * @return Array List of hexagons.
     */
    public ArrayList<Hexagon> getHexagons () {
        return this.hexagons;
    }

    /**
     * @return Robber object.
     */
    public Robber getRobber () {
        return this.robber;
    }

    /**
     * @return Array of Building objects. Indexed by intersection.
     */
    public Building[] getBuildings () {
        return this.buildings;
    }

    /**
     * @return Road adjacency graph.
     */
    public Road[][] getRoadGraph () {
        return roadGraph;
    }

    /* ----- generic setter methods ----- */

    public void setIntersectionGraph (ArrayList<ArrayList<Integer>> intersectionGraph) {
        this.intersectionGraph = intersectionGraph;
    }

    /**
     * @param hexagonIdRings 2d Array List of hexagons ids by rings.
     */
    public void setHexagonIdRings (ArrayList<ArrayList<Integer>> hexagonIdRings) {
        this.hexagonIdRings = hexagonIdRings;
    }

    /**
     * @param intersectionIdRings 2d Array List of intersection ids by rings.
     */
    public void setIntersectionIdRings (ArrayList<ArrayList<Integer>> intersectionIdRings) {
        this.intersectionIdRings = intersectionIdRings;
    }

    /**
     * @param hGraph A graph representing adjacency of intersections. E.g. iGraph[1][2] returns whether intersection 1 and intersection 2 are adjacent.
     */
    public void sethGraph (boolean[][] hGraph) {
        this.hGraph = hGraph;
    }

    /**
     * @param iGraph A graph representing adjacency of intersections. E.g. iGraph[1][2] returns whether intersection 1 and intersection 2 are adjacent.
     */
    public void setiGraph (boolean[][] iGraph) {
        this.iGraph = iGraph;
    }

    /**
     * @param hexToIntIdMap A map relating a hexagon to 6 adjacent intersections.
     */
    public void setHexToIntIdMap (ArrayList<ArrayList<Integer>> hexToIntIdMap) {
        this.hexToIntIdMap = hexToIntIdMap;
    }

    /**
     * @param intToHexIdMap A map relating an intersection to 3 adjacent hexagons.
     */
    public void setIntToHexIdMap (ArrayList<ArrayList<Integer>> intToHexIdMap) {
        this.intToHexIdMap = intToHexIdMap;
    }

    /**
     * @param buildings Array of all buildings on the board, indexed by intersection id.
     */
    public void setBuildings (Building[] buildings) {
        this.buildings = buildings;
    }

    /**
     * @param roads list of all roads on the board
     */
    public void setRoads (ArrayList<Road> roads) {
        this.roads = roads;
    }

    /**
     * @param roadGraph adjacency graph for roads
     */
    public void setRoadGraph (Road[][] roadGraph) {
        this.roadGraph = roadGraph;
    }

    /**
     * @param hexagons list of hexagons
     */
    public void setHexagons (ArrayList<Hexagon> hexagons) {
        this.hexagons = hexagons;
    }

    /**
     * @param robber Robber object
     */
    public void setRobber (Robber robber) {
        this.robber = robber;
    }

    public ArrayList<Port> getPortList () {
        return portList;
    }

    public void setPortList (ArrayList<Port> portList) {
        this.portList = portList;
    }

    public int getHighlightedHexagonId () {
        return highlightedHexagonId;
    }

    public void setHighlightedHexagonId (int highlightedHexagonId) {
        this.highlightedHexagonId = highlightedHexagonId;
    }

    public int getHighlightedIntersectionId () {
        return highlightedIntersectionId;
    }

    public void setHighlightedIntersectionId (int highlightedIntersectionId) {
        this.highlightedIntersectionId = highlightedIntersectionId;
    }

    /**
     * @param arr - graph array 2d boolean array
     */
    private void printGraph (boolean arr[][]) {
        Log.d(TAG, "printGraph() called with: arr = [" + arr + "]");
        StringBuilder str = new StringBuilder();
        str.append("\n\n----------------\n");
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                Log.i(TAG, "" + i + "-" + j + "=" + arr[i][j]);
            }
        }
    } // end printGraph

    /**
     * @param list - list to convert
     * @return - String
     */
    private String listToString (ArrayList<ArrayList<Integer>> list) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            str.append(i).append(": ");
            for (int j = 0; j < list.get(i).size(); j++) {
                str.append(list.get(i).get(j)).append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    } // end listToString method

    /**
     * @return String
     */
    @Override
    public String toString () {
        String str = "\n ---------- Board toString ---------- \n" + "robber=" + robber + "\nroads=" + roads + "\nbuildings: \n";
        for (int i = 0; i < buildings.length; i++) {
            str += "\t" + i + "=";
            if (buildings[i] == null) {
                str += "null";
            } else {
                str += buildings[i].toString();
            }
            if ((i + 1) % 9 == 0) {
                str += "\n";
            }
        }

        str += "\nhexagons:\n";

        for (int i = 0; i < hexagons.size(); i++) {
            str += "\t  ";
            str += hexagons.get(i).toString();
            if ((i + 1) % 2 == 0) {
                str += "\n";
            }
        }

        return str;
    }


    /*------------------------------Niraj Stuff----------------------------------------------*/
    public void setRobberLocation(int hexId){
        this.robber.setHexagonId(hexId);
    }
} // end Class