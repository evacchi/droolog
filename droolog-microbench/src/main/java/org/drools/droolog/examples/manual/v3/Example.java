package org.drools.droolog.examples.manual.v3;

import org.drools.droolog.meta.lib.v3.Unification;

import static org.drools.droolog.examples.manual.v3.Address_.*;
import static org.drools.droolog.examples.manual.v3.Person_.*;
import static org.drools.droolog.meta.lib.v3.TermState.*;

public class Example {

    public static void main(String[] args) {
        Person p1 = new Person("Paul", new Address(null, "Liverpool"), new Phone("123-123-123"));
        p1.address().meta().setTermState(street, Free);

        Person p2 = new Person(null, new Address("20 Forthlin Road", null), new Phone("123-123-123"));
        p2.meta().setTermState(name, Free);
        p2.address().meta().setTermState(city, Free);

        Person r = Unification.of(p1, p2).structure();

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(r);
    }
}
