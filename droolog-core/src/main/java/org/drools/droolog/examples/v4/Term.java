package org.drools.droolog.examples.v4;

public interface Term {

    void bind(Object value);
    ObjectTerm parentObject();
    void setParentObject(ObjectTerm parentObject);
    void setIndex(int index);
    int getIndex();

    interface Atom extends Term, Cloneable {
        void setValue(Object value);
        Object getValue();
    }
    interface Variable extends Term {}
    interface Structure extends Term {
        Term[] terms();
        Term term(int i);
        void term(int i, Term t);
        int size();
        Meta<?,?,?> meta();
    }

    interface ObjectTerm {
        Structure $getStructure();
        void $setStructure(Structure structure);
    }

    interface Meta<A extends Atom, V extends Variable, S extends Structure> {
        A createAtom();

        A createAtom(Term.Atom orig);

        A createAtom(Term.Variable orig);

        V createVariable();

        S createStructure();

    }

}
