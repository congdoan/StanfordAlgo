package cdoan.util;

import java.util.Random;

/**
 * Created by co on 15/06/2017.
 */
public class ArrayUtilInJava {

    public static void printArr(int[] a) {
        System.out.println(arr2Str(a));
    }

    private static String arr2Str(int[] a) {
        if (a == null || a.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        sb.append(a[0]);
        for (int i = 1; i < a.length; i++) {
            sb.append(", ").append(a[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    public static boolean isArraySorted(int[] a) {
        if (a == null || a.length <= 1) {
            return true;
        }
        return isArraySorted(a, true);
    }

    private static boolean isArraySorted(int[] a, boolean nondecreasing) {
        if (nondecreasing) {
            for (int i = 1; i < a.length; i++) {
                if (a[i - 1] > a[i])
                    return false;
            }
        } else {
            for (int i = 1; i < a.length; i++) {
                if (a[i - 1] < a[i])
                    return false;
            }
        }
        return true;
    }

    public static int[] randomArr() {
        Random random = new Random();
        int len = 15 + random.nextInt(16);
        int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            int val = random.nextInt(100);
            result[i] = random.nextInt(2) == 0 ? val : -val;
        }
        return result;
    }

}
