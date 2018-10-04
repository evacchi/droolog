package org.drools.droolog.meta.lib.v4;

public interface Meta<T> {
    int[] terms();
    Factory<T> factory();
}
