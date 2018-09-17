package org.drools.droolog.examples;

import org.drools.droolog.meta.lib4.TermState;

public class Unification {

    public void unify(Person leftPerson, Person rightPerson) {
        Person_ left = leftPerson.meta, right = rightPerson.meta;
        if (left.structureSize != right.structureSize) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < right.structureSize; i++) {
            TermState lt = left.getTerm(i), rt = right.getTerm(i);
            if (lt == TermState.Ground && rt == TermState.Ground) {
                if (!lt.equals(rt)) throw new IllegalArgumentException();
            } else if (lt == TermState.Ground && rt == TermState.Free) {
                right.setTerm(i, TermState.Ground);
                right.setValue(i, left.getValue(i));

            } else if (lt == TermState.Free && rt == TermState.Ground) {
                left.setTerm(i, TermState.Ground);
                left.setValue(i, right.getValue(i));

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
    }

}

