package games.stendhal.server.core.engine.generateini;

import java.io.PrintWriter;

public abstract class DatabaseConfiguration {

    public void write(PrintWriter out) {
        out.print(this.toIni());
    }

    public abstract String toIni();

}
