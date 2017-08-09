package cdoan.stanfordalgo.greedy.huffman;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


/**
 * Questions 1 & 2 of Programming Assignment 3 of Course 3.
 */
public class Course3Programming3Q1Q2 {
  
  private static class Node implements Comparable<Node> {
    private long weight;
    private Node left;
    private Node right;
    
    private Node(long weight) {
      this.weight = weight;
    }
    
    private Node(Node left, Node right) {
      this.weight = left.weight + right.weight;
      this.left = left;
      this.right = right;
    }
    
    public int compareTo(Node other) {
      return new Long(weight).compareTo(new Long(other.weight));
    }
  }
  
  private static class Pair {
    private int min;
    private int max;
    
    private Pair(int min, int max) {
      this.min = min;
      this.max = max;
    }
    
    public String toString() {
      return String.format("%d %d", min, max);
    }
  }
  
  private static class HuffAlgo {
    private Node root;
    
    private HuffAlgo(long[] weights) {
      Node[] leaves = new Node[weights.length];
      for (int i = 0; i < weights.length; i++) {
        leaves[i] = new Node(weights[i]);
      }
      root = build(leaves);
    }

    private Node build(Node[] leaves) {
      Arrays.sort(leaves);
      Queue<Node> internalNodes = new LinkedList<>();
      final int N = leaves.length;
      int i = 0; // index of current leaf
      Node min, min2;
      int iter = 1;
      while (N - i + internalNodes.size() > 1) {
        if (internalNodes.isEmpty()) {
          min = leaves[i++];
          min2 = leaves[i++];
        } else if (N - i == 0) {
          min = internalNodes.poll();
          min2 = internalNodes.poll();
        } else if (leaves[i].compareTo(internalNodes.peek()) <= 0) {
          // leaves[i] <= internalNodes.peek()
          min = leaves[i++];
          if (N - i == 0 || leaves[i].compareTo(internalNodes.peek()) > 0) {
            min2 = internalNodes.poll();
          } else {
            min2 = leaves[i++];
          }
        } else {
          // leaves[i] > internalNodes.peek()
          min = internalNodes.poll();
          if (internalNodes.isEmpty() || leaves[i].compareTo(internalNodes.peek()) <= 0) {
            min2 = leaves[i++];
          } else {
            min2 = internalNodes.poll();
          }
        }
        Node parent = new Node(min, min2);
        internalNodes.offer(parent);
      }
      return internalNodes.poll();
    }
    
    private Pair getMinMaxDepth() {
      return getMinMaxDepth(root);
    }
    private Pair getMinMaxDepth(Node x) {
      // Base case
      if (x == null) {
        return new Pair(-1, -1);
      }
      
      Pair left = getMinMaxDepth(x.left);
      Pair right = getMinMaxDepth(x.right);
      int min = 1 + Math.min(left.min, right.min);
      int max = 1 + Math.max(left.max, right.max);
      return new Pair(min, max);
    }
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: java cdoan.stanfordalgo.greedy.huffman.Course3Programming3Q1Q2 <input-weights-file>");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    final int N = s.nextInt();
    long[] weights = new long[N];
    for (int i = 0; i < N; i++) {
      weights[i] = s.nextLong();
    }
    s.close();
    
    HuffAlgo huff = new HuffAlgo(weights);
    Pair minMax = huff.getMinMaxDepth();
    System.out.println(minMax);
  }
  
}
