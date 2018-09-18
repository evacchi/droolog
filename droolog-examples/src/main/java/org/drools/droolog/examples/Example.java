package org.drools.droolog.examples;

import static org.drools.droolog.examples.Person_.*;
import static org.drools.droolog.meta.lib4.TermState.*;

public class Example {

    public static void main(String[] args) {
        Person p1 = new Person("paul", null);
        Structure.Meta<Person> p1Meta = p1.meta();
        p1Meta.setTermState(age, Free); // default is ground

        Person p2 = new Person(null, 50);
        Structure.Meta<Person> p2Meta = p2.meta();
        p2Meta.setTermState(name, Free);

        Person r = new Unification<>(p1, p2).structure();

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(r);
    }
}
