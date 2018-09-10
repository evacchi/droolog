package org.drools.droolog.meta;

import org.drools.droolog.meta.lib.Term;

public class Unification {

    public void unify(Term.Structure left, Term.Structure right) {
        if (left.size() != right.size()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < left.size(); i++) {
            Term lt = left.term(i), rt = right.term(i);
            if (lt instanceof Term.Atom && rt instanceof Term.Atom) {
                if (!lt.equals(rt)) throw new IllegalArgumentException();
            } else if (lt instanceof Term.Atom && rt instanceof Term.Variable) {
                copyAtom((Term.Atom) lt, right, i);
            } else if (lt instanceof Term.Variable && rt instanceof Term.Atom) {
                copyAtom((Term.Atom) rt, left, i);
            } else if (lt instanceof Term.Structure && rt instanceof Term.Variable) {
                copyCompound((Term.Structure) lt, right, i);
            } else if (lt instanceof Term.Variable && rt instanceof Term.Structure) {
                copyCompound((Term.Structure) rt, left, i);
            } else if (lt instanceof Term.Variable && rt instanceof Term.Variable) {
                Term.Atom la = createAtom((Term.Variable) lt, left, i);
                Term.Atom ra = createAtom((Term.Variable) rt, right, i);
                // we set both "manually" to an arbitrary valid value
                la.setValue(ra.getValue());
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    private void copyCompound(Term.Structure structure, Term.Structure parent, int index) {
        // fixme not working/tested yet
        Term.Meta<?,?,?> meta = structure.meta();
        Term.Structure s = meta.createStructure();
        s.bind(parent.parentObject());
        for (int i = 0; i < structure.size(); i++) {
            structure.term(i, meta.createVariable());
        }
        parent.term(index, s);

    }

    private Term.Atom createAtom(Term.Variable variable, Term.Structure structure, int index) {
        Term.Meta<?,?,?> meta = structure.meta();
        Term.Atom a1 = meta.createAtom(variable);
        a1.bind(structure.parentObject());
        structure.term(index, a1);
        return a1;
    }

    private void copyAtom(Term.Atom atom, Term.Structure term, int index) {
        Term.Meta<?,?,?> meta = term.meta();
        Term.Atom copy = meta.createAtom(atom);
        copy.bind(term.parentObject());
        copy.setValue(atom.getValue());
        term.term(index, copy);
    }
}

