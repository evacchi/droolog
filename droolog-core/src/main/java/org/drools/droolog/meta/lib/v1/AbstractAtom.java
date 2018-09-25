package org.drools.droolog.meta.lib.v1;

import java.util.Objects;

public abstract class AbstractAtom<T extends Term.ObjectTerm> extends AbstractTerm<T> implements Term.Atom {

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Atom &&
                Objects.equals(getValue(), ((Atom) obj).getValue());
    }
}