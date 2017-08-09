package cdoan.stanfordalgo.greedy.huffman;


import java.util.List;
import java.util.Map;


/**
 * Huffman coding algorithm.
 */
public interface HuffmanAlgo<T> {
  
  public static class Node<T> implements Comparable<Node<T>> {
    private T val;
    private int freq;
    private Node<T> left;
    private Node<T> right;
    
    public Node(Symbol<T> symbol) {
      val = symbol.val;
      freq = symbol.freq;
    }
    
    public Node(Node<T> l, Node<T> r) {
      freq = l.freq + r.freq;
      left = l;
      right = r;
    }
    
    public T getVal() {
      return val;
    }
    
    public int getFreq() {
      return freq;
    }
    
    public Node<T> getLeft() {
      return left;
    }
    
    public Node<T> getRight() {
      return right;
    }
    
    public int compareTo(Node<T> other) {
      return freq - other.freq;
    }
  }

  public static class Symbol<T> {
    private T val;
    private int freq;
    
    public Symbol(T val, int freq) {
      this.val = val;
      this.freq = freq;
    }
    
    //+ debug
    public String toString() {
      return String.format("(%s  %d)", val, freq);
    }
    //-
  }


  /**
   * Get the constructed Huffman codes.
   */
  public Map<T, String> getCodes();

  /**
   * Encode the given symbol sequence into its binary-encoded string.
   */
  public String encode(List<T> symbolSeq);
  
  /**
   * Decode the given binary-encoded string back to the original symbol sequence.
   */
  public List<T> decode(String bitSeq);
  
}
