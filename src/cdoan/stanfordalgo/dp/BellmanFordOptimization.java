package cdoan.stanfordalgo.dp;


import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * This class implements the Bellman Ford's general algorithm.
 * This implementation is optimized for space; It uses O(n) space where n is number of vertices.
 * It also detects and reports the actual Negative-Cost cycle if any.
 * It also computes the actual shortest paths, not just only their distances.
 * It is also optimized to terminate as soon as no progress is made.
 */
public class BellmanFordOptimization {
  
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
  
  public BellmanFordOptimization(Digraph g) {
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

      /* Detect & Report Negative-Cost Cycle */
      HashSet<Integer> visited = new HashSet<>();
      for (Integer v = 0; v < V; v++) {
        if (!visited.contains(v)) {
          HashSet<Integer> visitedInCycle = new HashSet<>();
          LinkedList<Integer> cycle = new LinkedList<>();
          dfsToDetectCycle(v, penultimate, visited, visitedInCycle, cycle);
          if (!cycle.isEmpty()) {
            throw new IllegalArgumentException(String.format("Round: %d; Graph has Negative-Cost Cycle: %s", i, cycle));
          }
        }
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
  
  private void dfsToDetectCycle(Integer v, Integer[] predecessor, 
                                HashSet<Integer> visited, 
                                HashSet<Integer> visitedInCycle, 
                                LinkedList<Integer> cycle) {
    visited.add(v);
    visitedInCycle.add(v);
    cycle.addFirst(v);
    Integer w = predecessor[v];
    if (w == null) {
      cycle.clear();
      return;
    }
    if (visitedInCycle.contains(w)) {
      // Negative_Cost Cycle found      
      cycle.subList(cycle.indexOf(w)+1, cycle.size()).clear(); // In the cycle delete all elements after the w element
      cycle.addFirst(w); // Prepend w to the cycle
      return;
    }
    dfsToDetectCycle(w, predecessor, visited, visitedInCycle, cycle);
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
    /* Digraph of 6 vertices without negative-cost cycle 
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
    */
    /* Digraph with 5 vertices and negative-cost cycle 
    Digraph g = new Digraph(5);
    g.addIncomingEdge(0, 1, 9);
    g.addIncomingEdge(0, 2, 1);
    g.addIncomingEdge(0, 4, 8);
    g.addIncomingEdge(1, 2, 3);
    g.addIncomingEdge(1, 3, 0);
    g.addIncomingEdge(2, 3, -7);
    g.addIncomingEdge(2, 4, 4);
    g.addIncomingEdge(3, 4, 2);
    g.addIncomingEdge(4, 1, 1);
    */
    /* Digraph with 5 vertices and negative-cost cycle 
    Digraph g = new Digraph(5);
    g.addIncomingEdge(0, 1, 2);
    g.addIncomingEdge(1, 2, 3);
    g.addIncomingEdge(1, 3, 1);
    g.addIncomingEdge(1, 4, 0);
    g.addIncomingEdge(2, 3, -7);
    g.addIncomingEdge(3, 4, 3);
    g.addIncomingEdge(4, 0, 1);
    g.addIncomingEdge(4, 2, 2);
    */
    Digraph g = randDigraph();
    System.out.printf("Input graph: %n%s%n", g);
    
    /* Compute Single-Source Shortest Paths */
    BellmanFordOptimization BF = new BellmanFordOptimization(g);
    final int V = g.V();
    int source = new Random().nextInt(V);
    System.out.printf("Source: %d%n", source);
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
   
  
  private static class DirectedEdge {
    private int tail;
    private int head;
    
    private DirectedEdge(int tail, int head) {
      this.tail = tail;
      this.head = head;
    }
    
    public int hashCode() {
      return Objects.hash(tail, head);
    }
    
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj == null || obj.getClass() != DirectedEdge.class) {
        return false;
      }
      DirectedEdge that = (DirectedEdge) obj;
      return tail == that.tail && head == that.head;
    }
  }
  
  private static Digraph randDigraph() {
    Random rd = new Random();
    int minV = 4, maxV = 13;
    int V = minV + rd.nextInt(maxV - minV + 1);
    int COMPLETE = V * (V - 1);
    int minE = (int) (0.2 * COMPLETE), maxE = (int) (0.5 * COMPLETE);
    int E = minE + rd.nextInt(maxE - minE + 1);
    HashSet<DirectedEdge> edges = new HashSet<>(E);
    Digraph g = new Digraph(V);
    int minCost = -3, maxCost = 15, costDiff = 19;
    for (int e = 1; e <= E; e++) {
      int tail, head;
      DirectedEdge edge;
      do {
        tail = rd.nextInt(V);
        head = rd.nextInt(V);
        edge = new DirectedEdge(tail, head);
      } while (tail == head || edges.contains(edge));
      int cost = minCost + rd.nextInt(costDiff);
      g.addIncomingEdge(tail, head, cost);
      edges.add(edge);
    }
    return g;
  }
   
}
