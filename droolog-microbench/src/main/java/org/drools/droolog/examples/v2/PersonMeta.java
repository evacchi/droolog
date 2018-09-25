package org.drools.droolog.examples.v2;

import org.drools.droolog.meta.lib.v2.Term;
import org.drools.droolog.meta.lib.v2.Term.*;

public class PersonMeta {

    public static final PersonMeta Instance = new PersonMeta();
    private static final Atom<Class<?>> ClassAtom = new Atom<>(PersonObject.class);

    public PersonObject of(String name, Address address, Phone phone) {
        return new PersonObject(name, address, phone);
    }

    public Structure<PersonObject> of(Term<String> name, Term<Address> address, Term<Phone> phone) {
        return new Structure<>(new Term[]{
                ClassAtom,
                name,
                address,
                phone
        });
    }

    public PersonObject of(Structure<PersonObject> structure) {
        Term[] terms = structure.terms();
        Atom<Class<?>> cls = (Atom<Class<?>>) terms[0];
        if (cls != ClassAtom) {
            throw new IllegalArgumentException();
        }
        Atom<String> name = (Atom<String>) terms[1];
        Atom<Address> address = (Atom<Address>) terms[2];
        Atom<Phone> phone = (Atom<Phone>) terms[2];
        return new PersonObject(name.value(), address.value(), phone.value());
    }
}
