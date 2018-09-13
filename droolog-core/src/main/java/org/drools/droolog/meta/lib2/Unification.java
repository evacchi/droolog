package org.drools.droolog.meta.lib2;

public class Unification {

    public <T> Term<T> unify(Term<T> left, Term<T> right) {
        if (left instanceof Term.Variable) {
            return unifyVariable((Term.Variable<T>) left, right);
        } else if (right instanceof Term.Variable) {
            return unifyVariable((Term.Variable<T>) right, left);
        } else if (left instanceof Term.Structure && right instanceof Term.Structure) {
            return unify((Term.Structure<T>) left, (Term.Structure<T>) right);
        } else if (left instanceof Term.Atom && right instanceof Term.Atom) {
            if (left == right) return left; else throw new IllegalArgumentException();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public <T> Term<T> unifyVariable(Term.Variable<T> left, Term<T> right) {
        if (right instanceof Term.Structure) {
            Term.Structure<T> s = (Term.Structure<T>) right;
            return unify(new Term.Structure<>(s.size()), s);
        } else if (right instanceof Term.Atom) {
            return right;
        } else if (right instanceof Term.Variable) {
            return new Term.Atom<T>(null);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public <T> Term.Structure<T> unify(Term.Structure<T> left, Term.Structure<T> right) {
        if (left.size() != right.size()) {
            throw new IllegalArgumentException();
        }

        Term<?>[] terms = new Term<?>[left.size()];

        for (int i = 0; i < left.size(); i++) {
            Term<Object> lt = (Term<Object>) left.term(i), rt = (Term<Object>) right.term(i);
            terms[i] = unify(lt, rt);
        }

        return new Term.Structure<>(terms);
    }

}

