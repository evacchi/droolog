package org.drools.droolog.examples;

import static org.drools.droolog.examples.Person_.*;

public class Person__ implements Structure.Factory<Person> {

    static final Person__ Instance = new Person__();

    public Object valueAt(Object o, int index) {
        Person p = (Person) o;
        switch(index) {
            case name:
                return p.name();
            case age:
                return p.age();
            default:
                throw new IllegalArgumentException();
        }
    }

    public Person of(Object... terms) {
        return new Person((String) terms[0], (Integer) terms[1]);
    }
}
