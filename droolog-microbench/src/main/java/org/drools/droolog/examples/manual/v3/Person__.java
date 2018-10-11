package org.drools.droolog.examples.manual.v3;

import org.drools.droolog.meta.lib.v3.*;

public class Person__ implements Structure.Factory<Person> {

    static final Person__ Instance = new Person__();

    public Object valueAt(Person o, int index) {
        switch(index) {
            case Person_.name:
                return o.name();
            case Person_.address:
                return o.address();
            default:
                throw new IllegalArgumentException();
        }
    }

    public Person of(Object... terms) {
        return new Person((String) terms[0], (Address) terms[1], (Phone) terms[2]);
    }

    @Override
    public Person variable() {
        Person p = new Person(null, null, null);
        p.meta().setTermState(Person_.name, TermState.Free);
        p.meta().setTermState(Person_.address, TermState.Free);
        p.meta().setTermState(Person_.phone, TermState.Free);
        return p;
    }
}
