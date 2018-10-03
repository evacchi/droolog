package org.drools.droolog.meta.lib.v4;

import static org.drools.droolog.meta.lib.v4.TermState.Ground;
import static org.drools.droolog.meta.lib.v4.TermState.Structure;

public class PersonMeta {

    public static final int
            firstName = 0,
            lastName = 1,
            address = 2,
            $size = 3;



    public int[] terms = {Ground, Ground, Structure(3)};

    private final Person p;

    PersonMeta(Person p) {
        this.p = p;
    }

    public Object[] values() {
        return ArrayOps.concat(
                ArrayOps.of(p.firstName(), p.lastName(), null),
                p.address().meta.values());
    }

    public static Person create(Object... values) {
        return new Person(
                (String) values[0],
                (String) values[1],
                new Address(
                        (String) values[3],
                        (String) values[4]));
    }
}

class PersonDescr {
    public static final int
            firstName = 0,
            lastName = 1,
            address = 2;
    public static Person create(Object... values) {
        return new Person(
                (String) values[0],
                (String) values[1],
                new Address(
                        (String) values[3],
                        (String) values[4]));
    }

}