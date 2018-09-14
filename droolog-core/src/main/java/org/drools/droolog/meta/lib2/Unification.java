package org.drools.droolog.meta.lib2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Unification<T> {

    private final Term<T> term;
    private final List<Binding<?>> bindings;

    public Unification(Term<T> left, Term<T> right) {
        // let's assume the two sets are disjoint for now
        int variableCount = countVariables(left) + countVariables(right);

        this.bindings = new ArrayList<>(variableCount);
        this.term = unify(left, right);
    }

    public Term<T> term() {
        return term;
    }

    public List<Binding<?>> bindings() {
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
            addBinding(left, t);
            return t;
        } else if (right instanceof Term.Atom) {
            addBinding(left, right);
            return right;
        } else if (right instanceof Term.Variable) {
            Term.Atom<T> atom = new Term.Atom<>(null);
            addBinding(left, atom);
            return atom;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private <T> void addBinding(Term.Variable<T> left, Term<T> t) {
        if (left instanceof Term.Underscore) return;
        this.bindings.add(new Binding<>(left, t));
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

    public static class Binding<U> {

        private final Term.Variable<U> variable;
        private final Term<U> term;

        public Binding(Term.Variable<U> variable, Term<U> term) {
            this.variable = variable;
            this.term = term;
        }

        public Term.Variable<U> variable() {
            return variable;
        }

        public Term<U> term() {
            return term;
        }

        @Override
        public String toString() {
            return String.format("%s -> %s", variable, term);
        }
    }
}

