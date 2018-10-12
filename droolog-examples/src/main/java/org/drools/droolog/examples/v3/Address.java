package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.Structure;

public class Address implements Structure<Address> {
    private final Structure.Meta<Address> meta = new Address_();

    final private String street;
    final private String city;
    final private String country;
    final private String code;

    public Address(String street, String city, String country, String code) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.code = code;
    }

    public String street() {
        return street;
    }

    public String city() {
        return city;
    }

    public String country() {
        return country;
    }

    public String code() {
        return code;
    }

    @Override
    public String toString() {
        return "Address{" +
                "meta=" + meta +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    @Override
    public Meta<Address> meta() {
        return meta;
    }
}
