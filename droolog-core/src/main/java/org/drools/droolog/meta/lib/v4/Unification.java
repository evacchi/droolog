package org.drools.droolog.meta.lib.v4;

import java.util.Arrays;

import static org.drools.droolog.meta.lib.v4.TermState.isVar;

public class Unification {

    public static int[] linearized(int[]... terms) {
        int len = 0;
        for (int[] ts : terms) {
            len += ts.length;
        }
        int[] linearized = new int[len];

        int j = 0;
        for (int[] ts : terms) {
            for (int t : ts) {
                linearized[j++] = t;
            }
        }
        return linearized;
    }

    private static int sizeOf(int t) {
        return isVar(t) ? 1 : t;
    }

    public static int[] of(int[] left, int[] right) {
        if (left.length != right.length) {
            throw new IllegalArgumentException(String.format("length mismatch %d != %d", left.length, right.length));
        }
        int size = 1+maxVar(left, right);
        int[] bindings = new int[size];
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
                    if (l != r) { //includes case when they are both structures
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
        return Math.max(Arrays.stream(left).max().orElse(-1), Arrays.stream(right).max().orElse(-1));
    }
}

