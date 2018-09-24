package org.drools.droolog.examples.v4;

import org.drools.droolog.meta.lib4.Structure;
import org.drools.droolog.meta.lib4.TermState;

public class Phone_ implements Structure.Meta<Phone> {

    final static int number = 0;
    TermState phone_state = TermState.Ground;

    @Override
    public TermState getTermState(int index) {
        switch (index) {
            case number:
                return phone_state;
                default:
                    throw new IllegalArgumentException();
        }
    }

    @Override
    public void setTermState(int index, TermState value) {
        switch (index) {
            case number:
                phone_state = value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }

    }

    @Override
    public Structure.Factory<Phone> structure() {
        return Phone__.Instance;
    }

    @Override
    public int size() {
        return 1;
    }
}
