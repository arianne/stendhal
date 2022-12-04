package games.stendhal.server.core.engine.generateini;

import java.io.PrintStream;

public abstract class DatabaseConfiguration {

    public void write(PrintStream out) {
        out.print(this.toIni());
    }

    public abstract String toIni();

}
