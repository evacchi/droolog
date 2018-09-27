package org.drools.droolog.meta.lib.v4;

public class TermState {

    public final static int Ground = Integer.MIN_VALUE;
    public static final int Unknown = Integer.MAX_VALUE;
    private int nextVar = 0;

    public int newVar() {
        return nextVar++;
    }

    public static int Structure(int size) {
        return size;
    }

    public static boolean isVar(int i) {
        return i >= 0;
    }

    public static boolean isStruct(int i) {
        return i < -1;
    }

    public static boolean isGround(int i) {
        return i == Ground;
    }


}
