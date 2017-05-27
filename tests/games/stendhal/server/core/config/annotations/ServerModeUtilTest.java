package games.stendhal.server.core.config.annotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ServerModeUtilTest {

	private final TestClass testClass = new TestClass();
	private final TestClassTestServerOnly testClassTestServerOnly = new TestClassTestServerOnly();

	private static final class TestClass {
		//
	}

	@TestServerOnly
	private static final class TestClassTestServerOnly {
		//
	}

	private void deactivateTestServerMode() {
		System.getProperties().remove(TestServerOnly.TEST_SERVER_PROPERTY);
	}

	private void activateTestServerMode() {
		System.setProperty(TestServerOnly.TEST_SERVER_PROPERTY, "XXX");
	}

	@Test
	public void testIsTestServer() {
		deactivateTestServerMode();
		assertThat(ServerModeUtil.isTestServer(), is(Boolean.FALSE));
	}

	@Test
	public void testIsTestServerForTestServer() {
		activateTestServerMode();
		assertThat(ServerModeUtil.isTestServer(), is(Boolean.TRUE));
		deactivateTestServerMode();
		assertThat(ServerModeUtil.isTestServer(), is(Boolean.FALSE));
	}

	@Test
	public void testIsActiveInCurrentServerContext() {
		deactivateTestServerMode();
		assertThat(ServerModeUtil.isActiveInCurrentServerContext(testClass), is(Boolean.TRUE));
		assertThat(ServerModeUtil.isActiveInCurrentServerContext(testClassTestServerOnly), is(Boolean.FALSE));
	}

	@Test
	public void testIsActiveInCurrentServerContextForTestServer() {
		activateTestServerMode();
		assertThat(ServerModeUtil.isActiveInCurrentServerContext(testClass), is(Boolean.TRUE));
		assertThat(ServerModeUtil.isActiveInCurrentServerContext(testClassTestServerOnly), is(Boolean.TRUE));
	}

}
