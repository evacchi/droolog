package org.drools.droolog.meta.lib.v4;

public class ArrayOps {
    public static int[] concat(int[]... terms) {
        int len = 0;
        for (int[] ts : terms) {
            len += ts.length;
        }
        int[] linearized = new int[len];

        int k = 0;
        for (int i = 0; i < terms.length; i++) {
            int[] ts = terms[i];
            for (int j = 0; j < ts.length; j++) {
                int t = ts[j];
                linearized[k++] = t;
            }
        }
        return linearized;
    }

    public static Object[] concat(Object[]... terms) {
        int len = 0;
        for (Object[] ts : terms) {
            len += ts.length;
        }
        Object[] linearized = new Object[len];

        int k = 0;
        for (int i = 0; i < terms.length; i++) {
            Object[] ts = terms[i];
            for (int j = 0; j < ts.length; j++) {
                Object t = ts[j];
                linearized[k++] = t;
            }
        }
        return linearized;
    }

    public static int[] of(int... v) {
        return v;
    }

    public static Object[] of(Object... v) {
        return v;
    }


}
