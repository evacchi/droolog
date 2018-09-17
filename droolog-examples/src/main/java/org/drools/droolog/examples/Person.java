package org.drools.droolog.examples;

import org.drools.droolog.meta.lib.Metable;

@Metable
public class Person {
    Person_ meta =  new Person_();
    String name;
    Integer age;
}
