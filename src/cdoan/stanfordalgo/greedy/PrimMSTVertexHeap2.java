package cdoan.stanfordalgo.greedy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
 * Implement the Prim's Minimum Spanning Tree algorithm using a Vertex-based Min Heap.
 */
public class PrimMSTVertexHeap2 {
  
  private static class NeighborCost implements Comparable<NeighborCost> {
    private int vertex;
    private int cost;
    
    private NeighborCost(int v, int weight) {
      vertex = v;
      cost = weight;
    }
    
    public int compareTo(NeighborCost nc) {
      return new Integer(cost).compareTo(new Integer(nc.cost));
    }
  }
  
  
  /**
   * V: number of vertices; assumming numbered 1 through V 
   * adj: mapping from a vertex to its adjacent vertices
   */
  public static long minSpanningCost(int V, Map<Integer, List<NeighborCost>> adj) {
    long sum = 0; // to hold the sum cost of the resulting Minimum Spanning Tree
    
    /* Initialize */
    int s = 1;
    HashSet<Integer> processedV = new HashSet<>(V);
    ArrayList<NeighborCost> unprocessedV = new ArrayList<>(V - 1);
    for (int v = 2; v <= V; v++) {
      unprocessedV.add(new NeighborCost(v, Integer.MAX_VALUE));
    }
    for (NeighborCost sNeighbor: adj.get(s)) {
      unprocessedV.set(sNeighbor.vertex - 2, sNeighbor);
    }
    PriorityQueue<NeighborCost> unprocessedVMinPQ = new PriorityQueue<>(unprocessedV);
    processedV.add(s);
    
    /* Main loop */
    while (processedV.size() < V) {
      NeighborCost minCostNeighbor = unprocessedVMinPQ.poll();
      int u = minCostNeighbor.vertex;
      processedV.add(u);
      int cost = minCostNeighbor.cost;
      sum += cost;
      for (NeighborCost neighbor: adj.get(u)) {
        int v = neighbor.vertex;
        if (!processedV.contains(v) && neighbor.cost < unprocessedV.get(v - 2).cost) {
          unprocessedVMinPQ.remove(unprocessedV.get(v - 2));
          unprocessedVMinPQ.offer(neighbor);
          unprocessedV.set(v - 2, neighbor);
        }
      }
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
    Map<Integer, List<NeighborCost>> adj = new HashMap<>(V);
    for (int i = 1; i <= V; i++) {
      adj.put(i, new LinkedList<>());
    }
    for (int i = 0; i < numEdges; i++) {
      int u = s.nextInt();
      int v = s.nextInt();
      int cost = s.nextInt();
      adj.get(u).add(new NeighborCost(v, cost));
      adj.get(v).add(new NeighborCost(u, cost));
    }
    s.close();
    
    System.out.println("MST's overall cost: " + minSpanningCost(V, adj));
  }
  
}
