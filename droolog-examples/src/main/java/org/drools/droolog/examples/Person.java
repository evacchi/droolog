package org.drools.droolog.examples;

import org.drools.droolog.meta.lib.Meta;
import org.drools.droolog.meta.lib.Metable;

@Metable
public class Person {
    @Meta Person_ meta = new Person_();
    String name;
    Integer age;
}
