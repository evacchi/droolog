package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.Structure;
import org.drools.droolog.meta.lib.v3.TermState;

public class Address_ implements Structure.Meta<Address> {

    final static int street = 0;
    final static int city = 1;
    final static int country = 2;
    final static int code = 3;
    TermState street_state = TermState.Ground;
    TermState city_state = TermState.Ground;
    TermState country_state = TermState.Ground;
    TermState code_state = TermState.Ground;

    @Override
    public TermState getTermState(int index) {
        switch (index) {
            case street:
                return street_state;
            case city:
                return city_state;
            case country:
                return country_state;
            case code:
                return code_state;
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
            case country:
                country_state = value;
                break;
            case code:
                code_state = value;
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
        return 4;
    }
}
