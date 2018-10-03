package org.drools.droolog.meta.lib.v4;

import static org.drools.droolog.meta.lib.v4.TermState.Ground;

public class AddressMeta {

    public static final int
            street = 0,
            city = 1;

    public int[] terms = { Ground, Ground };

    final Address address;

    public AddressMeta(Address address) {
        this.address = address;
    }

    public Object[] values() {
        return ArrayOps.of(address.street(), address.city());
    }
}
