package org.drools.droolog.examples.v2simple;

import org.drools.droolog.meta.lib.v2simple.Term;
import org.drools.droolog.meta.lib.v2simple.Unification;

import static org.drools.droolog.meta.lib.v2simple.Term.atom;
import static org.drools.droolog.meta.lib.v2simple.Term.variable;

public class Example {
    Term<String> second  = atom("McCartney");
    Term<String> street  = atom("20 Forthlin Road");
    Term<String> city    = atom("Liverpool");
    Term<String> zip     = atom("L18 9TN");
    Term<String> country = atom("United Kingdom");
    Term<String> first   = atom("Paul");

    PersonMeta person = PersonMeta.Instance;
    AddressMeta address = AddressMeta.Instance;
    Term.Structure<Person> p1 = person.of(first, variable(), variable());
    Term.Structure<Person> p2 = person.of(variable(), second, address.of(street, city, zip, country));


    public static void main(String[] args) {
        new Example().unify();
    }

    public Person unify() {

        {
            Unification<Person> unification = Unification.of(p1, p2);
            Term.Structure<Person> s = unification.term();
            return person.of(s);

            // System.out.println(s);
            // System.out.println(p);
            // System.out.println(unification.bindings());
        }

//        {
//            Term.Structure<Person> p1 = person.of(first, variable(), variable());
//            Term.Structure<Person> p2 = person.of(variable(), second, variable());
//
//            Unification<Person> unification = Unification.of(p1, p2);
//            Term.Structure<Person> s = unification.term();
//            Person p = person.of(s);
//
//            System.out.println(s);
//            System.out.println(p);
//            System.out.println(unification.bindings());
//        }
    }
}
