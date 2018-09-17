package org.drools.droolog.meta.lib;

import java.util.Objects;

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
        return "PersonObject{" +
                "name='" + name + '\'' +
                ", age=" + age +
                "} ";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PersonObject)) return false;
        PersonObject p = (PersonObject) obj;
        return Objects.equals(this.getName(), p.getName())
                && Objects.equals(this.getAge(), p.getAge());
    }
}
