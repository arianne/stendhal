package tiled.plugins.stendhal;

import tiled.core.*;
import tiled.io.*;
import java.util.Stack;
import java.io.*;

public class StendhalMapReader implements MapReader {
    /**
     * Method readMap
     *
     *
     * @param filename
     *
     @throws Exception
     *
     * @return
     *
     */
    public Map readMap(String filename) throws Exception {
        // TODO: Add your code here
        return null;
    }

    /**
     * Method readTileset
     *
     *
     * @param filename
     *
     @throws Exception
     *
     * @return
     *
     */
    public TileSet readTileset(String filename) throws Exception {
        // TODO: Add your code here
        return null;
    }

    /**
     * Method readMap
     *
     *
     * @param in
     *
     @throws Exception
     *
     * @return
     *
     */
    public Map readMap(InputStream in) throws Exception {
        // TODO: Add your code here
        return null;
    }

    /**
     * Method readTileset
     *
     *
     * @param in
     *
     @throws Exception
     *
     * @return
     *
     */
    public TileSet readTileset(InputStream in) throws Exception {
        // TODO: Add your code here
        return null;
    }

    public boolean accept(File pathname) {
        try {
            String path = pathname.getCanonicalPath().toLowerCase();
            if (path.endsWith(".stend")) {
                return true;
            }
        } catch (IOException e) {}
        return false;
    }

    public String getFilter() throws Exception {
        return "*.stend";
    }

    public String getName() {
        return "Stendhal reader";
    }
    
    public String getDescription() {
        return "+---------------------------------------------+\n" +
               "|      An experimental reader for Stendhal    |\n" +
               "|                                             |\n" +
               "|      (c) Miguel Angel Blanch Lardin 2005    |\n" +
               "|                                             |\n" +
               "+---------------------------------------------+";
    }

    public String getPluginPackage() {
        return "Stendhal Reader/Writer Plugin";
    }

    public void setErrorStack(Stack es) {
        // TODO: implement setErrorStack
    }
}
