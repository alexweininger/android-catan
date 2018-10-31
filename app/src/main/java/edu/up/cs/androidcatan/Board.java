package edu.up.cs.androidcatan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 30th, 2018
 * https://github.com/alexweininger/android-catan
 **/

public class Board {
    /*
     * External Citation
     * Date: 8 October 2018
     * Problem: Struggling to represent board and tiles.
     * Resource:
     * https://www.academia.edu/9699475/Settlers_of_Catan_Developing_an_Implementation_
     * of_an_Emerging_Classic_Board_Game_in_Java
     * Solution: We used the concepts and ideas from this research paper to help us represent the board
     * information and the hexagons.
     */

    /* 'Rings' are used to organize the following ID 2D-ArrayLists. Rings in context mean ring of hexagons or intersections
     * on the board. So for hexagons, the first ring contains the very middle hexagon. Ring 2 are the hexagons around that one.
     * Hexagon 0 is the center, and hex 1 is directly right of hex 0, and then they are numbered by ring. So ring 0 has 1
     * hexagon. Ring 2 has 6, and ring 3 (outer ring) has 12 hexagons.
     */

    private static final String TAG = "Board";

    // hexagonIdRings holds the IDs of each hexagon on the board, organized into rings.
    private ArrayList<ArrayList<Integer>> hexagonIdRings = new ArrayList<>();
    // intersectionIdRings holds the IDs of each intersection on the board, organized into rings.
    private ArrayList<ArrayList<Integer>> intersectionIdRings = new ArrayList<>();

    /*  hGraph and iGraph are 2d arrays that hold adjacency information for hexagons and intersections. */
    private boolean[][] hGraph = new boolean[19][19];
    private boolean[][] iGraph = new boolean[54][54];

    /* maps relating hex to intersection and intersection to hex ids */
    private ArrayList<ArrayList<Integer>> hexToIntIdMap = new ArrayList<>(); // rows: hex id - col: int ids
    private ArrayList<ArrayList<Integer>> intToHexIdMap = new ArrayList<>(); // rows: int id - col: hex id

    private Building[] buildings = new Building[53];

    private ArrayList<Road> roads = new ArrayList<>();

    private Road[][] roadGraph = new Road[54][54];

    private ArrayList<Hexagon> hexagons = new ArrayList<>(); // list of resource tiles
    private Robber robber; // robber object

    private ArrayList<Integer> portIntersectionLocations = new ArrayList<>(12);

    Board() {
        // populate ids
        populateHexagonIds();
        populateIntersectionIds();
        populatePortIntersectionIds();

        // generate adj. graphs
        generateHexagonGraph();
        generateIntersectionGraph();
        generateRoadMatrix();

        // print graphs
        printGraph(hGraph);
        printGraph(iGraph);

        // generate maps
        generateIntToHexMap();
        generateHexToIntMap();

        // generate hex tiles
        generateHexagonTiles();

        Log.d("devInfo", "INFO: int to hex map: " + this.intToHexIdMap.toString());
        Log.d("devInfo", "INFO: hex to int map" + this.hexToIntIdMap.toString());

        int desertTileId = 0;
        robber = new Robber(desertTileId);

    } // end Board constructor

    /**
     * @param b - board to copy
     */
    Board(Board b) {
        this.hexagonIdRings = b.getHexagonIdRings();
        this.intersectionIdRings = b.getIntersectionIdRings();
        this.hGraph = b.getHGraph();
        this.iGraph = b.getIGraph();
        this.hexToIntIdMap = b.getHexToIntIdMap();
        this.intToHexIdMap = b.getIntToHexIdMap();
        this.buildings = b.getBuildings();
        this.roads = b.getRoads();
        this.hexagons = b.getHexagons();
        this.robber = new Robber(b.getRobber());
        this.portIntersectionLocations = b.getPortIntersectionLocations();
        this.roadGraph = b.roadGraph;

    } // end Board deep copy constructor

    /* ----- helper / checking methods ----- */

    /**
     * @param playerId       - player to test if the intersection is connected
     * @param intersectionId - intersection to test
     * @return - is the intersection connected to the players buildings or roads?
     */
    private boolean isConnected(int playerId, int intersectionId) {
        // check if intersection has no building and no road
        if (!hasRoad(intersectionId) && this.buildings[intersectionId] == null) {
            return false;
        }
        // check if player is an owner of intersection
        return getIntersectionOwners(intersectionId).contains(playerId);
    }

    /* ----- road methods ----- */

    /**
     * @param playerId - player building the road
     * @param a        - intersection
     * @param b        - intersection
     * @return - if road can be placed
     */
    boolean validRoadPlacement(int playerId, int a, int b) {
        // check if intersections are adjacent
        if (!iGraph[a][b]) {
            return false;
        }

        // check if road is connected to players roads / buildings at either intersection
        if (!isConnected(playerId, a) && !isConnected(playerId, b)) {
            return false;
        }

        // check if 3 roads at either intersection
        if (getRoadsAtIntersection(a).size() > 2 || getRoadsAtIntersection(b).size() > 2) {
            return false;
        }

        // check if road is already built
        if (this.roadGraph[a][b].getOwnerId() != -1) {
            return false;
        }

        return true;
    }

    /**
     * @param playerId
     * @param intersectionA
     * @param intersectionB
     */
    void addRoad(int playerId, int intersectionA, int intersectionB) {
        Road road = new Road(playerId, intersectionA, intersectionB);
        this.roads.add(road);
        this.roadGraph[road.getIntersectionAId()][road.getIntersectionBId()].setOwnerId(road.getOwnerId());
        this.roadGraph[road.getIntersectionBId()][road.getIntersectionAId()] = road;
    }

    /**
     * @param i - intersection to check
     * @return returns if road is connected to given intersection
     */
    private boolean hasRoad(int i) {
        for (Road road : roadGraph[i]) {
            if (road.getOwnerId() != -1) {
                return true;
            }
        }
        return false;
    }

    // TODO
    int getPlayerRoadLength(int playerId) {
        return 0;
    }

    /**
     * TODO Andrew
     *
     * @param intersectionId       - intersection to start at
     * @param checkedIntersections - array list of already checked roads / intersections
     * @return - road length
     */
    int getRoadLength(int intersectionId, ArrayList<Integer> checkedIntersections) {
        checkedIntersections.add(intersectionId);
        // base case if road is dead end
        ArrayList<Integer> adjInts = getAdjacentIntersections(intersectionId);
        for (int i = 0; i < adjInts.size(); i++) {
            if (hasRoad(adjInts.get(i)) && !checkedIntersections.contains(adjInts.get(i))) {
                return getRoadLength(adjInts.get(i), checkedIntersections) + 1;
            }
        }
        return 0;
    }

    /* ----- building methods ----- */

    /**
     * @param playerId       - player building the building
     * @param intersectionId - intersection of building
     * @return - is the building location valid
     */
    boolean validBuildingLocation(int playerId, int intersectionId) {
        /* checks:
         * 1. if connected
         * 2. if occupied by building
         * 3. distance rule
         */

        // check if the intersection is connected to players' roads/buildings
        if(!isConnected(playerId, intersectionId)) {
            Log.i(TAG, "validBuildingLocation: invalid location because intersection " + intersectionId + " is not connected.");
            return false;
        }

        // check if intersection already has a building on it
        if(this.buildings[intersectionId] != null) {
            Log.i(TAG, "validBuildingLocation: invalid location because intersection " + intersectionId + " already has a building on it.");
            return false;
        }

        // check if adjacent intersections do not have buildings
        for (int intersection : getAdjacentIntersections(intersectionId)) { // for each adj. intersection
            if (this.buildings[intersectionId] != null) { // check if building exists there
                Log.i(TAG, "validBuildingLocation: invalid - building at intersection " + intersectionId + " violates the distance rule (" + intersection + " is adj. and has a building).");
                return false;
            }
        }

        return true;
    }

    private boolean hasBuilding(int intersectionId) {
        return this.buildings[intersectionId] != null;
    }

    /**
     * @param intersectionId - intersection id
     * @return - the building located at given intersection
     */
    Building getBuildingAtIntersection(int intersectionId) {
        return this.buildings[intersectionId];
    }

    /**
     * builds the ArrayList of Hexagon objects, creating the correct amount of each resource tile,
     * randomly assigning them to locations. Also randomly gives Hexagon a chit value.
     */
    private void generateHexagonTiles() {
        int[] resourceTypeCount = {4, 3, 3, 3, 4};
        int[] chitValuesCount = {0, 0, 1, 2, 2, 2, 2, 0, 2, 2, 2, 2, 1};
        int[] resources = {0, 1, 2, 3, 4};
        for (int i = 0; i < 18; i++) {
            int max = resourceTypeCount.length - 1;
            Random random = new Random();
            int randomResourceType = random.nextInt((max) + 1);
            while (resourceTypeCount[randomResourceType] < 0) {
                randomResourceType = random.nextInt((max) + 1);
            }
            max = chitValuesCount.length - 1;
            int randomChitValue = random.nextInt((max) + 1);
            while (chitValuesCount[randomChitValue] < 0) {
                randomChitValue = random.nextInt((max) + 1);
            }


            hexagons.add(new Hexagon(resources[randomResourceType], randomChitValue));
            resourceTypeCount[randomResourceType]--;
        }
    }

    /**
     * @param intersectionId - to check for owners
     * @return - ArrayList of playerIds who either own a road that is connected to intersection, or have a building that is on this intersection
     */
    public ArrayList<Integer> getIntersectionOwners(int intersectionId) {
        ArrayList<Integer> result = new ArrayList<Integer>();

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
        return result;
    }

    /**
     * @param i - intersection id
     * @return ArrayList of roads connected to that intersection
     */
    private ArrayList<Road> getRoadsAtIntersection(int i) {
        ArrayList<Road> result = new ArrayList<>();

        for (Road r : this.roads) {
            if (r.getIntersectionAId() == i || r.getIntersectionBId() == i) {
                result.add(r);
            }
        }
        return result;
    }

    /** TODO? do we need this?
     * returns whether a given player is an owner of the intersection
     *
     * @param intersectionId - intersection to check if playerId owns
     * @param playerId       - playerId to check against
     * @return
     */
    public boolean isIntersectionOwner(int intersectionId, int playerId) {
        return false;
    }

    /**
     * @param chitValue - value of dice sum and tile chit value that will produce resources
     * @return list of hexagons with chitValue AND DO NOT HAVE ROBBER - AW
     */
    ArrayList<Integer> getHexagonsFromChitValue(int chitValue) {
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
        return hexagonIdList;
    }

    /**
     * @param hexagonId - hexagonId to move the robber to
     * @return - true robber is moved, false if robber cannot be moved (trying to move to same hex) - AW
     */
    boolean moveRobber(int hexagonId) {
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
     * @param building       - building object
     */
    boolean addBuilding(int intersectionId, Building building) {
        if (this.buildings[intersectionId] != null) {
            Log.e(TAG, "addBuilding: Cannot add building, building already exists at intersection id: " + intersectionId);
            return false;
        }
        this.buildings[intersectionId] = building;
        return true;
    }

    /* ----- adjacency checking methods -----*/

    /**
     * TODO TEST
     * getAdjacentIntersections
     *
     * @param intersectionId - given intersection i (0-53)
     * @return - ArrayList of intersection ids that are adjacent to the given intersection id
     */
    ArrayList<Integer> getAdjacentIntersections(int intersectionId) {
        ArrayList<Integer> adjacentIntersections = new ArrayList<>(6);
        for (int i = 0; i < 54; i++) {
            if (adjacentIntersections.size() > 3) {
                Log.e(TAG, "getAdjacentIntersections: Received more than 3 adjacent intersections. That makes no sense.");
            }
            if (iGraph[intersectionId][i] || iGraph[i][intersectionId]) {
                adjacentIntersections.add(i);
            }
        }
        return adjacentIntersections;
    }

    /**
     * TODO TEST
     *
     * @param hexagonId - hexagon id that you want to get adjacency of
     * @return ArrayList<Integer> - list of adj. hex id's
     */
    public ArrayList<Integer> getAdjacentHexagons(int hexagonId) {
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
        return adjacentHexagons;
    }

    /**
     * @param intId1 - intersection id
     * @param intId2 - intersection id
     * @return - boolean adjacency
     */
    boolean intersectionAdjCheck(int intId1, int intId2) {
        return (iGraph[intId1][intId2] || iGraph[intId2][intId1]);
    }

    /**
     * @param hexId1 -
     * @param hexId2 -
     * @return - boolean
     */
    public boolean checkHexagonAdjacency(int hexId1, int hexId2) {
        return (hGraph[hexId1][hexId2] || hGraph[hexId2][hexId1]);
    }

    /** TODO ports
     * @param intersectionId - intersection to check for port adjacency
     * @return - if the given intersection is adjacent to a port AW
     */
    public boolean checkPortAdjacency(int intersectionId) {
        return portIntersectionLocations.contains(intersectionId);
    }

    /**
     * @param ring - ring of intersection
     * @param col  - column within ring of intersection
     * @return - int intersection id
     */
    private int getIntersectionId(int ring, int col) {
        return intersectionIdRings.get(ring).get(col);
    }

    /**
     * @param ring - hexagon ring (0-2)
     * @param col  - column within hexagon ring
     * @return - int hexagon id
     */
    private int getHexagonId(int ring, int col) {
        return hexagonIdRings.get(ring).get(col);
    }

    /**
     * @param hexagonId - hexagon id - AW
     * @return Hexagon
     */
    Hexagon getHexagonFromId(int hexagonId) {
        if (hexagonId < 0 || hexagonId >= this.hexagons.size()) { // error checking
            Log.d(TAG, "getHexagonFromId: ERROR cannot get hexagon with id: " + hexagonId + ". Does not exists in ArrayList hexagons.");
            return null;
        }
        return this.hexagons.get(hexagonId);
    }


    /*----- board helper methods for setting up board and populating data structures -----*/

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
     * generates the intersection adjacency graph
     */
    private void generateIntersectionGraph() {
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

                Log.d("dev", "skip: " + skipCount);
                if (hasNextLink) {
                    Log.d("dev", "nextLink: i: " + i + " col: " + col + " skip: " + skipCount);
                    if (col + ringIndexDiff == -1) {
                        iGraph[getIntersectionId(i, col)][getIntersectionId(i + 1, 15)] = true;
                    } else {
                        iGraph[getIntersectionId(i, col)][getIntersectionId(i + 1, col + ringIndexDiff)] = true;
                    }
                }
            }
        }
    } // end generateIntersectionGraph method

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

        intToHexIdMap.get(3).add(4);
        intToHexIdMap.get(3).add(5);
        intToHexIdMap.get(3).add(0);

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
        intToHexIdMap.get(8).add(10);
        intToHexIdMap.get(8).add(9);

        intToHexIdMap.get(9).add(2);
        intToHexIdMap.get(9).add(3);
        intToHexIdMap.get(9).add(10);

        intToHexIdMap.get(10).add(3);
        intToHexIdMap.get(10).add(11);
        intToHexIdMap.get(10).add(10);

        intToHexIdMap.get(11).add(3);
        intToHexIdMap.get(11).add(12);
        intToHexIdMap.get(11).add(11);

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
        for (int i = 0; i < iGraph.length; i++) {
            for (int j = 0; j < iGraph[i].length; j++) {
                roadGraph[i][j] = new Road(i, j, -1);
            }
        }
        for (int i = 0; i < roadGraph.length; i++) {
            for (int j = 0; j < roadGraph[i].length; j++) {
                roadGraph[j][i] = roadGraph[i][j];
            }
        }
    }

    /**
     * TODO remove and fix
     * adds ports to the intersection and port hash map
     */
    private void populatePortIntersectionIds() {
        for (int i = 0; i < 6; i++) {
            portIntersectionLocations.add(17 + i * 6);
            portIntersectionLocations.add(17 + i * 6 + 1);
        }
    }

    /*----- generic getter methods -----*/

    private ArrayList<ArrayList<Integer>> getHexagonIdRings() {
        return hexagonIdRings;
    }

    private ArrayList<ArrayList<Integer>> getIntersectionIdRings() {
        return intersectionIdRings;
    }

    private boolean[][] getHGraph() {
        return hGraph;
    }

    private boolean[][] getIGraph() {
        return iGraph;
    }

    private ArrayList<ArrayList<Integer>> getHexToIntIdMap() {
        return hexToIntIdMap;
    }

    private ArrayList<ArrayList<Integer>> getIntToHexIdMap() {
        return intToHexIdMap;
    }

    private ArrayList<Road> getRoads() {
        return this.roads;
    }

    private ArrayList<Hexagon> getHexagons() {
        return this.hexagons;
    }

    private Robber getRobber() {
        return this.robber;
    }

    private Building[] getBuildings() {
        return this.buildings;
    }

    private ArrayList<Integer> getPortIntersectionLocations() {
        return this.portIntersectionLocations;
    }

    /**
     * @param arr - graph array 2d boolean array
     */
    private void printGraph(boolean arr[][]) {
        StringBuilder str = new StringBuilder();
        str.append("\n\n----------------\n");
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                str.append(i).append("-").append(j).append("=");
                if (arr[i][j]) str.append("t\t");
                else str.append("f\t");
            }
            str.append("\n");
        }
        Log.d("dev", "" + str.toString());
    } // end printGraph

    /**
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Hexagon IDs:\n");
        str.append(listToString(this.hexagonIdRings));
        str.append("Intersection IDs:\n");
        str.append(listToString(this.intersectionIdRings));
        Log.d("dev", "" + str.toString());
        return str.toString();
    } // end toString()

    /**
     * @param list - list to convert
     * @return - String
     */
    private String listToString(ArrayList<ArrayList<Integer>> list) {
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
} // end Class