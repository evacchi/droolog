package org.drools.droolog.examples;

import org.drools.droolog.meta.lib.ObjectTerm;

@ObjectTerm
public interface Address {
    String street();
    String city();
    String zip();
    String country();
}
