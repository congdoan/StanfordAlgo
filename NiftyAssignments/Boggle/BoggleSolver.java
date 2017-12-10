
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class BoggleSolver {

    private Trie trie;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trie = new Trie(dictionary);
    }
    
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        /* Prefix-match-based approach */
        // Enumerate only paths in board whose corresponding string is the prefix of at least a valid word in the dictionary.
        // This means we prune off a path as soon as finding no dictionary words have the corresponding string of that path as their prefix.
        // Then collect only paths that correspond to valid words in dictionary.
        HashSet<String> allValidWords = new HashSet<>();
        int m = board.rows();
        int n = board.cols();
        // List of adjacent grid cells for each grid cell
        ArrayList<Integer>[][] adjacentCellIndexLists = adjacentCellIndexLists(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                collectValidWords(board, i, j, adjacentCellIndexLists, allValidWords);
            }
        }
        return allValidWords;
    }

    /* Collect all paths that start from cell (i, j) and each forms a valid word in dictionary */
    private void collectValidWords(BoggleBoard grid, int startRow, int startCol, ArrayList<Integer>[][] adjacentCellIndexLists, HashSet<String> accumulatedWords)  {
        char c = grid.getLetter(startRow, startCol);
        String letter = c != 'Q' ? "" + c : "QU";
        Trie.Node firsttNode = trie.childNodeOfRootAt(letter);
        if (firsttNode == null) {
            return;
        }

        Stack<Trie.Node> nodeStack = new Stack<>();
        nodeStack.push(firsttNode);

        int m = grid.rows();
        int n = grid.cols();

        // Array to store the access-next index in the list of adjacent grid cells for each grid cell
        int[][] nextIndices = new int[m][n];

        /* Traverse board graph nonrecursively using stack */
        Stack<Integer> iStack = new Stack<>();
        Stack<Integer> jStack = new Stack<>();
        boolean[][] visited = new boolean[m][n];
        StringBuilder currentPath = new StringBuilder();
        iStack.push(startRow);
        jStack.push(startCol);
        visited[startRow][startCol] = true;
        currentPath.append(letter);
        if (firsttNode.endOfWord()) {
            accumulatedWords.add(currentPath.toString());
        }
        ArrayList<Integer> adjacentCellIndexList;
        int i;
        int j;
        while (!iStack.isEmpty()) {
            i = iStack.peek();
            j = jStack.peek();
            boolean hasNextAdjacentCellUnvisited = false;
            adjacentCellIndexList = adjacentCellIndexLists[i][j];
            for ( ; nextIndices[i][j] < adjacentCellIndexList.size(); nextIndices[i][j]++) {
                int adjacentCellIndex = adjacentCellIndexList.get(nextIndices[i][j]);
                int adjRow = adjacentCellIndex / n;
                int adjCol = adjacentCellIndex % n;
                if (!visited[adjRow][adjCol]) {
                    // Idea: we also return whether or not this prefix is actually a valid word in dictionary
                    c = grid.getLetter(adjRow, adjCol);
                    letter = c != 'Q' ? "" + c : "QU";
                    Trie.Node nextNode = trie.childNodeOfParentAt(nodeStack.peek(), letter);
                    if (nextNode != null) {
                        hasNextAdjacentCellUnvisited = true;
                        nextIndices[i][j]++;

                        nodeStack.push(nextNode);
                        iStack.push(adjRow);
                        jStack.push(adjCol);
                        visited[adjRow][adjCol] = true;
                        currentPath.append(letter);
                        if (nextNode.endOfWord()) {
                            accumulatedWords.add(currentPath.toString());
                        }

                        break;
                    }
                }
            }
            if (!hasNextAdjacentCellUnvisited) {
                // Done processing cell (i, j), backtracking to its predecessor cell
                nodeStack.pop();
                iStack.pop();
                jStack.pop();
                nextIndices[i][j] = 0;
                visited[i][j] = false;
                if (grid.getLetter(i, j) != 'Q') {
                    currentPath.deleteCharAt(currentPath.length()-1);
                } else {
                    currentPath.delete(currentPath.length()-2, currentPath.length());
                }
            }
        }
    }

    private ArrayList<Integer>[][] adjacentCellIndexLists(int m, int n)  {
        ArrayList<Integer>[][] adjacentCellIndexLists = (ArrayList<Integer>[][]) new ArrayList[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                ArrayList<Integer> adjacentCellIndexList = new ArrayList<>();
                // right
                if (j+1 < n) {
                    int cellIndex = i*n + j+1;
                    adjacentCellIndexList.add(cellIndex);
                }
                // right-down
                if (j+1 < n && i+1 < m) {
                    int cellIndex = (i+1)*n + j+1;
                    adjacentCellIndexList.add(cellIndex);
                }
                // down
                if (i+1 < m) {
                    int cellIndex = (i+1)*n + j;
                    adjacentCellIndexList.add(cellIndex);
                }
                // down-left
                if (i+1 < m && j > 0) {
                    int cellIndex = (i+1)*n + j-1;
                    adjacentCellIndexList.add(cellIndex);
                }
                // left
                if (j > 0) {
                    int cellIndex = i*n + j-1;
                    adjacentCellIndexList.add(cellIndex);
                }
                // left-up
                if (j > 0 && i > 0) {
                    int cellIndex = (i-1)*n + j-1;
                    adjacentCellIndexList.add(cellIndex);
                }
                // up
                if (i > 0) {
                    int cellIndex = (i-1)*n + j;
                    adjacentCellIndexList.add(cellIndex);
                }
                // up-right
                if (i > 0 && j+1 < n) {
                    int cellIndex = (i-1)*n + j+1;
                    adjacentCellIndexList.add(cellIndex);
                }
                adjacentCellIndexLists[i][j] = adjacentCellIndexList;
            }
        }
        return adjacentCellIndexLists;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("word must be non-null");
        }

        if (!trie.contains(word)) {
            return 0;
        }

        int score;
        switch (word.length()) {
            case 0:
            case 1:
            case 2:
                score = 0;
                break;
            case 3:
            case 4:
                score = 1;
                break;
            case 5:
                score = 2;
                break;
            case 6:
                score = 3;
                break;
            case 7:
                score = 5;
                break;
            default:
                score = 11;
                break;
        }
        return score;
    }


    /**
     * Unit tests the BoggleSolver class.
     */
    public static void main(String[] args) {
        String[] filenameSuffixs = { "yawl.txt", "zingarelli2005.txt" };
        long boardScore = 0, maxBoardScore = 0, minBoardScore = Long.MAX_VALUE, totalBoardScores = 0;
        for (String suffix : filenameSuffixs) {
            long start = System.currentTimeMillis();
            String[] dictionary = readFromFile("boggle-testing/dictionary-" + suffix);
            BoggleSolver bs = new BoggleSolver(dictionary);
            StdOut.println("Time to read dictionary file and build trie: " + (System.currentTimeMillis() - start) + " milliseconds");
            StdOut.println("Dictionary File : " + suffix);
            StdOut.println("Dictionary Words: " + dictionary.length);

            /* Mesure time */
            final int numTests = 1000;
            int numWrongTimes = 0;
            final int m = 5, n = 5;
            long time = 0;
            for (int i = 1; i <= numTests; i++) {
                BoggleBoard board = new BoggleBoard(m, n);

                start = System.currentTimeMillis();
                Iterable<String> allValidWords = bs.getAllValidWords(board);
                time += (System.currentTimeMillis() - start);

                // Add up word scores
                for (String w : allValidWords) {
                    boardScore += bs.scoreOf(w);
                }
                maxBoardScore = Math.max(maxBoardScore, boardScore);
                minBoardScore = Math.min(minBoardScore, boardScore);
                totalBoardScores += boardScore;
                boardScore = 0;
            }
            StdOut.printf("Time to solve %d random letter-frequency %dx%d boards using optimized prefix query  : %d milliseconds%n", numTests, m, n, time);
            StdOut.printf("Max board score = %d; Min board score = %d; Avg board score = %f; %n", maxBoardScore, minBoardScore, (totalBoardScores * 1.0 / numTests));

            StdOut.println("\n***********************************************************************************\n");
        }
    }

    private static String[] readFromFile(String filename) {
        In in = new In(filename);
        return in.readAllLines();
    }

}
