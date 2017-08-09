package cdoan.stanfordalgo.greedy.huffman;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


/**
 * Iterative implementation of Huffman algorithm using sorting and 2 queues.
 * Running time: O(N*logN) due to sorting preprocessing step.
 */
public class HuffmanUsing2Queues<T> extends HuffmanAlgoBase<T> {
  
  public HuffmanUsing2Queues(List<Symbol<T>> sigma) {
    super(sigma);
  }
  
  public Node<T> construct(List<Node<T>> initialLeaves) {
    Collections.sort(initialLeaves);
    Queue<Node<T>> leafQueue = new LinkedList<>(initialLeaves);
    Queue<Node<T>> internalQueue = new LinkedList<>();
    return construct(leafQueue, internalQueue);
  }

  private Node<T> construct(Queue<Node<T>> leafQueue, Queue<Node<T>> internalQueue) {
    while (leafQueue.size() + internalQueue.size() > 1) {
      /* Dequeue the two nodes with the lowest frequency ( weight ) */
      Node<T> lowestFreq, lowestFreq2;
      if (internalQueue.isEmpty()) {
        lowestFreq = leafQueue.poll();
        lowestFreq2 = leafQueue.poll();
      } else if (leafQueue.isEmpty()) {
        lowestFreq = internalQueue.poll();
        lowestFreq2 = internalQueue.poll();
      } else if (leafQueue.peek().compareTo(internalQueue.peek()) <= 0) {
        lowestFreq = leafQueue.poll();
        if (leafQueue.isEmpty()) {
          lowestFreq2 = internalQueue.poll();
        } else {
          lowestFreq2 = leafQueue.peek().compareTo(internalQueue.peek()) <= 0 ? leafQueue.poll() : internalQueue.poll();
        }
      } else { // leafQueue.peek() > internalQueue.peek()
        lowestFreq = internalQueue.poll();
        if (internalQueue.isEmpty()) {
          lowestFreq2 = leafQueue.poll();
        } else {
          lowestFreq2 = leafQueue.peek().compareTo(internalQueue.peek()) <= 0 ? leafQueue.poll() : internalQueue.poll();
        }
      }
      // Sibling them under a common parent
      Node<T> parent = new Node<>(lowestFreq, lowestFreq2);
      // Add the parent to the internal node queue
      internalQueue.offer(parent);
    }
    
    // The remaining node (in the internal node queue) is the root node
    return internalQueue.poll();
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
