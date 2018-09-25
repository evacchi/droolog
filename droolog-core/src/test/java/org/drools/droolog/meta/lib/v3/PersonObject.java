package org.drools.droolog.meta.lib.v3;

import java.util.Objects;

import static org.drools.droolog.meta.lib.v3.Term.*;

public class PersonObject implements Person, Term.Structure<Person> {

    private final String name;
    private final Term.Type nameType;
    private final Integer age;
    private final Term.Type ageType;

    PersonObject(
            String name,
            Term.Type nameType,
            Integer age,
            Term.Type ageType) {
        this.name = name;
        this.nameType = nameType;
        this.age = age;
        this.ageType = ageType;
    }

    @Override
    public String name() {
        Term.Type.requireAtom(nameType);
        return name;
    }

    @Override
    public Integer age() {
        Term.Type.requireAtom(ageType);
        return age;
    }

    @Override
    public Person value() {
        return this;
    }

    @Override
    public Term<?>[] terms() {
        return new Term<?>[]{
                of(this.getClass(), Type.Atom),
                of(name, nameType),
                of(age, ageType)
        };
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PersonObject)) return false;
        PersonObject o = (PersonObject) obj;
        return Objects.equals(name, o.name()) && Objects.equals(age, o.age());
    }

    @Override
    public String toString() {
        return "PersonObject{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
