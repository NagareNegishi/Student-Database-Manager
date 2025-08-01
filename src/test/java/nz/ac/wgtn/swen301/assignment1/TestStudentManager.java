package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


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

    @BeforeEach
    public void resetCache() {
        StudentManager.reset();
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
    public void testFetchStudent2() {
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
    public void testFetchStudent3() {
        // Test case 3: IllegalArgumentException for null/empty ID
        assertThrows(IllegalArgumentException.class, () -> StudentManager.fetchStudent(null));
        assertThrows(IllegalArgumentException.class, () -> StudentManager.fetchStudent(""));
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
    public void testFetchDegree2() {
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
    public void testFetchDegree3() {
        // Test case 3: IllegalArgumentException for null/empty ID
        assertThrows(IllegalArgumentException.class, () -> StudentManager.fetchDegree(null));
        assertThrows(IllegalArgumentException.class, () -> StudentManager.fetchDegree(""));
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
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchStudent("id0"));
    }

    @Test
    public void testRemove2() {
        // Test case 2: NoSuchRecordException for invalid student
        Student invalidStudent = new Student("id-1", "invalid", "invalid", null);
        assertThrows(NoSuchRecordException.class, () -> StudentManager.remove(invalidStudent));
    }

    @Test
    public void testRemove3() {
        // Test case 3: IllegalArgumentException for null
        assertThrows(IllegalArgumentException.class, () -> StudentManager.remove(null));
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
        assertThrows(NoSuchRecordException.class, () -> StudentManager.update(invalidStudent));
    }

    @Test
    public void testUpdate3() throws Exception {
        // Test case 3: IllegalArgumentException for null and long name
        assertThrows(IllegalArgumentException.class, () -> StudentManager.update(null));

        Student student = StudentManager.fetchStudent("id0");
        student.setName("longlonglonglong");
        assertThrows(IllegalArgumentException.class, () -> StudentManager.update(student));

        student.setName("long");
        student.setFirstName("longlonglonglong");
        assertThrows(IllegalArgumentException.class, () -> StudentManager.update(student));
    }

    // TestStudentManager::testNewStudent

    @Test
    public void testNewStudent1() throws Exception {
        // Test case 1: Valid student, existing degree
        Degree degree = StudentManager.fetchDegree("deg0");
        Student student1 = StudentManager.newStudent("valid", "yes", degree);
        Student student2 = StudentManager.fetchStudent(student1.getId());

        assertNotNull(student1);
        assertEquals(student1.getId(), student2.getId());
        assertEquals("yes", student1.getFirstName());
        assertEquals("valid", student1.getName());
        assertEquals("deg0", student1.getDegree().getId());
        assertEquals("BSc Computer Science", student1.getDegree().getName());

        assertEquals("yes", student2.getFirstName());
        assertEquals("valid", student2.getName());
        assertEquals("deg0", student2.getDegree().getId());
        assertEquals("BSc Computer Science", student2.getDegree().getName());
    }

    @Test
    public void testNewStudent2() throws Exception {
        // Test case 2: Valid student, new degree
        Degree degree = new Degree("deg99", "test"); // id varchar(5), 99 is max
        Student student1 = StudentManager.newStudent("valid", "yes", degree);
        Student student2 = StudentManager.fetchStudent(student1.getId());

        assertNotNull(student1);
        assertEquals(student1.getId(), student2.getId());
        assertEquals("yes", student1.getFirstName());
        assertEquals("valid", student1.getName());
        assertEquals("deg99", student1.getDegree().getId());
        assertEquals("test", student1.getDegree().getName());

        assertEquals("yes", student2.getFirstName());
        assertEquals("valid", student2.getName());
        assertEquals("deg99", student2.getDegree().getId());
        assertEquals("test", student2.getDegree().getName());
    }

    @Test
    public void testNewStudent3() throws Exception {
        // Test case 3: Valid student, new degree with null id
        Degree degree = new Degree(null, "test"); // id varchar(5), 99 is max
        Student student1 = StudentManager.newStudent("valid", "yes", degree);
        Student student2 = StudentManager.fetchStudent(student1.getId());

        assertNotNull(student1);
        assertEquals(student1.getId(), student2.getId());
        assertEquals(student1.getDegree().getId(), student2.getDegree().getId());
        assertTrue(student1.getDegree().getId().startsWith("deg"));
        assertEquals("yes", student1.getFirstName());
        assertEquals("valid", student1.getName());
        assertEquals("test", student1.getDegree().getName());

        assertEquals("yes", student2.getFirstName());
        assertEquals("valid", student2.getName());
        assertEquals("test", student2.getDegree().getName());
    }

    @Test
    public void testNewStudent4() throws Exception {
        // Test case 4: IllegalArgumentException for null and long name
        Degree degree = StudentManager.fetchDegree("deg0");
        assertThrows(IllegalArgumentException.class, () ->
                StudentManager.newStudent("longlonglonglong", "yes", degree));
        assertThrows(IllegalArgumentException.class, () -> StudentManager.newStudent("valid", "longlonglonglong", degree));
    }

    @Test
    public void testNewStudent5() {
        // Test case 5: IllegalArgumentException for null degree
        assertThrows(IllegalArgumentException.class, () -> StudentManager.newStudent("valid", "yes", null));
    }

    @Test
    public void testNewStudent6() throws Exception {
        // Test case 6: Valid student, new degree with invalid id
        Degree degree = new Degree("invalid999", "test"); // id varchar(5), 99 is max
        Student student1 = StudentManager.newStudent("valid", "yes", degree);
        Student student2 = StudentManager.fetchStudent(student1.getId());

        assertNotNull(student1);
        assertEquals(student1.getId(), student2.getId());
        assertEquals("yes", student1.getFirstName());
        assertEquals("valid", student1.getName());
        assertEquals("test", student1.getDegree().getName());

        assertTrue(student1.getDegree().getId().matches("deg\\d{1,2}"));
        assertNotEquals("invalid999", student1.getDegree().getId());

        assertEquals("yes", student2.getFirstName());
        assertEquals("valid", student2.getName());
        assertEquals("test", student2.getDegree().getName());
        assertEquals(student1.getDegree().getId(), student2.getDegree().getId());
    }

    // TestStudentManager::testFetchAllStudentIds

    @Test
    public void testFetchAllStudentIds1(){
        // Test case 1: Verify set
        Collection<String> ids = StudentManager.fetchAllStudentIds();
        assertNotNull(ids);
        assertEquals(10000, ids.size());
        assertTrue(ids.contains("id0"));
        assertTrue(ids.contains("id9999"));
        assertFalse(ids.contains("id-1"));
    }

    @Test
    public void testFetchAllStudentIds2() throws NoSuchRecordException {
        // Test case 2: Verify set after insert new student
        Degree degree = StudentManager.fetchDegree("deg0");
        Student student = StudentManager.newStudent("valid", "yes", degree);
        Collection<String> ids = StudentManager.fetchAllStudentIds();
        assertEquals(10001, ids.size());
        assertTrue(ids.contains(student.getId()));
        assertTrue(ids.contains("id0"));
        assertTrue(ids.contains("id9999"));
        assertFalse(ids.contains("id"));
        assertFalse(ids.contains("id1111111111111"));
    }

    @Test
    public void testPerformance() throws NoSuchRecordException {
        // It should be able to handle e 500 random queries per second
        Collection<String> ids = StudentManager.fetchAllStudentIds();
        List<String> idList = new ArrayList<>(ids); // for iteration, convert to list
        int listSize = idList.size();
        Random random = new Random();
        int count = 0;
        long startTime = System.nanoTime();
        long endTime = startTime + 1_000_000_000L; // 1 second in nanoseconds

        while(System.nanoTime() < endTime) {
            String randomId = idList.get(random.nextInt(listSize));
            StudentManager.fetchStudent(randomId);
            count++;
        }
        System.err.println("Performance test result: " + count + " queries/second (target: 500+)");
        assertTrue(count >= 500, "Expected at least 500 queries, but got " + count);
    }
}
