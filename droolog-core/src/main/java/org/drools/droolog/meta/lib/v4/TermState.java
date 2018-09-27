package org.drools.droolog.meta.lib.v4;

public class TermState {

    public final static int Ground = 1;
    public static final int Unknown = 0;
    public static final int Free = Integer.MIN_VALUE;
    private int nextVar = 0;

    public int newVar() {
        return --nextVar;
    }

    public static int Structure(int size) {
        return size;
    }
}
