package org.drools.droolog.meta.lib.v4;

import java.util.Arrays;

public class Unification {

    public static boolean isVar(int i) {
        return i >= 0;
    }

    public static int[] linearized(int[][] vars) {
        int len = 0;
        for (int[] var : vars) {
            len += var.length;
        }
        int[] linearized = new int[len];
        for (int i = 0, j = 0; i < vars.length; i++) {
            for (int v : vars[i]) {
                linearized[j++] = v;
            }
        }
        return linearized;
    }

    public static int[] of(int[] left, int[] right) {
        if (left.length != right.length) {
            throw new IllegalArgumentException("mismatching length");
        }
        int[] bindings = new int[maxVar(left, right)];
        for (int i = 0; i < left.length; i++) {
            int l = left[i];
            int r = right[i];

            if (isVar(l)) {
                if (isVar(r)) {
                    bindings[l] = TermState.Unknown;
                    bindings[r] = TermState.Unknown;
                } else {
                    bindings[l] = groundRight(i);
                }
            } else {
                if (isVar(r)) {
                    bindings[r] = groundLeft(i);
                } else {
                    if (l != r) {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
        return bindings;
    }

    private static int groundLeft(int i) {
        return i + 1;
    }

    private static int groundRight(int i) {
        return -(i + 1);
    }

    private static int maxVar(int[] left, int[] right) {
        return Math.max(Arrays.stream(left).max().orElse(-1), Arrays.stream(right).max().orElse(-1)) + 1;
    }
}

