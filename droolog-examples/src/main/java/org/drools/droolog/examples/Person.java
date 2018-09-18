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
    private final Address address;

    public Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public String name() {
        return name;
    }

    public Address address() {
        return address;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}
