import java.util.Random;


public class Trie {

    private static final int R = 26;
    private static final char FIRST_LETTER = 'A';


    public static class Node {
        private boolean endOfWord;
        private Node[] children = new Node[R];

        public boolean endOfWord() {
            return endOfWord;
        }

        private Node childAt(char c) {
            int idx = c - FIRST_LETTER;
            return children[idx];
        }
        private void setChildAt(char c, Node x) {
            int idx = c - FIRST_LETTER;
            children[idx] = x;
        }
    }


    private Node root;
    private int nodeCount;


    public Trie(String[] words) {
        this();
        if (words == null) {
            return;
        }
        for (String word : words) {
            this.add(word);
        }
    }

    public Trie() {}

    public void add(String key) {
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) {
            x = new Node();
            nodeCount++;
        }
        if (d == key.length()) {
            x.endOfWord = true;
            return x;
        }
        char c = key.charAt(d);
        Node child = x.childAt(c);
        child = add(child, key, d + 1);
        x.setChildAt(c, child);
        return x;
    }

    public boolean contains(String key) {
        return contains(root, key, 0);
    }

    private boolean contains(Node x, String key, int d) {
        if (x == null) {
            return false;
        }
        if (d == key.length()) {
            return x.endOfWord;
        }
        char c = key.charAt(d);
        return contains(x.childAt(c), key, d + 1);
    }

    public boolean[] hasWordWithPrefix(String prefix)  {
        return hasWordWithPrefix(root, prefix, 0);
    }

    private boolean[] hasWordWithPrefix(Node x, String prefix, int d) {
        if (x == null) {
            return new boolean[] { false, false };
        }
        if (d == prefix.length()) {
            return new boolean[] { true, x.endOfWord };
        }
        char c = prefix.charAt(d);
        return hasWordWithPrefix(x.childAt(c), prefix, d + 1);
    }

    public Node childNodeOfRootAt(String letter) {
        return childNodeOfParentAt(root, letter);
    }

    public Node childNodeOfParentAt(Node parent, String letter) {
        char c = letter.charAt(0);
        Node childNode = parent.childAt(c);
        if (c != 'Q') {
            return childNode;
        }
        return childNode == null ? null : childNode.childAt('U');
    }

    private int getNodeCount() {
        return nodeCount;
    }

    private void removeAllNodes() {
        root = null;
        nodeCount = 0;
    }


    public static void main(String[] args) {
        String[] randomOrderWords = { 
                                        "rebuke", "regimen", "regulation", "reprimand", "reprove", "routine",
                                        "selfcommand", "selfgoverment", "selfretraint", "strictness",
                                        "obedience", "orderness",
                                        "penalize", "punish",
                                        "direction", "discipline",
                                        "instruction",
                                        "training",
                                        "control"
                                    };
        Trie t = new Trie(randomOrderWords);


        /* Test 'boolean hasWordWithPrefix(String prefix)' */
        String[] testPrefixs = { "rep", "self", "dis",
                                 "repk", "selfh", "disx" };
        boolean[] result = new boolean[testPrefixs.length];
        for (int i = 0; i < testPrefixs.length; i++) {
            result[i] = t.hasWordWithPrefix(testPrefixs[i])[0];
        }
        boolean[] expected = { true, true, true,
                               false, false, false };
        System.out.printf("Is the result equal to the expected one? => %s%n", twoArraysEqual(result, expected));


        /* Test 'boolean contains(String key)' method */
        /*
        String[] testKeys = { "discipline", "routine", "control",
                              "selfcontrol", "selfdiscipline", "focus" };
        boolean[] testKeyMemberships = new boolean[testKeys.length];
        for (int i = 0; i < testKeys.length; i++) {
            testKeyMemberships[i] = t.contains(testKeys[i]);
        }
        boolean[] expected = { true, true, true,
                               false, false, false };
        System.out.printf("Is the actual result equal to the expected one? => %s%n", twoArraysEqual(testKeyMemberships, expected));
        */


        /* Examine number of nodes */
        /*
        System.out.println("0) Number of nodes: " + t.getNodeCount());
        for (int i = 1; i <= 26; i++) {
            t.removeAllNodes();
            shuffleArray(randomOrderWords);
            StringBuilder sb = new StringBuilder();
            for (String w : randomOrderWords) {
                t.add(w);
                sb.append(w + ", ");
            }
            int len = sb.length();
            sb.delete(len-2, len);
            System.out.printf("Order of Insert:%n%s%n", sb.toString());
            System.out.printf("%d) Number of Nodes: %d%n", i, t.getNodeCount());
        }
        */
    }

    private static boolean twoArraysEqual(boolean[] a, boolean[] b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null || a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    private static void shuffleArray(String[] a) {
        int startOfUnshuffle = 1;
        Random r = new Random();
        for (; startOfUnshuffle < a.length; startOfUnshuffle++) {
            int randomIndex = r.nextInt(startOfUnshuffle + 1);
            String tmp = a[randomIndex];
            a[randomIndex] = a[startOfUnshuffle];
            a[startOfUnshuffle] = tmp;
        }
    }

}
