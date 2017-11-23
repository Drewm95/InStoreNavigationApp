/**********************************************************************************************
 THE PATH CLASS WILL BE AN INTERMEDIATE STEP BETWEEN STORE VIEW AND NAVIGATION VIEW THAT WILL
 CALCULATE THE SHORTEST PATH FOR THE LIST USING THE STORE NAVIGATED.
 *********************************************************************************************/

package com.example.andrew.instorenavigation;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Path extends Activity{
    //Objects for mapping of edges and nodes
    private int nodeCount;
    private int[][] nodeEdgeData;
    private ArrayList<Integer> nodesVisited;
    private Node[] nodes;

    //Key Values to be passed to queryNodes.
    private String start;
    private ArrayList<String> products;
    private String Store_ID;
    final private Context context;

    //Every even number will be a diatance, and every odd number will be a direction associated with
        //the distance before hand.
    private ArrayList<Integer> route;

    //Path constructor taking a store id, a start node, and products.
    Path(String StoreID, ArrayList<String> products, String start, Context context) {
        this.Store_ID = StoreID;
        this.products = products;
        this.start = start;
        this.context = context;
        this.nodesVisited = new ArrayList<>();

        //Add start node.
        nodesVisited.add(Integer.parseInt(start));
        nodeCount++;

        //Put product names into a string to query for nodes containing products.
        String productNames= "";
        for (int i = 0; i < products.size(); i++) {
            if (i != products.size()-1) {
                productNames += products.get(i) + ",";
            } else {
                productNames += products.get(i);
            }
        }
        queryNodes(productNames, StoreID, context);
    }

    //Find nodes that need to be traveled to.
    private void queryNodes(final String Product_Names, final String storeId, Context context) {

        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/getNode.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            parseNodes(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();
                params.put("Product_Names", Product_Names);
                params.put("Store_SID", Store_ID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //Find all edges that connect the nodes.
    private void queryEdges(final String NodeIDs, final String storeId, Context context) {
        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/getEdge.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            parseEdges(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                String Store_SID = storeId;

                Map<String, String> params = new HashMap<String, String>();
                params.put("Node_NIDs", NodeIDs);
                params.put("Store_SID", Store_SID);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //After finding the shortest path, find the collection of real edges that must be navigated.
    private void queryPath(final String Node_IDs, final String storeId, final Context context) {

        //Connect to the database and authenticate
        RequestQueue queue = Volley.newRequestQueue(context);
        String responseValue = null;


        String url = "http://34.238.160.248/getPath.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        if(response.length() >= 1){
                            if(context instanceof StoreView){
                                ((StoreView)context).goToNavView(response);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Node_NIDs", Node_IDs);
                params.put("Store_SID", storeId);

                return params;
            }
        };
        queue.add(postRequest);
    }

    //Calculate shortest path using the edge lengths given by query Edges.
    private void calculatePath() {

        int[] parent = new int[this.nodeCount];
        int min;
        int u = 0;
        int v = 0;
        int edgeCount = 0;

        nodes = new Node[nodeCount];

        //All other nodes are not connected, parent of -1
        for (int i = 0; i < nodeCount; i++) {
            parent[i] = 0;
        }

        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = new Node(i);
            nodes[i].setID(nodeEdgeData[i][i]);
        }

        //Print out the matrix
        for (int i = 0; i < nodeCount; i++) {
            System.out.println();
            for (int j = 0; j < nodeCount; j++) {
                System.out.printf("%4s", nodeEdgeData[i][j]);
                Log.d("Path", "" + nodeEdgeData[i][j]);
            }
        }
        System.out.println("\n");

        //Continue loop until path is found
        while (edgeCount < nodeCount-1) {
            min = Integer.MAX_VALUE;
            for(int i = 0; i < nodeCount; i++) {
                if (!nodes[i].atCap()) {
                    for(int j = i+1; j < nodeCount; j++) {
                        if (!nodes[j].atCap()) {
                            if (nodeEdgeData[i][j] < min) {
                                min = nodeEdgeData[i][j];
                                u = i;
                                v = j;
                            }
                        }
                    }
                }
            }

            int temp1 = u;
            int temp2 = v;

            nodeEdgeData[u][v] = nodeEdgeData[v][u] = Integer.MAX_VALUE;

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

        //Format path to pass to queryNodes.
        String nodesFormatted = "";

        Node previousNode = nodes[0];
        Node currentNode = previousNode.getConnections().get(0);

        nodesFormatted += start + "`";
        for (int i = 1; i < nodeCount; i++) {
            if (currentNode.getConnections().size() == 1) {
                if (currentNode.getConnections().get(0) == previousNode) {
                    nodesFormatted += currentNode.getId() + "`";
                } else {
                    nodesFormatted += currentNode.getConnections().get(0).getId() + "`";
                }
            } else {
                if (currentNode.getConnections().get(0) == previousNode) {
                    nodesFormatted += currentNode.getConnections().get(1).getId() + "`";
                    previousNode = currentNode;
                    currentNode = currentNode.getConnections().get(1);
                } else {
                    nodesFormatted += currentNode.getConnections().get(0).getId() + "`";
                    previousNode = currentNode;
                    currentNode = currentNode.getConnections().get(0);
                }
            }
        }
        queryPath(nodesFormatted, Store_ID, context);
    }

    //Parse node and store them into the nodeEdgeData
    private void parseNodes(String nodeString){
        int i = 0;

        for(int j = 0; j < nodeString.length(); j++) {
            if (nodeString.substring(j,j+1).equals("`")) {
                Integer temp = Integer.parseInt(nodeString.substring(i,j));
                if (!nodesVisited.contains(temp)) {
                    nodesVisited.add(temp);
                    //Store each node ID at it's location in the tables
                    nodeCount++;
                }
                i = j + 1;
            }
        }
        nodeEdgeData = new int[nodeCount][nodeCount];

        for (int j = 0; j < nodesVisited.size(); j++) {
            nodeEdgeData[j][j] = nodesVisited.get(j);
        }

        queryEdges(nodeString, Store_ID, context);
    }

    //Parse edges and store them into the nodeEdgeData
    private void parseEdges (String edges){
        int [] nodeIDs = new int[nodeCount];
        ArrayList<Integer> edgeCollection = new ArrayList<>();

        int i = 0;
        int commaCount = 0;

        for(int j = 0; j < edges.length(); j++) {
            if (edges.substring(j,j+1).equals("`")) {
                commaCount++;
                if (commaCount == 3) {
                    commaCount = 0;
                    Integer temp = Integer.parseInt(edges.substring(i,j));
                    edgeCollection.add(temp);
                }
                i = j + 1;
            }
        }
        this.calculatePath();
    }
}