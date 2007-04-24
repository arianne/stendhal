package games.stendhal.common;

import games.stendhal.client.update.ClasspathEntry;

import org.junit.Assert;
import org.junit.Test;

public class TestClasspathEntry {

	@Test
	public void testParsingSimpleJar() {
		String test = "stendhal-0.54.jar";
		ClasspathEntry ce = new ClasspathEntry(test);
		Assert.assertEquals(test + " filename", "stendhal-0.54.jar", ce.getFilename());
		Assert.assertEquals(test + " type", "stendhal", ce.getType());
		Assert.assertEquals(test + " version", "0.54", ce.getVersion());
	}

	@Test
	public void testParsingJarInFolder() {
		String test = "/tmp/stendhal-0.54.jar";
		ClasspathEntry ce = new ClasspathEntry(test);
		Assert.assertEquals(test + " filename", "/tmp/stendhal-0.54.jar", ce.getFilename());
		Assert.assertEquals(test + " type", "stendhal", ce.getType());
		Assert.assertEquals(test + " version", "0.54", ce.getVersion());
	}

	@Test
	public void testCompare() {
		ClasspathEntry v054 = new ClasspathEntry("stendhal-0.54.jar");
		ClasspathEntry v055 = new ClasspathEntry("stendhal-0.55.jar");
		ClasspathEntry classes = new ClasspathEntry(".");

		Assert.assertTrue("< (1)", v054.compareTo(v055) < 0);
		Assert.assertTrue("> (1)", v055.compareTo(v054) > 0);
		Assert.assertTrue("= (1)", v054.compareTo(v054) == 0);
		Assert.assertTrue("= (2)", v055.compareTo(v055) == 0);
		Assert.assertTrue("= (3)", classes.compareTo(classes) == 0);
		Assert.assertTrue("< (2)", v054.compareTo(classes) < 0);
		Assert.assertTrue("> (2)", classes.compareTo(v054) > 0);
	}

}
