package org.drools.droolog.meta.lib.v4;

import java.util.Arrays;

import org.junit.Test;

import static org.drools.droolog.meta.lib.v4.TermState.Ground;
import static org.drools.droolog.meta.lib.v4.TermState.Structure;
import static org.junit.Assert.assertArrayEquals;

public class Example {

    private static final int
            firstName = 1,
            lastName = 2,
            address = 3,
            street = 4,
            city = 5;

    @Test
    public void unify() {
        TermState ts = new TermState();
        Person p1 = new Person("Paul", null, new Address(null, "Liverpool"));
        Person p2 = new Person(null, "McCartney", new Address("20 Forthlin Road", null));

        int A = ts.newVar(), B = ts.newVar(), C = ts.newVar(), D = ts.newVar();
        int[] p1terms = p1.meta.terms;
        p1terms[PersonMeta.firstName] = Ground;
        p1terms[PersonMeta.lastName] = B;
        p1terms[PersonMeta.address] = Structure(2);

        int[] a1Terms = p1.address().meta.terms;
        a1Terms[AddressMeta.street] = C;
        a1Terms[AddressMeta.city] = Ground;

        int[] p2terms = p2.meta.terms;
        p2terms[PersonMeta.firstName] = A;
        p2terms[PersonMeta.lastName] = Ground;
        p2terms[PersonMeta.address] = Structure(2);
        int[] a2terms = p2.address().meta.terms;
        a2terms[AddressMeta.street] = Ground;
        a2terms[AddressMeta.city] = D;

        int[] pp1 = Unification.linearized(p1terms, a1Terms);
        int[] pp2 = Unification.linearized(p2terms, a2terms);

        int[] bindings = new int[ts.varCount()];
        Unification.of(pp1, pp2, bindings);
        int[] expected = {
                -firstName, // Paul
                lastName, // McCartney
                street, // 20 Forthlin Road
                -city  // Liverpool
        };
        System.out.println(Arrays.toString(expected));
        System.out.println(Arrays.toString(bindings));
        assertArrayEquals(expected, bindings);

        Object[] fp1 = PersonMeta.valuesOf(p1);
        Object[] fp2 = PersonMeta.valuesOf(p2);
        Object[] vbindings = Unification.values(fp1, fp2, bindings);
        Unification.args(fp1, pp1, vbindings);

        System.out.println(Arrays.toString(vbindings));

        Person unified = PersonFactory.create(fp1);

        System.out.println(unified);
    }


//    static class FieldReader implements IntFunction<Object> {
//
//        final Person p;
//
//        public FieldReader(Person p) {
//            this.p = p;
//        }
//
//        @Override
//        public Object apply(int i) {
//            switch (i) {
//                case firstName:
//                    return p.firstName();
//                case lastName:
//                    return p.lastName();
//                case address:
//                    return null;
//                case street:
//                    return p.address().street();
//                case city:
//                    return p.address().city();
//                default:
//                    throw new ArrayIndexOutOfBoundsException(i);
//            }
//        }
//    }

    static class PersonFactory {

        public static Person create(Object... values) {
            return new Person((String) values[0], (String) values[1], new Address((String) values[3], (String) values[4]));
        }
    }
}
