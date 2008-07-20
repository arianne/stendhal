package games.stendhal.server.core.reflectiondebugger;

/**
 * This class is used to test the reflection code.
 *
 * @author hendrik
 */
public class MockParentClass {

	// this class is used by reflection
	@SuppressWarnings("unused")
	private int parentPrivateInt = 1;
	public String parentPublicString = "text";
}
