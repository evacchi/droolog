package org.drools.droolog.meta.lib.v4;

import static org.drools.droolog.meta.lib.v4.TermState.isVar;

public class Unification {

    public static <T> Object[] values(Object[] left, Object[] right, int[] bindings) {
        Object[] values = new Object[bindings.length];
        for (int tidx = 0; tidx < bindings.length; tidx++) {
            int binding = bindings[tidx];
            if (binding < 0) {
                values[tidx] = left[-binding - 1];
            } else if (binding > 0) {
                values[tidx] = right[binding - 1];
            }
        }
        return values;
    }

    public static void of(int[] left, int[] right, int[] bindings) {
        if (left.length != right.length) {
            throw new IllegalArgumentException(String.format("length mismatch %d != %d", left.length, right.length));
        }
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
    }

    private static int groundRight(int i) {
        return i + 1;
    }

    private static int groundLeft(int i) {
        return -(i + 1);
    }

    public static void args(Object[] fields, int[] terms, Object[] vbindings) {
        for (int i = 0; i < fields.length; i++) {
            int t = terms[i];
            if (isVar(t)) {
                fields[i] = vbindings[t];
            }
        }
    }
}

