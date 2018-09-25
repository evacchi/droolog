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
    private final Address address;
    private final Phone phone;

    public Person(String name, Address address, Phone phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String name() {
        return name;
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
                ", address=" + address +
                ", phone=" + phone +
                '}';
    }
}
