package games.stendhal.tools.statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.ceil;

/**
 * Stores coverage data
 *
 * @author Pihlqvist
 */

public class AdHocCoverage {


    private final String PATH_NAME = "";
    private String FILE_NAME;

    private int[] branches;

    /**
     * Create a AdHocCoverage object in preparation to store coverage data
     * @param method name of method
     * @param branchAmount total amount of branches in method
     */
    public AdHocCoverage(String method, int branchAmount) {
        FILE_NAME = PATH_NAME + method + ".txt";
        branches = new int[branchAmount];
        Arrays.fill(branches, 0);

        try {
            readFile();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Invoked when a branch has been reached, the class will store the id
     *
     * @param branchID
     */
    public void branchReached(int branchID) {
        assert(branchID < branches.length);
        branches[branchID] += 1;

        try {
            writeFile();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Calculates the percentile coverage of branches as a float.
     *
     * @return percentile of covered branches
     */
    private double calculateCoverage() {
        int reached = 0;
        for (int i = 0; i < branches.length; i++) {
            if (branches[i] > 0) {
                reached += 1;
            }
        }
        double coverage = (double) reached/branches.length;
        assert(coverage <= 1);

        return coverage;
    }

    /**
     * Reads a buffer file and stores the data in class structures
     *
     */
    private void readFile() throws IOException {
        // If file don't exist create one.
        File test = new File(Paths.get(FILE_NAME).toString());
        if (!test.exists()) {
            PrintWriter writer = new PrintWriter(Paths.get(FILE_NAME).toString());
            writer.print("Coverage: ");
            writer.close();
        }

        // If file exist, read data from it and store it in class array.
        else {
            List<String> allLines = Files.readAllLines(
                    Paths.get(FILE_NAME));

            for (int i = 4; i < allLines.size(); i++) {
                assert (Integer.valueOf(allLines.get(i)) <= branches.length);
                branches[Integer.valueOf(allLines.get(i))] += 1;
            }
        }
    }

    /**
     * Store data from class structs to a file
     *
     */
    private void writeFile() throws IOException {
        FileWriter fw = new FileWriter(Paths.get(FILE_NAME).toString());
        double coverage = calculateCoverage();

        // Write the report summary
        fw.write(String.format("Coverage: %.3f %%", coverage));
        fw.write(System.lineSeparator()); //new line
        fw.write(String.format("Branches: %d", branches.length));
        fw.write(System.lineSeparator()); //new line
        fw.write(String.format("Reached: %d", (int)ceil(branches.length*coverage)));
        fw.write(System.lineSeparator()); //new line
        fw.write("#");
        fw.write(System.lineSeparator()); //new line

        // List the ID's of branches reached
        for (int i = 0; i < branches.length; i++) {
            if (branches[i] > 0) {
                fw.write(String.format("%d", i));
                fw.write(System.lineSeparator()); //new line
            }
        }
        fw.close();
    }

}