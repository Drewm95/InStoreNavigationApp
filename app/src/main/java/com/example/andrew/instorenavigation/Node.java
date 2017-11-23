package com.example.andrew.instorenavigation;

import java.util.ArrayList;

/**
 * Created by Andrew on 11/9/17.
 * Responsible for creating nodes for the Path class to run it's algorithm
 */

public class Node {

    private ArrayList<Integer> edges;
    private int id;
    //Limit will always be two because we only care to come to the node once, and leave the node once,
    //barring the start node (left once) and the end node (come to once).
    private int limit = 2;

    //This is to check to make sure that a node has reached its limit and therefore has been visited and
    //will not be visited again. Set to true once the condition is met.
    private boolean atCap = false;

    //This is to tell you which node you are coming from and going to, on the users end, we will be showing
    //the going to as the next item for them to get
    private ArrayList<Node> connections;

    //Instantiate the node with the limit (2) and create the edge and connection lists
    Node(int id) {
        //Start node will always be node 0. It's limit must be 1.
        if (id == 0) {
            limit = 1;
        }
        //starting node will need a limit one
        this.id = id;

        edges = new ArrayList<Integer>();
        connections = new ArrayList<Node>();
    }

    //This is going to be used to calculate what the shortest path between two product nodes is
    //will pull the different edges from the database that can connect to the target node from
    //the source node.
    public void addEdge(int edge, Node node) {
        edges.add(edge);
        connections.add(node);
        if (edges.size() == limit) {
            atCap = true;
        }
    }

    //Return the atCap boolean to the algorithm so that it knows whether or not the node can be navigated to.
    //This boolean is not returne to the algorithm in the instance that the user is passing over to get to
    //another node.
    public boolean atCap() {
        return atCap;
    }

    public void setID(int id) {
        this.id = id;
    }

    public ArrayList<Node> getConnections() {
        return connections;
    }

    public int getId() {
        return this.id;
    }
}
