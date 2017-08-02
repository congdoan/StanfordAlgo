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
 * Implement the Prim's Minimum Spanning Tree algorithm using a Vertex-based Min Heap.
 */
public class PrimMSTVertexHeap {
  
  private static class NeighborCost implements Comparable<NeighborCost> {
    private int vertex;
    private int cost;
    
    private NeighborCost(int v, int weight) {
      vertex = v;
      cost = weight;
    }
    
    public int compareTo(NeighborCost nc) {
      return cost - nc.cost;
    }
  }
  
  
  /**
   * V: number of vertices; assumming numbered 1 through V 
   * adj: mapping from a vertex to its adjacent vertices
   */
  public static long minSpanningCost(int V, Map<Integer, List<NeighborCost>> adj) {
    long sum = 0; // to hold the sum cost of the resulting Minimum Spanning Tree
    
    /* Initialize */
    int s = 1; // arbitrary start vertex
    HashSet<Integer> processedV = new HashSet<>(V); // hold those vertices processed
    // Min heap to hold unprocessed vertices (adjacent to processed ones) 
    // whose key is defined by a min-cost edge between it and a processed vertex
    PriorityQueue<NeighborCost> unprocessedNeighbors = new PriorityQueue<>();
    HashMap<Integer, NeighborCost> neighborsInPQ = new HashMap<>(V); // Hold those vertices added to the min heap
    processedV.add(s); // mark s as processed    
    for (NeighborCost sNeighbor: adj.get(s)) {
      unprocessedNeighbors.offer(sNeighbor);     // add neighbors of s to the min heap
      neighborsInPQ.put(sNeighbor.vertex, sNeighbor); // mark that this neighbor added to the heap
    }
    
    /* Main loop */
    while (processedV.size() < V) {
      NeighborCost minNeighbor = unprocessedNeighbors.poll();
      int v = minNeighbor.vertex;
      int cost = minNeighbor.cost;
      sum += cost;
      processedV.add(v);
      //neighborsInPQ.remove(v);
      for (NeighborCost vNeighbor: adj.get(v)) {
        if (processedV.contains(vNeighbor.vertex)) {
          continue;
        }
        NeighborCost vNeighborInPQ = neighborsInPQ.get(vNeighbor.vertex);
        if (vNeighborInPQ != null) {
          // This v's neighbor already in heap
          if (vNeighbor.cost < vNeighborInPQ.cost) {
            // Update unprocessedNeighbors replacing vNeighborInPQ with vNeighbor 
            unprocessedNeighbors.remove(vNeighborInPQ);
            unprocessedNeighbors.offer(vNeighbor);
            // Update neighborsInPQ
            neighborsInPQ.put(vNeighbor.vertex, vNeighbor);
          }
        } else {
          // Add vNeighbor to unprocessedNeighbors & neighborsInPQ
          unprocessedNeighbors.offer(vNeighbor);
          neighborsInPQ.put(vNeighbor.vertex, vNeighbor);
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
