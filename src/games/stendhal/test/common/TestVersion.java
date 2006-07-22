package games.stendhal.test.common;

import games.stendhal.common.Version;

public class TestVersion extends TestCase {

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
	
	public static void main(String[] args) {
		TestVersion testCase = new TestVersion();
		testCase.testExtractVersion();
		testCase.testCheckVersionCompatibility();
	}
}
