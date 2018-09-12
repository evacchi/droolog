package org.drools.droolog.meta.lib2;

import org.drools.droolog.meta.lib2.Term.*;

public class PersonMeta {

    private static final Atom<Class<?>> ClassAtom = new Atom<>(PersonObject.class);

    public PersonObject of(String name, Integer age) {
        return new PersonObject(name, age);
    }

    public Structure<PersonObject> of(Term<String> name, Term<Integer> age) {
        return new Structure<>(new Term[]{
                ClassAtom,
                name,
                age
        });
    }

    public PersonObject of(Structure<PersonObject> structure) {
        Term[] terms = structure.terms();
        Atom<Class<?>> cls = (Atom<Class<?>>) terms[0];
        if (cls != ClassAtom) {
            throw new IllegalArgumentException();
        }
        Atom<String> name = (Atom<String>) terms[1];
        Atom<Integer> age = (Atom<Integer>) terms[2];
        return new PersonObject(name.value(), age.value());
    }
}
