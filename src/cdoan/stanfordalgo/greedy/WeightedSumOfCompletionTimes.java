package cdoan.stanfordalgo.greedy;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;


/**
 * Questions 1 & 2 of programming assignment 1 of course 3.
 * 
 * Compute the sum of weighted completion times of the resulting schedule 
 * that sequences jobs in decreasing order of the difference (weight - length).
 * IMPORTANT: if two jobs have equal difference (weight - length),
 *  you should schedule the job with higher weight first.
 */
public class WeightedSumOfCompletionTimes {
  
  private static class Job {
    private int weight;
    private int length;
    
    private Job(int w, int l) {
      weight = w;
      length = l;      
    }
  }
  
  
  public static long weightedSum(Job[] jobs, Comparator<Job> cmp) {
    long sum = 0;
    Arrays.sort(jobs, cmp);
    int C = 0; // Completion time of job
    for (Job j: jobs) {
      C += j.length;
      sum += j.weight * C;
    }
    return sum;
  }
  
  
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: java cdoan.stanfordalgo.greedy.WeightedSumOfCompletionTimes <input-jobs-file>");
      System.exit(1);
    }
    
    Scanner s = new Scanner(new File(args[0]));
    int n = s.nextInt();
    Job[] jobs = new Job[n];
    for (int i = 0; i < n; i++) {
      jobs[i] = new Job(s.nextInt(), s.nextInt());
    }
    s.close();

    Comparator<Job> cmp = (Job i, Job j) -> {
      int jDiff = j.weight - j.length;
      int iDiff = i.weight - i.length;
      if (jDiff != iDiff) {
        return jDiff - iDiff;
      }
      return j.weight - i.weight;
    };
    System.out.println("Sum of weighted completion times of decreasing weight - length schedule: " + weightedSum(jobs, cmp));

    cmp = (Job i, Job j) -> {
      double jRatio = (j.weight * 1.0) / j.length;
      double iRatio = (i.weight * 1.0) / i.length;
      return new Double(jRatio).compareTo(new Double(iRatio));
    };
    System.out.println("Sum of weighted completion times of decreasing weight/length schedule:   " + weightedSum(jobs, cmp));
  }
  
}
