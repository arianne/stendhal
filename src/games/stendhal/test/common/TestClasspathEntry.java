package games.stendhal.test.common;

import games.stendhal.client.update.ClasspathEntry;

public class TestClasspathEntry extends TestCase {

	public void testParsingSimpleJar() {
		String test = "stendhal-0.54.jar";
		ClasspathEntry ce = new ClasspathEntry(test);
		assertEquals(test + " filename", "stendhal-0.54.jar", ce.getFilename());
		assertEquals(test + " type",     "stendhal", ce.getType());
		assertEquals(test + " version",  "0.54", ce.getVersion());
	}

	public void testParsingJarInFolder() {
		String test = "/tmp/stendhal-0.54.jar";
		ClasspathEntry ce = new ClasspathEntry(test);
		assertEquals(test + " filename", "/tmp/stendhal-0.54.jar", ce.getFilename());
		assertEquals(test + " type",     "stendhal", ce.getType());
		assertEquals(test + " version",  "0.54", ce.getVersion());
	}

	public static void main(String[] args) {
		TestClasspathEntry testCase = new TestClasspathEntry();

		testCase.runTestCase(TestClasspathEntry.class);
	}
}
