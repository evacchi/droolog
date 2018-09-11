package org.drools.droolog.processor;

class Fields {

    static String getterNameOf(String fieldName) {
        return "get" + capitalized(fieldName);
    }

    static String setterNameOf(String fieldName) {
        return "set" + capitalized(fieldName);
    }

    static String capitalized(String original) {
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
