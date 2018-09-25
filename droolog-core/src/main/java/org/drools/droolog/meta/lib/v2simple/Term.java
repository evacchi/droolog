package org.drools.droolog.meta.lib.v2simple;

import java.util.Arrays;

public interface Term<T> {

    final class Atom<T> implements Term<T> {

        private final T value;

        public Atom(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("Atom(%s)", value);
        }
    }

    class Variable<T> implements Term<T> {

        @Override
        public String toString() {
            return "V@" + Integer.toHexString(hashCode());
        }
    }

    class Underscore<T> extends Variable<T> {
        @Override
        public String toString() {
            return "_G@" + Integer.toHexString(hashCode());
        }
    }

    final class Structure<T> implements Term<T> {

        private final Term<?>[] terms;

        public Structure(int n) {
            this.terms = new Term<?>[n];
            for (int i = 0; i < n; i++) {
                terms[i] = new Underscore<>();
            }
        }

        public Structure(Term<?>[] terms) {
            this.terms = terms;
        }

        public Term<?>[] terms() {
            return terms;
        }

        public Term<?> term(int i) {
            return terms[i];
        }

        public int size() {
            return terms.length;
        }

        @Override
        public String toString() {
            return "Structure" + Arrays.toString(terms);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Structure &&
                    Arrays.deepEquals(terms, ((Structure) obj).terms());
        }
    }

    public static <T> Term.Atom<T> atom(T value) {
        return new Term.Atom<>(value);
    }
    public static <T> Term.Variable<T> variable() {
        return new Term.Variable<>();
    }
    public static <T> Term.Variable<T> $() {
        return new Term.Underscore<>();
    }

}
