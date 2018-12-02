package edu.up.cs.androidcatan.catan.gamestate;

// Java program to print DFS traversal from a given given graph

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;

// This class represents a directed graph using adjacency list
// representation
class Graph implements Runnable{
    private static final String TAG = "Graph";
    private int V;   // No. of vertices
    public int count = 0;
    private ArrayList<Road> pr;

    public void setPr (ArrayList<Road> pr) {
        this.pr = pr;
    }

    public int getMaxRoadLength () {
        return maxRoadLength;
    }

    public void setMaxRoadLength (int maxRoadLength) {
        this.maxRoadLength = maxRoadLength;
    }

    private int maxRoadLength;

    // Array  of lists for Adjacency List Representation
    private LinkedList<Integer> adj[];

    // Constructor
    Graph (int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i) {
            adj[i] = new LinkedList();
        }
        maxRoadLength = 0;
    }

    //Function to add an edge into the graph
    void addEdge (int v, int w) {
        adj[v].add(w);  // Add w to v's list.
        adj[w].add(v);
        Log.e(TAG, "addEdge: added edge: " + v + ", " + w);
    }

    // A function used by DFS
    void DFSUtil (int v, boolean visited[]) {
        // Mark the current node as visited and print it
        visited[v] = true;
        Log.w(TAG, v + " ");

        // Recur for all the vertices adjacent to this vertex
        Iterator<Integer> i = adj[v].listIterator();

        while (i.hasNext()) {
            int n = i.next();
            if (!visited[n]) {
                Log.d(TAG, "DFSUtil: calling dfsutil on " + n);

                DFSUtil(n, visited);
            } else {
                Log.d(TAG, "DFSUtil: already visited " + v);
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

        // Call the recursive helper function to print DFS traversal
        DFSUtil(v, visited);
        Log.e(TAG, "DFS: count= " + count + " real count = " + (count - 1));
        return count - 1;
    }

//    public int getMaxRoadLength(ArrayList<Road> pr) {
//        maxRoadLength = -1;
//
//        for (int i = 0; i < pr.size(); i++) {
//            int l = DFS(pr.get(i).getIntersectionAId());
//            if (l > maxRoadLength) maxRoadLength = l;
//            if (maxRoadLength == pr.size()) {
//                return maxRoadLength;
//            }
//        }
//        for (int i = 0; i < pr.size(); i++) {
//            int l = DFS(pr.get(i).getIntersectionBId());
//            if (l > maxRoadLength) maxRoadLength = l;
//            if (maxRoadLength == pr.size()) {
//                return maxRoadLength;
//            }
//        }
//        Log.e(TAG, "getMaxRoadLength returning " + maxRoadLength);
//        return maxRoadLength;
//    }

    @Override
    public void run () {

        if (pr.size() < 5) return;

        for (int i = 0; i < pr.size(); i++) {
            int l = DFS(pr.get(i).getIntersectionAId());
            if (l > maxRoadLength) maxRoadLength = l;
            Log.d(TAG, "run: looping");
            if (maxRoadLength == pr.size()) {
                Log.e(TAG, "run: Breaking because max size is equal to amount of roads.");
                break;
            }
        }
        for (int i = 0; i < pr.size(); i++) {
            int l = DFS(pr.get(i).getIntersectionBId());
            Log.d(TAG, "run: looping 2");
            if (l > maxRoadLength) maxRoadLength = l;
            if (maxRoadLength == pr.size()) {
                Log.e(TAG, "run: Breaking because max size is equal to amount of roads.");
                break;
            }
        }
    }
}