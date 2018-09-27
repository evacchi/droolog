package org.drools.droolog.meta.lib.v4;

import static org.drools.droolog.meta.lib.v4.TermState.Ground;
import static org.drools.droolog.meta.lib.v4.TermState.Structure;

public class PersonMeta {
    public static final int
            firstName = 0,
            lastName = 1,
            address = 2;

    public int[] terms = { Ground, Ground, Structure(2) };
}
