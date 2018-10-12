package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.Structure;
import org.drools.droolog.meta.lib.v3.TermState;

import static org.drools.droolog.examples.v3.Phone_.*;

public class Phone__ implements Structure.Factory<Phone> {

    final static Phone__ Instance = new Phone__();

    @Override
    public Object valueAt(Phone a, int index) {
        switch (index) {
            case number:
                return a.number();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Phone of(Object... terms) {
        return new Phone((String) terms[0]);
    }

    @Override
    public Phone variable() {
        Phone address = new Phone(null);
        address.meta().setTermState(number, TermState.Free);
        return address;
    }
}
