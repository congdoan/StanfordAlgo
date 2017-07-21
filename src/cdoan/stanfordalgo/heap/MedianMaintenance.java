package cdoan.stanfordalgo.heap;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Scanner;


/**
 * Heap-based implementation of median maintenance.
 * Programming assignment 3 of course 2.
 */
public class  MedianMaintenance<T extends Comparable<T>> {
  
  // max heap of half of smaller values
  private PriorityQueue<T> maxHeapLo = new PriorityQueue<>((T a, T b) -> b.compareTo(a));
  // min heap of half of larger values
  private PriorityQueue<T> minHeapHi = new PriorityQueue<>();
  
  /**
   * Return the number of values received so far.
   */
  public long getCount() {
    return maxHeapLo.size() + minHeapHi.size();
  }
  
  /**
   * Compute the median of the sequence of values received up to now.
   */
  public T getMedianUpToNow(T x) {
    /* The first 2 values */
    if (maxHeapLo.isEmpty()) {
      // First value
      maxHeapLo.offer(x);
      return x;
    }
    if (minHeapHi.isEmpty()) {
      // Second value
      if (x.compareTo(maxHeapLo.peek()) > 0) {
        minHeapHi.offer(x);
      } else {
        minHeapHi.offer(maxHeapLo.poll());
        maxHeapLo.offer(x);
      }
      return maxHeapLo.peek();
    }
    
    /* Add this value to one of the heaps */
    if (x.compareTo(maxHeapLo.peek()) < 0) {
      // Add x to smaller half
      maxHeapLo.offer(x);
    } else if (x.compareTo(minHeapHi.peek()) > 0) {
      // Add x to larger half
      minHeapHi.offer(x);
    } else {
      // x goes between 2 halves
      // Add x to smaller half
      maxHeapLo.offer(x);
    }
    
    /* Rebalance 2 heaps such that either they have same size or size of smaller half = 1 + size of larger half */
    if (maxHeapLo.size() > minHeapHi.size() + 1) {
      minHeapHi.offer(maxHeapLo.poll());
    } else if (maxHeapLo.size() < minHeapHi.size()) {
      maxHeapLo.offer(minHeapHi.poll());
    }
    
    return maxHeapLo.peek();
  }
  
  /** 
   * Unit tests the implementation.
   */
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: cdoan.stanfordalgo.heap.MedianMaintenance <data_file>");
      System.exit(1);
    }
    
    MedianMaintenance<Integer> mm = new MedianMaintenance<>();
    int medianSum = 0;
    Scanner s = new Scanner(new File(args[0]));
    while (s.hasNextInt()) {
      medianSum += mm.getMedianUpToNow(s.nextInt());
      medianSum %= 10000;
    }
    s.close();
    System.out.printf("The last 4 digits of sum over all the %d medians: %d%n", mm.getCount(), medianSum);    
  }
  
}
