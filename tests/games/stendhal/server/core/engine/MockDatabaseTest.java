package games.stendhal.server.core.engine;

import org.junit.BeforeClass;
import org.junit.Test;

public class MockDatabaseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testcreateMockDatabase() throws Exception {
		new MockDatabase();
		MockDatabase.resetDatabase();
	}
}
