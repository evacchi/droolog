package org.drools.droolog.examples.v2;

import org.drools.droolog.meta.lib.v2.ObjectTerm;

@ObjectTerm
public interface Person {
    String firstName();
    String lastName();
    @ObjectTerm
    Address address();
}
