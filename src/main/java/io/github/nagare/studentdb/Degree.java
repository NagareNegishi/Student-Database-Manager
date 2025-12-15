package io.github.nagare.studentdb;

import java.util.Objects;

/**
 * Represents a degree program.
 */
public class Degree {
    private String id;
    private String name;

    public Degree() {
    }

    public Degree(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Degree degree = (Degree) o;
        return Objects.equals(id, degree.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Degree{id='" + id + "', name='" + name + "'}";
    }
}