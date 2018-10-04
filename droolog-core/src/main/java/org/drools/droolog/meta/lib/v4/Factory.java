package org.drools.droolog.meta.lib.v4;

public interface Factory<T> {
    Object[] values(Object o);
    T create(Object... args);
}
