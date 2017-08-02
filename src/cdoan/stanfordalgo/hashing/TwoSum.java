package cdoan.stanfordalgo.hashing;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;


/**
 * Solve the 2Sum problem using hash table for the programming assignment 4 of course 2.
 */
public class TwoSum {
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: java cdoan.stanfordalgo.hashing.TwoSum <input-file> [input-size(optional)]");
      System.exit(1);
    }
    
    // Preprocess: add all the values from input file to a set
    HashSet<Long> set = args.length > 1 ? new HashSet<>(Integer.parseInt(args[1])) : new HashSet<>();
    Scanner s = new Scanner(new File(args[0]));
    while (s.hasNextLong()) {
      set.add(s.nextLong());
    }
    s.close();
    
    // Count number of target values t in given range such that t = x + y where x & y are distinct numbers from input file
    int count = 0;
    for (long t = -10000; t <= 10000; t++) {
      for (long y: set) {
        if (y != t - y && set.contains(t - y)) {
          count++;
          break;
        }
      }
    }
    System.out.println(count);
  }
  
}
