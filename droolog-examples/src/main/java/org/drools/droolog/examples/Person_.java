package org.drools.droolog.examples;

import org.drools.droolog.meta.lib4.Term;

public class Person_ {
    public final static int Name = 0;
    public final static int Age = 1;

    Term.Type name_type;
    float name_degree;
    Term.Type age_type;
    float age_degree;

    public Term.Type type(int idx) {
        switch (idx) {
            case Name:
                return name_type;
            case Age:
                return age_type;
            default:
                throw new ArrayIndexOutOfBoundsException(idx);
        }
    }

    public float degree(int idx) {
        switch (idx) {
            case Name:
                return name_degree;
            case Age:
                return age_degree;
            default:
                throw new ArrayIndexOutOfBoundsException(idx);
        }
    }
}
