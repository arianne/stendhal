package games.stendhal.server.core.config.annotations;
/**
 * Utility class that distinguishes between testservers and normal servers
 *
 * @author madmetzger
 */
public abstract class ServerModeUtil {

	/**
	 * Determines if the current running server is a testserver
	 *
	 * @return <code>true</code> if the server is a test server, otherwise
	 * 	<code>false</code>
	 */
	public static boolean isTestServer() {
		return System.getProperty(TestServerOnly.TEST_SERVER_PROPERTY) != null;
	}

	/**
	 * Determines if the given object should be active in the current server
	 *
	 * @param obj
	 * @return true if object should be active
	 */
	public static boolean isActiveInCurrentServerContext(Object obj) {
		boolean hasTestServerAnnotation = obj.getClass().isAnnotationPresent(TestServerOnly.class);
		boolean isTestServer = isTestServer();
		if(!isTestServer) {
			return !hasTestServerAnnotation;
		}
		return true;
	}
}
