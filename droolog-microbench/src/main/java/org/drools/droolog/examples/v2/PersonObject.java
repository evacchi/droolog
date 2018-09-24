package org.drools.droolog.examples.v2;

import java.util.Objects;

public class PersonObject implements Person {

    private final String name;
    private final Address address;
    private final Phone phone;

    public PersonObject(String name, Address address, Phone phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Address address() {
        return address;
    }

    @Override
    public Phone phone() {
        return phone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PersonObject)) return false;
        PersonObject o = (PersonObject) obj;
        return Objects.equals(name, o.name())
                && Objects.equals(address, o.address())
                && Objects.equals(phone, o.phone());
    }

    @Override
    public String toString() {
        return "PersonObject{" +
                "name='" + name + '\'' +
                ", address=" + address +
                ", phone=" + phone +
                '}';
    }
}
