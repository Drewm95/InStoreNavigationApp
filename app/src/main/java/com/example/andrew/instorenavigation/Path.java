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

/**
 * Created by Andrew on 11/9/17.
 * Will parse JSON information and pass it to the node class so that nodes can be generated to
 * calculate a path.
 */

public class Path extends Activity implements Interactor{

    private int nodeCount;
    private int[][] nodeEdgeData;
    private int start;

    private ArrayList<Integer> nodesVisited;
    private ArrayList<Integer> products;
    private int store;

    //Every even number will be a diatance, and every odd number will be a direction associated with
        //the distance before hand.
    private ArrayList<Integer> route;

    //The data from the JSON file in the database will return an arraylist of the following format:
    //      {[NaX, NaY, NbX, NbY, L],[NbX, NbY, NcX, NcY, L],...}
    //Which is listing the X and Y of connected nodes and the length between them. The collection
    // contains the nodes needed to travel between a start and end node.

    Path(int store, ArrayList<Integer> products, int start, Context context) {
        this.store = store;
        this.products = products;
        this.start = start;

        for (int i = 0; i < products.size(); i++) {
            this.query("" + products.get(i), "" + store, context);
        }
    }

    public void query(final String productId, final String storeId, Context context) {


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

                        if(response.length() > 1){
                            Integer temp = Integer.parseInt(response);
                            if (!nodesVisited.contains(temp)) {
                                nodesVisited.add(temp);
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
                String Product_PID = productId;
                String Store_SID = storeId;

                Map<String, String> params = new HashMap<String, String>();
                params.put("Product_PID", Product_PID);
                params.put("Store_SID", Store_SID);

                return params;
            }
        };
        queue.add(postRequest);

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
            nodes[i] = new Node(i);
        }

        //Fill matrix with edge values
        for (int i = 0; i < nodeCount; i++) {
            parent[i] = 0;
            for (int j = i; j < nodeCount; j++) {
                matrix[i][j] = 0;
                if (matrix[i][j] == 0) {
                    matrix[i][j] = -1;
                }
                //if nodes were the same, then there was no edge
                if (i == j) {
                    matrix[i][j] = -1;
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
                Log.d("Path", "" + matrix[i][j]);
            }
        }
        System.out.println("\n");

        //Continue loop until path is found
        while (edgeCount - 1 < nodeCount) {
            min = -1;
            for(int i = 0; i < nodeCount; i++) {
                if (!nodes[i].atCap()) {
                    for(int j = i; j < nodeCount; j++) {
                        if (!nodes[j].atCap()) {
                            if (matrix[i][j] < min || min == -1) {
                                min = matrix[i][j];
                                //u = i;
                                //v = j;
                            }
                        }
                    }
                }
            }
            /*
            int temp1 = u;
            int temp2 = v;

            matrix[u][v] = matrix[v][u] = -1;

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
            */
        }
    }

    private void parseData(){
        //TODO Feed data and switch to NavigationView and

    }
}
