package cdoan.stanfordalgo.graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;


/**
 * Programming assignment 1 of course 2.
 * Generic implemention of Kosaraju algorithm to compute Strongly Connected Components (SCC) of directed graphs.
 * The type for graph vertices is generic type T.
 */
public class SccKosarajuAlgoGeneric<T> {
  
  private Map<T, List<T>> adj; //adjacency list
  
  /** 
   * Constructor.
   */ 
  public SccKosarajuAlgoGeneric(Map<T, List<T>> adjacencyList) {
    adj = adjacencyList;
  }
  
  /**
   * Two traversal pass algorithm to compute SCCs of a directed graph.
   * Backward edge traversal pass to get the magical order of vertices (decreasing order of finishing times).
   * Forward edge traversal pass, processing vertices in decreasing order of their finishing time, to peel off SCCs one at a time.
   */
  public List<List<T>> kosaraju() {
    Deque<T> decreasingOrder = dfsLoopBackward();
    return dfsLoopForward(decreasingOrder);
  }
  
  /* Helper method to compute the decreasing order of vertices' finishing times */
  private Deque<T> dfsLoopBackward() {
    Deque<T> decreasingOrder = new LinkedList<>();
    Map<T, List<T>> backAdj = backwardAdjacencyList();
    /* to keep track of which vertex in each adjacency list needs to be explored next */
    Map<T, Iterator<T>> backIter = new HashMap<>(backAdj.size());
    for (Map.Entry<T, List<T>> entry: backAdj.entrySet()) {
      backIter.put(entry.getKey(), entry.getValue().iterator());
    }
    Set<T> explored = new HashSet<>();
    for (T v: backAdj.keySet()) {
      if (!explored.contains(v)) {
        dfsBackwardIterative(v, backIter, explored, decreasingOrder);
      }
    }
    return decreasingOrder;
  }
  
  /* Create backward/incoming adjacency list from forward/outgoing adjacency list */
  private Map<T, List<T>> backwardAdjacencyList() {
    Map<T, List<T>> backAdj = new HashMap<>();
    for (Map.Entry<T, List<T>> entry: adj.entrySet()) {
      T v = entry.getKey();
      List<T> vNeighbors = entry.getValue();
      for (T w: vNeighbors) {
        List<T> wInwardNeighbors = backAdj.get(w);
        if (wInwardNeighbors == null) {
          wInwardNeighbors = new LinkedList<>();
          backAdj.put(w, wInwardNeighbors);
        }
        wInwardNeighbors.add(v);
      }
    }
    return backAdj;
  }
  
  /* Helper method to do a DFS from source vertex v to compute the decreasing order */
  private void dfsBackwardIterative(T v, Map<T, Iterator<T>> backIter, Set<T> explored, Deque<T> decreasingOrder) {
    /* Iterative version to avoid StackOverflowError */
    Deque<T> st = new LinkedList<>();
    st.addFirst(v);
    while (!st.isEmpty()) {
      v = st.getFirst();
      explored.add(v);
      Iterator<T> neighbors = backIter.get(v);
      if (neighbors == null || !neighbors.hasNext()) {
        // Done with vertex v
        st.removeFirst();
        decreasingOrder.addFirst(v);
      } else {
        T neighbor = neighbors.next();
        if (!explored.contains(neighbor)) {
          st.addFirst(neighbor);
        }
      }
    }
  }
  
  /* Helper method to compute SCCs */
  private List<List<T>> dfsLoopForward(Deque<T> decreasingOrder) {
    List<List<T>> SCCs = new LinkedList<>();
    /* to keep track of which vertex in each adjacency list needs to be explored next */
    Map<T, Iterator<T>> iter = new HashMap<>(adj.size());
    for (Map.Entry<T, List<T>> entry: adj.entrySet()) {
      iter.put(entry.getKey(), entry.getValue().iterator());
    }
    Set<T> explored = new HashSet<>();
    while (!decreasingOrder.isEmpty()) {
      T v = decreasingOrder.removeFirst();
      if (!explored.contains(v)) {
        List<T> scc = new ArrayList<>();
        dfsForwardIterative(v, iter, explored, scc);
        SCCs.add(scc);
      }
    }
    return SCCs;
  }
  
  /* Helper method to do a DFS from source vertex v to populate the current SCC containing v */
  private void dfsForwardIterative(T v, Map<T, Iterator<T>> iter, Set<T> explored, List<T> scc) {
    /* Iterative version to avoid StackOverflowError */
    Deque<T> st = new LinkedList<>();
    st.addFirst(v);
    while (!st.isEmpty()) {
      v = st.getFirst();
      explored.add(v);
      Iterator<T> neighborIter = iter.get(v);
      if (neighborIter == null || !neighborIter.hasNext()) {
        // Done with vertex v
        st.removeFirst();
        scc.add(v);
      } else {
        T neighbor = neighborIter.next();
        if (!explored.contains(neighbor)) {
          st.addFirst(neighbor);
        }
      }
    }
  }
  

  /**
   * Run test.
   */
  public static void main(String[] args) throws IOException {
    Map<Integer, List<Integer>> adj;
    if (args.length > 0) {
      adj = readGraphFromFile(args[0]);
    } else {
      adj = new HashMap<>(9);
      adj.put(1, Arrays.asList(2));
      adj.put(2, Arrays.asList(1, 3, 6));
      adj.put(3, Arrays.asList(4, 6));
      adj.put(4, Arrays.asList(5, 6));
      adj.put(5, Arrays.asList(3));
      adj.put(6, Arrays.asList(5));
      adj.put(7, Arrays.asList(1, 4, 9));
      adj.put(8, Arrays.asList(1, 7));
      adj.put(9, Arrays.asList(5, 8));
    }
    SccKosarajuAlgoGeneric<Integer> algo = new SccKosarajuAlgoGeneric<>(adj);
    List<List<Integer>> SCCs = algo.kosaraju();
    List<Integer> sizes = new ArrayList<>(SCCs.size());
    for (List<Integer> scc: SCCs) {
      sizes.add(scc.size());
    }
    List<Integer> sizesOf5LargestSccs = kLargestItems(sizes, 5);
    final int size = sizesOf5LargestSccs.size();
    if (size < 5) {
      for (int i = size; i < 5; i++) {
        sizesOf5LargestSccs.add(0);
      }
    }
    System.out.println("The sizes of 5 largest SCCs:" + sizesOf5LargestSccs);
  }
  
  /* Utility method for test data */
  private static Map<Integer, List<Integer>> readGraphFromFile(String filename) throws IOException {
    Map<Integer, List<Integer>> adj = new HashMap<>(765714);
    Scanner s = new Scanner(new File(filename));
    int prevTail = s.nextInt();
    int tail = prevTail;
    List<Integer> heads = new LinkedList<>();
    heads.add(s.nextInt());
    while (s.hasNextInt()) {
      tail = s.nextInt();
      if (tail != prevTail) {
        adj.put(prevTail, heads);
        heads = new LinkedList<>();
      }
      heads.add(s.nextInt());
      prevTail = tail;
    }
    adj.put(tail, heads);
    s.close();
    return adj;
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
