package org.drools.droolog.meta.lib2;

import java.util.stream.Stream;

public class Unification<T> {

    private final Term.Structure<T> term;
    private final Bindings bindings;

    public static <T> Unification<T> of(Term.Structure<T> left, Term.Structure<T> right) {
        return new Unification<>(left, right);
    }

    Unification(Term.Structure<T> left, Term.Structure<T> right) {
        // let's assume the two sets are disjoint for now
        int variableCount = countVariables(left) + countVariables(right);
        this.bindings = new Bindings(variableCount);
        this.term = unify(left, right);
    }

    public Term.Structure<T> term() {
        return term;
    }

    public Bindings bindings() {
        return bindings;
    }

    private <T> int countVariables(Term<T> t) {
        if (t instanceof Term.Atom) {
            return 0;
        } else if (t instanceof Term.Variable && !(t instanceof Term.Underscore)) {
            return 1;
        } else if (t instanceof Term.Structure) {
            return Stream.of(((Term.Structure<T>) t).terms()).mapToInt(this::countVariables).sum();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private <T> Term<T> unify(Term<T> left, Term<T> right) {
        if (left instanceof Term.Variable) {
            return unifyVariable((Term.Variable<T>) left, right);
        } else if (right instanceof Term.Variable) {
            return unifyVariable((Term.Variable<T>) right, left);
        } else if (left instanceof Term.Structure && right instanceof Term.Structure) {
            return unify((Term.Structure<T>) left, (Term.Structure<T>) right);
        } else if (left instanceof Term.Atom && right instanceof Term.Atom) {
            if (left == right) {
                return left;
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private <T> Term<T> unifyVariable(Term.Variable<T> left, Term<T> right) {
        if (right instanceof Term.Structure) {
            Term.Structure<T> s = (Term.Structure<T>) right;
            Term.Structure<T> t = unify(new Term.Structure<>(s.size()), s);
            bindings.put(left, t);
            return t;
        } else if (right instanceof Term.Atom) {
            bindings.put(left, right);
            return right;
        } else if (right instanceof Term.Variable) {
            Term.Atom<T> atom = new Term.Atom<>(null);
            bindings.put(left, atom);
            return atom;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private <T> Term.Structure<T> unify(Term.Structure<T> left, Term.Structure<T> right) {
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

