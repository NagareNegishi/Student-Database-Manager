package io.github.nagare.studentdb;

import java.util.Objects;

/**
 * Represents a student record.
 */
public class Student {
    private String id;
    private String name;
    private String firstName;
    private Degree degree;

    public Student() {
    }

    // parameter order: name comes BEFORE firstName
    public Student(String id, String name, String firstName, Degree degree) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.degree = degree;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Student{id='" + id + "', firstName='" + firstName +
                "', name='" + name + "', degree=" + degree + "}";
    }
}