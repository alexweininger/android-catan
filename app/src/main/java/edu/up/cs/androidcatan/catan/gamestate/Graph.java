package edu.up.cs.androidcatan.catan.gamestate;

// Java program to print DFS traversal from a given given graph

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/
// This class represents a directed graph using adjacency list
// representation
public class Graph implements Runnable, Serializable {
    private static final String TAG = "Graph";
    private static final long serialVersionUID = -8879389794133273667L;
    private int V;   // No. of vertices
    public int count = 0;
    private ArrayList<Road> pr;
    private ArrayList<Road> allRoads;
    private int playerIdWithLongestRoad;
    private boolean hasCycle;

    public void setPlayerIdWithLongestRoad (int playerWithLongestRoad) {
        this.playerIdWithLongestRoad = playerWithLongestRoad;
    }

    public int getPlayerIdWithLongestRoad () {
        return playerIdWithLongestRoad;
    }

    public void setPr (ArrayList<Road> pr) {
        this.pr = pr;
    }

    public int getMaxRoadLength () {
        return maxRoadLength;
    }

    public ArrayList<Road> getAllRoads () {
        return allRoads;
    }

    public void setAllRoads (ArrayList<Road> allRoads) {
        this.allRoads = allRoads;
    }

    public void setMaxRoadLength (int maxRoadLength) {
        this.maxRoadLength = maxRoadLength;
    }

    private int maxRoadLength;

    // Array  of lists for Adjacency List Representation
    private LinkedList<Integer> adj[];

    // Constructor
    public Graph (int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i) {
            adj[i] = new LinkedList();
        }
        maxRoadLength = 0;
    }

    //Function to add an edge into the graph
    public void addEdge (int v, int w) {
        adj[v].add(w);  // Add w to v's list.
        adj[w].add(v);
        Log.e(TAG, "addEdge: added edge: " + v + ", " + w);
    }

    // A function used by DFS
    public void DFSUtil (int v, boolean visited[], int parent) {
        // Mark the current node as visited and print it
        visited[v] = true;

        // Recur for all the vertices adjacent to this vertex
        Iterator<Integer> i = adj[v].listIterator();

        while (i.hasNext()) {
            int n = i.next();
            if (!visited[n]) {
                Log.d(TAG, "DFSUtil: calling dfsutil on " + n);

                DFSUtil(n, visited, v);
            } else {
                if (n != v && n != parent) {
                    Log.d(TAG, "DFSUtil: already visited " + v);
                    Log.e(TAG, "DFSUtil: FOUND CYCLE");
                    this.hasCycle = true;

                }
            }
        }
        Log.w(TAG, "DFSUtil: count++");
        count++;
    }

    // The function to do DFS traversal. It uses recursive DFSUtil()
    int DFS (int v) {

        count = 0;
        // Mark all the vertices as not visited(set as
        // false by default in java)
        boolean visited[] = new boolean[V];
        for (int i = 0; i < V; i++) {
            visited[i] = false;
        }
        boolean isCycle = false;
        // Call the recursive helper function to print DFS traversal
        DFSUtil(v, visited, -1);
        Log.e(TAG, "DFS: count= " + count + " real count = " + (count - 1));
        return count - 1;
    }

    public int getMaxRoadLength (ArrayList<Road> pr) {
        maxRoadLength = -1;

        for (int i = 0; i < pr.size(); i++) {
            int l = DFS(pr.get(i).getIntersectionAId());
            if (l == maxRoadLength && maxRoadLength > 4) {
                Log.e(TAG, "getMaxRoadLength returning " + maxRoadLength);
                return maxRoadLength;
            }
            if (l > maxRoadLength) maxRoadLength = l;
            if (maxRoadLength == pr.size()) {
                Log.e(TAG, "getMaxRoadLength returning " + maxRoadLength);
                return maxRoadLength;
            }
        }
        //        for (int i = 0; i < pr.size(); i++) {
        //            int l = DFS(pr.get(i).getIntersectionBId());
        //            if (l > maxRoadLength) maxRoadLength = l;
        //            if (maxRoadLength == pr.size()) {
        //                return maxRoadLength;
        //            }
        //        }
        Log.e(TAG, "getMaxRoadLength returning " + maxRoadLength);
        return maxRoadLength;
    }

    @Override
    public void run () {
        updatePlayerWithLongestRoad();
    }

    /**
     * @param ownerId owner id
     * @return dfs
     */
    public int dfs (int ownerId) {
        ArrayList<Road> pr = new ArrayList<>();

        for (Road road : this.allRoads) {
            if (road.getOwnerId() == ownerId) {
                this.addEdge(road.getIntersectionAId(), road.getIntersectionBId());
                pr.add(road);
            }
        }

        if (pr.size() < 5) {
            return -1;
        }
        this.setPr(pr);
        int m = this.getMaxRoadLength(pr);
        Log.d(TAG, "dfs() returned: " + ownerId);
        return m;
    }

    /**
     * Main method to calculate the longest road trophy holder. - AL
     *
     * @return returns the playerid with the longest road for now (may need to change so that it returns the value instead)
     */
    public synchronized int updatePlayerWithLongestRoad () {
        Log.d(TAG, "updatePlayerWithLongestRoad() called");
        ArrayList<Integer> longestRoadPerPlayer = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            //for each player there is an adjacency map as well as a list
            ArrayList<Road> playerRoads = new ArrayList<>();
            ArrayList<Integer> currentPlayerRoadLength = new ArrayList<>();
            for (Road road : this.allRoads) {
                if (road.getOwnerId() == i) {
                    playerRoads.add(road);
                }
            }

            if (playerRoads.size() < 5) {
                longestRoadPerPlayer.add(i, 0);
                break;
            } else {
                Log.w(TAG, "updatePlayerWithLongestRoad: Started dfs on " + i);
                int l = dfs(i);
//                if (hasCycle) l++;
                currentPlayerRoadLength.add(l);
                int max = 0;
                for (int n = 0; n < currentPlayerRoadLength.size(); n++) {
                    max = currentPlayerRoadLength.get(0);
                    if (currentPlayerRoadLength.get(n) >= max) {
                        max = currentPlayerRoadLength.get(n);
                    }
                }
                longestRoadPerPlayer.add(i, max);
            }
        }
        int playerIdLongestRoad = -1;
        int currLongestRoad = 0;
        //currently gives the longest road trophy to the most recent player checked within the array if
        //it shares the longest road with a prior player
        for (int n = 0; n < longestRoadPerPlayer.size(); n++) {
            if (longestRoadPerPlayer.get(n) > 4) {
                if (longestRoadPerPlayer.get(n) > currLongestRoad) {
                    currLongestRoad = longestRoadPerPlayer.get(n);
                    playerIdLongestRoad = n;
                }
            }
        }

        Log.d(TAG, "updatePlayerWithLongestRoad: currentLongestRoad=" + currLongestRoad);
        Log.d(TAG, "updatePlayerWithLongestRoad() returned: " + playerIdLongestRoad);
        this.setPlayerIdWithLongestRoad(playerIdLongestRoad);
        return playerIdLongestRoad;
    }
}