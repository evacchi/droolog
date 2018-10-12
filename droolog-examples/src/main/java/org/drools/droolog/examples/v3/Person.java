package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.Structure;

//@Metable
public class Person implements Structure<Person> {
    private Person_ meta = new Person_();

    @Override
    public Meta<Person> meta() {
        return meta;
    }

    private final String name;
    private final String lastName;
    private final Address address;
    private final Phone phone;

    public Person(String name, String lastName, Address address, Phone phone) {
        this.name = name;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    public String name() {
        return name;
    }

    public String lastName() {
        return lastName;
    }

    public Address address() {
        return address;
    }

    public Phone phone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", lastName=" + lastName +
                ", address=" + address +
                ", phone=" + phone +
                '}';
    }
}
