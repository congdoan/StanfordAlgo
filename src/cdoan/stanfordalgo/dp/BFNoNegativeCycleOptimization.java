package cdoan.stanfordalgo.dp;


import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;


/**
 * This class implements the Bellman Ford's basic algorithm. It assumes that the input graph has No negative-cost cycle.
 * This implementation is optimized for space; It uses O(n) space where n is number of vertices.
 * It also computes the actual shortest paths, not just only their distances.
 * It is also optimized to terminate as soon as no progress is made.
 */
public class BFNoNegativeCycleOptimization {
  
  private static class IncomingEdge {
    private int tail;
    private int cost;
    
    private IncomingEdge(int tail, int cost) {
      this.tail = tail;
      this.cost = cost;
    }
    
    /* For debugging/displaying purpose */
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
  
  public BFNoNegativeCycleOptimization(Digraph g) {
    this.g = g;
  }
  
  public int[] shortestPaths(int s, LinkedList<Integer>[] sp) {
    final int V = g.V();
    int[] d = new int[V];                   // d[v] = distance of a shortest path from s to v using at most V-1 edges
    Integer[] penultimate = new Integer[V]; // penultimate[v] = penultimate (second-to-last) vertex on a shortest path from s to v
    
    // Base case (when using 0 edge)
    Arrays.fill(d, Integer.MAX_VALUE);
    d[s] = 0;
    
    
    // Main loop to compute SP distances
    for (int i = 1; i < V; i++) {    // outer loop over the edge budget i
      boolean noChange = true;
      for (int v = 0; v < V; v++) {  // inner loop over all the distination vertices v
        IncomingEdge bestHop = bestIncomingEdge(v, d);
        if (bestHop != null && d[bestHop.tail] + bestHop.cost < d[v]) {
          d[v] = d[bestHop.tail] + bestHop.cost;
          penultimate[v] = bestHop.tail;
          noChange = false;
        }
      }
      if (noChange) {
        break;
      }
    }
    
    // Reconstruct the shortest paths
    for (int v = 0; v < V; v++) {
      sp[v].addFirst(v);
      int i = v;
      while (penultimate[i] != null) {
        sp[v].addFirst(penultimate[i]);
        i = penultimate[i];
      }
    }
    
    return d;
  }
  
  private IncomingEdge bestIncomingEdge(int v, int[] d) {
    IncomingEdge bestHop = null;
    for (IncomingEdge e: g.getIncomingEdges(v)) {
      if (d[e.tail] < Integer.MAX_VALUE) {
        if (bestHop == null || d[e.tail] + e.cost < d[bestHop.tail] + bestHop.cost) {
          bestHop = e;
        }
      }
    }
    return bestHop;
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
    BFNoNegativeCycleOptimization BF = new BFNoNegativeCycleOptimization(g);
    final int V = g.V();
    int source = new Random().nextInt(V);
    LinkedList<Integer>[] shortestPaths = (LinkedList<Integer>[]) (new LinkedList[V]); // to hold the actual shortestPaths
    for (int v = 0; v < V; v++) {
      shortestPaths[v] = new LinkedList<>();
    }
    int[] distances = BF.shortestPaths(source, shortestPaths);
    for (int v = 0; v < V; v++) {
      if (distances[v] != Integer.MAX_VALUE) {
        System.out.printf("SP %d--->%d: %s (distance = %d)%n", source, v, shortestPaths[v], distances[v]);
      } else {
        System.out.printf("SP %d--->%d: No path%n", source, v);
      }
    }
  }
   
}
