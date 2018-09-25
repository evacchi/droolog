package org.drools.droolog.examples.v2;

import org.drools.droolog.examples.v2.Person;
import org.drools.droolog.meta.lib.v2.Term;
import org.drools.droolog.meta.lib.v2.Unification;

import static org.drools.droolog.meta.lib.v2.Term.atom;
import static org.drools.droolog.meta.lib.v2.Term.variable;

public class Example {

    public static void main(String[] args) {
        var first   = atom("Paul");
        var second  = atom("McCartney");
        var street  = atom("20 Forthlin Road");
        var city    = atom("Liverpool");
        var zip     = atom("L18 9TN");
        var country = atom("United Kingdom");

        PersonMeta person = PersonMeta.Instance;
        AddressMeta address = AddressMeta.Instance;

        {
            Term.Structure<Person> p1 = person.of(first, variable(), variable());
            Term.Structure<Person> p2 = person.of(variable(), second, address.of(street, city, zip, country));

            Unification<Person> unification = Unification.of(p1, p2);
            Term.Structure<Person> s = unification.term();
            Person p = person.of(s);

            System.out.println(s);
            System.out.println(p);
            System.out.println(unification.bindings());
        }

        {
            Term.Structure<Person> p1 = person.of(first, variable(), variable());
            Term.Structure<Person> p2 = person.of(variable(), second, variable());

            Unification<Person> unification = Unification.of(p1, p2);
            Term.Structure<Person> s = unification.term();
            Person p = person.of(s);

            System.out.println(s);
            System.out.println(p);
            System.out.println(unification.bindings());
        }

    }

}