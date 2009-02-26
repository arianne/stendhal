package games.stendhal.server.core.reflectiondebugger;

/**
 * This class is used to test the reflection code.
 *
 * @author hendrik
 */
public class MockParentClass {
	public String parentPublicString = "text";
	
	// this class is used by reflection
	@SuppressWarnings("unused")
	private int parentPrivateInt = 1;
	
}
