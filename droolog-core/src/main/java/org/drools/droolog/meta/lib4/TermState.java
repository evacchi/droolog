package org.drools.droolog.meta.lib4;

public interface TermState {
    default boolean isVariable() { return false; }
    default boolean isGround() { return false; }
    default boolean isStructure() { return false; }
    default boolean isFree() { return false; }

    TermState Free     = new Variable(null) {
        @Override
        public boolean isFree() {
            return true;
        }

        @Override
        public String toString() {
            return "Free";
        }
    };

    TermState Ground   = new TermState() {
        @Override
        public boolean isGround() {
            return true;
        }
        @Override
        public String toString() {
            return "Ground";
        }

    };

    TermState Structure = new TermState() {
        @Override
        public boolean isStructure() {
            return true;
        }
        @Override
        public String toString() {
            return "Structure";
        }

    };

    static Variable Variable(String name) {
        return new Variable(name);
    }

    class Variable implements TermState {
        public String name;
        public Variable(String name) {
            this.name = name;
        }

        @Override
        public boolean isVariable() {
            return true;
        }

        @Override
        public String toString() {
            return "V:"+name;
        }

    }
}
