package org.drools.droolog.meta;

import org.drools.droolog.meta.lib.Generated;

// this is the public interface (with getters/setters)
@Generated
public class PersonObject extends Person {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String toString() {
        return "PersonTerm{" +
                "name='" + name + '\'' +
                ", age=" + age +
                "} ";
    }

}
