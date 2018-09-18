package org.drools.droolog.meta.lib4;

public interface Structure<T extends Structure<T>> {

    Meta<T> meta();

    interface Meta<T extends Structure<T>> {

        TermState getTermState(int index);

        void setTermState(int index, TermState value);

        Factory<T> structure();

        int size();
    }

    interface Factory<T extends Structure<T>> {
        Object valueAt(Object o, int index);

        T of(Object... terms);

        T variable();

    }
}
