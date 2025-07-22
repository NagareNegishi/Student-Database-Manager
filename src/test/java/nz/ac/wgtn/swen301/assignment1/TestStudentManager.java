package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentManager, to be extended.
 */
public class TestStudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND IN ITS INITIAL STATE BEFORE EACH TEST RUNS
    @BeforeEach
    public  void init () {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    @Test
    public void dummyTest() throws Exception {
        Student student = new StudentManager().fetchStudent("id42");
        // THIS WILL INITIALLY FAIL !!
        assertNotNull(student);
    }

    // TestStudentManager::testFetchStudent
    // id0 will always be: James Smith, deg0 (BSc Computer Science)
    // id1 will always be: John Jones, deg1 (BSc Computer Graphics)

    @Test
    public void testFetchStudent1() throws Exception {
        // Test case 1: Valid ID retrieval and data correctness
        Student student = StudentManager.fetchStudent("id0");
        assertNotNull(student);
        assertEquals("id0", student.getId());
        assertEquals("James", student.getFirstName());
        assertEquals("Smith", student.getName());
        assertEquals("deg0", student.getDegree().getId());
        assertEquals("BSc Computer Science", student.getDegree().getName());
    }

    @Test
    public void testFetchStudent2()  throws Exception {
        // Test case 2: NoSuchRecordException for invalid ID
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchStudent("id-1"); // negative
        });
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchStudent("id10000"); // default table is till 9999
        });
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchStudent("ID0"); // wrong format
        });
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchStudent("0"); // wrong format
        });
    }

    @Test
    public void testFetchStudent3()  throws Exception {
        // Test case 3: IllegalArgumentException for null/empty ID
        assertThrows(IllegalArgumentException.class, () -> {
            StudentManager.fetchStudent(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            StudentManager.fetchStudent("");
        });
    }

    @Test
    public void testFetchStudent4()  throws Exception {
        // Test case 4: Caching behavior (must be same instance)
        Student student1 = StudentManager.fetchStudent("id0");
        Student student2 = StudentManager.fetchStudent("id0");
        // it should be same instance, so use same not equal
        assertSame(student1, student2);
    }

    // TestStudentManager::testFetchDegree
    // deg0 will always be: BSc Computer Science

    @Test
    public void testFetchDegree1() throws Exception {
        // Test case 1: Valid ID retrieval and data correctness
        Degree degree = StudentManager.fetchDegree("deg0");
        assertNotNull(degree);
        assertEquals("deg0", degree.getId());
        assertEquals("BSc Computer Science", degree.getName());
    }

    @Test
    public void testFetchDegree2() throws Exception {
        // Test case 2: NoSuchRecordException for invalid ID
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchDegree("deg-1"); // negative
        });
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchDegree("deg10000"); // default table is till 9999
        });
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchDegree("DEG0"); // wrong format
        });
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchDegree("0"); // wrong format
        });
    }

    @Test
    public void testFetchDegree3() throws Exception {
        // Test case 3: IllegalArgumentException for null/empty ID
        assertThrows(IllegalArgumentException.class, () -> {
            StudentManager.fetchDegree(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            StudentManager.fetchDegree("");
        });
    }

    @Test
    public void testFetchDegree4() throws Exception {
        // Test case 4: Caching behavior (must be same instance)
        Degree degree1 = StudentManager.fetchDegree("deg0");
        Degree degree2 = StudentManager.fetchDegree("deg0");
        // it should be same instance, so use same not equal
        assertSame(degree1, degree2);
    }

    // TestStudentManager::testRemove

    @Test
    public void testRemove1() throws Exception {
        // Test case 1: Valid student
        Student student = StudentManager.fetchStudent("id0");
        StudentManager.remove(student);
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.fetchStudent("id0");
        });
    }

    @Test
    public void testRemove2() throws Exception {
        // Test case 2: NoSuchRecordException for invalid student
        Student invalidStudent = new Student("id-1", "invalid", "invalid", null);
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.remove(invalidStudent);
        });
    }

    @Test
    public void testRemove3() throws Exception {
        // Test case 3: IllegalArgumentException for null
        assertThrows(IllegalArgumentException.class, () -> {
            StudentManager.remove(null);
        });
    }

    //TestStudentManager::testUpdate

    @Test
    public void testUpdate1() throws Exception {
        // Test case 1: Valid student
        // id0 will always be: James Smith, deg0 (BSc Computer Science)
        Student student = StudentManager.fetchStudent("id0");

        // case no change
        StudentManager.update(student);
        Student updated = StudentManager.fetchStudent("id0");
        assertEquals("James", updated.getFirstName());
        assertEquals("Smith", updated.getName());
        assertEquals("deg0", updated.getDegree().getId());

        // update all attributes
        student.setFirstName("abc");
        student.setName("xyz");
        student.setDegree(StudentManager.fetchDegree("deg1")); // BSc Computer Graphics
        StudentManager.update(student);
        updated = StudentManager.fetchStudent("id0");
        assertEquals("abc", updated.getFirstName());
        assertEquals("xyz", updated.getName());
        assertEquals("deg1", updated.getDegree().getId());
        assertEquals("BSc Computer Graphics", updated.getDegree().getName());
    }

    @Test
    public void testUpdate2() throws Exception {
        // Test case 2: NoSuchRecordException for invalid update
        Student invalidStudent = new Student("id-1", "Smith", "James", StudentManager.fetchDegree("deg0"));
        assertThrows(NoSuchRecordException.class, () -> {
            StudentManager.update(invalidStudent);
        });
    }

    @Test
    public void testUpdate3() throws Exception {
        // Test case 3: IllegalArgumentException for null and long name
        assertThrows(IllegalArgumentException.class, () -> {
            StudentManager.update(null);
        });

        Student student = StudentManager.fetchStudent("id0");
        student.setName("longlonglonglong");
        assertThrows(IllegalArgumentException.class, () -> {
            StudentManager.update(student);
        });

        student.setName("long");
        student.setFirstName("longlonglonglong");
        assertThrows(IllegalArgumentException.class, () -> {
            StudentManager.update(student);
        });
    }


}
