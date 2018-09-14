package org.drools.droolog.meta.lib2;

import org.junit.Test;

import static org.drools.droolog.meta.lib2.Term.*;
import static org.junit.Assert.assertEquals;

public class MetaTest {

    @Test
    public void test() {
        Atom<String> paul = atom("Paul");
        Atom<Integer> _50 = atom(50);

        PersonMeta person = PersonMeta.Instance;
        Structure<PersonObject> p1 = person.of(paul, variable());
        Structure<PersonObject> p2 = person.of(variable(), _50);

        Structure<PersonObject> r = Unification.of(p1, p2).term();
        PersonObject paulObject = person.of(r);

        System.out.println(r);
        System.out.println(paulObject);

        assertEquals(person.of(paul, _50), r);
        assertEquals(person.of(paul.value(), _50.value()), paulObject);
    }
}
