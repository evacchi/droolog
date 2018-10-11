package org.drools.droolog.examples.v4;

import java.util.Arrays;

import org.drools.droolog.meta.lib.v4.ArrayOps;
import org.drools.droolog.meta.lib.v4.TermState;
import org.drools.droolog.meta.lib.v4.Unification;

import static org.drools.droolog.meta.lib.v4.TermState.Ground;
import static org.drools.droolog.meta.lib.v4.TermState.Structure;

public class Example {

    public static void main(String[] args) {
        new Example().unify();
    }

    public Person unify() {
        TermState ts = new TermState();
        Person p1 = new Person("Paul", null, new Address(null, "Liverpool"), new Phone("123-123-123"));
        Person p2 = new Person(null, "McCartney", new Address("20 Forthlin Road", null), new Phone(null));

        int A = ts.newVar(), B = ts.newVar(), C = ts.newVar(), D = ts.newVar(), E = ts.newVar();
        int[] p1terms = p1.meta.terms;
        p1terms[PersonMeta.firstName] = Ground;
        p1terms[PersonMeta.lastName] = B;
        p1terms[PersonMeta.address] = Structure(3);
        p1terms[PersonMeta.phone]   = Structure(1);
        int[] a1Terms = p1.address().meta.terms;
        a1Terms[AddressMeta.street] = C;
        a1Terms[AddressMeta.city] = Ground;
        int[] pp1Terms = p1.phone().meta.terms();
        pp1Terms[PhoneMeta.number] = Ground;

        int[] p2terms = p2.meta.terms;
        p2terms[PersonMeta.firstName] = A;
        p2terms[PersonMeta.lastName] = Ground;
        p2terms[PersonMeta.address] = Structure(3);
        p2terms[PersonMeta.phone]   = Structure(1);
        int[] a2terms = p2.address().meta.terms;
        a2terms[AddressMeta.street] = Ground;
        a2terms[AddressMeta.city] = D;
        int[] pp2Terms = p2.phone().meta.terms();
        pp2Terms[PhoneMeta.number] = E;

        int[] pp1 = ArrayOps.concat(p1terms, a1Terms, pp1Terms);
        int[] pp2 = ArrayOps.concat(p2terms, a2terms, pp2Terms);

        int[] bindings = new int[ts.varCount()];
        Unification.of(pp1, pp2, bindings);
//        int[] expected = {
//                left(PersonMeta.firstName, 0), // Paul
//                right(PersonMeta.lastName, 0), // McCartney
//                right(AddressMeta.street, PersonMeta.$size), // 20 Forthlin Road
//                left(AddressMeta.city, PersonMeta.$size),  // Liverpool
//                left(PhoneMeta.number, PersonMeta.$size + AddressMeta.$size)
//        };
//        System.out.println(Arrays.toString(expected));
//        System.out.println(Arrays.toString(bindings));
//        assert Arrays.equals(expected, bindings);

        Object[] fp1 = PersonFactory.Instance.values(p1);
        Object[] fp2 = PersonFactory.Instance.values(p2);
        Object[] vbindings = Unification.values(fp1, fp2, bindings);
        Unification.args(fp1, pp1, vbindings);

//        System.out.println(Arrays.toString(vbindings));

        Person unified = PersonFactory.Instance.create(fp1);

        return unified;
    }

    private int left(int i, int offset) {
        return -(i+offset+1);
    }

    private int right(int i, int offset) {
        return i+offset+1;
    }

}
