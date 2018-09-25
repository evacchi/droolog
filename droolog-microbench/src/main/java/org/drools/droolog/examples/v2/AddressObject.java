package org.drools.droolog.examples.v2;

import java.util.Objects;

public class AddressObject implements Address {

    private final String street;
    private final String city;

    public AddressObject(String street, String city) {
        this.street = street;
        this.city = city;
    }

    @Override
    public String street() {
        return street;
    }

    @Override
    public String city() {
        return city;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AddressObject)) return false;
        AddressObject o = (AddressObject) obj;
        return Objects.equals(street, o.street()) && Objects.equals(city, o.city());
    }

    @Override
    public String toString() {
        return "AddressObject{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
