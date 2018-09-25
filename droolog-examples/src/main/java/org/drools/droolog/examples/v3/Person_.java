package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib4.Structure;
import org.drools.droolog.meta.lib4.TermState;

public class Person_ implements Structure.Meta<Person> {

    public static final int name = 0;

    public static final int address = 1;

    @Override
    public int size() {
        return 2;
    }

    private TermState name_term = TermState.Ground;

    private TermState address_term = TermState.Structure;

    public TermState getTermState(int index) {
        switch(index) {
            case name:
                return name_term;
            case address:
                return address_term;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setTermState(int index, TermState value) {
        switch(index) {
            case name:
                name_term = value;
                break;
            case address:
                address_term = value;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Structure.Factory<Person> structure() {
        return Person__.Instance;
    }
}
