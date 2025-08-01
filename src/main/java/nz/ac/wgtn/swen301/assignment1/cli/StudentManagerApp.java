package nz.ac.wgtn.swen301.assignment1.cli;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.Degree;
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
     * @param arg
     */
    public static void main (String[] arg) {

        Options options = new Options();
        options.addOption("select", true, "display the respective student record");
        options.addOption("all", false, " display all student records");
        options.addOption("export", true, "write all student records to a file in CSV format");

        CommandLineParser parser = new DefaultParser(); // assuming partial match is true in default

        // I could implement all in main, but probably make sense to implement helper for each option (need to check if it allowed)
        try {
            CommandLine line = parser.parse(options, arg);

            // option 1
            if (line.hasOption("select")) { // if we dont have to print degree, lets ignore, unless it give me extra point (need confirm)
                String id = line.getOptionValue("select");
                try {
                    Student student = StudentManager.fetchStudent(id);
                    System.out.println(student.getId() + ", " +  student.getFirstName() + ", " + student.getName());
                } catch (NoSuchRecordException e) {
                    System.err.println("No student found with ID: " + id);
                } catch (RuntimeException e) {
                    System.err.println("Database error occurred: " + e.getMessage());
                }
            }

            // option 2
            // do i need to sort??????
            if (line.hasOption("all")) {
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

            // option 3
            // do i need to sort??????
            /*
            * I have 2 options:
            * 1. build string first and write once (StringBuilder)
            * 2. write each line (PrintWriter)
            *
            * option1 is all or nothing and need more memory to prepare large string
            * option2 can be partially success, more system call
            *
            * need to chack marking requirement!!!!!!!!!!!!!!!!!!!!!
            *
            * but partially succeed file be any useful? we are not providing any function to fill the missing line
            * -> so if user fail -> try again -> output will have duplicate.
            * let s pick all or nothing for now
            * */

            if (line.hasOption("export")) {
                String filename = line.getOptionValue("export");
                String firstRow = "id,first_name,name,degree\n";
                StringBuilder csv = new StringBuilder();
                csv.append(firstRow);

                Collection<String> ids = StudentManager.fetchAllStudentIds();
                for (String id: ids) {
                    try {
                        Student student = StudentManager.fetchStudent(id);
                        Degree degree = student.getDegree();
                        csv.append(student.getId()).append(",").append(student.getFirstName()).append(",")
                                        .append(student.getName()).append(",").append(degree.getId()).append("\n");
                    } catch (NoSuchRecordException e) {
                        System.err.println("No student found with ID: " + id);
                    } catch (RuntimeException e) {
                        System.err.println("Database error occurred: " + e.getMessage());
                    }
                }

                // write them to a file name <file> in CSV format
                // Do i need to enforce format? but it will change file name. check!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //String csvFilename = filename.endsWith(".csv") ? filename : filename + ".csv";
                try {Files.write(Paths.get(filename), csv.toString().getBytes());}
                catch (java.io.IOException e){
                    System.err.println("Error writing file: " + e.getMessage());
                }


            }


            if (!line.hasOption("select") && !line.hasOption("all") && !line.hasOption("export")) {
                System.out.println("Please provide a valid option: -select, -all, or -export");
            }



        }
        catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }




    }



    /*


     -all – fetch all student records, and display the result on the console
    (System.out) as one line containing at least id, firstname and name
    *
    * -> need to confirm but all record in one line? or each record is one line? should be latter
    * use get all id method, foreach the collection(set) (depend on order i may need to change implementation from hash to tree set)
    * fetch -> print maybe able to use the method for id fetch above
    *
    *

     -export <file> - fetch all student records, and write them to a file named
    <file> in CSV format, using a comma as a separator, and the first row containing
    the column names separated by comma. The structure of the table should be the
    same as in the table with example data shown above.
    *
    * structure need to be, so be careful name order, this one likely requires degree, them need to import it too
    * or maybe this is where i need to use proxy to just get degid from student? if it is possible
    * it maybe better to make all fetch method for option 2 and 3, but use different output method
    * first row... i think i can hard code, or do i need to use class get variable name?
    *
    * id first_name name degree

    * */




}
