package org.drools.droolog.meta.lib2;

public class Unification {

    public <T> Term.Structure<T> unify(Term.Structure<T> left, Term.Structure<T> right) {
        if (left.size() != right.size()) {
            throw new IllegalArgumentException();
        }

        Term<?>[] terms = new Term<?>[left.size()];

        for (int i = 0; i < left.size(); i++) {
            Term lt = left.term(i), rt = right.term(i);
            if (lt instanceof Term.Atom && rt instanceof Term.Atom) {
                if (lt != rt) throw new IllegalArgumentException();
                else terms[i] = lt;
            } else if (lt instanceof Term.Atom && rt instanceof Term.Variable) {
                terms[i] = lt;
            } else if (lt instanceof Term.Variable && rt instanceof Term.Atom) {
                terms[i] = rt;
            } else if (lt instanceof Term.Structure && rt instanceof Term.Variable) {
                throw new UnsupportedOperationException();
                //copyCompound((Term.Structure) lt, right, i);
            } else if (lt instanceof Term.Variable && rt instanceof Term.Structure) {
                throw new UnsupportedOperationException();
                //copyCompound((Term.Structure) rt, left, i);
            } else if (lt instanceof Term.Variable && rt instanceof Term.Variable) {
                terms[i] = new Term.Atom<Object>(null);
                // we set both "manually" to an arbitrary valid value
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return new Term.Structure<>(terms);
    }

}

