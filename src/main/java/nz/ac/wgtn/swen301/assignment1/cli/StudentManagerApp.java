package nz.ac.wgtn.swen301.assignment1.cli;

public class StudentManagerApp {

    // THE FOLLOWING METHOD MUST BE IMPLEMENTED
    /**
     * Executable: the user will provide argument(s) and print details to the console as described in the assignment brief,
     * E.g. a user could invoke this by running "java -cp <someclasspath> <arguments></arguments>"
     * @param arg
     */
    public static void main (String[] arg) {}


    /*
     *  use standard pattern
     * https://commons.apache.org/proper/commons-cli/introduction.html
     * 1. define -> make option
     * 2. parse -> make parser
     *          -> parse input from -> which will be commandLine
     * 3. interrogation
     *    depend on command line call method in student manager (so i need to import it)
     *    use switch? enum? or simple if/else?
     *    I could implement all in main, but probably make sense to implement helper for each option (need to check if it allowed)
     */

    /*
    * understand options:

    ● -select id42 – fetch the respective student record with id id42 (or any other
    id), and display the result on the console (System.out) as one line containing at
    least id, firstname and name
    *
    * -> student doesn't seem to have toString method, so manually print out with 3 getters
    * if we dont have to print degree, lets ignore, unless it give me extra point (need confirm)
    * use fetch student method
    *

    ● -all – fetch all student records, and display the result on the console
    (System.out) as one line containing at least id, firstname and name
    *
    * -> need to confirm but all record in one line? or each record is one line? should be latter
    * use get all id method, foreach the collection(set) (depend on order i may need to change implementation from hash to tree set)
    * fetch -> print maybe able to use the method for id fetch above
    *
    *

    ● -export <file> - fetch all student records, and write them to a file named
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
