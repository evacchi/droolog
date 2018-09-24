package org.drools.droolog.examples.v4;

import org.drools.droolog.meta.lib4.Structure;
import org.drools.droolog.meta.lib4.TermState;

public class Address_ implements Structure.Meta<Address> {

    final static int street = 0;
    TermState street_state = TermState.Ground;
    final static int city = 1;
    TermState city_state = TermState.Ground;

    @Override
    public TermState getTermState(int index) {
        switch (index) {
            case street:
                return street_state;
            case city:
                return city_state;
                default:
                    throw new IllegalArgumentException();
        }
    }

    @Override
    public void setTermState(int index, TermState value) {
        switch (index) {
            case street:
                street_state = value;
                break;
            case city:
                city_state = value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }

    }

    @Override
    public Structure.Factory<Address> structure() {
        return Address__.Instance;
    }

    @Override
    public int size() {
        return 2;
    }
}
