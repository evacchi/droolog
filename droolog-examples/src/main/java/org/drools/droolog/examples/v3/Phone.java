package org.drools.droolog.examples.v3;

import java.util.Objects;

import org.drools.droolog.meta.lib.v3.Structure;

public class Phone implements Structure<Phone> {

    Phone_ meta = new Phone_();

    private final String number;

    public Phone(String number) {
        this.number = number;
    }

    public String number() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Phone phone = (Phone) o;
        return Objects.equals(number, phone.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "Phone{" +
                "number='" + number + '\'' +
                '}';
    }

    @Override
    public Meta<Phone> meta() {
        return meta;
    }
}
