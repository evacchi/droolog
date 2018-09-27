package org.drools.droolog.examples.v4;

import java.util.Arrays;

import org.drools.droolog.meta.lib.v4.TermState;
import org.drools.droolog.meta.lib.v4.Unification;

import static org.drools.droolog.meta.lib.v4.TermState.Ground;

public class Example {

    public static void main(String[] args) {
        TermState ts = new TermState();
        Person p1 = new Person("Paul", null, new Address(null, "Liverpool"));

        int A = ts.newVar(), B = ts.newVar(), C = ts.newVar(), D = ts.newVar(), E = ts.newVar();
        p1.meta.terms = new int[]{ Ground, B, Ground };
        p1.address().meta.terms = new int[]{ D, Ground };

        Person p2 = new Person(null, "McCartney", new Address("20 Forthlin Road", null));
        p2.meta.terms = new int[]{ A, Ground, C };
        p2.address().meta.terms = new int[] { Ground, E };

        int[] left = Unification.linearized(new int[][]{p1.meta.terms, p1.address().meta.terms});
        int[] right = Unification.linearized(new int[][]{p2.meta.terms, p2.address().meta.terms});

        int[] bindings = Unification.of(left, right);
        System.out.println(Arrays.toString(bindings));

    }
}
