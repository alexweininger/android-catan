package edu.up.cs.androidcatan.catan.gamestate;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
 * https://github.com/alexweininger/android-catan
 **/

public class Board implements Serializable {

    private static final long serialVersionUID = -4950803135763998136L;
    private static final String TAG = "Board";

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

    // hexagonIdRings holds the IDs of each hexagon on the board, organized into rings.
    private ArrayList<ArrayList<Integer>> hexagonIdRings = new ArrayList<>();
    // intersectionIdRings holds the IDs of each intersection on the board, organized into rings.
    private ArrayList<ArrayList<Integer>> intersectionIdRings = new ArrayList<>();

    /*  hGraph and iGraph are 2d arrays that hold adjacency information for hexagons and intersections. */
    private boolean[][] hGraph = new boolean[19][19];

    // Maps relating hex to intersection and intersection to hex ids
    private ArrayList<ArrayList<Integer>> hexToIntIdMap = new ArrayList<>(); // rows: hex id - col: int ids
    private ArrayList<ArrayList<Integer>> intToHexIdMap = new ArrayList<>(); // rows: int id - col: hex id

    // Array of buildings on the board - indexed by intersection.
    private Building[] buildings = new Building[54];

    // List of all roads on board, in no particular order.
    private ArrayList<Road> roads = new ArrayList<>();

    // Adjacency graph identical to iGraph, however only contains Road objects and null.
    private Road[][] roadMatrix = new Road[54][54];

    // adjacency list representing all roads that can be built on the board
    private ArrayList<ArrayList<Road>> roadGraph = new ArrayList<>(54);

    private ArrayList<Hexagon> hexagons = new ArrayList<>(); // list of resource tiles

    private ArrayList<Port> portList = new ArrayList<>(); // list of ports on the board

    // adjacency list for intersections on the board
    private ArrayList<ArrayList<Integer>> intersectionAdjacencyList = new ArrayList<>();

    private Robber robber; // Robber object

    private int highlightedHexagonId = -1; // currently highlighted hexagon id
    private int highlightedIntersectionId = -1; // currently highlighted intersection ids

    /**
     * Board constructor
     */
    public Board() {
        Log.d(TAG, "Board() constructor called");

        // robber object
        this.robber = new Robber(0);

        populateHexagonIds(); // populate ids
        populateIntersectionIds();

        generateHexagonGraph(); // generate adj. graphs
        generateNewIntersectionGraphManually(); // new intersection graph
        generateRoadMatrix();

        generateIntToHexMap(); // generate maps
        generateHexToIntMap();

        // while the chit rule is not followed, generate a new tile order
        do {
            this.hexagons.clear();
            generateHexagonTiles(); // generate hex tiles
        } while (!checkChitRule());

        generatePorts(); // create port objects
    } // end Board constructor

    /**
     * @param b - board to copy
     */
    public Board(Board b) {
        if (b == null) {
            Log.e(TAG, "Board: board is null in board copy constructor");
            return;
        }
        if (b.getRobber() == null) {
            Log.e(TAG, "Board: board is null in board copy constructor");
            return;
        }
        populateHexagonIds(); // populate ids
        populateIntersectionIds();
        generateHexagonGraph();
        generateIntToHexMap();
        generateHexToIntMap();
        this.setRobber(new Robber(b.getRobber())); // class
        generateNewIntersectionGraphManually();
        this.setHighlightedHexagonId(b.highlightedHexagonId);
        this.setHighlightedIntersectionId(b.highlightedIntersectionId);
        this.setRoadGraph(b.roadGraph);
        generateRoadMatrix();

        for (Road road : b.getRoads()) {
            roads.add(new Road(road.getOwnerId(), road.getIntersectionAId(), road.getIntersectionBId()));
        }
        for (int i = 0; i < b.roadMatrix.length; i++) {
            for (int i1 = 0; i1 < b.roadMatrix[i].length; i1++) {
                this.roadMatrix[i][i1] = new Road(b.roadMatrix[i][i1].getOwnerId(), b.roadMatrix[i][i1].getIntersectionAId(), b.roadMatrix[i][i1].getIntersectionBId());
            }
        }

        System.arraycopy(b.roadMatrix, 0, this.roadMatrix, 0, b.roadMatrix.length);
        for (int i = 0; i < b.roadMatrix.length; i++) {
            this.roadMatrix[i] = Arrays.copyOf(b.roadMatrix[i], b.roadMatrix[i].length);
        }

        synchronized (this) {
            for (int i = 0; i < b.buildings.length; i++) {
                if (b.buildings[i] == null) this.buildings[i] = null;
                if (b.buildings[i] instanceof Settlement) {
                    Log.d(TAG, "Board: copying settlement");
                    this.buildings[i] = new Settlement(b.buildings[i].getOwnerId());
                } else if (b.buildings[i] instanceof City) {
                    this.buildings[i] = new City(b.buildings[i].getOwnerId());
                }
            }
        }
        Log.i(TAG, "Board: b.buildings=" + Arrays.toString(this.buildings));

        for (Hexagon hexagon : b.hexagons) {
            this.hexagons.add(new Hexagon(hexagon));
        }
        generatePorts(); // generate ports
    } // end Board deep copy constructor

    /* ----- validate building methods ----- */

    /**
     * @param playerId - player building the building
     * @param intersectionId - intersection of building
     * @return - is the building location valid
     */
    public boolean validBuildingLocation(int playerId, boolean isSetupPhase, int intersectionId) {
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

        // it is not the setup phase of the game
        if (!isSetupPhase) {
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
        for (int intersection : this.intersectionAdjacencyList.get(intersectionId)) { // for each adj. intersection
            Log.d(TAG, "validBuildingLocation: DISTANCE RULE - Checking intersection " + intersection + " for a building.");
            if (this.buildings[intersection] != null) { // check if building exists there
                Log.i(TAG, "validBuildingLocation: invalid - building at intersection " + intersectionId + " violates the distance rule (" + intersection + " is adj. and has a building).");
                return false;
            }
        }
        Log.d(TAG, "validBuildingLocation() returned: " + true);
        return true;
    }

    /**
     * checks to see is the location is valid place to build a city
     *
     * @param playerId the ID of the player who will own it
     * @param intersectionId the location on the board
     * @return If the player can build a city at given intersection.
     */
    public boolean validCityLocation(int playerId, int intersectionId) {
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
        if ((this.buildings[intersectionId] instanceof City)) {
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

    /* ----- road helper / checking methods ----- */

    /**
     * adds a road to the matrix
     *
     * @param playerId the id the player who owns it
     * @param intersectionA the starting intersection
     * @param intersectionB the ending intersection
     */
    public void addRoad(int playerId, int intersectionA, int intersectionB) {
        Log.d(TAG, "addRoad() called with: playerId = [" + playerId + "], intersectionA = [" + intersectionA + "], intersectionB = [" + intersectionB + "]");
        Road road = new Road(playerId, intersectionA, intersectionB);
        this.roads.add(road);
        this.roadMatrix[road.getIntersectionAId()][road.getIntersectionBId()].setOwnerId(road.getOwnerId());
        this.roadMatrix[road.getIntersectionBId()][road.getIntersectionAId()].setOwnerId(road.getOwnerId());
    }

    /**
     * @param i - intersection to check
     * @return returns if road is connected to given intersection
     */
    public boolean hasRoad(int i) {
        Log.i(TAG, "hasRoad: " + this.toString() + "");
        Log.d(TAG, "hasRoad() called with: i = [" + i + "]");
        if (i < 0) {
            Log.d(TAG, "hasRoad: negative input returned " + false);
            return false;
        }

        for (Road road : roads) {
            if (road.getIntersectionAId() == i || road.getIntersectionBId() == i) {
                return true;
            }
        }

        Log.d(TAG, "hasRoad() returned: " + false);
        return false;
    }

    /**
     * @param playerId - player to test if the intersection is connected
     * @param intersectionId - intersection to test
     * @return - is the intersection connected to the players buildings or roads?
     */
    public boolean isConnected(int playerId, int intersectionId) {
        Log.d(TAG, "isConnected() called with: playerId = [" + playerId + "], intersectionId = [" + intersectionId + "]");
        // check if intersection has no building and no road
        if (intersectionId < 0 || playerId < 0) {
            Log.d(TAG, "isConnected: invalid parameter" + false);
            return false;
        }

        // check if the intersection does NOT have a road
        if (!hasRoad(intersectionId)) {
            // check if there is NOT a building at the intersection
            if (this.buildings[intersectionId] == null) {
                Log.e(TAG, "isConnected: Not connected. Returned: " + false);
                return false;
            } else {
                Log.d(TAG, "isConnected: this.buildings[intersectionsId] is not null, intersectionId=" + intersectionId);
            }
        } else {
            Log.d(TAG, "isConnected: returned false");
        }
        // check if player is an owner of intersection
        Log.d(TAG, "isConnected() returned: " + getIntersectionOwners(intersectionId).contains(playerId));
        return getIntersectionOwners(intersectionId).contains(playerId);
    }

    /**
     * @param playerId - player building the road
     * @param a - intersection
     * @param b - intersection
     * @return - if road can be placed
     */
    public boolean validRoadPlacement(int playerId, boolean isSetupPhase, int a, int b, int settlementIntersection) {
        Log.d(TAG, "validRoadPlacement() called with: playerId = [" + playerId + "], isSetupPhase = [" + isSetupPhase + "], a = [" + a + "], b = [" + b + "]");

        // check if intersections are adjacent
        if (!this.intersectionAdjacencyList.get(a).contains(b)) {
            Log.e(TAG, "validRoadPlacement: Invalid road placement. Intersections are not adjacent.");
            Log.i(TAG, "validRoadPlacement: intersectionAdjacencyList: " + this.intersectionAdjacencyList.toString());
            return false;
        }

        // if it is the setup phase make sure the road is connected to the settlement they just built
        if (isSetupPhase)
            if (a != settlementIntersection && b != settlementIntersection) return false;

        // check if road is connected to players roads / buildings at either intersection
        if (isConnected(playerId, a) || isConnected(playerId, b)) {
            // check if 3 roads at either intersection
            if (getRoadsAtIntersection(a).size() > 2 || getRoadsAtIntersection(b).size() > 2) {
                Log.e(TAG, "validRoadPlacement: Invalid road placement. Roads are already built at this intersection.");
                return false;
            }
            // check if road is already built at the given intersections
            Log.i(TAG, "validRoadPlacement: this.roadMatrix.getOwnerId: " + this.roadMatrix[a][b].getOwnerId());
            if (this.roadMatrix[a][b].getOwnerId() != -1) {
                Log.e(TAG, "validRoadPlacement: Invalid road placement. A road is already built here. Returning false.");
                return false;
            }
            Log.d(TAG, "validRoadPlacement: Valid road placement.");
            return true;
        } else {
            Log.e(TAG, "validRoadPlacement: Invalid road placement. IntersectionDrawable(s) are not connected to players buildings or roads.");
            return false;
        }
    }

    /**
     * @param playerId - player building the road
     * @param a - intersection
     * @param b - intersection
     * @return - if road can be placed
     */
    public boolean validRoadPlacement(int playerId, boolean isSetupPhase, int a, int b) {
        return validRoadPlacement(playerId, isSetupPhase, a, b, -1);
    }

    /**
     * Depth-First-Search for looking for the longest road
     *
     * @param ownerId the ID of the player
     * @return longest road
     */
    public int dfs(int ownerId) {
        // check owner id validity
        if (ownerId < 0 || ownerId > 3) {
            Log.e(TAG, "dfs: ownerId invalid");
            return -1;
        }
        ArrayList<Road> pr = new ArrayList<>();
        Graph rg = new Graph(54);
        for (Road road : roads) {
            if (road.getOwnerId() == ownerId) {
                rg.addEdge(road.getIntersectionAId(), road.getIntersectionBId());
                pr.add(road);
            }
        }

        if (pr.size() < 5) {
            return -1;
        }
        rg.setPr(pr);
        Thread roadCalcThread = new Thread(rg);
        roadCalcThread.start();
        int m = rg.getMaxRoadLength();
        Log.d(TAG, "dfs() returned: " + ownerId);
        return m;
    }

    /**
     * Main method to calculate the longest road trophy holder. - AL
     *
     * @param playerList list of player objects
     * @return returns the playerId with the longest road for now (may need to change so that it returns the value instead)
     */
    public int getPlayerWithLongestRoad(ArrayList<Player> playerList) {
        Log.i(TAG, "updatePlayerWithLongestRoad() called with: playerList = [" + playerList + "]");
        ArrayList<Integer> longestRoadPerPlayer = new ArrayList<>();
        for (Player player : playerList) {
            //for each player there is an adjacency map as well as a list
            ArrayList<Road> playerRoads = new ArrayList<>();
            ArrayList<Integer> currentPlayerRoadLength = new ArrayList<>();
            for (Road road : roads) {
                if (road.getOwnerId() == player.getPlayerId()) {
                    playerRoads.add(road);
                }
            }

            if (playerRoads.size() < 5) {
                longestRoadPerPlayer.add(player.getPlayerId(), 0);
                break;
            } else {
                currentPlayerRoadLength.add(dfs(player.getPlayerId()));
                int max = 0;
                for (int n = 0; n < currentPlayerRoadLength.size(); n++) {
                    max = currentPlayerRoadLength.get(0);
                    if (currentPlayerRoadLength.get(n) >= max) {
                        max = currentPlayerRoadLength.get(n);
                    }
                }
                longestRoadPerPlayer.add(player.getPlayerId(), max);
            }
        }
        int playerIdLongestRoad = -1;
        int currLongestRoad = 0;
        //currently gives the longest road trophy to the most recent player checked within the array if
        //it shares the longest road with a prior player
        for (int n = 0; n < longestRoadPerPlayer.size(); n++) {
            if (longestRoadPerPlayer.get(n) > 0) {
                if (longestRoadPerPlayer.get(n) > currLongestRoad) {
                    currLongestRoad = longestRoadPerPlayer.get(n);
                    playerIdLongestRoad = n;
                }
            }
        }
        Log.d(TAG, "updatePlayerWithLongestRoad() returned: " + playerIdLongestRoad);
        return playerIdLongestRoad;
    }

    /**
     * @param intersectionId - to check for owners
     * @return - ArrayList of playerIds who either own a road that is connected to intersection, or have a building that is on this intersection
     */
    public ArrayList<Integer> getIntersectionOwners(int intersectionId) {
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
    public ArrayList<Road> getRoadsAtIntersection(int i) {
        Log.d(TAG, "getRoadsAtIntersection() called with: i = [" + i + "]");
        ArrayList<Road> result = new ArrayList<>();

        for (Road r : this.roads) {
            if (r.getIntersectionAId() == i || r.getIntersectionBId() == i) result.add(r);
        }
        Log.d(TAG, "getRoadsAtIntersection() returned: " + result);
        return result;
    }

    /**
     * @param chitValue - value of dice sum and tile chit value that will produce resources
     * @return list of hexagons with chitValue AND DO NOT HAVE ROBBER - AW
     */
    public ArrayList<Integer> getHexagonsFromChitValue(int chitValue) {
        Log.d(TAG, "getHexagonsFromChitValue() called with: chitValue = [" + chitValue + "]");
        ArrayList<Integer> hexagonIdList = new ArrayList<>();
        for (int i = 0; i < this.hexagons.size(); i++) {
            // check for chit value
            if (this.hexagons.get(i).getChitValue() == chitValue) {
                // check if robber is on hexagon
                if (this.robber.getHexagonId() != i) hexagonIdList.add(i);
                else
                    Log.i(TAG, "getHexagonsFromChitValue: robber was detected on hexagon, hexagon with id: " + i + " not producing resources chit values: " + chitValue);
            }
        }
        if (hexagonIdList.size() > 2)  // error checking
            Log.e(TAG, "getHexagonsFromChitValue: returning a list with more than 2 hexagons with chit values of: " + chitValue);

        Log.d(TAG, "getHexagonsFromChitValue() returned: " + hexagonIdList);
        return hexagonIdList;
    }

    /**
     * @param hexagonId - hexagonId to move the robber to
     * @return - true robber is moved, false if robber cannot be moved (trying to move to same hex) - AW
     */
    public boolean moveRobber(int hexagonId) {
        Log.d(TAG, "moveRobber() called with: hexagonId = [" + hexagonId + "]");
        // check if moving to same hexagon
        if (hexagonId < 0 || hexagonId > 18) return false;

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
    public boolean addBuilding(int intersectionId, Building building) {
        Log.d(TAG, "addBuilding() called with: intersectionId = [" + intersectionId + "], building = [" + building + "]");
        if (intersectionId < 0 || intersectionId > 53) {
            Log.e(TAG, "addBuilding: IntersectionId is invalid and not within bounds");
            return false;
        }

        if (this.buildings[intersectionId] != null) {
            if (building instanceof City) {
                if (!(this.buildings[intersectionId] instanceof Settlement)) {
                    return false;
                }
            } else {
                Log.e(TAG, "addBuilding: Cannot add building, building already exists at intersection id: " + intersectionId);
                return false;
            }
        }
        building.setOwnerId(building.getOwnerId());
        this.buildings[intersectionId] = building;
        return true;
    }

    /**
     * populating hexagonIdRings with hex IDs (0-18, 19 hexagons)
     */
    private void populateHexagonIds() {
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
    private void populateIntersectionIds() {
        int id = 0;
        for (int i = 0; i < 3; i++) {
            this.intersectionIdRings.add(new ArrayList<Integer>());
            for (int j = 0; j < ((2 * i) + 1) * 6; j++) {
                this.intersectionIdRings.get(i).add(id);
                id++;
            }
        }
    }

    /* ----- adjacency checking methods -----*/

    /**
     * @param hexagonId - hexagon id that you want to get adjacency of
     * @return ArrayList<Integer> - list of adj. hex id's
     */
    public ArrayList<Integer> getAdjacentHexagons(int hexagonId) {
        Log.d(TAG, "getAdjacentHexagons() called with: hexagonId = [" + hexagonId + "]");
        ArrayList<Integer> adjacentHexagons = new ArrayList<>(6);

        if (hexagonId < 0 || hexagonId > 18) return adjacentHexagons;

        for (int i = 0; i < 19; i++) {
            if (adjacentHexagons.size() > 6) {
                Log.d(TAG, "getAdjacentHexagons: ERROR got more than 6 adjacent hexagons");
                break;
            }
            if (hGraph[hexagonId][i] || hGraph[i][hexagonId]) adjacentHexagons.add(i);
        }
        Log.d(TAG, "getAdjacentHexagons() returned: " + adjacentHexagons);
        return adjacentHexagons;
    }

    /**
     * @param ring - ring of intersection
     * @param col - column within ring of intersection
     * @return - int intersection id
     */
    public int getIntersectionId(int ring, int col) {
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
    public int getHexagonId(int ring, int col) {
        return hexagonIdRings.get(ring).get(col);
    }

    /**
     * @param hexagonId - hexagon id - AW
     * @return Hexagon
     */
    public Hexagon getHexagonFromId(int hexagonId) {
        if (hexagonId < 0 || hexagonId >= this.hexagons.size()) { // error checking
            Log.d(TAG, "getHexagonFromId: ERROR cannot get hexagon with id: " + hexagonId + ". Does not exists in ArrayList hexagons.");
            return null;
        }
        return this.hexagons.get(hexagonId);
    }

    /*----- board helper methods for setting up board and populating data structures -----*/

    /**
     * Generates an array list containing all the chit values to be put on the board
     * the list it returns is shuffled to make it random.
     *
     * @return list of chits to put on the board
     */
    public ArrayList<Integer> generateChitList() {
        Log.d(TAG, "generateChitList() called");
        ArrayList<Integer> chitList = new ArrayList<>();

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
        Log.d(TAG, "generateChitList() returned: " + chitList);
        return chitList;
    }

    /**
     * this method makes sure the tiles follow the chit rule which states:
     * no chit values of 6 or 8 can be adjacent to one another
     *
     * @return If hexagon tiles follow the rule stating that no 6/8 chit can be adjacent to one another.
     */
    public boolean checkChitRule() {
        Log.d(TAG, "checkChitRule() called");
        // checks if any 8's or 6's are adjacent to one another

        // go through all hexagons
        for (int i = 0; i < this.hexagons.size(); i++) {
            if (hexagons.get(i).getChitValue() == 8) {
                for (Integer integer : getAdjacentHexagons(i)) {
                    if (integer != i) {
                        if (hexagons.get(integer).getChitValue() == 8) {
                            Log.e(TAG, "generateHexagonTiles: Chits 8 adjacent, reshuffling the hexagon tiles...");
                            Log.d(TAG, "checkChitRule() returned: " + false);
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
                            Log.d(TAG, "checkChitRule() returned: " + false);
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
    private void generateHexagonTiles() {
        Log.i(TAG, "generateHexagonTiles() called");

        //arrays that contain information regarding what each hexagon will contain
        int[] resourceTypeCount = {3, 4, 4, 3, 4, 1};
        final int[] resourceTypeCountsCorrect = {3, 4, 4, 3, 4, 1};
        int[] resources = {0, 1, 2, 3, 4, 5};

        Random random = new Random();
        ArrayList<Integer> chitList = generateChitList();

        //iterates through the hexagons and assigns each individual one the information required
        while (this.hexagons.size() < 19) {

            int randomResourceType;
            do {
                randomResourceType = random.nextInt(resourceTypeCount.length);
            } while (resourceTypeCount[randomResourceType] < 1);

            if (randomResourceType == 5) {
                Log.w(TAG, "generateHexagonTiles: randomResourceType = 5. Desert tile id = " + (hexagons.size()));
                hexagons.add(new Hexagon(resources[randomResourceType], 0, hexagons.size()));
            } else {
                Log.e(TAG, "generateHexagonTiles: size(): " + hexagons.size());
                int randomChitValue = chitList.get(0);
                chitList.remove(0);
                hexagons.add(new Hexagon(resources[randomResourceType], randomChitValue, hexagons.size()));
            }
            resourceTypeCount[randomResourceType]--;
            if (resources[randomResourceType] == 5) robber.setHexagonId(this.hexagons.size() - 1);

            Log.i(TAG, "generateHexagonTiles: hexagonsSize: " + this.hexagons.size());
        }

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

        // make sure there are exactly the correct amount of resource tiles on the board
        for (int i = 0; i < resourceCountChecks.length; i++) {
            if (resourceCountChecks[i] < resourceTypeCountsCorrect[i]) {
                Log.e(TAG, "generateHexagonTiles: Resource tile count check failed for resource " + i + ". There are " + resourceCountChecks[i] + " of this resources when there should only be " + resourceTypeCountsCorrect[i] + ".");
            }
        }
    }

    /**
     * generateHexagonGraph
     */
    private void generateHexagonGraph() {
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

    /**
     * Method puts the hexagons in a new order starting from the top left to bottom right (line by line)
     *
     * @return An array list of reorganized hexagons
     */
    public ArrayList<Hexagon> getHexagonListForDrawing() {
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
     * Checks to see if there is a building already at the given intersection or not
     *
     * @param intersectionId - intersection id
     * @return whether there is a building at that given intersection
     */
    public boolean hasBuilding(int intersectionId) {
        if (intersectionId < 0 || intersectionId > 53) return false;
        return this.buildings[intersectionId] != null;
    }

    /**
     * @param intersectionId - intersection id
     * @return - the building located at given intersection
     */
    public Building getBuildingAtIntersection(int intersectionId) {
        return this.buildings[intersectionId];
    }

    private void generateNewIntersectionGraphManually() {

        for (int i = 0; i < 54; i++) {
            this.intersectionAdjacencyList.add(new ArrayList<Integer>());
        }

        intersectionAdjacencyList.get(0).add(1); // ring 0 start
        intersectionAdjacencyList.get(0).add(6);
        intersectionAdjacencyList.get(0).add(5);

        intersectionAdjacencyList.get(1).add(0);
        intersectionAdjacencyList.get(1).add(9);
        intersectionAdjacencyList.get(1).add(2);

        intersectionAdjacencyList.get(2).add(1);
        intersectionAdjacencyList.get(2).add(12);
        intersectionAdjacencyList.get(2).add(3);

        intersectionAdjacencyList.get(3).add(2);
        intersectionAdjacencyList.get(3).add(4);
        intersectionAdjacencyList.get(3).add(15);

        intersectionAdjacencyList.get(4).add(3);
        intersectionAdjacencyList.get(4).add(5);
        intersectionAdjacencyList.get(4).add(18);

        intersectionAdjacencyList.get(5).add(0);
        intersectionAdjacencyList.get(5).add(4);
        intersectionAdjacencyList.get(5).add(21);

        intersectionAdjacencyList.get(6).add(7); // ring 1 start
        intersectionAdjacencyList.get(6).add(0);
        intersectionAdjacencyList.get(6).add(23);

        intersectionAdjacencyList.get(7).add(6);
        intersectionAdjacencyList.get(7).add(8);
        intersectionAdjacencyList.get(7).add(26);

        intersectionAdjacencyList.get(8).add(7);
        intersectionAdjacencyList.get(8).add(9);
        intersectionAdjacencyList.get(8).add(29);

        intersectionAdjacencyList.get(9).add(8);
        intersectionAdjacencyList.get(9).add(10);
        intersectionAdjacencyList.get(9).add(1);

        intersectionAdjacencyList.get(10).add(9);
        intersectionAdjacencyList.get(10).add(11);
        intersectionAdjacencyList.get(10).add(31);

        intersectionAdjacencyList.get(11).add(10);
        intersectionAdjacencyList.get(11).add(12);
        intersectionAdjacencyList.get(11).add(34);

        intersectionAdjacencyList.get(12).add(2);
        intersectionAdjacencyList.get(12).add(11);
        intersectionAdjacencyList.get(12).add(13);

        intersectionAdjacencyList.get(13).add(12);
        intersectionAdjacencyList.get(13).add(14);
        intersectionAdjacencyList.get(13).add(36);

        intersectionAdjacencyList.get(14).add(13);
        intersectionAdjacencyList.get(14).add(15);
        intersectionAdjacencyList.get(14).add(39);

        intersectionAdjacencyList.get(15).add(3);
        intersectionAdjacencyList.get(15).add(14);
        intersectionAdjacencyList.get(15).add(16);

        intersectionAdjacencyList.get(16).add(15);
        intersectionAdjacencyList.get(16).add(17);
        intersectionAdjacencyList.get(16).add(41);

        intersectionAdjacencyList.get(17).add(16);
        intersectionAdjacencyList.get(17).add(18);
        intersectionAdjacencyList.get(17).add(44);

        intersectionAdjacencyList.get(18).add(17);
        intersectionAdjacencyList.get(18).add(4);
        intersectionAdjacencyList.get(18).add(19);

        intersectionAdjacencyList.get(19).add(18);
        intersectionAdjacencyList.get(19).add(20);
        intersectionAdjacencyList.get(19).add(46);

        intersectionAdjacencyList.get(20).add(19);
        intersectionAdjacencyList.get(20).add(21);
        intersectionAdjacencyList.get(20).add(49);

        intersectionAdjacencyList.get(21).add(5);
        intersectionAdjacencyList.get(21).add(20);
        intersectionAdjacencyList.get(21).add(22);

        intersectionAdjacencyList.get(22).add(21);
        intersectionAdjacencyList.get(22).add(23);
        intersectionAdjacencyList.get(22).add(51);

        intersectionAdjacencyList.get(23).add(6);
        intersectionAdjacencyList.get(23).add(22);
        intersectionAdjacencyList.get(23).add(24);

        intersectionAdjacencyList.get(24).add(23); // ring 2 start
        intersectionAdjacencyList.get(24).add(25);
        intersectionAdjacencyList.get(24).add(53);

        intersectionAdjacencyList.get(25).add(24);
        intersectionAdjacencyList.get(25).add(26);

        intersectionAdjacencyList.get(26).add(7);
        intersectionAdjacencyList.get(26).add(25);
        intersectionAdjacencyList.get(26).add(27);

        intersectionAdjacencyList.get(27).add(26);
        intersectionAdjacencyList.get(27).add(28);

        intersectionAdjacencyList.get(28).add(27);
        intersectionAdjacencyList.get(28).add(29);

        intersectionAdjacencyList.get(29).add(8);
        intersectionAdjacencyList.get(29).add(28);
        intersectionAdjacencyList.get(29).add(30);

        intersectionAdjacencyList.get(30).add(29);
        intersectionAdjacencyList.get(30).add(31);

        intersectionAdjacencyList.get(31).add(10);
        intersectionAdjacencyList.get(31).add(30);
        intersectionAdjacencyList.get(31).add(32);

        intersectionAdjacencyList.get(32).add(31);
        intersectionAdjacencyList.get(32).add(33);

        intersectionAdjacencyList.get(33).add(32);
        intersectionAdjacencyList.get(33).add(34);

        intersectionAdjacencyList.get(34).add(11);
        intersectionAdjacencyList.get(34).add(33);
        intersectionAdjacencyList.get(34).add(35);

        intersectionAdjacencyList.get(35).add(34);
        intersectionAdjacencyList.get(35).add(36);

        intersectionAdjacencyList.get(36).add(13);
        intersectionAdjacencyList.get(36).add(35);
        intersectionAdjacencyList.get(36).add(37);

        intersectionAdjacencyList.get(37).add(36);
        intersectionAdjacencyList.get(37).add(38);

        intersectionAdjacencyList.get(38).add(37);
        intersectionAdjacencyList.get(38).add(39);

        intersectionAdjacencyList.get(39).add(38);
        intersectionAdjacencyList.get(39).add(14);
        intersectionAdjacencyList.get(39).add(40);

        intersectionAdjacencyList.get(40).add(39);
        intersectionAdjacencyList.get(40).add(41);

        intersectionAdjacencyList.get(41).add(16);
        intersectionAdjacencyList.get(41).add(40);
        intersectionAdjacencyList.get(41).add(42);

        intersectionAdjacencyList.get(42).add(41);
        intersectionAdjacencyList.get(42).add(43);

        intersectionAdjacencyList.get(43).add(42);
        intersectionAdjacencyList.get(43).add(44);

        intersectionAdjacencyList.get(44).add(17);
        intersectionAdjacencyList.get(44).add(43);
        intersectionAdjacencyList.get(44).add(45);

        intersectionAdjacencyList.get(45).add(44);
        intersectionAdjacencyList.get(45).add(46);

        intersectionAdjacencyList.get(46).add(19);
        intersectionAdjacencyList.get(46).add(47);
        intersectionAdjacencyList.get(46).add(45);

        intersectionAdjacencyList.get(47).add(46);
        intersectionAdjacencyList.get(47).add(48);

        intersectionAdjacencyList.get(48).add(47);
        intersectionAdjacencyList.get(48).add(49);

        intersectionAdjacencyList.get(49).add(20);
        intersectionAdjacencyList.get(49).add(48);
        intersectionAdjacencyList.get(49).add(50);

        intersectionAdjacencyList.get(50).add(49);
        intersectionAdjacencyList.get(50).add(51);

        intersectionAdjacencyList.get(51).add(22);
        intersectionAdjacencyList.get(51).add(50);
        intersectionAdjacencyList.get(51).add(52);

        intersectionAdjacencyList.get(52).add(51);
        intersectionAdjacencyList.get(52).add(53);

        intersectionAdjacencyList.get(53).add(52);
        intersectionAdjacencyList.get(53).add(24);
    }

    /**
     * generates the hexagon to integer map from the integer to hexagon map
     */
    private void generateHexToIntMap() {
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
    private void generateIntToHexMap() {
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
    private void generateRoadMatrix() {
        for (int i = 0; i < 54; i++) {
            for (int j = 0; j < 54; j++) {
                roadMatrix[i][j] = new Road(-1, i, j);
            }
        }
        for (int i = 0; i < roadMatrix.length; i++) {
            for (int j = 0; j < roadMatrix[i].length; j++) {
                roadMatrix[j][i] = roadMatrix[i][j];
            }
        }
    }

    /**
     * Creates ports along the given intersection, and assigns them proper ge values
     */
    private void generatePorts() {
        portList.add(new Port(25, 26, 3, 3)); //Ore
        portList.add(new Port(29, 30, 2, 1)); //Grain
        portList.add(new Port(32, 33, 3, -1)); //Anything
        portList.add(new Port(35, 36, 2, 2)); //Lumber
        portList.add(new Port(39, 40, 2, 0)); //Brick
        portList.add(new Port(42, 43, 3, -1)); //anything
        portList.add(new Port(45, 46, 3, -1)); //anything
        portList.add(new Port(52, 53, 3, -1)); //anything
        portList.add(new Port(49, 50, 2, 4));  //Wool
    }

    /* ----- generic getter methods ----- */

    public ArrayList<ArrayList<Integer>> getIntersectionAdjacencyList() {
        return intersectionAdjacencyList;
    }

    /**
     * @return Hexagon adjacency graph.
     */
    public boolean[][] getHGraph() {
        return hGraph;
    }

    /**
     * @return Map of hexagons to intersections.
     */
    public ArrayList<ArrayList<Integer>> getHexToIntIdMap() {
        return hexToIntIdMap;
    }

    /**
     * @return Map of intersections to hexagons.
     */
    public ArrayList<ArrayList<Integer>> getIntToHexIdMap() {
        return intToHexIdMap;
    }

    /**
     * @return Array List of Road objects.
     */
    public ArrayList<Road> getRoads() {
        return this.roads;
    }

    /**
     * @return Array List of hexagons.
     */
    public ArrayList<Hexagon> getHexagons() {
        return this.hexagons;
    }

    /**
     * @return Robber object.
     */
    public Robber getRobber() {
        return this.robber;
    }

    /**
     * @return Array of Building objects. Indexed by intersection.
     */
    public Building[] getBuildings() {
        return this.buildings;
    }

    /**
     * @return Road adjacency graph.
     */
    public Road[][] getRoadMatrix() {
        return roadMatrix;
    }

    /* ----- generic setter methods ----- */

    private void setRoadGraph(ArrayList<ArrayList<Road>> roadGraph) {
        this.roadGraph = roadGraph;
    }

    /**
     * @param buildings Array of all buildings on the board, indexed by intersection id.
     */
    public void setBuildings(Building[] buildings) {
        this.buildings = buildings;
    }

    /**
     * @param roads list of all roads on the board
     */
    public void setRoads(ArrayList<Road> roads) {
        this.roads = roads;
    }

    /**
     * @param robber Robber object
     */
    public void setRobber(Robber robber) {
        this.robber = new Robber(robber);
    }

    public ArrayList<Port> getPortList() {
        return portList;
    }

    private void setHighlightedHexagonId(int highlightedHexagonId) {
        this.highlightedHexagonId = highlightedHexagonId;
    }

    private void setHighlightedIntersectionId(int highlightedIntersectionId) {
        this.highlightedIntersectionId = highlightedIntersectionId;
    }

    /**
     * @return String
     */
    @Override
    public String toString() {
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
} // end Class