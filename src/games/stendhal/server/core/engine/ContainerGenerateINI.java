package games.stendhal.server.core.engine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import games.stendhal.server.core.engine.generateini.DatabaseConfiguration;
import games.stendhal.server.core.engine.generateini.H2DatabaseConfiguration;
import games.stendhal.server.core.engine.generateini.ServerIniConfiguration;

/**
 * Generate a server.ini in a container environment. Uses fixed datbase H2 and provides ability to setup
 * path to database files of H2.
 */
public class ContainerGenerateINI {

    private String databasePath;
    private Integer keySize;

    public ContainerGenerateINI(String databasePath, Integer keySize) {
        this.databasePath = databasePath;
        this.keySize = keySize;
    }

    public void write(String serverIni) throws FileNotFoundException {
        final PrintWriter out = new PrintWriter(new FileOutputStream(serverIni));
        DatabaseConfiguration db = new H2DatabaseConfiguration(databasePath);
        ServerIniConfiguration configuration = new ServerIniConfiguration(db , keySize);
        configuration.write(out);
        out.close();
    }

}
