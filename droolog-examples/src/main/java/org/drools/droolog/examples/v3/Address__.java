package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib4.Structure;
import org.drools.droolog.meta.lib4.TermState;

import static org.drools.droolog.examples.v3.Address_.city;
import static org.drools.droolog.examples.v3.Address_.street;

public class Address__ implements Structure.Factory<Address> {

    final static Address__ Instance = new Address__();

    @Override
    public Object valueAt(Object o, int index) {
        Address a = (Address) o;
        switch (index) {
            case street:
                return a.street();
            case city:
                return a.city();
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
        address.meta().setTermState(street, TermState.Free);
        address.meta().setTermState(city, TermState.Free);
        return address;
    }
}
