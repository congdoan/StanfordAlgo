package cdoan.stanfordalgo.week4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


/**
 * Implements Karger Randomized Contraction algorithm for Graph Min Cut problem.
 * Input: undirected, unweighted and connected graph.
 * Output: the size (i.e. number of crossing edges) of the cutset of a min cut with high probability.
 */
public class MinCutKargerAlgo<T extends Comparable<T>> {
  
  /**
   * Represnts an undirected graph edge (i.e. unordered pair of vertices).
   */
  private static class Edge<T extends Comparable<T>> {
    private T u;
    private T v;
    
    private Edge(T u, T v) {
      if (u.compareTo(v) < 0) {
        this.u = u;
        this.v = v;
      } else {
        this.u = v;
        this.v = u;
      }
    }
    
    public int hashCode() {
      return (31 * u.hashCode()) + v.hashCode();
    }    
    
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj == null || obj.getClass() != Edge.class) {
        return false;
      }
      Edge other = (Edge) obj;
      return u.equals(other.u) && v.equals(other.v);
    }
    
    public String toString() {
      return String.format("%s-%s", u, v);
    }
  }
  
  /**
   * Represents an unordered pair of two vertex sets, which were merged during the algorithm's execution.
   */
  private static class FusedEdge<T> {
    private Set<T> fu;
    private Set<T> fv;
    
    private FusedEdge(Set<T> fu, Set<T> fv) {
      this.fu = fu;
      this.fv = fv;
    }
    
    public String toString() {
      return String.format("%s-%s", fu, fv);
    }
  }

  
  private Set<T> vertices;
  private Set<Edge<T>> edges;
  
  public MinCutKargerAlgo(List<T> vertices) {
    this.vertices = new HashSet<>(vertices);
    this.edges = new HashSet<>();
  }
  
  public boolean addEdge(T u, T v) {
    if (!vertices.contains(u) || !vertices.contains(v)) {
      throw new IllegalArgumentException(String.format("nonexistent vertex '%s' or '%s'", u, v));
    }
    if (u.equals(v)) {
      throw new IllegalArgumentException(String.format("'%s' and '%s' are the same vertex", u, v));
    }
    return edges.add(new Edge<T>(u, v));
  }
  
  public String toString() {
    return String.format("V: %s%nE: %s", vertices, edges);
  }
  
  /**
   * Monte Carlo Karger Randomized Contraction algorithm.
   */
  public int kargerContractionAlgo() {
    int fewestCrossEdges = Integer.MAX_VALUE;
    final int V = vertices.size();
    final long numTries = (long) (V * V * Math.log(V));
    for (int i = 0; i < numTries; i++) {
      fewestCrossEdges = Math.min(fewestCrossEdges, runContractionAlgo1Time());
    }
    return fewestCrossEdges;
  }
  
  /* Single-try Karger Randomized Contraction algorithm. */
  private int runContractionAlgo1Time() {
    Set<Set<T>> vertexSet = vertices2FusedVertices();
    Map<Set<T>, List<FusedEdge<T>>> vertexToEdgesMap = new HashMap<>();
    List<FusedEdge<T>> edgeList = edges2FusedEdges(vertexToEdgesMap);
    Random rd = new Random();
    while (vertexSet.size() > 2) {
      contractEdge(vertexSet, edgeList, vertexToEdgesMap, rd.nextInt(edgeList.size()));
    }
    return edgeList.size();
  }

  /* Contracts the edge that is uniformly selected at random. */
  private void contractEdge(Set<Set<T>> vertexSet, 
                            List<FusedEdge<T>> edgeList, 
                            Map<Set<T>, List<FusedEdge<T>>> vertexToEdgesMap, 
                            int randomEdgeIdx) {
    FusedEdge<T> randomEdge = edgeList.get(randomEdgeIdx);
    
    // Remove randomEdge from edge list
    edgeList.remove(randomEdgeIdx);
    
    /* Merge edge's 2 vertices into single one */
    Set<T> mergedVertex = new HashSet<>(randomEdge.fu);
    mergedVertex.addAll(randomEdge.fv);
    // Within vertexSet replace the edge's 2 vertices with mergedVertex
    vertexSet.remove(randomEdge.fu);
    vertexSet.remove(randomEdge.fv);
    vertexSet.add(mergedVertex);
    // Find those edges that have randomEdge.fu or randomEdge.fv as an endpoint then replace it with mergedVertex
    contractEdgeHelper(randomEdge, edgeList, vertexToEdgesMap, mergedVertex);
  }
  
  /* Helper method of contractEdge method above */
  private void contractEdgeHelper(FusedEdge<T> contractingEdge, 
                                  List<FusedEdge<T>> edgeList, 
                                  Map<Set<T>, List<FusedEdge<T>>> vertexToEdgesMap, 
                                  Set<T> mergedVertex) {
    List<FusedEdge<T>> uEdges = vertexToEdgesMap.get(contractingEdge.fu);
    List<FusedEdge<T>> vEdges = vertexToEdgesMap.get(contractingEdge.fv);
    // First, remove from vertexToEdgesMap the edge being contracted
    uEdges.remove(contractingEdge);
    vEdges.remove(contractingEdge);

    List<FusedEdge<T>> adjEdges = new ArrayList<>(uEdges);
    adjEdges.addAll(vEdges);
    for (FusedEdge<T> edge : adjEdges) {
      if ((edge.fu.equals(contractingEdge.fu) && edge.fv.equals(contractingEdge.fv))
           || (edge.fu.equals(contractingEdge.fv) && edge.fv.equals(contractingEdge.fu))) {
        /* Remove self-loop edge (i.e. edge that is parallel to the edge being contracted) */
        edgeList.remove(edge); // O(# of remaining edges) time - SLOW
        uEdges.remove(edge);
        vEdges.remove(edge);
      } else if (edge.fu.equals(contractingEdge.fu) || edge.fu.equals(contractingEdge.fv)) {
        // Replace edge's fu with mergedVertex
        edge.fu = mergedVertex;
      } else if (edge.fv.equals(contractingEdge.fu) || edge.fv.equals(contractingEdge.fv)) {
        // Replace edge's fv with mergedVertex
        edge.fv = mergedVertex;
      }
    }
    
    // Update vertexToEdgesMap
    vertexToEdgesMap.remove(contractingEdge.fu);
    vertexToEdgesMap.remove(contractingEdge.fv);
    uEdges.addAll(vEdges);
    vertexToEdgesMap.put(mergedVertex, uEdges);
  }
  
  /* Each vertex in the graph's vertex set is copied over into a single-element set of itself */
  private Set<Set<T>> vertices2FusedVertices() {
    Set<Set<T>> vertexSet = new HashSet<>();
    for (T v : vertices) {
      Set<T> fv = new HashSet<>(Arrays.asList(v));
      vertexSet.add(fv);
    }
    return vertexSet;
  }
  
  /* Each Edge in the graph's edge set is copied over into a FusedEdge defined near the top */
  private List<FusedEdge<T>> edges2FusedEdges(Map<Set<T>, List<FusedEdge<T>>> vertexToEdgesMap) {
    List<FusedEdge<T>> edgeList = new ArrayList<>();
    for (Edge<T> e : edges) {
      Set<T> fu = new HashSet<>(Arrays.asList(e.u));
      Set<T> fv = new HashSet<>(Arrays.asList(e.v));
      FusedEdge<T> fe = new FusedEdge<>(fu, fv);
      List<FusedEdge<T>> tmpEdges = vertexToEdgesMap.get(fu);
      if (tmpEdges == null) {
        tmpEdges = new ArrayList<>();
        vertexToEdgesMap.put(fu, tmpEdges);
      }
      tmpEdges.add(fe);
      tmpEdges = vertexToEdgesMap.get(fv);
      if (tmpEdges == null) {
        tmpEdges = new ArrayList<>();
        vertexToEdgesMap.put(fv, tmpEdges);
      }
      tmpEdges.add(fe);
      edgeList.add(fe);
    }
    return edgeList;
  }


  /**
   * Read vertices and edges from text file to build an undirected graph.
   */
  public static void main(String[] args) throws IOException {
    /* Build a connected undirected graph */
    List<Integer> vertices;
    Map<Integer, List<Integer>> vertex2AdjVertices;
    if (args.length > 0) {
      vertices = new ArrayList<>();
      vertex2AdjVertices = new HashMap<>();
      readFromFile(args[0], vertices, vertex2AdjVertices);
    } else {
      vertices = Arrays.asList(1, 2, 3, 4, 5);
      vertex2AdjVertices = new HashMap<>();
      vertex2AdjVertices.put(1, Arrays.asList(2, 5));
      vertex2AdjVertices.put(2, Arrays.asList(1, 3, 4, 5));
      vertex2AdjVertices.put(3, Arrays.asList(2, 4));
      vertex2AdjVertices.put(4, Arrays.asList(2, 3, 5));
      vertex2AdjVertices.put(5, Arrays.asList(1, 2, 4));
    }
    MinCutKargerAlgo<Integer> g = new MinCutKargerAlgo<>(vertices);
    for (Map.Entry<Integer, List<Integer>> v2Adj : vertex2AdjVertices.entrySet()) {
      Integer v = v2Adj.getKey();
      List<Integer> adj = v2Adj.getValue();
      for (Integer w : adj) {
        g.addEdge(v, w);
      }
    }
    
    long start = System.currentTimeMillis();
    int minCut = g.kargerContractionAlgo();
    long runtime = (System.currentTimeMillis() - start) / 1000;
    System.out.println("Run time (in seconds): " + runtime);
    System.out.println(String.format("Minimum Cut has %d crossing edges.", minCut));
  }
  
  /* Utility method for loading test data */
  private static void readFromFile(String fileName, List<Integer> vertices, 
                                   Map<Integer, List<Integer>> vertex2AdjVertices) throws IOException {
    Scanner fileScan = new Scanner(new File(fileName));
    Scanner lineScan;
    while (fileScan.hasNextLine()) {
      lineScan = new Scanner(fileScan.nextLine());
      int v = lineScan.nextInt();
      vertices.add(v);
      List<Integer> adj = new ArrayList<>();
      while (lineScan.hasNextInt()) {
        adj.add(lineScan.nextInt());
      }
      vertex2AdjVertices.put(v, adj);
      lineScan.close();
    }
    fileScan.close();
  }
  
}
