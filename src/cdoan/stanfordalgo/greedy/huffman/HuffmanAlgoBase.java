package cdoan.stanfordalgo.greedy.huffman;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;


public abstract class HuffmanAlgoBase<T> implements HuffmanAlgo<T> {
  
  private Node<T> tree;
  private Map<T, String> codes;
  
  
  /**
   * Contruct minimum-average-coding-length prefix-free binary tree for the given set of symbols.
   * Input:  a set of symbols with their frequencies.
   * Output: an encoded binary tree where each leaf corresponds to a symbol and the root-to-leaf denotes the symbol's binary code.
   *         And this tree is the optimal one in the sense that it minimizes the average encoding length (expected # of bits to encode a symbol).
   */
  public HuffmanAlgoBase(List<Symbol<T>> sigma) {
    List<Node<T>> nonsiblingSubtrees = new ArrayList<>();
    for (Symbol<T> symbol: sigma) {
      nonsiblingSubtrees.add(new Node<>(symbol));
    }
    tree = construct(nonsiblingSubtrees);
    codes = new HashMap<>(sigma.size());
    traverseToGetCodes(tree, codes, "");
  }
  
  public abstract Node<T> construct(List<Node<T>> initialLeaves);

  /**
   * Get the constructed Huffman codes.
   */
  public Map<T, String> getCodes() {
    return codes;
  }
  
  private void traverseToGetCodes(Node<T> x, Map<T, String> codes, String path) {
    // Base case
    if (x == null) {
      return;
    }
    
    Node<T> left = x.getLeft();
    Node<T> right = x.getRight();
    
    // Check & collect leaf's code
    if (left == null && right == null) {
      codes.put(x.getVal(), path);
    }
    
    // Recur
    traverseToGetCodes(left, codes, path + "0");
    traverseToGetCodes(right, codes, path + "1");
  }
  
  /**
   * Encode the given symbol sequence into its binary-encoded string using the constructed Huffman coding tree.
   */
  public String encode(List<T> symbolSeq) {
    StringBuilder sb = new StringBuilder();
    for (T val: symbolSeq) {
      sb.append(codes.get(val));
    }
    return sb.toString();
  }
  
  /**
   * Decode the given binary-encoded string back to the original symbol sequence using the constructed Huffman coding tree.
   */
  public List<T> decode(String bitSeq) {
    List<T> seq = new LinkedList<>();
    Node<T> cur = tree;
    for (int i = 0; i < bitSeq.length(); i++) {
      if (bitSeq.charAt(i) == '0') {
        cur = cur.getLeft();
      } else {
        cur = cur.getRight();
      }
      if (cur.getVal() != null) {
        // Reach leaf, collect its symbol
        seq.add(cur.getVal());
        cur = tree;
      }
    }
    return seq;
  }
  
}
