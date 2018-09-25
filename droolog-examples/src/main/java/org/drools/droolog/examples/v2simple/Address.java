package org.drools.droolog.examples.v2simple;

import java.util.Objects;

import org.drools.droolog.meta.lib.v2simple.ObjectTerm;

@ObjectTerm
public class Address {
    private final String street;
    private final String city;
    private final String zip;
    private final String country;

    public Address(String street, String city, String zip, String country) {
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.country = country;
    }

    public String street() {
        return street;
    }

    public String city() {
        return city;
    }

    public String zip() {
        return zip;
    }

    public String country() {
        return country;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(zip, address.zip) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zip, country);
    }
}
