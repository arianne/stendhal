package games.stendhal.server.core.engine.generateini;

import junit.framework.TestCase;

public class MySqlDatabaseConfigurationTest extends TestCase {

    public void testToIni() {
        assertEquals(
                "database_adapter=marauroa.server.db.adapter.MySQLDatabaseAdapter" + System.lineSeparator()
                + "jdbc_url=jdbc:mysql://host/db?useUnicode=yes&characterEncoding=UTF-8" + System.lineSeparator()
                + "jdbc_class=com.mysql.jdbc.Driver" + System.lineSeparator()
                + "jdbc_user=user" + System.lineSeparator()
                + "jdbc_pwd=password" + System.lineSeparator(),
                new MySqlDatabaseConfiguration("db", "host", "user", "password").toIni());
    }

}
