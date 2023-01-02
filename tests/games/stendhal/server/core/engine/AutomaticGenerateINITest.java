package games.stendhal.server.core.engine;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import junit.framework.TestCase;

public class AutomaticGenerateINITest extends TestCase {

	public void testWriteWithDefaultValues() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new AutomaticGenerateINI(new HashMap<>()).write(baos);
		assertTrue(baos.toString().contains("database_adapter=marauroa.server.db.adapter.H2DatabaseAdapter"));
		assertTrue(baos.toString().contains("jdbc_url=jdbc:h2:/stendhal/data/h2db;AUTO_RECONNECT=TRUE;DB_CLOSE_ON_EXIT=FALSE"));
		assertTrue(baos.toString().contains("jdbc_class=org.h2.Driver"));
		
	}

}
