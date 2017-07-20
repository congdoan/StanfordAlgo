package cdoan.stanfordalgo.graph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;


/**
 * Programming assignment 2 of course 2.
 * Implements Dijkstra algorithm to compute shortest paths from a single vertex to all other ones in a weighted graph.
 */
public class DijkstraUndirectedWeightedGraph {
  
  private static class HeadWithCost {
    private int head;
    private int cost;
    
    private HeadWithCost(int edgeHead, int edgeCost) {
      head = edgeHead;
      cost = edgeCost;
    }
  }
  
  private static class DijkstraScore implements Comparable<DijkstraScore> {
    private int unseenVertex; //represents a verex in unconquerred/undiscovered territory/set
    private long score; //represents the min-length from the source vertex s to this unseenVertex
    
    private DijkstraScore(int unprocessedVertex, long minLength) {
      unseenVertex = unprocessedVertex;
      score = minLength;
    }
    
    public int compareTo(DijkstraScore other) {
      return (score < other.score) ? -1 : ((score > other.score) ? 1 : 0);
    }
  }
  
  
  private int V; //number of vertices, which are labeled as integers from 1 to V
  private LinkedList<HeadWithCost>[] adj; //adjacency list of outgoing vertices along with their edge cost
  
  /**
   * Constructor.
   */
  public DijkstraUndirectedWeightedGraph(int numVertices) {
    V = numVertices;
    adj = new LinkedList[V + 1];
    for (int v = 1; v <= V; v++) {
      adj[v] = new LinkedList<HeadWithCost>();
    }
  }
  
  /**
   * Adds a weighted undirected edge.
   */
  public void addEdge(int u, int v, int weight) {
    adj[u].add(new HeadWithCost(v, weight));
    adj[v].add(new HeadWithCost(u, weight));
  }
  
  /**
   * Heap-based implementation of Dijkstra algorithm.
   */
  public long[] dijkstra(int s) {
    /* Initialize */
    long[] d = new long[V + 1];
    Arrays.fill(d, Long.MAX_VALUE); //if no path exists from s to v then d[v] is defined as Long.MAX_VALUE
    d[s] = 0;
    // Mark s as processed
    HashSet<Integer> processed = new HashSet<>(V - 1); //presents conquered zone
    processed.add(s);
    // Add outgoing neighbors of s to heap
    PriorityQueue<DijkstraScore> heap = new PriorityQueue<>();
    HashMap<Integer, DijkstraScore> vertex2HeapScore = new HashMap<>();
    for (HeadWithCost neighbor: adj[s]) {
      DijkstraScore neighborScore = new DijkstraScore(neighbor.head, d[s] + neighbor.cost);
      heap.offer(neighborScore);
      vertex2HeapScore.put(neighbor.head, neighborScore);
    }
    
    /* Main loop */
    for (int i = 1; i < V; i++) {
      // Remove the best vertex from the heap, which represents not-yet-processed set of vertices
      DijkstraScore best = heap.poll();
      // Declare the min-length (shortest-path) distance to this vertex done (i.e. suck it into the processed set)
      int justPick = best.unseenVertex;
      d[justPick] = best.score;
      processed.add(justPick);
      vertex2HeapScore.remove(justPick);
      // Now updates the heap due to just moving best from unconquered territory (denoted by heap) to conquered one
      for (HeadWithCost outwardPair: adj[justPick]) {
        int neighbor = outwardPair.head;
        if (!processed.contains(neighbor)) {
          long newCandidate = d[justPick] + outwardPair.cost;
          DijkstraScore neighborScore = vertex2HeapScore.get(neighbor);
          if (neighborScore != null) {
            if (newCandidate < neighborScore.score) {
              // Replace the existing larger score with the newly smaller one in heap
              heap.remove(neighborScore);
              neighborScore.score = newCandidate;
              heap.offer(neighborScore);
            }
          } else {
            // Add the score to heap the first time
            neighborScore = new DijkstraScore(neighbor, newCandidate);
            heap.offer(neighborScore);
            vertex2HeapScore.put(neighbor, neighborScore);
          }
        }
      }
    }
    
    return d;
  }
  
  /**
   * Unit tests the implementation.
   */
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: java cdoan.stanfordalgo.graph <path_to_graph_data_file>");
      System.exit(1);
    }
    
    LinkedList<String> rows = readRowsFromFile(args[0]);
    final int V = rows.size();
    DijkstraUndirectedWeightedGraph g = new DijkstraUndirectedWeightedGraph(V);
    for (String row: rows) {
      addAdjacencyListToGraph(row, g);
    }
    
    // Call to compute shortest-path distances from source vertex 1 to all other ones
    long[] d = g.dijkstra(1);
    
    if (V == 200) {
      // Report the shortest-path distances to vertices: 7,37,59,82,99,115,133,165,188,197
      System.out.println("Shortest-path distances to vertices 7,37,59,82,99,115,133,165,188,197:");
      int[] vertices = {7,37,59,82,99,115,133,165,188,197};
      for (int v: vertices) {
        System.out.print((d[v] == Long.MAX_VALUE ? 1000000 : d[v]) + ",");
      }
      System.out.println();
    }
  }
  
  /* Utility method to read test data */
  private static LinkedList<String> readRowsFromFile(String filename) throws IOException {
    LinkedList<String> rows = new LinkedList<>();
    Scanner s = new Scanner(new File(filename));
    while (s.hasNextLine()) {
      rows.add(s.nextLine());
    }
    s.close();
    return rows;
  }
  
  /* Utility method to read test data */
  private static void addAdjacencyListToGraph(String row, DijkstraUndirectedWeightedGraph g) {
    Scanner s = new Scanner(row).useDelimiter(",|\\s+");
    int v = s.nextInt();
    while (s.hasNext()) {
      g.addEdge(v, s.nextInt(), s.nextInt());
    }
    s.close();
  }
  
}
