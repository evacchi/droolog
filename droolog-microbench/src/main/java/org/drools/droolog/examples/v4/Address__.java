package org.drools.droolog.examples.v4;

import org.drools.droolog.meta.lib4.Structure;
import org.drools.droolog.meta.lib4.TermState;

public class Address__ implements Structure.Factory<Address> {

    final static Address__ Instance = new Address__();

    @Override
    public Object valueAt(Address o, int index) {
        switch (index) {
            case Address_.street:
                return o.street();
            case Address_.city:
                return o.city();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Address of(Object... terms) {
        return new Address((String) terms[0], (String) terms[1]);
    }

    @Override
    public Address variable() {
        Address address = new Address(null, null);
        address.meta().setTermState(Address_.street, TermState.Free);
        address.meta().setTermState(Address_.city, TermState.Free);
        return address;
    }
}
