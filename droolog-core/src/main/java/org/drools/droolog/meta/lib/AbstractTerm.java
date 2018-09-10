package org.drools.droolog.meta.lib;

public abstract class AbstractTerm<T extends Term.ObjectTerm> implements Term {
    int index = -1;
    T parent;

    public T parentObject() {
        return parent;
    }

    @Override
    public void setParentObject(ObjectTerm parentObject) {
        this.parent = (T) parentObject;
    }

    @Override
    public void bind(Object value) {
        this.parent = (T) value;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

}

