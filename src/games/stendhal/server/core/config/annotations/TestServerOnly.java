/**
 *
 */
package games.stendhal.server.core.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker Annotation for Objects that should be active only on a testserver
 *
 * @author madmetzger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestServerOnly {

	/**
	 * Property name for a system property that must be set to make a server a testserver
	 */
	public static final String TEST_SERVER_PROPERTY = "stendhal.testserver";

}
