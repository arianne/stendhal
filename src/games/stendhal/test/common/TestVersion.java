package games.stendhal.test.common;

import games.stendhal.common.Version;

public class TestVersion {

	public void testExtractVersion() {
		assertEquals("extratVersion 0: ", "",       Version.extractVersion("0.52.1", 0));
		assertEquals("extratVersion 1: ", "0",      Version.extractVersion("0.52.1", 1));
		assertEquals("extratVersion 2: ", "0.52",   Version.extractVersion("0.52.1", 2));
		assertEquals("extratVersion 3: ", "0.52.1", Version.extractVersion("0.52.1", 3));
		assertEquals("extratVersion 4: ", "0.52.1", Version.extractVersion("0.52.1", 4));
	}
	
	public void testCheckVersionCompatibility() {
		assertTrue("VersionCompatible 0.52 ~ 0.52", Version.checkVersionCompatibility("0.52", "0.52"));
		assertTrue("VersionCompatible 0.52 ~ 0.52.0", Version.checkVersionCompatibility("0.52", "0.52.0"));
		assertTrue("VersionCompatible 0.52 ~ 0.52.1", Version.checkVersionCompatibility("0.52", "0.52.1"));
		assertTrue("VersionCompatible ! 0.53 ~ 0.52", !Version.checkVersionCompatibility("0.53", "0.52"));
		assertTrue("VersionCompatible ! 0.52 ~ 0.53", !Version.checkVersionCompatibility("0.52", "0.53"));
	}
	
	private void assertTrue(String text, boolean condition) {
		if (!condition) {
			String msg = "asertTrue failed (" + text + "): " + condition;
			throw new AssertionError(msg);
		}
	}

	private void assertEquals(String text, String string1, String string2) {
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

	public static void main(String[] args) {
		TestVersion testCase = new TestVersion();
		testCase.testExtractVersion();
		testCase.testCheckVersionCompatibility();
	}
}
