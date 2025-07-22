package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.*;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Return the existing instance, it suggests this class need to Cache instances
     * Which likely connect to memory leak document later??
     * For now, lets use map of id and instance with private static
     * Later study and concern WeakReference<???> for instance
     */
    private static final Map<String, Student> studentCache = new HashMap<>(); // ID, Student
    private static final Map<String, Degree> degreeCache = new HashMap<>(); // ID, Degree

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
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testUpdate (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws NoSuchRecordException {
        if (student == null) {
            throw new IllegalArgumentException("student must not be null");
        }

        /*
        * ASK!!!!!!!!!!!!!!!!!!!
        * - how i should treat name? can update or not?
        * - how should i treat invalid degree? can i leave it to database?
        * - if i handle? is it illigal? nosuch? or update
        * */


    }


    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not being used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * If the Degree does not exist, create a new row in the database for it. 
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name
     * @param firstName
     * @param degree
     * @return a freshly created student instance
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testNewStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student newStudent(String name,String firstName,Degree degree) {
        return null;
    }

    /**
     * Get all student ids currently being used in the database.
     * @return
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> fetchAllStudentIds() {
        return null;
    }



}
