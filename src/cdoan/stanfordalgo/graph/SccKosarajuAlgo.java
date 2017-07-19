package cdoan.stanfordalgo.graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;


/**
 * Programming assignment 1 of course 2.
 * Implement Kosaraju algorithm to compute Strongly Connected Components of directed graphs
 */
public class SccKosarajuAlgo {

  private int V; //number of vertices
  private LinkedList<Integer>[] adj; //adjacency list
  
  /** 
   * Constructor.
   */ 
  public SccKosarajuAlgo(int numVertices) {
    V = numVertices + 1;
    adj = new LinkedList[V];
    for (int i = 1; i < V; i++) {
      adj[i] = new LinkedList<Integer>();
    }
  }
  
  /**
   * Add a directed edge.
   */
  public void addEdge(int tail, int head) {
    adj[tail].add(head);
  }
  
  /**
   * Two traversal pass algorithm to compute SCCs of a directed graph.
   * Backward edge traversal pass to get the magical order of vertices (decreasing order of finishing times).
   * Forward edge traversal pass, processing vertices in decreasing order of their finishing time, to peel off SCCs one at a time.
   */
  public LinkedList<Integer> kosaraju() {
    LinkedList<Integer> decreasingOrder = dfsLoopBackward();
    return dfsLoopForward(decreasingOrder);
  }
  
  /* Helper method to compute the decreasing order of vertices' finishing times */
  private LinkedList<Integer> dfsLoopBackward() {
    LinkedList<Integer>[] backAdj = backwardAdjacencyList();
    /* to keep track of which vertex in each adjacency list needs to be explored next */
    Iterator<Integer>[] backIter = new Iterator[V];
    for (int i = 1; i < V; i++) {
      backIter[i] = backAdj[i].iterator();
    }
    HashSet<Integer> explored = new HashSet<>();
    LinkedList<Integer> decreasingOrder = new LinkedList<>();
    for (int v = 1; v < V; v++) {
      if (!explored.contains(v)) {
        dfsBackward(v, backIter, explored, decreasingOrder);
      }
    }
    return decreasingOrder;
  }
  
  private LinkedList<Integer>[] backwardAdjacencyList() {
    LinkedList<Integer>[] backAdj = new LinkedList[V];
    for (int i = 1; i < V; i++) {
      backAdj[i] = new LinkedList<Integer>();
    }
    for (int v = 1; v < V; v++) {
      for (int w: adj[v]) {
        backAdj[w].add(v);
      }
    }
    return backAdj;
  }
  
  /* Helper method to do a DFS from source vertex v to compute the decreasing order */
  private void dfsBackward(Integer v, Iterator<Integer>[] backIter, 
                           HashSet<Integer> explored, LinkedList<Integer> decreasingOrder) {
    /* Iterative version to avoid StackOverflowError */
    LinkedList<Integer> st = new LinkedList<>();
    st.addFirst(v);
    while (!st.isEmpty()) {
      v = st.getFirst();
      explored.add(v);
      Iterator<Integer> neighbors = backIter[v];
      if (neighbors.hasNext()) {
        Integer neighbor = neighbors.next();
        if (!explored.contains(neighbor)) {
          st.addFirst(neighbor);
        }
      } else {
        // Done with vertex v
        st.removeFirst();
        decreasingOrder.addFirst(v);
      }
    }
  }
  
  /* Helper method to compute SCCs */
  private LinkedList<Integer> dfsLoopForward(LinkedList<Integer> decreasingOrder) {
    LinkedList<Integer> sccSizes = new LinkedList<>();
    /* to keep track of which vertex in each adjacency list needs to be explored next */
    Iterator<Integer>[] iter = new Iterator[V];
    for (int i = 1; i < V; i++) {
      iter[i] = adj[i].iterator();
    }
    HashSet<Integer> explored = new HashSet<>();
    while (!decreasingOrder.isEmpty()) {
      Integer v = decreasingOrder.removeFirst();
      if (!explored.contains(v)) {
        int size = dfsIterative(v, iter, explored);
        sccSizes.add(size);
      }
    }
    return sccSizes;
  }
  
  /* Helper method to do a DFS from source vertex v to populate the current SCC containing v */
  private int dfsIterative(Integer v, Iterator<Integer>[] iter, HashSet<Integer> explored) {
    /* Iterative version to avoid StackOverflowError */
    int count = 0;
    LinkedList<Integer> st = new LinkedList<>();
    st.addFirst(v);
    while (!st.isEmpty()) {
      v = st.getFirst();
      explored.add(v);
      Iterator<Integer> neighbors = iter[v];
      if (neighbors.hasNext()) {
        Integer neighbor = neighbors.next();
        if (!explored.contains(neighbor)) {
          st.addFirst(neighbor);
        }
      } else {
        // Done with vertex v
        st.removeFirst();
        count++;
      }
    }
    return count;
  }
  
  
  /**
   * Run test.
   */
  public static void main(String[] args) throws IOException {
    SccKosarajuAlgo g;
    if (args.length > 0) {
      g = new SccKosarajuAlgo(875714);
      readGraphFromFile(args[0], g);
    } else {
      g = new SccKosarajuAlgo(9);
      g.addEdge(1, 2);
      
      g.addEdge(2, 1);
      g.addEdge(2, 3);
      g.addEdge(2, 6);
      
      g.addEdge(3, 4);
      g.addEdge(3, 6);
      
      g.addEdge(4, 5);
      g.addEdge(4, 6);
      
      g.addEdge(5, 3);
      
      g.addEdge(6, 5);
      
      g.addEdge(7, 1);
      g.addEdge(7, 4);
      g.addEdge(7, 9);
      
      g.addEdge(8, 1);
      g.addEdge(8, 7);
      
      g.addEdge(9, 5);
      g.addEdge(9, 8);
    }
    LinkedList<Integer> sccSizes = g.kosaraju();
    List<Integer> sizesOf5LargestSccs = kLargestItems(sccSizes, 5);
    final int size = sizesOf5LargestSccs.size();
    if (size < 5) {
      for (int i = size; i < 5; i++) {
        sizesOf5LargestSccs.add(0);
      }
    }
    System.out.println(sizesOf5LargestSccs);
  }
  
  /* Utility method for test data */
  private static void readGraphFromFile(String filename, SccKosarajuAlgo g) throws IOException {
    Scanner s = new Scanner(new File(filename));
    while (s.hasNextInt()) {
      g.addEdge(s.nextInt(), s.nextInt());
    }
    s.close();
  }
  
  /* Helper method for selecting k Largest items in the given list */
  private static <T extends Comparable<T>> List<T> kLargestItems(final List<T> items, final int k) {
    Comparator<T> descendingOrderCmp = (T a, T b) -> b.compareTo(a);
    final int n = items.size();
    PriorityQueue<T> pq = new PriorityQueue<T>(n, descendingOrderCmp);
    for (T item: items) {
      pq.offer(item);
    }
    List<T> res = new ArrayList<>(k);
    if (n >= k) {
      for (int i = 0; i < k; i++) {
        res.add(pq.poll());
      }
    } else {
      while (!pq.isEmpty()) {
        res.add(pq.poll());
      }
    }
    return res;
  }
  
}
