package org.drools.droolog.meta.lib.v2;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.drools.droolog.meta.lib.UnsafeAccess;
import sun.misc.Unsafe;

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

    class Structure<T> implements Term<T> {

//        private static final long arrayFieldOffset;

        private static final int base;

        private static final int scale;
        private static final Unsafe unsafe;

        private static final int shift;

        static {
            unsafe = UnsafeAccess.UNSAFE;
            base = unsafe.arrayBaseOffset(Object[].class);
            scale = unsafe.arrayIndexScale(Object[].class);
            shift = 31 - Integer.numberOfLeadingZeros(scale);
        }

        public final Term<?>[] terms;

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

        public Term<?> termRaw(int i) {
            return getRaw(byteOffset(i));
        }

        private static long byteOffset(int i) {
            return ((long) i << shift) + base;
        }

        private Term<?> getRaw(long offset) {
            return (Term<?>) unsafe.getObjectVolatile(terms, offset);
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
