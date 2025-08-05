package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.*;

import java.sql.*;
import java.util.Collection;
import java.util.WeakHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/**
 * A student manager providing basic CRUD operations for instances of Student, and a read operation for instances of Degree.
 * @author jens dietrich
 */
public class StudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND THE APPLICATION CAN CONNECT TO IT WITH JDBC
    static {
        StudentDB.init();
    }

    // DO NOT REMOVE BLOCK ENDS HERE

    // THE FOLLOWING METHODS MUST BE IMPLEMENTED :


    //Cache for Student and Degree instances using WeakHashMap to allow garbage collection
    private static final Map<String, Student> studentCache = new WeakHashMap<>(); // ID, Student
    private static final Map<String, Degree> degreeCache = new WeakHashMap<>(); // ID, Degree

    /**
     * Return a student instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id the unique identifier of the student to retrieve; must not be null or empty
     * @return Student instance with the specified ID
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student fetchStudent(String id) throws NoSuchRecordException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Student ID must not be null or empty");
        }

        // Case already exists
        if (studentCache.containsKey(id)) return studentCache.get(id);

        // Fetch from the database
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM STUDENTS WHERE id = ?");
            stmt.setString(1, id);
            ResultSet results = stmt.executeQuery();

            // ID is unique, and none of provided record has null
            if (!results.next()) throw new NoSuchRecordException();

            // STUDENTS table is: (id, first_name, name, degree)
            String firstname = results.getString("first_name");
            String name = results.getString("name");
            String degreeID = results.getString("degree");

            Degree degree = fetchDegree(degreeID);

            // but Constructor is (id, name, firstName, degree), bad design, but I can not change template
            Student student = new Student(id, name, firstname, degree);

            studentCache.put(id, student);
            return student;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch student with ID: " + id, e);
        }
    }

    /**
     * Return a degree instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id the unique identifier of the degree to retrieve; must not be null or empty
     * @return Degree instance with the specified ID
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchDegree (followed by optional numbers if multiple tests are used)
     */
    public static Degree fetchDegree(String id) throws NoSuchRecordException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Degree ID must not be null or empty");
        }

        // Case already exists
        if (degreeCache.containsKey(id)) return degreeCache.get(id);

        // Fetch from the database
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM DEGREES WHERE id = ?");
            stmt.setString(1, id);
            ResultSet results = stmt.executeQuery();

            // ID is unique, and none of provided record has null
            if (!results.next()) throw new NoSuchRecordException();

            // DEGREES table is :(id, name)
            String name = results.getString("name");
            Degree degree =  new Degree(id, name);
            degreeCache.put(id, degree);
            return degree;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch degree with ID: " + id, e);
        }
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student student instance to be removed from database
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testRemove
     */
    public static void remove(Student student) throws NoSuchRecordException {
        if (student == null) {
            throw new IllegalArgumentException("student must not be null");
        }
        // Delete from database first
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM STUDENTS WHERE id = ?");
            stmt.setString(1, student.getId());
            int rowsAffected  = stmt.executeUpdate();
            if (rowsAffected == 0) throw new NoSuchRecordException();
            // rowsAffected should be 1, but enforcing database structure is not this functions responsibility
            studentCache.remove(student.getId()); // clear cache
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove student: " + student.getId(), e);
        }
    }

    /**
     * Update (synchronize) a student instance with the database.
     * The id will not be changed, but the values for first names or degree in the database might be changed by this operation.
     * After executing this command, the attribute values of the object and the respective database value are consistent.
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param student student instance to be updated in database
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testUpdate (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws NoSuchRecordException {
        if (student == null) {
            throw new IllegalArgumentException("student must not be null");
        }

        // not required, but help performance
        validateNameLengths(student.getName(), student.getFirstName());

        // Update database first
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            PreparedStatement stmt = conn.prepareStatement("UPDATE STUDENTS SET first_name = ?, name = ?, degree = ? WHERE id = ?");
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getDegree().getId());
            stmt.setString(4, student.getId());
            int rowsAffected  = stmt.executeUpdate();
            if (rowsAffected == 0) throw new NoSuchRecordException();
            // rowsAffected should be 1, but enforcing database structure is not this functions responsibility
            studentCache.put(student.getId(), student);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update student: " + student.getId(), e);
        }
    }

    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not being used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * If the Degree does not exist, create a new row in the database for it. 
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name last name of new student
     * @param firstName first name of new student
     * @param degree degree of new student
     * @return a freshly created student instance
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testNewStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student newStudent(String name,String firstName,Degree degree) {
        validateNameLengths(name, firstName);
        if (degree == null) throw new IllegalArgumentException("degree must not be null");

        Degree fixed;
        String degreeId = degree.getId();
        if (degreeId == null || !degreeId.matches("deg\\d{1,2}")) {
            // Case 1: No ID provided or invalid - generate new ID and create degree
            degreeId = nextDegreeId();
            System.err.println("Warning: degree has invalid ID, using: " + degreeId); // user need feedback
            fixed = new Degree(degreeId, degree.getName());
            insertDegree(fixed);
        } else if (degreeExists(degreeId)) {
            // Case 2: ID exists - use existing degree from database
            try {
                fixed = fetchDegree(degreeId);
            } catch (NoSuchRecordException e) {
                throw new RuntimeException("Inconsistent database state: ", e);
            }
        } else {
            // Case 3: ID provided but doesn't exist - create with provided ID
            fixed = new Degree(degreeId, degree.getName());
            insertDegree(fixed);
        }
        String studentId = nextStudentId();
        Student student = new Student(studentId, name, firstName, fixed);
        insertStudent(student);
        return student;
    }

    /**
     * Get all student ids currently being used in the database.
     * @return Collection<String>, use ArrayList to sort IDs (can be HashSet if uniqueness is primary concern)
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> fetchAllStudentIds() {
        List<String> ids = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("SELECT id FROM STUDENTS ORDER BY id");
            while (results.next()) {
                ids.add(results.getString("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all student IDs", e);
        }
        return ids;
    }

    /**
     * FOR TESTING ONLY - clears caches between tests
     * Should only be called from test fixtures (@BeforeEach/@AfterEach)
     */
    static void reset() {
        studentCache.clear();
        degreeCache.clear();
    }

    /**
     * Helper method to ensure names and first names of Student can only be max 10 characters long.
     * While database has its own length constraint, this provides early exit and explicit feedback.
     * As database does not have NOT NULL constraints, it will not prevent null values.
     * @param name name of Student
     * @param firstName first name of Student
     */
    private static void validateNameLengths(String name, String firstName){
        if (name != null && name.length() > 10) {
            throw new IllegalArgumentException("student name must be up to 10 characters");
        }
        if (firstName != null && firstName.length() > 10) {
            throw new IllegalArgumentException("student first name must be up to 10 characters");
        }
    }

    /**
     * Generates the next available student ID by finding the highest existing ID and incrementing it.
     * Handles the case where the STUDENTS table is empty by returning "id0".
     * Uses MAX(id) approach which does not fill gaps in existing IDs.
     * @return the next available student ID in format "id{number}"
     * @throws RuntimeException if a database error occurs while querying for the maximum ID
     */
    private static String nextStudentId() {
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("SELECT MAX(id) FROM STUDENTS"); // will be id or null
            if (results.next()) {
                String id = results.getString(1);
                if (id == null) return "id0"; // table is empty
                String number = id.substring(2);
                int next = Integer.parseInt(number) + 1;
                return "id" + next;
            }
            return "id0"; // Should never reach here
        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate next student ID", e);
        }
    }

    /**
     * Generates the next available degree ID by finding the highest existing ID and incrementing it.
     * Handles the case where the DEGREES table is empty by returning "deg0".
     * @return the next available Degree ID in format "deg{number}"
     * @throws RuntimeException if a database error occurs while querying for the maximum ID
     */
    private static String nextDegreeId() {
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("SELECT MAX(id) FROM DEGREES"); // will be id or null
            if (results.next()) {
                String id = results.getString(1);
                if (id == null) return "deg0"; // table is empty
                String number = id.substring(3);
                int next = Integer.parseInt(number) + 1;
                return "deg" + next;
            }
            return "deg0"; // Should never reach here
        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate next degree ID", e);
        }
    }

    /**
     * Inserts a new student record into the STUDENTS table and updates the cache.
     * @param student the student to insert
     * @throws RuntimeException if the database insertion fails
     */
    private static void insertStudent(Student student) {
        if (student == null) throw new IllegalArgumentException("student must not be null");
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO STUDENTS (id, first_name, name, degree) VALUES (?,?,?,?)"
            );
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getName());
            stmt.setString(4, student.getDegree().getId());
            int rowsAffected  = stmt.executeUpdate();
            if (rowsAffected == 0) throw new RuntimeException("Failed to insert student");
            studentCache.put(student.getId(), student);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert student", e);
        }
    }

    /**
     * Inserts a new degree record into the DEGREES table and updates the cache.
     * @param degree the degree to insert
     * @throws RuntimeException if the database insertion fails
     */
    private static void insertDegree(Degree degree) {
        if (degree == null) throw new IllegalArgumentException("degree must not be null");
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")){
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO DEGREES (id, name) VALUES (?,?)");
            stmt.setString(1, degree.getId());
            stmt.setString(2, degree.getName());
            int rowsAffected  = stmt.executeUpdate();
            if (rowsAffected == 0) throw new RuntimeException("Failed to insert degree");
            degreeCache.put(degree.getId(), degree);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert degree", e);
        }
    }

    /**
     * Checks if a degree with the specified ID exists in the DEGREES table.
     * @param degreeId the degree ID to check
     * @return true if degree exists, false otherwise
     * @throws RuntimeException if database error occurs
     */
    private static boolean degreeExists(String degreeId) {
        if (degreeId == null) throw new IllegalArgumentException("degreeId must not be null");
        try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:studentdb")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM DEGREES WHERE id = ?");
            stmt.setString(1, degreeId);
            ResultSet results = stmt.executeQuery();
            if (results.next()) { // COUNT(*) returns int
                return results.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check degree existence", e);
        }
    }

//    /**
//     * Connection reuse optimization for fetchStudent().
//     * Performance impact varies by environment - kept for potential future use.
//     * Currently unused due to mixed performance results across different systems.
//     * <br>
//     * This function is designed to be used by fetchStudent() when internally fetching degree data.
//     * The connection can be reused safely, with connection closing handled by the caller.
//     * Returns a degree instance with values from the row with the respective id in the database.
//     * If an instance with this id already exists, returns the existing instance without creating a second one.
//     * @param id the unique identifier of the degree to retrieve; must not be null or empty
//     * @param conn Connection passed from fetchStudent
//     * @return Degree instance with the specified ID
//     * @throws NoSuchRecordException if no record with such an id exists in the database
//     */
//    private static Degree fetchDegreeWithConnection(String id, Connection conn) throws NoSuchRecordException {
//        // Case already exists
//        if (degreeCache.containsKey(id)) return degreeCache.get(id);
//        // Fetch from the database
//        try {
//            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM DEGREES WHERE id = ?");
//            stmt.setString(1, id);
//            ResultSet results = stmt.executeQuery();
//            // ID is unique, and none of provided record has null
//            if (!results.next()) throw new NoSuchRecordException();
//            // DEGREES table is : (id, name)
//            String name = results.getString("name");
//            Degree degree =  new Degree(id, name);
//            degreeCache.put(id, degree);
//            return degree;
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to fetch degree with ID: " + id, e);
//        }
//    }

}
