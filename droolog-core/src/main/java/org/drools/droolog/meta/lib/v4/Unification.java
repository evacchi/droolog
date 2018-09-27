package org.drools.droolog.meta.lib.v4;

import java.util.Arrays;

import static org.drools.droolog.meta.lib.v4.TermState.Free;
import static org.drools.droolog.meta.lib.v4.TermState.Ground;

public class Unification {

    public static boolean isVar(int i) {
        return i < 0;
    }

    public static boolean isStruct(int i) {
        return i > 1;
    }

    public static boolean isGround(int i) {
        return i == Ground;
    }

    public static int[] linearized(int[]... terms) {
        int len = 0;
//        for (int[] vs : vars)
        {
            for (int t : terms[0]) {
                len += sizeOf(t);
            }
        }
        int[] linearized = new int[len];
        Arrays.fill(linearized, Free);
        int j = 0;
        for (int t : terms[0]) {
            if (isStruct(t)) {
                j+=sizeOf(t);
            } else {
                linearized[j++] = t;
            }
        }
        return linearized;
    }

    private static int sizeOf(int t) {
        return isVar(t)? 1 : t;
    }

    public static int[] of(int[] left, int[] right) {
//        if (left.length != right.length) {
//            throw new IllegalArgumentException(String.format("length mismatch %d != %d", left.length, right.length));
//        }
        int maxVar = maxVar(left, right);
        int size = maxVar + freeVars(left, right);
        int[] bindings = new int[size];
        int freeCount = maxVar+1;
        for (int i = 0; i < left.length; i++) {
            int l = left[i];
            int r = right[i];
            if (l == Free) {
                l = -(++freeCount);
            }
            if (r == Free) {
                r = -(++freeCount);
            }
            if (isVar(l)) {
                if (isVar(r)) {
                    bindings[indexOf(l)] = TermState.Unknown;
                    bindings[indexOf(r)] = TermState.Unknown;
                } else {
                    bindings[indexOf(l)] = groundRight(i);
                }
            } else {
                if (isVar(r)) {
                    bindings[indexOf(r)] = groundLeft(i);
                } else {
                    if (l != r) { //includes case when they are both structures
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
        return bindings;
    }

    private static int indexOf(int i) {
        return Math.abs(i) - 1;
    }

    private static int groundLeft(int i) {
        return i + 1;
    }

    private static int groundRight(int i) {
        return -(i + 1);
    }

    private static int maxVar(int[] left, int[] right) {
        return -Math.min(Arrays.stream(left).filter(n -> n != Free).min().orElse(-1), Arrays.stream(right).filter(n -> n != Free).min().orElse(-1));
    }

    private static int freeVars(int[] left, int[] right) {
        return (int) (Arrays.stream(left).filter(n -> n == Free).count() + Arrays.stream(right).filter(n -> n == Free).count());
    }

}

