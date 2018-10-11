package org.drools.droolog.examples.manual.v3;

import java.util.Objects;

import org.drools.droolog.meta.lib.v3.*;

public class Phone implements Structure<Phone> {
    private final Meta<Phone> meta = new Phone_();

    final private String number;

    public Phone(String number) {
        this.number = number;
    }

    public String number() {
        return number;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "meta=" + meta +
                ", number='" + number + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Phone && Objects.equals(number, ((Phone) obj).number());
    }

    @Override
    public Meta<Phone> meta() {
        return meta;
    }
}
