package org.drools.droolog.meta.lib.v4;

public class TermState {

    public static final int Structure = -2;
    public final static int Ground = -1;
    public static final int Unknown = 0;
    private int nextVar = 0;

    public int newVar() {
        return nextVar++;
    }
}
