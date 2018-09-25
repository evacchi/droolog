package org.drools.droolog.examples.v2;

import org.drools.droolog.meta.lib.v2.Term;
import org.drools.droolog.meta.lib.v2.Term.Atom;
import org.drools.droolog.meta.lib.v2.Term.Structure;

public class PhoneMeta {

    public static final PhoneMeta Instance = new PhoneMeta();
    private static final Atom<Class<?>> ClassAtom = new Atom<>(PhoneObject.class);

    public Phone of(String number) {
        return new PhoneObject(number);
    }

    public Structure<Phone> of(Term<String> number) {
        return new Structure<>(new Term[]{
                ClassAtom,
                number
        });
    }

    public Phone of(Structure<Phone> structure) {
        Term[] terms = structure.terms();
        Atom<Class<?>> cls = (Atom<Class<?>>) terms[0];
        if (cls != ClassAtom) {
            throw new IllegalArgumentException();
        }
        Atom<String> number = (Atom<String>) terms[1];
        return new PhoneObject(number.value());
    }
}
