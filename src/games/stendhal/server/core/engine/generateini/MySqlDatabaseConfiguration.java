package games.stendhal.server.core.engine.generateini;

/**
 * Representation of a MySQL datatabse configuration.
 *
 */
public class MySqlDatabaseConfiguration extends DatabaseConfiguration {

    private String dbName;
    private String dbHost;
    private String dbUser;
    private String dbPassword;

    public MySqlDatabaseConfiguration(String databaseName, String databaseHost, String user, String password) {
        super();
        this.dbName = databaseName;
        this.dbHost = databaseHost;
        this.dbUser = user;
        this.dbPassword = password;
    }



    @Override
    public String toIni() {
        StringBuilder sb = new StringBuilder();
        sb.append("database_adapter=marauroa.server.db.adapter.MySQLDatabaseAdapter");
        sb.append(System.lineSeparator());
        sb.append("jdbc_url=jdbc:mysql://" + dbHost + "/" + dbName + "?useUnicode=yes&characterEncoding=UTF-8");
        sb.append(System.lineSeparator());
        sb.append("jdbc_class=com.mysql.jdbc.Driver");
        sb.append(System.lineSeparator());
        sb.append("jdbc_user=" + dbUser);
        sb.append(System.lineSeparator());
        sb.append("jdbc_pwd=" + dbPassword);
        sb.append(System.lineSeparator());
        return sb.toString();
    }

}
