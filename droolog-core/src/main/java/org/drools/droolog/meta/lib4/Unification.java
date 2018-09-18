package org.drools.droolog.meta.lib4;

import org.drools.droolog.meta.lib4.Structure;
import org.drools.droolog.meta.lib4.TermState;

public class Unification<T> {

    public static <T> Unification<T> of(Structure<T> leftStructure, Structure<T> rightStructure) {
        return new Unification<>(leftStructure, rightStructure);
    }

    private T result;
    Unification(Structure<T> leftStructure, Structure<T> rightStructure) {
        result = unify(leftStructure, rightStructure);
    }

    public T structure() {
        return result;
    }

    private static <T> T unify(Structure<T> leftStructure, Structure<T> rightStructure) {
        Structure.Meta<T> left = leftStructure.meta(), right = rightStructure.meta();
        if (left.size() != right.size()) {
            throw new IllegalArgumentException();
        }
        Structure.Factory<T> f = left.structure();

        Object[] groundTerms = new Object[right.size()];
        for (int i = 0; i < right.size(); i++) {
            TermState lt = left.getTermState(i), rt = right.getTermState(i);
            if (lt == TermState.Ground && rt == TermState.Ground) {
                if (!lt.equals(rt)) throw new IllegalArgumentException();
            } else if (lt == TermState.Ground && rt == TermState.Free) {
                Object v = f.valueAt(leftStructure, i);
                groundTerms[i] = v;

            } else if (lt == TermState.Free && rt == TermState.Ground) {
                Object v = f.valueAt(rightStructure, i);
                groundTerms[i] = v;
            } else if (lt == TermState.Free && rt == TermState.Free) {

                /// todo also recursive
            } else {
                throw new UnsupportedOperationException();
            }
        }

         return f.of(groundTerms);
    }

}

