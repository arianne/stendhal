package games.stendhal.server.core.engine.generateini;

import junit.framework.TestCase;

public class H2DatabaseConfigurationTest extends TestCase {

    public void testToString() {
        assertEquals(
            "database_adapter=marauroa.server.db.adapter.H2DatabaseAdapter" + System.lineSeparator()
            + "jdbc_url=jdbc:h2:~/stendhal/database/h2db;AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=FALSE" + System.lineSeparator()
            + "jdbc_class=org.h2.Driver" + System.lineSeparator()
            + "",
            new H2DatabaseConfiguration().toIni());
    }

    public void testToStringWithParameter() {
        assertEquals(
            "database_adapter=marauroa.server.db.adapter.H2DatabaseAdapter" + System.lineSeparator()
            + "jdbc_url=jdbc:h2:/opt/stendhalserver/data/database/h2db;AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=FALSE" + System.lineSeparator()
            + "jdbc_class=org.h2.Driver" + System.lineSeparator()
            + "",
            new H2DatabaseConfiguration("/opt/stendhalserver/data/database/h2db").toIni());
    }

}
