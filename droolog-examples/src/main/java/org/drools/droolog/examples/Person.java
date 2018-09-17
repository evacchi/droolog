package org.drools.droolog.examples;

import org.drools.droolog.meta.lib.Meta;

//@Metable
public class Person {
    @Meta Person_ meta = new Person_(this);
    String name;
    Integer age;

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
