package org.drools.droolog.examples;

import org.drools.droolog.meta.lib2.Term;
import org.drools.droolog.meta.lib2.Unification;

import static org.drools.droolog.meta.lib2.Term.atom;
import static org.drools.droolog.meta.lib2.Term.variable;

public class Example {

    public static void main(String[] args) {
        var first   = atom("Paul");
        var second  = atom("McCartney");
        var street  = atom("20 Forthlin Road");
        var city    = atom("Liverpool");
        var zip     = atom("L18 9TN");
        var country = atom("United Kingdom");

        PersonMeta person = new PersonMeta();
        AddressMeta address = new AddressMeta();

        Term.Structure<Address> a = address.of(street, city, zip, country);

        Term.Structure<Person> p1 = person.of(first, variable(), variable());
        Term.Structure<Person> p2 = person.of(variable(), second, a);

        Unification<Person> unification = new Unification<>(p1, p2);
        Term.Structure<Person> r = (Term.Structure<Person>) unification.term();
        Person paulObject = person.of(r);

        System.out.println(r);
        System.out.println(paulObject);
        System.out.println(unification.bindings());

    }

}
