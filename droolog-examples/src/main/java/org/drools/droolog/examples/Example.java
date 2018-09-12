package org.drools.droolog.examples;

import org.drools.droolog.meta.lib2.Term;
import org.drools.droolog.meta.lib2.Unification;
import org.junit.Test;

import static org.drools.droolog.meta.lib2.Term.atom;
import static org.drools.droolog.meta.lib2.Term.variable;
import static org.junit.Assert.assertEquals;

public class Example {

    public static void main(String[] args) {
        Term.Atom<String> paul = atom("Paul");
        Term.Atom<Integer> _50 = atom(50);

        PersonMeta person = new PersonMeta();
        Term.Structure<PersonObject> p1 = person.of(paul, variable());
        Term.Structure<PersonObject> p2 = person.of(variable(), _50);

        Term.Structure<PersonObject> r = new Unification().unify(p1, p2);
        PersonObject paulObject = person.of(r);

        System.out.println(r);
        System.out.println(paulObject);

    }

}
