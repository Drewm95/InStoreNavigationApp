package com.example.andrew.instorenavigation;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Created by Andrew on 11/9/17.
 * Will parse JSON information and pass it to the node class so that nodes can be generated to
 * calculate a path.
 */

public class Path {

    private int nodeCount;
    private ArrayList<String[]> nodeEdgeData;

    //The data from the JSON file in the database will return an arraylist of the following format:
    //      {[NaX, NaY, NbX, NbY, L],[NbX, NbY, NcX, NcY, L],...}
    //Which is listing the X and Y of connected nodes and the length between them. The collection
    // contains the nodes needed to travel between a start and end node.

    Path(ArrayList<String[]> nodeEdgeData) {
        this.nodeEdgeData = nodeEdgeData;
    }

    public void run() {

        this.calculatePath();
    }

    //int limit is the degree limitation (in our case, always 2)
    private void calculatePath() {

        //generate a matrix to store the nodes and distance in
        int[][] matrix = new int[this.nodeCount][this.nodeCount];

        int[] parent = new int[this.nodeCount];
        int min;
        int u = 0;
        int v = 0;
        int edgeCount = 0;

        Node[] nodes = new Node[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = new Node();
        }

        //Fill matrix with edge values
        for (int i = 0; i < nodeCount; i++) {
            parent[i] = 0;
            for (int j = i; j < nodeCount; j++) {
                matrix[i][j] = 0;
                //If edge is of 0, forced it to be inifinity (relatively large int)
                if (matrix[i][j] == 0) {
                    matrix[i][j] = 999;
                }
                //if nodes were the same, then there was no edge
                if (i == j) {
                    matrix[i][j] = 999;
                    //inverse relationship held the same edge value (dist between i and j == dist between j and i)
                } else {
                    matrix[j][i] = matrix[i][j];
                }
            }
        }

        //Print out the matrix
        for (int i = 0; i < nodeCount; i++) {
            System.out.println();
            for (int j = 0; j < nodeCount; j++) {
                System.out.printf("%4s", matrix[i][j]);
            }
        }
        System.out.println("\n");

        //Continue loop until path is found
        while (edgeCount - 1 < nodeCount) {
            min = 999;
            for(int i = 0; i < nodeCount; i++) {
                if (!nodes[i].atCap()) {
                    for(int j = i; j < nodeCount; j++) {
                        if (!nodes[j].atCap()) {
                            if (matrix[i][j] < min) {
                                min = matrix[i][j];
                                u = i;
                                v = j;
                            }
                        }
                    }
                }
            }
            int temp1 = u;
            int temp2 = v;

            matrix[u][v] = matrix[v][u] = 999;

            //These while loops are used to find the root of the tree and set it as the parent of the newly connected node
            //this is used to find the root element of each node tree
            while(parent[u] != 0) {
                u = parent[u];
            }
            while(parent[v] != 0) {
                v = parent[v];
            }

            //Check to see that the root of each tree is not the same, if so, connect the two trees with the edge.
            if (v != u) {
                if (!nodes[temp1].atCap()) {
                    nodes[temp1].addEdge(min, nodes[temp2]); nodes[temp2].addEdge(min, nodes[temp1]);
                    edgeCount++;
                    System.out.println("Edge Found: " + temp1 + " <-> " + temp2 + " Weight: " + min);
                }
                parent[v] = u;
            }
        }
    }
}
