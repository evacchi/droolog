package org.drools.droolog.examples;

import org.drools.droolog.meta.lib.ObjectTerm;

@ObjectTerm
public interface Person {
    String firstName();
    String secondName();
    @ObjectTerm Address address();
}
