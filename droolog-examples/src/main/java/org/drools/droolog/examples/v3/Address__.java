package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.Structure;
import org.drools.droolog.meta.lib.v3.TermState;

import static org.drools.droolog.examples.v3.Address_.*;

public class Address__ implements Structure.Factory<Address> {

    final static Address__ Instance = new Address__();

    @Override
    public Object valueAt(Address a, int index) {
        switch (index) {
            case street:
                return a.street();
            case city:
                return a.city();
            case country:
                return a.country();
            case code:
                return a.code();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Address of(Object... terms) {
        return new Address((String) terms[0], (String) terms[1], (String) terms[2], (String) terms[3]);
    }

    @Override
    public Address variable() {
        Address address = new Address(null, null, null, null);
        address.meta().setTermState(street, TermState.Free);
        address.meta().setTermState(city, TermState.Free);
        address.meta().setTermState(country, TermState.Free);
        address.meta().setTermState(code, TermState.Free);
        return address;
    }
}
