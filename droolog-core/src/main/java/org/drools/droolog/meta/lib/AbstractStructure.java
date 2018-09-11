package org.drools.droolog.meta.lib;

import java.util.Arrays;

public abstract class AbstractStructure<T extends Term.ObjectTerm> extends AbstractTerm<T> implements Term.Structure {
    private final Term[] terms;

    public AbstractStructure(Term[] terms) {
        this.terms = terms;
    }

    @Override
    public Term[] terms() {
        return terms;
    }

    @Override
    public Term term(int i) {
        return terms[i];
    }

    @Override
    public void term(int i, Term t) {
        terms[i] = t;
        t.bind(parent);
        t.setIndex(i);
    }

    @Override
    public int size() {
        return terms.length;
    }

    public void bind(Object o) {
        T tt = (T) o;
        this.parent = tt;
        tt.$setStructure(this);
        for (int i = 0; i < terms.length; i++) {
            Term t = terms[i];
            t.setIndex(i);
            t.bind(o);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(terms);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Structure)) return false;
        Structure s = (Structure) obj;
        return Arrays.deepEquals(this.terms(), s.terms());
    }

}

