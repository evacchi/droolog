package org.drools.droolog.examples.v4;

import java.util.Arrays;

import org.drools.droolog.meta.lib.v4.ArrayOps;
import org.drools.droolog.meta.lib.v4.TermState;
import org.drools.droolog.meta.lib.v4.Unification;
import org.junit.Test;

import static org.drools.droolog.meta.lib.v4.TermState.Ground;
import static org.drools.droolog.meta.lib.v4.TermState.Structure;
import static org.junit.Assert.assertArrayEquals;

public class Example {

    public static void main(String[] args) {
        new Example().unify();
    }

    public void unify() {
        TermState ts = new TermState();
        Person p1 = new Person("Paul", null, new Address(null, "Liverpool"));
        Person p2 = new Person(null, "McCartney", new Address("20 Forthlin Road", null));

        int A = ts.newVar(), B = ts.newVar(), C = ts.newVar(), D = ts.newVar();
        int[] p1terms = p1.meta.terms;
        p1terms[PersonMeta.firstName] = Ground;
        p1terms[PersonMeta.lastName] = B;
        p1terms[PersonMeta.address] = Structure(3);
        int[] a1Terms = p1.address().meta.terms;
        a1Terms[AddressMeta.street] = C;
        a1Terms[AddressMeta.city] = Ground;

        int[] p2terms = p2.meta.terms;
        p2terms[PersonMeta.firstName] = A;
        p2terms[PersonMeta.lastName] = Ground;
        p2terms[PersonMeta.address] = Structure(3);
        int[] a2terms = p2.address().meta.terms;
        a2terms[AddressMeta.street] = Ground;
        a2terms[AddressMeta.city] = D;

        int[] pp1 = ArrayOps.concat(p1terms, a1Terms);
        int[] pp2 = ArrayOps.concat(p2terms, a2terms);

        int[] bindings = new int[ts.varCount()];
        Unification.of(pp1, pp2, bindings);
        int[] expected = {
                left(PersonMeta.firstName, 0), // Paul
                right(PersonMeta.lastName, 0), // McCartney
                right(AddressMeta.street, PersonMeta.$size), // 20 Forthlin Road
                left(AddressMeta.city, PersonMeta.$size)  // Liverpool
        };
        System.out.println(Arrays.toString(expected));
        System.out.println(Arrays.toString(bindings));
        assertArrayEquals(expected, bindings);

        Object[] fp1 = p1.meta.values();
        Object[] fp2 = p2.meta.values();
        Object[] vbindings = Unification.values(fp1, fp2, bindings);
        Unification.args(fp1, pp1, vbindings);

        System.out.println(Arrays.toString(vbindings));

//        Person unified = PersonMeta.create(fp1);

//        System.out.println(unified);
    }

    private int left(int i, int offset) {
        return -(i+offset+1);
    }

    private int right(int i, int offset) {
        return i+offset+1;
    }

}
