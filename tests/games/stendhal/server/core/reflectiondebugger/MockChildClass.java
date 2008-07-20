package games.stendhal.server.core.reflectiondebugger;

/**
 * This class is used to test the reflection code.
 *
 * @author hendrik
 */
public class MockChildClass extends MockParentClass {

	// this class is used by reflection
	@SuppressWarnings("unused")
	private float childPrivateFloat = 2.0f;
	public boolean childPublicBoolean = true;
}
