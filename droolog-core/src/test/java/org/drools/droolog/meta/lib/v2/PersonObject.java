package org.drools.droolog.meta.lib.v2;

import java.util.Objects;

public class PersonObject implements Person {

    private final String name;
    private final Integer age;

    public PersonObject(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Integer age() {
        return age;
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
