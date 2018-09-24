package org.drools.droolog.examples.v2;

import java.util.Objects;

public class PhoneObject implements Phone {

    private final String number;

    public PhoneObject(String number) {
        this.number = number;
    }

    @Override
    public String number() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PhoneObject)) return false;
        PhoneObject o = (PhoneObject) obj;
        return Objects.equals(number, o.number());
    }

    @Override
    public String toString() {
        return "AddressObject{" +
                "number='" + number + '\'' +
                '}';
    }
}
