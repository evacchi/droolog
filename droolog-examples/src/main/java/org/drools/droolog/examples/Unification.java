package org.drools.droolog.examples;

import org.drools.droolog.meta.lib4.TermState;

public class Unification<T> {
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
//                right.setTerm(i, TermState.Ground);
                Object v = f.valueAt(leftStructure, i);
                groundTerms[i] = v;

            } else if (lt == TermState.Free && rt == TermState.Ground) {
//                left.setTerm(i, TermState.Ground);
                Object v = f.valueAt(rightStructure, i);
                groundTerms[i] = v;

//            } else if (lt instanceof Term.Structure && rt instanceof Term.Variable) {
//                copyCompound((Term.Structure) lt, right, i);
//            } else if (lt instanceof Term.Variable && rt instanceof Term.Structure) {
//                copyCompound((Term.Structure) rt, left, i);
            } else if (lt == TermState.Free && rt == TermState.Free) {
//                Term.Atom la = createAtom((Term.Variable) lt, left, i);
//                Term.Atom ra = createAtom((Term.Variable) rt, right, i);
//                // we set both "manually" to an arbitrary valid value
//                la.setValue(ra.getValue());
            } else {
                throw new UnsupportedOperationException();
            }
        }

         return f.of(groundTerms);
    }

}

