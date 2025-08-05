package nz.ac.wgtn.swen301.assignment1.cli;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import org.apache.commons.cli.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class StudentManagerApp {

    // THE FOLLOWING METHOD MUST BE IMPLEMENTED
    /**
     * Executable: the user will provide argument(s) and print details to the console as described in the assignment brief,
     * E.g. a user could invoke this by running "java -cp <someclasspath> <arguments></arguments>"
     * @param arg command line arguments
     */
    public static void main (String[] arg) {
        Options options = new Options();
        options.addOption("select", true, "display the respective student record");
        options.addOption("all", false, " display all student records");
        options.addOption("export", true, "write all student records to a file in CSV format");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, arg);
            if (line.hasOption("select")) {
                handleSelect(line.getOptionValue("select"));
            } else if (line.hasOption("all")) {
                handleAll();
            } else if (line.hasOption("export")) {
                handleExport(line.getOptionValue("export"));
            } else {
                System.out.println("Please provide a valid option: -select, -all, or -export");
            }
        }
        catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }

    /**
     * Handle -select option: display single student record
     * @param id the student ID to fetch
     */
    private static void handleSelect(String id) {
        try {
            Student student = StudentManager.fetchStudent(id);
            System.out.println(student.getId() + ", " +  student.getFirstName() + ", " + student.getName());
        } catch (NoSuchRecordException e) {
            System.err.println("No student found with ID: " + id);
        } catch (RuntimeException e) {
            System.err.println("Database error occurred: " + e.getMessage());
        }
    }

    /**
     * Handle -all option: display all student records, no sorting required
     */
    private static void handleAll() {
        Collection<String> ids = StudentManager.fetchAllStudentIds();
        for (String id: ids) {
            try {
                Student student = StudentManager.fetchStudent(id);
                System.out.println(student.getId() + ", " +  student.getFirstName() + ", " + student.getName());
            } catch (NoSuchRecordException e) {
                System.err.println("No student found with ID: " + id);
            } catch (RuntimeException e) {
                System.err.println("Database error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Handle -export option: write all student records to CSV file, use the exact filename, no sorting required
     * <br>
     * Design Decision: StringBuilder vs PrintWriter approach
     * Option 1 (chosen): Build complete string first, then write once (StringBuilder)
     *   - Pros: All-or-nothing approach, prevents partial files, simpler error handling
     *   - Cons: Higher memory usage for large datasets, entire operation fails if any error
     * Option 2 (not chosen): Write each line individually (PrintWriter)
     *   - Pros: Lower memory usage, can handle very large datasets
     *   - Cons: Partial success possible, duplicate data on retry, more complex error recovery
     * @param filename the output file name
     */
    private static void handleExport(String filename) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,first_name,name,degree\n");
        Collection<String> ids = StudentManager.fetchAllStudentIds();
        for (String id: ids) {
            try {
                Student student = StudentManager.fetchStudent(id);
                csv.append(student.getId()).append(",").append(student.getFirstName()).append(",")
                        .append(student.getName()).append(",").append(student.getDegree().getId()).append("\n");
            } catch (NoSuchRecordException e) {
                System.err.println("No student found with ID: " + id);
            } catch (RuntimeException e) {
                System.err.println("Database error occurred: " + e.getMessage());
            }
        }
        try {
            Files.write(Paths.get(filename), csv.toString().getBytes());
        } catch (java.io.IOException e){
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}
