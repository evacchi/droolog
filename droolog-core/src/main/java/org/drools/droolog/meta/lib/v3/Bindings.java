package org.drools.droolog.meta.lib.v3;

import java.util.Arrays;

public class Bindings {

    private final Entry<?>[] bindings;
    private int len = 0;

    Bindings(int variableCount) {
        this.bindings = new Entry<?>[variableCount];
    }

    <T> void put(Term.Variable<T> left, Term<T> t) {
        if (left instanceof Term.Underscore) {
            return;
        }
        Entry<T> e = (Entry<T>) entry(left);
        if (e == null) {
            this.bindings[len] = new Entry<>(left).term(t);
            len++;
        } else {
            e.term(t);
        }
    }

    public Entry<?> entry(Term.Variable<?> v) {
        for (int i = 0; i < len; i++) {
            Entry<?> e = bindings[i];
            if (e.variable == v) {
                return e;
            }
        }
        return null;
    }


    public Term<?> get(Term.Variable<?> v) {
        Entry<?> e = entry(v);
        if (e == null) return null;
        else return e.term;
    }

    @Override
    public String toString() {
        return Arrays.toString(bindings);
    }

    public static class Entry<U> {

        private final Term.Variable<U> variable;
        private Term<U> term;

        public Entry(Term.Variable<U> variable) {
            this.variable = variable;
        }

        Entry<U> term(Term<U> t) {
            this.term = t;
            return this;
        }

        @Override
        public String toString() {
            return String.format("%s -> %s", variable, term);
        }
    }
}
