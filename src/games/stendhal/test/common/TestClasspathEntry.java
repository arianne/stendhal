package games.stendhal.test.common;

import games.stendhal.client.update.ClasspathEntry;

public class TestClasspathEntry extends TestCase {

	public void testParsingSimpleJar() {
		String test = "stendhal-0.54.jar";
		ClasspathEntry ce = new ClasspathEntry(test);
		assertEquals(test + " filename", "stendhal-0.54.jar", ce.getFilename());
		assertEquals(test + " type", "stendhal", ce.getType());
		assertEquals(test + " version", "0.54", ce.getVersion());
	}

	public void testParsingJarInFolder() {
		String test = "/tmp/stendhal-0.54.jar";
		ClasspathEntry ce = new ClasspathEntry(test);
		assertEquals(test + " filename", "/tmp/stendhal-0.54.jar", ce.getFilename());
		assertEquals(test + " type", "stendhal", ce.getType());
		assertEquals(test + " version", "0.54", ce.getVersion());
	}

	public void testCompare() {
		ClasspathEntry v054 = new ClasspathEntry("stendhal-0.54.jar");
		ClasspathEntry v055 = new ClasspathEntry("stendhal-0.55.jar");
		ClasspathEntry classes = new ClasspathEntry(".");

		assertTrue("< (1)", v054.compareTo(v055) < 0);
		assertTrue("> (1)", v055.compareTo(v054) > 0);
		assertTrue("= (1)", v054.compareTo(v054) == 0);
		assertTrue("= (2)", v055.compareTo(v055) == 0);
		assertTrue("= (3)", classes.compareTo(classes) == 0);
		assertTrue("< (2)", v054.compareTo(classes) < 0);
		assertTrue("> (2)", classes.compareTo(v054) > 0);
	}

	public static void main(String[] args) {
		TestClasspathEntry testCase = new TestClasspathEntry();

		testCase.runTestCase(TestClasspathEntry.class);
	}
}
