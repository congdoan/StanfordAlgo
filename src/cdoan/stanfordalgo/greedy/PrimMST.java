package cdoan.stanfordalgo.greedy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;


/**
 * Question 3 of programming assignment 1 of course 3.
 * 
 * Implement the Prim's Minimum Spanning Tree algorithm using a Edge-based Min Heap.
 */
public class PrimMST {
  
  private static class Edge implements Comparable<Edge> {
    private int endpoint1;
    private int endpoint2;
    private int cost;
    
    private Edge(int u, int v, int weight) {
      endpoint1 = u;
      endpoint2 = v;
      cost = weight;
    }
    
    public int compareTo(Edge e) {
      return cost - e.cost;
    }
  }
  
  
  /**
   * V: number of vertices; assumming numbered 1 through V 
   * E: array of edges
   * V2EIdx: mapping from vertex to indices of its incident edges in array E
   */
  public static long minSpanningCost(int V, Edge[] E, Map<Integer, List<Integer>> V2EIdx) {
    long sum = 0; // to hold the sum cost of the resulting Minimum Spanning Tree
    
    /* Initialize */
    int s = 1; // arbitrary start vertex
    HashSet<Integer> processedV = new HashSet<>(V); // hold those vertices processed
    PriorityQueue<Edge> crossEdgesMinPQ = new PriorityQueue<>(); // Min heap to hold those edges crossing processed and unprocessed vertices
    processedV.add(s); // Mark s as processed
    // Add incident edges of s to the min heap
    for (int idx: V2EIdx.get(s)) {
      crossEdgesMinPQ.offer(E[idx]);
    }
    
    /* Main loop */
    while (processedV.size() < V) {
      Edge e = crossEdgesMinPQ.poll(); // Extract min-cost edge
      
      // Detect either the newly discovered vertex called v or both endpoints of this edge already processed
      int v = e.endpoint1;
      if (processedV.contains(v)) {
        v = e.endpoint2;
      }      
      if (processedV.contains(v)) {
        // Skip this edge as there is already a path between its 2 endpoints
        continue;
      }
      
      processedV.add(v); // Mark v as processed
      // Add incident crossing edges of v to the min heap
      for (int idx: V2EIdx.get(v)) {
        int w = v != E[idx].endpoint1 ? E[idx].endpoint1 : E[idx].endpoint2;
        if (!processedV.contains(w)) {
          crossEdgesMinPQ.offer(E[idx]);
        }
      }
      
      sum += e.cost;
    }
    
    return sum;
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: java cdoan.stanfordalgo.greedy.PrimMST <input-edges-file>");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    int V = s.nextInt();
    int numEdges = s.nextInt();
    Edge[] E = new Edge[numEdges];
    Map<Integer, List<Integer>> V2EIdx = new HashMap<>(V);
    for (int i = 1; i <= V; i++) {
      V2EIdx.put(i, new LinkedList<Integer>());
    }
    for (int i = 0; i < numEdges; i++) {
      int u = s.nextInt();
      int v = s.nextInt();
      E[i] = new Edge(u, v, s.nextInt());
      V2EIdx.get(u).add(i);
      V2EIdx.get(v).add(i);
    }
    s.close();
    
    System.out.println("MST's overall cost: " + minSpanningCost(V, E, V2EIdx));
  }
  
}
