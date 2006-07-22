package games.stendhal.test.common;


public class TestCase {

	protected void assertTrue(String text, boolean condition) {
		if (!condition) {
			String msg = "asertTrue failed (" + text + "): " + condition;
			throw new AssertionError(msg);
		}
	}

	protected void assertEquals(String text, String string1, String string2) {
		boolean ok = false;
		if (string1 == null) {
			ok = (string2 == null);
		} else {
			ok = string1.equals(string2);
		}
		
		if (!ok) {
			String msg = "asertEquals failed (" + text + "): \"" + string1 + "\" \"" + string2 + "\"";
			throw new AssertionError(msg);
		}
	}

}
