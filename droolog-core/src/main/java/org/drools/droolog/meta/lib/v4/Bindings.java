package org.drools.droolog.meta.lib.v4;

import java.util.Arrays;

public class Bindings {

    private final int[] values;
    private final Side[] sides;

    Bindings(int variableCount) {
        this.values = new int[variableCount];
        Arrays.fill(values, -1);
        this.sides = new Side[variableCount];
    }

    <T> void put(int var, int value, Side side) {
        int e = values[var];
        if (e == -1) {
            values[var] = value;
            sides[var] = side;
        } else {
            throw new IllegalArgumentException(String.valueOf(value));
        }
    }

    public int get(int var) {
        return values[var];
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }


    enum Side {
        Left, Right;
    }

}
