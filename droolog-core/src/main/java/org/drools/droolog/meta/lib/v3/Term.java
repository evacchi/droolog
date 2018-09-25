package org.drools.droolog.meta.lib.v3;

public interface Term<T> {

    static <T> Term<T> of(T value, Type type) {
        switch (type) {
            case Atom:
                return new Atom<>(value);
            case Variable:
                return new Variable<>();
            case Structure:
                return (Term<T>) value;
            default:
                throw new IllegalArgumentException();
        }
    }

    T value();

    Type type();

    final class Atom<T> implements Term<T> {

        private final T value;

        public Atom(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }

        public Type type() { return Type.Atom; }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Atom && this.value().equals(((Atom) obj).value());
        }

        @Override
        public String toString() {
            return String.format("Atom(%s)", value);
        }
    }

    class Variable<T> implements Term<T> {
        public T value() {
            return null;
        }

        public Type type() { return Type.Variable; }

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


    interface Structure<T> extends Term<T> {
        default Type type() { return Type.Structure; }
        Term<?>[] terms();
        default Term<?> term(int i) { return terms()[i]; }
        int size();
    }

    enum Type {
        Atom, Variable, Structure, Unknown;
        public boolean isAtom() {
            return this == Atom;
        }
        static void requireAtom(Type t) {
            if (!t.isAtom()) throw new AssertionError("term is not atom");
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
