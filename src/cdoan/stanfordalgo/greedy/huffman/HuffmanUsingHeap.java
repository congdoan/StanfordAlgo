package cdoan.stanfordalgo.greedy.huffman;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;


/**
 * Heap-based recursive implementation of Huffman algorithm.
 * Running time: O(N*logN).
 */
public class HuffmanUsingHeap<T> extends HuffmanAlgoBase<T> {
  
  public HuffmanUsingHeap(List<Symbol<T>> sigma) {
    super(sigma);
  }
  
  public Node<T> construct(List<Node<T>> initialLeaves) {
    PriorityQueue<Node<T>> nonsiblingHeap = new PriorityQueue<>(initialLeaves);
    return construct(nonsiblingHeap);
  }

  private Node<T> construct(PriorityQueue<Node<T>> nonsiblingHeap) {
    // Extract two lowest frequency subtrees from the heap
    Node<T> left = nonsiblingHeap.poll();
    Node<T> right = nonsiblingHeap.poll();
    // Sibling them under a common parent
    Node<T> parent = new Node<>(left, right);
    
    // Base case
    if (nonsiblingHeap.isEmpty()) {
      return parent;
    }
    
    // Add their parent to the heap
    nonsiblingHeap.offer(parent);
    
    // Recur
    return construct(nonsiblingHeap);
  }

  
  /**
   * Unit test.
   */
  public static void main(String[] args) {
    List<Symbol<Character>> sigma = new ArrayList<>();
    //Character[] ch = {'a', 'b', 'c', 'd', 'e', 'f'};
    //int[] freq = {3, 2, 6, 8, 2, 6};
    Character[] ch = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    int[] freq = {3, 2, 6, 8, 2, 6, 1, 4};
    for (int i = 0; i < ch.length; i++) {
      sigma.add(new Symbol<Character>(ch[i], freq[i]));
    }
    System.out.println("Sigma: " + sigma);
    HuffmanAlgo<Character> huff = new HuffmanNaive<>(sigma);
    Map<Character, String> codes = huff.getCodes();
    System.out.println("Codes: " + codes);
    List<Character> originalSeq = randSymbolSeq(ch);
    System.out.println("Input Sequence:   " + originalSeq);
    String encodedString = huff.encode(originalSeq);
    System.out.println("Encoded Sequence: " + encodedString);
    List<Character> decodedSeq = huff.decode(encodedString);
    System.out.println("Decoded Sequence: " + decodedSeq);
    assert decodedSeq.equals(originalSeq);
  }
  
  private static <T> List<T> randSymbolSeq(T[] sigma) {
    Random rd = new Random();
    final int MIN = 10;
    final int MAX = 30;
    int n = MIN + rd.nextInt(MAX - MIN + 1);
    List<T> seq = new LinkedList<>();
    for (int i = 0; i < n; i++) {
      seq.add(sigma[rd.nextInt(sigma.length)]);
    }
    return seq;
  }
  
}
