package games.stendhal.server.core.engine.generateini;

/**
 * Representation of a H2DB configuration.
 */
public class H2DatabaseConfiguration extends DatabaseConfiguration {

    private String databasePath;

    public H2DatabaseConfiguration() {
        this("~/stendhal/database/h2db");
    }

    public H2DatabaseConfiguration(String databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public String toIni() {
        StringBuilder sb = new StringBuilder();
        sb.append("database_adapter=marauroa.server.db.adapter.H2DatabaseAdapter");
        sb.append(System.lineSeparator());
        sb.append(String.format("jdbc_url=jdbc:h2:%1$s;AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=FALSE", this.databasePath));
        sb.append(System.lineSeparator());
        sb.append("jdbc_class=org.h2.Driver");
        sb.append(System.lineSeparator());
        return sb.toString();
    }

}
