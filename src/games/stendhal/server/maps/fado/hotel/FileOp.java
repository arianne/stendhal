/*
 * FileOp.java
 *
 * Created on March 18, 2007, 7:24 PM
 */

package games.stendhal.server.maps.fado.hotel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 *
 * @author timothyb89
 */
public class FileOp {
    
    private static final Logger logger = Log4J.getLogger(FileOp.class);
    
    /** Creates a new instance of FileOp */
    public FileOp() {
    
        /**
         * Provides some basic file operation techniques to read or write text to/from a file.
         */
    
    }
    public String readFile(String fileToRead) throws java.io.FileNotFoundException, IOException {
        Log4J.startMethod(logger, "readFile");
        
        File file = new File(fileToRead);// a new file
        
        boolean fileExists = false;
        
        if (file.exists()) {
            fileExists = true;
        } else {
            logger.error("File not found: " + fileToRead); // dishes out an error is the file doesn't exist'
            return null;
        }
        
        String textFromFile = null; //used later it file exists
        if (fileExists) { //if the file exists, open up a file reader and get its contents.
            try {
                FileReader fr = new FileReader(file); // opens file
                BufferedReader br = new BufferedReader(fr);
                br.close();
                fr.close();
               
                
                try {
                    textFromFile = br.readLine(); //reads the text and puts into string
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
                return null; //returns null if file does not exist
            }
        }
        if (file.exists()) return textFromFile;
        else {
            logger.error("File not found: " + fileToRead);
            return null; //if we get past the other ifs, this should catch a null string.
            
        }
        // All done.
        
        
    }
    
    /** writeToFile
     * Writes to a file
     *@param file The file to write to
     *@param text The text that will be written
     */
    public void writeToFile(String file, String text) throws IOException, FileNotFoundException {
        Log4J.startMethod(logger, "writeToFile");
        
        String oldText = null;
        try {
            oldText = readFile(file);
        } catch (FileNotFoundException ex) {
            logger.error("File not found: " + file);
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            fw.write(oldText + " " + text);
            fw.close();
            logger.info("File written");
        } catch (IOException ex) { // more IOException madness...
            ex.printStackTrace();
        }
        
        Log4J.finishMethod(logger, "writeToFile");
    }
    
}
