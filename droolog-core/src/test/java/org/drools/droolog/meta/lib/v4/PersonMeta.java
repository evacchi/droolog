package org.drools.droolog.meta.lib.v4;

import static org.drools.droolog.meta.lib.v4.TermState.Ground;
import static org.drools.droolog.meta.lib.v4.TermState.Structure;

public class PersonMeta {

    public static final int
            firstName = 0,
            lastName = 1,
            address = 2;

    public int[] terms = {Ground, Ground, Structure(2)};

    public static Object[] valuesOf(Object o) {
        Person p = (Person) o;
        return new Object[]{
                p.firstName(),
                p.lastName(),
                null,
                p.address().street(),
                p.address().city()
        };
    }

}
