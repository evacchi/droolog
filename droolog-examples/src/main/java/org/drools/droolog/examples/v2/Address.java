package org.drools.droolog.examples.v2;

import org.drools.droolog.meta.lib.v2.ObjectTerm;

@ObjectTerm
public interface Address {
    String street();
    String city();
    String zip();
    String country();
}
