package org.drools.droolog.examples;

import org.drools.droolog.meta.lib4.TermState;

public interface Structure<T> {

    Meta<T> meta();

    interface Meta<T> {

        TermState getTermState(int index);

        void setTermState(int index, TermState value);

        Factory<T> structure();

        int size();
    }

    interface Factory<T> {
        Object valueAt(Object o, int index);

        T of(Object... terms);

    }
}
