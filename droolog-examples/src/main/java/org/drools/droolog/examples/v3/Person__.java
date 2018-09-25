package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib4.Structure;
import org.drools.droolog.meta.lib4.TermState;

public class Person__ implements Structure.Factory<Person> {

    static final Person__ Instance = new Person__();

    public Object valueAt(Object o, int index) {
        Person p = (Person) o;
        switch(index) {
            case Person_.name:
                return p.name();
            case Person_.address:
                return p.address();
            default:
                throw new IllegalArgumentException();
        }
    }

    public Person of(Object... terms) {
        return new Person((String) terms[0], (Address) terms[1]);
    }

    @Override
    public Person variable() {
        Person p = new Person(null, null);
        p.meta().setTermState(Person_.name, TermState.Free);
        p.meta().setTermState(Person_.address, TermState.Free);
        return p;
    }
}
