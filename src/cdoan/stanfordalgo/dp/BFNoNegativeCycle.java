package cdoan.stanfordalgo.dp;


import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;


/**
 * This class implements the Bellman Ford's basic algorithm. It assumes that the input graph has No negative-cost cycle.
 * This implementation is Not optimized for space; It uses O(n^2) space where n is number of vertices.
 */
public class BFNoNegativeCycle {
  
  private static class IncomingEdge {
    private int tail;
    private int cost;
    
    private IncomingEdge(int tail, int cost) {
      this.tail = tail;
      this.cost = cost;
    }
    
    public String toString() {
      return String.format("(%d,%d)", tail, cost);
    }
  }
      
  private static class Digraph {
    private int V;
    private int E;
    private List<IncomingEdge>[] adj; // adjacency list of incoming edges
    
    public Digraph(int V) {
      this.V = V;
      adj = (List<IncomingEdge>[]) new List[V];
      for (int tail = 0; tail < V; tail++) {
        adj[tail] = new LinkedList<>();
      }
      E = 0;
    }
    
    public int V() {
      return V;
    }
    
    public void addIncomingEdge(int tail, int head, int cost) {
      adj[head].add(new IncomingEdge(tail, cost));
      E++;
    }
    
    public List<IncomingEdge> getIncomingEdges(int v) {
      return adj[v];
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("V: ").append(V).append("\n");
      sb.append("E: ").append(E).append("\n");
      for (int v = 0; v < V; v++) {
        sb.append(v).append("'s incoming (neighbor, associated cost) list: ").append(adj[v]).append("\n");
      }
      return sb.toString();
    }
  }


  private Digraph g;
  
  public BFNoNegativeCycle(Digraph g) {
    this.g = g;
  }
  
  public int[] shortestPaths(int s) {
    final int V = g.V();
    int[][] d = new int[V][V]; // d[i][v] = distance of a shortest path from s to v using at most i edges
    
    // Base case (when using 0 edge)
    Arrays.fill(d[0], Integer.MAX_VALUE);
    d[0][s] = 0;
    
    
    // Main loop to compute SP distances
    for (int i = 1; i < V; i++) {    // outer loop over the edge budget i
      for (int v = 0; v < V; v++) {  // inner loop over all the distination vertices v
        int bestOverIncomingEdges = bestOverIncomingEdges(v, d[i-1]);
        d[i][v] = Math.min(d[i-1][v], bestOverIncomingEdges);
      }
    }
    
    return d[V-1];
  }
  
  private int bestOverIncomingEdges(int v, int[] prevRoundDistance) {
    int best = Integer.MAX_VALUE;
    for (IncomingEdge e: g.getIncomingEdges(v)) {
      int w = e.tail;
      int wv = e.cost;
      if (prevRoundDistance[w] < Integer.MAX_VALUE) {
        best = Math.min(best, prevRoundDistance[w] + wv);
      }
    }
    return best;
  }
  
  
  public static void main(String[] args) {
    /* Prepare input graph */
    Digraph g = new Digraph(6);
    g.addIncomingEdge(0, 1, 1);
    g.addIncomingEdge(0, 4, 6);
    g.addIncomingEdge(1, 3, -7);
    g.addIncomingEdge(1, 4, 2);
    g.addIncomingEdge(2, 0, 2);
    g.addIncomingEdge(2, 1, 8);
    g.addIncomingEdge(3, 2, 6);
    g.addIncomingEdge(4, 2, -1);
    g.addIncomingEdge(4, 3, -8);
    g.addIncomingEdge(4, 5, -5);
    g.addIncomingEdge(5, 0, 7);
    g.addIncomingEdge(5, 1, 4);
    g.addIncomingEdge(5, 3, -4);
    System.out.printf("Input graph: %n%s%n", g);
    
    /* Compute Single-Source Shortest Paths */
    BFNoNegativeCycle BF = new BFNoNegativeCycle(g);
    final int V = g.V();
    int source = new Random().nextInt(V);
    int[] distances = BF.shortestPaths(source);
    for (int i = 0; i < V; i++) {
      System.out.printf("Distance of a SP %d--->%d: %s%n", source, i, (distances[i] != Integer.MAX_VALUE ? distances[i] : "+Infinite"));
    }
  }
   
}
