package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.*;

import static org.drools.droolog.examples.v3.Person_.*;

public class Person__ implements Structure.Factory<Person> {

    static final Person__ Instance = new Person__();

    public Object valueAt(Person o, int index) {
        switch(index) {
            case name:
                return o.name();
            case address:
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
        p.meta().setTermState(name, TermState.Free);
        p.meta().setTermState(address, TermState.Free);
        p.meta().setTermState(phone, TermState.Free);
        return p;
    }
}
