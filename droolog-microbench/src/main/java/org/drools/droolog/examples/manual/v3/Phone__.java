package org.drools.droolog.examples.manual.v3;

import org.drools.droolog.meta.lib.v3.*;

public class Phone__ implements Structure.Factory<Phone> {

    final static Phone__ Instance = new Phone__();

    @Override
    public Object valueAt(Phone o, int index) {
        switch (index) {
            case Phone_.number:
                return o.number();
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
        Phone phone = new Phone(null);
        phone.meta().setTermState(Phone_.number, TermState.Free);
        return phone;
    }
}
