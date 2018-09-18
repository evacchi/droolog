package org.drools.droolog.meta.lib4;


public class Unification<T extends Structure<T>> {

    public static <T extends Structure<T>> Unification<T> of(Structure<T> leftStructure, Structure<T> rightStructure) {
        return new Unification<>(leftStructure, rightStructure);
    }

    private T result;
    Unification(Structure<T> leftStructure, Structure<T> rightStructure) {
        result = unify(leftStructure, rightStructure);
    }

    public T structure() {
        return result;
    }

    private static <T extends Structure<T>> T unify(Structure<T> leftStructure, Structure<T> rightStructure) {
        Structure.Meta<T> left = leftStructure.meta(), right = rightStructure.meta();
        if (left.size() != right.size()) {
            throw new IllegalArgumentException();
        }
        Structure.Factory<T> f = left.structure();

        Object[] groundTerms = new Object[right.size()];
        for (int i = 0; i < right.size(); i++) {
            TermState lt = left.getTermState(i), rt = right.getTermState(i);
            if (lt.isGround() && rt.isGround()) {
                if (!lt.equals(rt)) throw new IllegalArgumentException();
            } else if (lt.isGround() && rt.isVariable()) {
                groundTerms[i] = f.valueAt(leftStructure, i);
            } else if (lt.isVariable() && rt.isGround()) {
                groundTerms[i] = f.valueAt(rightStructure, i);
            } else if (lt.isVariable() && rt.isStructure()) {
                Structure s = (Structure) f.valueAt(rightStructure, i);
                Structure v = s.meta().structure().variable();
                groundTerms[i] = unify(s, v);
            } else if (lt.isStructure() && rt.isStructure()) {
                groundTerms[i] = unify((Structure) f.valueAt(leftStructure, i), (Structure) f.valueAt(rightStructure, i));
            } else if (lt.isVariable() && rt.isVariable()) {
                /// todo
            } else {
                throw new UnsupportedOperationException();
            }
        }

         return f.of(groundTerms);
    }

}

