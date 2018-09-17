package org.drools.droolog.meta.lib3;

import org.junit.Test;

import static org.drools.droolog.meta.lib3.Term.*;
import static org.junit.Assert.assertEquals;

public class MetaTest {

    @Test
    public void test() {
        Atom<String> paul = atom("Paul");
        Atom<Integer> _50 = atom(50);

        PersonMeta person = PersonMeta.Instance;
        PersonObject p1 = person.of(paul, variable());
        PersonObject p2 = person.of(variable(), _50);

        Structure<Person> r = Unification.of(p1, p2).term();
        Person paulObject = r.value();

        System.out.println(r);
        System.out.println(paulObject);

        assertEquals(person.of(paul, _50), r);
        assertEquals(person.of(paul.value(), _50.value()), paulObject);
    }
}
