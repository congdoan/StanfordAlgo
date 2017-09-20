package cdoan.stanfordalgo.part4;


import java.io.*;
import java.util.*;


/**
 * Programming Assignment #4 of Part 4.
 * Solve 2-SAT problem by reducing it to the Strongly Connected Components.
 */
public class TwoSatScc {
  
  private static class Digraph {
    private static final String NEWLINE = System.getProperty("line.separator");
    
    private final int V;         // number of vertices
    private final int N;         // number of non-negated variables ( also = V / 2 )
    private int E;               // number of edges
    private List<Integer>[] adj; // adj[v] = adjacency list for vertex v
    
    private Digraph(int V) {
      if (V < 0) {
        throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
      }
      this.V = V;
      this.N = V / 2;
      this.E = 0;
      adj = (List<Integer>[]) new List[V + 1]; // Note that we ignore adj[0] (vertices range from 1 to V inclusive)
      for (int v = 1; v <= V; v++) {
        adj[v] = new LinkedList<Integer>();
      }
    }
    
    private int V() {
      return V;
    }
    
    private int E() {
      return E;
    }
    
    private void addEdge(int A, int B) {
      // Encode negated variable A (denoted as negative integer in the input) as vertex identified by value N - A
      if (A < 0) {
        A = N - A;
      }
      if (B < 0) {
        B = N - B;
      }
      validateVertex(A);
      validateVertex(B);
      adj[A].add(B);
      E++;
    }
    
    private Iterable<Integer> adj(int A) {
      validateVertex(A);
      return adj[A];
    }
    
    private Digraph reverse() {
      Digraph rev = new Digraph(V);
      for (int v = 1; v <= V; v++) {
        for (int w: adj[v]) {
          rev.addEdge(w, v);
        }
      }
      return rev;
    }
    
    private void validateVertex(int A) {
      if (A < 1 || A > V) {
        throw new IllegalArgumentException(String.format("vertex %d is not between 1 and %d", A, V));
      }
    }
    
    public String toString() {
      StringBuilder s = new StringBuilder();
      s.append(V + " vertices, " + E + " edges " + NEWLINE);
      for (int v = 1; v <= V; v++) {
        s.append(String.format("%d: ", v));
        for (int w : adj[v]) {
          s.append(String.format("%d ", w));
        }
        s.append(NEWLINE);
      }
      return s.toString();
    }
  }
  
  
  private static class DepthFirstOrder {
    private Digraph G;        // input directed graph
    private boolean[] marked; // marked[v] = has v been marked in dfs?
    
    private DepthFirstOrder(Digraph G) {
      if (G == null) {
        throw new NullPointerException("G must be nonnull");
      }
      this.G = G;
    }
    
    private Iterable<Integer> reversePost() {
      LinkedList<Integer> reversedFinishingOrder = new LinkedList<>();
      marked = new boolean[G.V() + 1];
      for (int v = 1; v <= G.V(); v++) {
        if (!marked[v]) {
          dfs(v, reversedFinishingOrder);
        }
      }
      return reversedFinishingOrder;
    }
    
    private void dfs(int v, LinkedList<Integer> reversedFinishingOrder) {
      marked[v] = true;
      for (int w: G.adj(v)) {
        if (!marked[w]) {
          dfs(w, reversedFinishingOrder);
        }
      }
      // Done with vertex v
      reversedFinishingOrder.addFirst(v);
    }
  }
  
  
  private static class KosarajuSCC {
    private Digraph G;        // input directed graph
    private boolean[] marked; // marked[v] = has vertex v been visited?
    private int[] id;         // id[v] = id of strong component containing v
    private int count;        // number of strongly-connected components
    
    private KosarajuSCC(Digraph G) {
      if (G == null) {
        throw new NullPointerException("G must be nonnull");
      }
      this.G = G;
      
      // Compute reverse postorder of reverse graph
      DepthFirstOrder dfo = new DepthFirstOrder(G.reverse());
      
      // Run DFS on G, using reverse postorder to guide calculation
      marked = new boolean[G.V() + 1];
      id = new int[G.V() + 1];
      count = 1;
      for (int v: dfo.reversePost()) {
        if (!marked[v]) {
          dfs(v);
          count++;
        }
      }
    }
    
    private void dfs(int v) {
      marked[v] = true;
      id[v] = count;
      for (int w: G.adj(v)) {
        if (!marked[w]) {
          dfs(w);
        }
      }
    }
    
    private boolean stronglyConnected(int v, int w) {
      G.validateVertex(v);
      G.validateVertex(w);
      return id[v] == id[w];
    }
  }
  
  
  /**
   * If any two variables X and X' are on a cycle i.e. path(X -> X') and path(X' -> X) both exists, then the CNF is unsatisfiable.
   * This means if X and X' lie in the same Strongly Connected Component, the CNF is unsatisfiable.
   */
  public static boolean checkSatisfiability(Digraph G) {
    KosarajuSCC scc = new KosarajuSCC(G);
    int N = G.V() / 2;
    for (int v = 1; v <= N; v++) {
      int vNegated = v + N;
      if (scc.stronglyConnected(v, vNegated)) {
        return false;
      }
    }
    return true;
  }
  
  
  private static Digraph readInput(String filename) throws IOException {
    Scanner s = new Scanner(new File(filename));
    final int N = s.nextInt(); // number of non-negated variables = number of clauses
    final int V = 2 * N; // number of vertices in implication graph = 2 * number of non-negated variables
    Digraph G = new Digraph(V);
    for (int i = 0; i < N; i++) {
      // Encode each clause A V B as two edges A' -> B and B' -> A where A' = not A, B' = not B
      int A = s.nextInt(), B = s.nextInt();
      G.addEdge(-A, B);
      G.addEdge(-B, A);
    }
    s.close();
    return G;
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Execution: java cdoan.stanfordalgo.part4.TwoSatScc <2Sat-instance-files>");
      System.exit(1);
    }
    
    String binStr = "";
    for (int i = 0; i < args.length; i++) {
      // Read input instance into implication graph
      Digraph G = readInput(args[i]);      
      // Compute strongly connected components
      // Determine satisfiability of input instance
      boolean satisfiable = checkSatisfiability(G);
      binStr += (satisfiable ? "1" : "0");
    }
    System.out.println(binStr);    
  }
  
}
