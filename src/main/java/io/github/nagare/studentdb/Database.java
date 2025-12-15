package io.github.nagare.studentdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Utility class to initialize the in-memory Derby database.
 */
public class Database {

    private static final String DB_URL = "jdbc:derby:memory:studentdb";

    private static final String[] LAST_NAMES = {
            "Smith", "Jones", "Taylor", "Muller", "Tipene", "MacDonald",
            "Smith", "Ramirez", "Wang", "Singh", "Petersen", "da Silva",
            "Kumar", "Patel", "Hashimoto", "Wakanabe", "Kim", "Hansen", "Mobutu"
    };

    private static final String[] FIRST_NAMES = {
            "James", "John", "Janice", "Max", "Keira", "Tim", "Tom",
            "Kate", "Perry", "Alex", "Dave", "Thomas", "Sue", "Monoa", "Joao"
    };

    private static final String[][] DEGREES = {
            {"deg0", "BSc Computer Science"},
            {"deg1", "BSc Computer Graphics"},
            {"deg2", "BE Cybersecurity"},
            {"deg3", "BE Software Engineering"},
            {"deg4", "BSc Mathematics"},
            {"deg5", "BSc Chemistry"},
            {"deg6", "BA Art"},
            {"deg7", "BA Philosophy"},
            {"deg8", "BCom Finance"},
            {"deg9", "BCom Marketing"}
    };

    /**
     * Initialize or reset the database.
     * Creates tables and populates with test data.
     */
    public static void init() {
        try {
            // Try to drop existing tables
            try (Connection conn = DriverManager.getConnection(DB_URL + ";create=true");
                 Statement stmt = conn.createStatement()) {

                try {
                    stmt.execute("DROP TABLE STUDENTS");
                } catch (Exception e) {
                    // Table might not exist, ignore
                }

                try {
                    stmt.execute("DROP TABLE DEGREES");
                } catch (Exception e) {
                    // Table might not exist, ignore
                }

                // Create DEGREES table
                stmt.execute(
                        "CREATE TABLE DEGREES (" +
                                "id VARCHAR(50) PRIMARY KEY, " +
                                "name VARCHAR(255) NOT NULL)"
                );

                // Create STUDENTS table
                stmt.execute(
                        "CREATE TABLE STUDENTS (" +
                                "id VARCHAR(50) PRIMARY KEY, " +
                                "first_name VARCHAR(100) NOT NULL, " +
                                "name VARCHAR(100) NOT NULL, " +
                                "degree VARCHAR(50) NOT NULL)"
                );

                // Insert degrees
                for (String[] degree : DEGREES) {
                    stmt.execute(String.format(
                            "INSERT INTO DEGREES (id, name) VALUES ('%s', '%s')",
                            degree[0], degree[1]
                    ));
                }

                // Insert students (10,000 records)
                for (int i = 0; i < 10000; i++) {
                    String id = "id" + i;
                    String firstName = FIRST_NAMES[i % FIRST_NAMES.length];
                    String lastName = LAST_NAMES[i % LAST_NAMES.length];
                    String degreeId = "deg" + (i % 10);

                    stmt.execute(String.format(
                            "INSERT INTO STUDENTS (id, first_name, name, degree) " +
                                    "VALUES ('%s', '%s', '%s', '%s')",
                            id, firstName, lastName, degreeId
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}