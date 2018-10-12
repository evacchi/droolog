package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.Structure;
import org.drools.droolog.meta.lib.v3.TermState;

public class Person__ implements Structure.Factory<Person> {

    static final Person__ Instance = new Person__();

    public Object valueAt(Person p, int index) {
        switch(index) {
            case Person_.name:
                return p.name();
            case Person_.lastName:
                return p.lastName();
            case Person_.address:
                return p.address();
            case Person_.phone:
                return p.phone();
            default:
                throw new IllegalArgumentException();
        }
    }

    public Person of(Object... terms) {
        return new Person((String) terms[0], (String) terms[1], (Address) terms[2], (Phone) terms[3]);
    }

    @Override
    public Person variable() {
        Person p = new Person(null, null, null, null);
        p.meta().setTermState(Person_.name, TermState.Free);
        p.meta().setTermState(Person_.lastName, TermState.Free);
        p.meta().setTermState(Person_.address, TermState.Free);
        p.meta().setTermState(Person_.phone, TermState.Free);
        return p;
    }
}
