package edu.up.cs.androidcatan.catan.gamestate;

// Java program to print DFS traversal from a given given graph

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;

// This class represents a directed graph using adjacency list
// representation
class Graph {
    private static final String TAG = "Graph";
    private int V;   // No. of vertices
    public int count = 0;

    // Array  of lists for Adjacency List Representation
    private LinkedList<Integer> adj[];

    // Constructor
    Graph (int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i) {
            adj[i] = new LinkedList();
        }
    }

    //Function to add an edge into the graph
    void addEdge (int v, int w) {
        adj[v].add(w);  // Add w to v's list.
        adj[w].add(v);
        Log.e(TAG, "addEdge: added edge");
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
//                count++;
                DFSUtil(n, visited);
            } else {
                Log.d(TAG, "DFSUtil: already visited " + v);
            }
        }
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
        Log.e(TAG, "DFS: count=" + count);
        return count - 1;
    }
}