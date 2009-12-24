package games.stendhal.common;

import games.stendhal.client.update.ClasspathEntry;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestClasspathEntry {

	/**
	 * Tests for parsingSimpleJar.
	 */
	@Test
	public void testParsingSimpleJar() {
		final String test = "stendhal-0.54.jar";
		final ClasspathEntry ce = new ClasspathEntry(test);
		assertEquals(test + " filename", "stendhal-0.54.jar", ce
				.getFilename());
		assertEquals(test + " type", "stendhal", ce.getType());
		assertEquals(test + " version", "0.54", ce.getVersion());
	}

	/**
	 * Tests for parsingJarInFolder.
	 */
	@Test
	public void testParsingJarInFolder() {
		final String test = "/tmp/stendhal-0.54.jar";
		final ClasspathEntry ce = new ClasspathEntry(test);
		assertEquals(test + " filename", "/tmp/stendhal-0.54.jar", ce
				.getFilename());
		assertEquals(test + " type", "stendhal", ce.getType());
		assertEquals(test + " version", "0.54", ce.getVersion());
	}

	/**
	 * Tests for compare.
	 */
	@Test
	public void testCompare() {
		final ClasspathEntry v054 = new ClasspathEntry("stendhal-0.54.jar");
		final ClasspathEntry v055 = new ClasspathEntry("stendhal-0.55.jar");
		final ClasspathEntry classes = new ClasspathEntry(".");

		assertEquals("< (1)", -1, v054.compareTo(v055));
		assertEquals("> (1)", 1, v055.compareTo(v054));
		assertEquals("= (1)", 0, v054.compareTo(v054));
		assertEquals("= (2)", 0, v055.compareTo(v055));
		assertEquals("= (3)", 0, classes.compareTo(classes));
		assertEquals("< (2)", -1, v054.compareTo(classes));
		assertEquals("> (2)", 1, classes.compareTo(v054));
	}

}
