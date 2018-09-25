package org.drools.droolog.meta.lib.v3;

public class PersonMeta {

    public static final PersonMeta Instance = new PersonMeta();

    public PersonObject of(String name, Integer age) {
        return new PersonObject(name, Term.Type.Atom, age, Term.Type.Atom);
    }

    public PersonObject of(Term<String> name, Term<Integer> age) {
        return new PersonObject(name.value(), name.type(), age.value(), age.type());
    }

    public PersonObject variable() {
        return new PersonObject(null, Term.Type.Variable, null, Term.Type.Variable);
    }

    public PersonObject of(Term<?>[] terms) {
        if (terms.length == 0) {
            return variable();
        } else {
            return new PersonObject((String) terms[1].value(), terms[1].type(), (Integer) terms[2].value(), terms[2].type());
        }
    }
}
