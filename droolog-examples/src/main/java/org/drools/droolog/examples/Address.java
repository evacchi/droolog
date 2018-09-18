package org.drools.droolog.examples;

import org.drools.droolog.meta.lib4.Structure;

public class Address implements Structure<Address> {
    private final Structure.Meta<Address> meta = new Address_();

    final private String street;
    final private String city;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public String street() {
        return street;
    }

    public String city() {
        return city;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    @Override
    public Meta<Address> meta() {
        return meta;
    }
}
