package games.stendhal.test.groovy;

public class ChildClass extends ParentClass {
	private static ChildClass instance = null;
	
	protected ChildClass() {
		// hide constructor; Singleton pattern
	}

	public static synchronized ChildClass get() {
		if (instance == null) {
			instance = new ChildClass();
		}
		return instance;
	}
}
