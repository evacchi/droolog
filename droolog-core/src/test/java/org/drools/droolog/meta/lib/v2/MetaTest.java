package org.drools.droolog.meta.lib.v2;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetaTest {

    @Test
    public void test() {
        Term.Atom<String> paul = Term.atom("Paul");
        Term.Atom<Integer> _50 = Term.atom(50);

        PersonMeta person = PersonMeta.Instance;
        Term.Structure<PersonObject> p1 = person.of(paul, Term.variable());
        Term.Structure<PersonObject> p2 = person.of(Term.variable(), _50);

        Term.Structure<PersonObject> r = Unification.of(p1, p2).term();
        PersonObject paulObject = person.of(r);

        System.out.println(r);
        System.out.println(paulObject);

        Assert.assertEquals(person.of(paul, _50), r);
        assertEquals(person.of(paul.value(), _50.value()), paulObject);
    }
}
