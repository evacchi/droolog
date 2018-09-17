package org.drools.droolog.meta.lib4;

public interface TermState {
    TermState Free     = new TermState() {};
    TermState Ground   = new TermState() {};
    TermState Unkwnown = new TermState() {};
    static Variable Variable(String name) {
        return new Variable(name);
    }
    final class Variable implements TermState {
        public String name;
        public Variable(String name) {
            this.name = name;
        }
    }
}
