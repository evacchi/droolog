package org.drools.droolog.examples.v1;

import org.drools.droolog.meta.lib.v1.Unification;

public class Example {
    PersonMeta m = PersonMeta.Instance;
    PersonObject obj1 = m.createPerson();
    PersonObject obj2 = m.createPerson();

    Example() {

        // person(paul, X)
        obj1.setName("Paul");
        m.termOf(obj1).terms(
                m.createAtom(),
                m.createVariable());

        // person(Y, 50)
        obj2.setAge(50);
        m.termOf(obj2).terms(
                m.createVariable(),
                m.createAtom());
    }

    public static void main(String... args) {

        new Example().unify();

        // both obj1, obj2 result in
        // person(paul, 50)

        // System.out.println(obj1);
        // PersonMeta.Structure t1 = m.termOf(obj1);
        // System.out.println(t1);

        // System.out.println(obj2);
        // PersonMeta.Structure t2 = m.termOf(obj2);
        // System.out.println(t2);

    }

    private PersonObject unify() {
        new Unification().unify(
                m.termOf(obj1),
                m.termOf(obj2));
        return obj1;
    }
}
