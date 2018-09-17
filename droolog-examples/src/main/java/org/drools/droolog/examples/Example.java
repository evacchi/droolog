package org.drools.droolog.examples;

import org.drools.droolog.meta.lib4.TermState;

public class Example {

    public static void main(String[] args) {
        Person p1 = new Person();
        p1.name = "paul";
        p1.age = 50;
        p1.meta.setTerm(Person_.name, TermState.Ground);
        p1.meta.setTerm(Person_.age, TermState.Ground);

        Person p2 = new Person();
        p2.meta.setTerm(Person_.name, TermState.Free);
        p2.meta.setTerm(Person_.age, TermState.Free);

        new Unification().unify(p1, p2);

        System.out.println(p1);
        System.out.println(p2);
    }
}
