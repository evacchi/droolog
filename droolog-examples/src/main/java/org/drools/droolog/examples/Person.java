package org.drools.droolog.examples;

import org.drools.droolog.meta.lib4.Structure;

//@Metable
public class Person implements Structure<Person> {
    private Person_ meta = new Person_();

    @Override
    public Meta<Person> meta() {
        return meta;
    }

    private final String name;
    private final Integer age;

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String name() {
        return name;
    }

    public Integer age() {
        return age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
