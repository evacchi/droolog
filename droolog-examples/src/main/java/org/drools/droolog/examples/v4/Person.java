package org.drools.droolog.examples.v4;

import java.util.Objects;

import org.drools.droolog.meta.lib.v4.ObjectTerm;

@ObjectTerm
public class Person {
    public final PersonMeta meta = new PersonMeta();

    private final String firstName;
    private final String lastName;
    @ObjectTerm
    private final Address address;
    @ObjectTerm
    private final Phone phone;

    public Person(String firstName, String lastName, Address address, Phone phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    public String firstName() {
        return firstName;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return Objects.equals(meta, person.meta) &&
                Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName) &&
                Objects.equals(address, person.address) &&
                Objects.equals(phone, person.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meta, firstName, lastName, address, phone);
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                ", phone=" + phone +
                '}';
    }
}
