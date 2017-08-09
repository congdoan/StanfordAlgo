package cdoan.stanfordalgo.greedy.huffman;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;


/**
 * Naive recursive implementation of Huffman algorithm.
 * Running time: O(n^2).
 */
public class HuffmanNaive<T> extends HuffmanAlgoBase<T> {
  
  public HuffmanNaive(List<Symbol<T>> sigma) {
    super(sigma);
  }
  
  public Node<T> construct(List<Node<T>> nonsiblingSubtrees) {
    int n = nonsiblingSubtrees.size();
    
    // Base case
    if (n == 2) {
      Node<T> left = nonsiblingSubtrees.get(0);
      Node<T> right = nonsiblingSubtrees.get(1);
      Node<T> root = new Node<>(left, right);
      return root;
    }
    
    // Find 2 lowest frequency meta-symbols
    int lowest = 0;
    for (int i = 1; i < n; i++) {
      if (nonsiblingSubtrees.get(i).getFreq() < nonsiblingSubtrees.get(lowest).getFreq()) {
        lowest = i;
      }
    }
    Node<T> tmp = nonsiblingSubtrees.get(0);
    nonsiblingSubtrees.set(0, nonsiblingSubtrees.get(lowest));
    nonsiblingSubtrees.set(lowest, tmp);
    int secondLowest = 1;
    for (int i = 2; i < n; i++) {
      if (nonsiblingSubtrees.get(i).getFreq() < nonsiblingSubtrees.get(secondLowest).getFreq()) {
        secondLowest = i;
      }
    }
    // Sibling them under the same parent
    Node<T> parent = new Node<>(nonsiblingSubtrees.get(0), nonsiblingSubtrees.get(secondLowest));
    // Within the remaining separated subtrees replace them with their parent
    nonsiblingSubtrees.remove(secondLowest);
    nonsiblingSubtrees.set(0, parent);
    
    // Recur
    return construct(nonsiblingSubtrees);
  }

  
  /**
   * Unit test.
   */
  public static void main(String[] args) {
    List<Symbol<Character>> sigma = new ArrayList<>();
    //Character[] ch = {'a', 'b', 'c', 'd', 'e', 'f'};
    //int[] freq = {3, 2, 6, 8, 2, 6};
    //Character[] ch = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    //int[] freq = {3, 2, 6, 8, 2, 6, 1, 4};
    Character[] ch = {'a', 'b', 'c', 'd', 'e'};
    int[] freq = {32, 25, 20, 18, 5};
    for (int i = 0; i < ch.length; i++) {
      sigma.add(new Symbol<Character>(ch[i], freq[i]));
    }
    System.out.println("Sigma: " + sigma);
    HuffmanAlgo<Character> huff = new HuffmanNaive<>(sigma);
    Map<Character, String> codes = huff.getCodes();
    System.out.println("Codes: " + codes);
    int weightedSumOfLengths = 0, sumOfWeights = 0;
    for (int i = 0; i < ch.length; i++) {
      weightedSumOfLengths += freq[i] * codes.get(ch[i]).length();
      sumOfWeights += freq[i];
    }
    System.out.println("Average coding length: " + ((weightedSumOfLengths * 1.0) / sumOfWeights));
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
