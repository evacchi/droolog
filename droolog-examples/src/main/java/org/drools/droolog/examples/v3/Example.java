package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.Unification;

import static org.drools.droolog.examples.v3.Address_.*;
import static org.drools.droolog.examples.v3.Phone_.*;
import static org.drools.droolog.meta.lib.v3.TermState.*;

public class Example {

    public static void main(String[] args) {
        new Example().unify();
    }

    Person p1 = new Person("Paul", null, new Address(null, "Liverpool", "L18 9TN", "United Kingdom"), new Phone("123-123-123"));
    Person p2 = new Person(null, "McCartney", new Address("20 Forthlin Road", null,"L18 9TN", "United Kingdom"), new Phone(null));

    public Example() {
        p1.meta().setTermState(Person_.lastName, Free);
        p1.address().meta().setTermState(street, Free);

        p2.meta().setTermState(Person_.name, Free);
        p2.address().meta().setTermState(city, Free);
        p2.phone().meta().setTermState(number, Free);
    }

    public Person unify() {

        Person r = Unification.of(p1, p2).structure();
//
//         System.out.println(p1);
//         System.out.println(p2);
//         System.out.println(r);

        return r;
    }
}
