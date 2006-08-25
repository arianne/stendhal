package games.stendhal.test.groovy;

public class ParentClass {
	private static ParentClass instance = null;
	
	protected ParentClass() {
		// hide constructor; Singleton pattern
	}

	public static synchronized ParentClass get() {
		if (instance == null) {
			instance = new ParentClass();
		}
		return instance;
	}
}
