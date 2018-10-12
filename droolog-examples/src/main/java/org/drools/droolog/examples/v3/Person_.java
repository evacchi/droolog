package org.drools.droolog.examples.v3;

import org.drools.droolog.meta.lib.v3.Structure;
import org.drools.droolog.meta.lib.v3.TermState;

public class Person_ implements Structure.Meta<Person> {

    public static final int name = 0;
    public static final int lastName = 1;
    public static final int address = 2;
    public static final int phone = 3;

    @Override
    public int size() {
        return 4;
    }

    private TermState name_term = TermState.Ground;
    private TermState lastName_term = TermState.Ground;
    private TermState address_term = TermState.Structure;
    private TermState phone_term = TermState.Structure;

    public TermState getTermState(int index) {
        switch(index) {
            case name:
                return name_term;
            case lastName:
                return lastName_term;
            case address:
                return address_term;
            case phone:
                return phone_term;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setTermState(int index, TermState value) {
        switch(index) {
            case name:
                name_term = value;
                break;
            case lastName:
                lastName_term = value;
            case address:
                address_term = value;
                break;
            case phone:
                phone_term = value;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Structure.Factory<Person> structure() {
        return Person__.Instance;
    }
}
