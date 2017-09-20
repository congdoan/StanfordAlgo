package cdoan.stanfordalgo.dp;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.Random;


/**
 * This class implements the Bellman Ford's general algorithm.
 * This implementation is optimized for space; It uses O(n) space where n is number of vertices.
 * It also detects Negative-Cost cycle.
 */
public class BellmanFord {
  
  public static class Digraph {
    private static class OutgoingEdge {
      private int head;
      private int cost;
      
      private OutgoingEdge(int head, int cost) {
        this.head = head;
        this.cost = cost;
      }
      
      public String toString() {
        return String.format("(%d,%d)", head, cost);
      }
    }
    
    private int V;
    private int E;
    private List<OutgoingEdge>[] adj;
    
    public Digraph(int V) {
      this.V = V;
      adj = (List<OutgoingEdge>[]) new List[V];
      for (int tail = 0; tail < V; tail++) {
        adj[tail] = new LinkedList<>();
      }
      E = 0;
    }
    
    public int V() {
      return V;
    }
    
    public int E() {
      return V;
    }
    
    public void addEdge(int tail, int head, int cost) {
      adj[tail].add(new OutgoingEdge(head, cost));
      E++;
    }
    
    public List<IncomingEdge>[] getIncomingAdj() {
      List<IncomingEdge>[] incomingAdj = (List<IncomingEdge>[]) new List[V];
      for (int head = 0; head < V; head++) {
        incomingAdj[head] = new LinkedList<>();
      }
      for (int tail = 0; tail < V; tail++) {
        for (OutgoingEdge e: adj[tail]) {
          int head = e.head;
          int cost = e.cost;
          incomingAdj[head].add(new IncomingEdge(tail, cost));
        }
      }
      return incomingAdj;
    }
    
    public Digraph getCopy() {
      Digraph copy = new Digraph(V, E, adj);
      return copy;
    }
    
    private Digraph(int V, int E, List<OutgoingEdge>[] adj) {
      this.V = V;
      this.E = E;
      this.adj = (List<OutgoingEdge>[]) new List[V];
      System.arraycopy(adj, 0, this.adj, 0, V);
    }
    
    public void incrementVertex() {
      List<OutgoingEdge>[] newAdj = (List<OutgoingEdge>[]) new List[V + 1];
      System.arraycopy(adj, 0, newAdj, 0, V);
      newAdj[V] = new LinkedList<>();
      V++;
      adj = newAdj;
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("V: ").append(V).append("\n");
      sb.append("E: ").append(E).append("\n");
      for (int v = 0; v < V; v++) {
        sb.append(v).append(": ").append(adj[v]).append("\n");
      }
      return sb.toString();
    }
  }


  private static class IncomingEdge {
    private int tail;
    private int cost;
    
    private IncomingEdge(int tail, int cost) {
      this.tail = tail;
      this.cost = cost;
    }
  }
    

  private Digraph g;
  
  public BellmanFord(Digraph g) {
    this.g = g;
  }
  
  public boolean shortestPaths(int s, int[] d) {
    final int V = d.length;
    
    // Base case (when edge budget i = 0)
    Arrays.fill(d, Integer.MAX_VALUE);
    d[s] = 0;
    
    
    // Main loop to compute SP distances
    List<IncomingEdge>[] incomingAdj = g.getIncomingAdj();
    for (int i = 1; i < V; i++) {    // outer loop over the edge budget i
      for (int v = 0; v < V; v++) {  // inner loop over all the distination vertices v
        int bestOverIncomingEdges = bestOverIncomingEdges(d, incomingAdj[v]);
        d[v] = Math.min(d[v], bestOverIncomingEdges);
      }
    }
    
    // Extra iteration to check if the graph has a Negative cycle reachable from s
    for (int v = 0; v < V; v++) {  // loop over all the distination vertices v
      int bestOverIncomingEdges = bestOverIncomingEdges(d, incomingAdj[v]);
      if (bestOverIncomingEdges < d[v]) {
        // The graph has a negative cycle reachable from the source vertex s
        return false;
      }
    }
    
    return true;
  }
  
  private int bestOverIncomingEdges(int[] d, List<IncomingEdge> incomingNeighbors) {
    int best = Integer.MAX_VALUE;
    for (IncomingEdge e: incomingNeighbors) {
      int w = e.tail;
      int wv = e.cost;
      if (d[w] < Integer.MAX_VALUE) {
        best = Math.min(best, d[w] + wv);
      }
    }
    return best;
  }
  
  public boolean hasNegativeCycle() {
    Digraph g2 = g.getCopy();
    /* Add a dummy vertex, and an edge from it to each other vertex with cost of 0, then run Bellman-Ford from it as source vertex */
    final int V = g.V();
    g.incrementVertex();
    int dummy = V;
    for (int v = 0; v < V; v++) {
      g.addEdge(dummy, v, 0);
    }
    int[] distances = new int[V + 1];
    boolean hasCycle = !shortestPaths(dummy, distances);
    g = g2; // restore the graph
    return hasCycle;
  }
  
  
  public static void main(String[] args) {
    Digraph g = randDigraph();
    System.out.println(g); // DEBUG
    
    BellmanFord BF = new BellmanFord(g);
    
    /* Compute Single-Source Shortest Paths */
    final int V = g.V();
    int source = new Random().nextInt(V);
    int[] distances = new int[V];
    if (!BF.shortestPaths(source, distances)) {
      System.out.println("The graph has negative cycle reachable from the source vertex " + source);
    }
    for (int i = 0; i < V; i++) {
      System.out.printf("Distance of a SP %d--->%d: %s%n", source, i, (distances[i] != Integer.MAX_VALUE ? distances[i] : "+Infinite"));
    }
    
    /* Test whether the graph has a negative cycle */
    System.out.println(BF.hasNegativeCycle() ? "the graph has negative cycle" : "the graph has No negative cycle");
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
    Set<DirectedEdge> edges = new HashSet<>(E);
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
      g.addEdge(tail, head, cost);
      edges.add(edge);
    }
    return g;
  }
   
}
