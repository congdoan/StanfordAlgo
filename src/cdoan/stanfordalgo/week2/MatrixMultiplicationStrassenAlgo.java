package cdoan.stanfordalgo.week2;

import java.util.Random;


public class MatrixMultiplicationStrassenAlgo {

  public static void main(String[] args) {
    int dimension = 32;
    if (args.length > 0) {
      try {
        dimension = Integer.parseInt(args[0]);
      } catch (NumberFormatException nfe) {}
    }
    if (!isPowerOf2(dimension)) {
      System.out.println("Usage: java MatrixMultiplicationStrassenAlgo <power-of-2-number>");
      System.exit(1);
    }
    long[][] X = randSqrMatrix(dimension);
    long[][] Y = randSqrMatrix(dimension);
    if (!matrixEqual(strassen(X, Y), cubicAlgo(X, Y))) {
      throw new AssertionError("Incorrect implementation!");
    }
  }
  
  private static boolean isPowerOf2(int n) {
    return (n & (n - 1)) == 0;
  }
  
  /**
   * Matrix Multiplycation Strassen Divide-and-Conquer O(~ n^2.808) running time.
   */
  public static long[][] strassen(long[][] X, long[][] Y) {
    if (X.length < 8) {
      return cubicAlgo(X, Y);
    }
    
    // Split matrix into 4 blocks
    final int blkSz = X.length / 2;
    long[][] A = new long[blkSz][blkSz];
    long[][] B = new long[blkSz][blkSz];
    long[][] C = new long[blkSz][blkSz];
    long[][] D = new long[blkSz][blkSz];
    populateBlocks(X, A, B, C, D);
    long[][] E = new long[blkSz][blkSz];
    long[][] F = new long[blkSz][blkSz];
    long[][] G = new long[blkSz][blkSz];
    long[][] H = new long[blkSz][blkSz];
    populateBlocks(Y, E, F, G, H);
    
    // Recursively compute 7 Strassen-chosen products
    long[][] P1 = strassen(A, subtract(F, H));
    long[][] P2 = strassen(add(A, B), H);
    long[][] P3 = strassen(add(C, D), E);
    long[][] P4 = strassen(D, subtract(G, E));
    long[][] P5 = strassen(add(A, D), add(E, H));
    long[][] P6 = strassen(subtract(B, D), add(G, H));
    long[][] P7 = strassen(subtract(A, C), add(E, F));
    
    // Compute 4 blocks of X*Y out of 7 products computed above
    long[][] Z1 = add(subtract(add(P5, P4), P2), P6);
    long[][] Z2 = add(P1, P2);
    long[][] Z3 = add(P3, P4);
    long[][] Z4 = subtract(add(P1, P5), add(P3, P7));
    // Combine 4 blocks to get X*Y
    return combineBlocks(Z1, Z2, Z3, Z4);
  }
  
  /* M11, M12, M21, M22 are top-left, top-right, bottom-left, bottom-right blocks respectively */
  private static void populateBlocks(long[][] M, long[][] M11, long[][] M12, long[][] M21, long[][] M22) {
    final int blkSz = M11.length;
    for (int i = 0; i < blkSz; i++) {
      System.arraycopy(M[i], 0, M11[i], 0, blkSz);
      System.arraycopy(M[i], blkSz, M12[i], 0, blkSz);
      System.arraycopy(M[blkSz + i], 0, M21[i], 0, blkSz);
      System.arraycopy(M[blkSz + i], blkSz, M22[i], 0, blkSz);
    }
  }
  
  /* M11, M12, M21, M22 are top-left, top-right, bottom-left, bottom-right blocks respectively */
  private static long[][] combineBlocks(long[][] M11, long[][] M12, long[][] M21, long[][] M22) {
    final int blkSz = M11.length;
    long[][] M = new long[2 * blkSz][2 * blkSz];
    for (int i = 0; i < blkSz; i++) {
      System.arraycopy(M11[i], 0, M[i], 0, blkSz);
      System.arraycopy(M12[i], 0, M[i], blkSz, blkSz);
      System.arraycopy(M21[i], 0, M[blkSz + i], 0, blkSz);
      System.arraycopy(M22[i], 0, M[blkSz + i], blkSz, blkSz);
    }
    return M;
  }
  
  /* Helper method to compute addition of 2 matrices: X + Y */
  private static long[][] add(long[][] X, long[][] Y) {
    final int n = X.length;
    long[][] res = new long[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        res[i][j] = X[i][j] + Y[i][j];
      }
    }
    return res;
  }
  
  /* Helper method to compute subtraction of 2 matrices: X - Y */
  private static long[][] subtract(long[][] X, long[][] Y) {
    final int n = X.length;
    long[][] res = new long[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        res[i][j] = X[i][j] - Y[i][j];
      }
    }
    return res;
  }
  
  /* Brute-force O(n^3) running time */
  private static long[][] cubicAlgo(long[][] X, long[][] Y) {
    final int n = X.length;
    long[][] Z = new long[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        for (int k = 0; k < n; k++) {
          Z[i][j] += X[i][k] * Y[k][j];
        }
      }
    }
    return Z;
  }

  /* Utility method to generate a square matrix of random integers */
  private static long[][] randSqrMatrix(int n) {
    long[][] M = new long[n][n];
    Random rand = new Random();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        M[i][j] = rand.nextInt();
      }
    }
    return M;
  }
  
  /* Helper method for testing if X == Y */
  private static boolean matrixEqual(long[][] X, long[][] Y) {
    final int n = X.length;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (X[i][j] != Y[i][j]) {
          return false;
        }
      }
    }
    return true;
  }

}
