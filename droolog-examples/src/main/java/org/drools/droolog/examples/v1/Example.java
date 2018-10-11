package org.drools.droolog.examples.v1;

import org.drools.droolog.meta.lib.v1.Unification;

public class Example {

    public static Person main(String... args) {
        PersonMeta m = PersonMeta.Instance;

        // person(paul, X)
        PersonObject obj1 = m.createPerson();
        obj1.setName("Paul");
        m.termOf(obj1).terms(
                m.createAtom(),
                m.createVariable());

        // person(Y, 50)
        PersonObject obj2 = m.createPerson();
        obj2.setAge(50);
        m.termOf(obj2).terms(
                m.createVariable(),
                m.createAtom());

        new Unification().unify(
                m.termOf(obj1),
                m.termOf(obj2));

        // both obj1, obj2 result in
        // person(paul, 50)

        // System.out.println(obj1);
        // PersonMeta.Structure t1 = m.termOf(obj1);
        // System.out.println(t1);

        // System.out.println(obj2);
        // PersonMeta.Structure t2 = m.termOf(obj2);
        // System.out.println(t2);

        return obj1;
    }
}
