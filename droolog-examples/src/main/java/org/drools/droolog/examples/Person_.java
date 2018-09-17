package org.drools.droolog.examples;

import org.drools.droolog.meta.lib4.TermState;

public class Person_ {

    public static final int name = 0;

    public static final int age = 1;

    public static final int structureSize = 2;

    private Person parent;

    private org.drools.droolog.meta.lib4.TermState name_term = org.drools.droolog.meta.lib4.TermState.Free;

    private org.drools.droolog.meta.lib4.TermState age_term = org.drools.droolog.meta.lib4.TermState.Free;

    public Person_(Person parent) {
        this.parent = parent;
    }

    public org.drools.droolog.meta.lib4.TermState getTerm(int index) {
        switch(index) {
            case name:
                return name_term;
            case age:
                return age_term;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setTerm(int index, TermState value) {
        switch(index) {
            case name:
                name_term = value;
                break;
            case age:
                age_term = value;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setValue(int index, Object value) {
        switch(index) {
            case name:
                parent.name = (String) value;
                break;
            case age:
                parent.age = (Integer) value;
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    public Object getValue(int index) {
        switch(index) {
            case name:
                return parent.name;
            case age:
                return parent.age;
            default:
                throw new IllegalArgumentException();
        }
    }
}
