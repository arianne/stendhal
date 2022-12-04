package games.stendhal.server.core.engine.generateini;

import java.io.PrintWriter;

/**
 * Abstract super class for database configurations.
 */
public abstract class DatabaseConfiguration {

    /**
     * Writes out the {@link DatabaseConfiguration}.
     * @param out
     *  The {@link PrintWriter} to write on.
     */
    public void write(PrintWriter out) {
        out.print(this.toIni());
    }

    /**
     * Generates a {@link String} representation for a server.ini file.
     * @return the configuration as ini for the server.ini.
     */
    public abstract String toIni();

}
