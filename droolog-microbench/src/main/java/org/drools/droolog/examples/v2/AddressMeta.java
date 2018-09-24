package org.drools.droolog.examples.v2;

import org.drools.droolog.meta.lib2.Term;
import org.drools.droolog.meta.lib2.Term.Atom;
import org.drools.droolog.meta.lib2.Term.Structure;

public class AddressMeta {

    public static final AddressMeta Instance = new AddressMeta();
    private static final Atom<Class<?>> ClassAtom = new Atom<>(AddressObject.class);

    public Address of(String street, String city) {
        return new AddressObject(street, city);
    }

    public Structure<Address> of(Term<String> street, Term<String> city) {
        return new Structure<>(new Term[]{
                ClassAtom,
                street,
                city
        });
    }

    public Address of(Structure<Address> structure) {
        Term[] terms = structure.terms();
        Atom<Class<?>> cls = (Atom<Class<?>>) terms[0];
        if (cls != ClassAtom) {
            throw new IllegalArgumentException();
        }
        Atom<String> name = (Atom<String>) terms[1];
        Atom<String> city = (Atom<String>) terms[2];
        return new AddressObject(name.value(), city.value());
    }
}
