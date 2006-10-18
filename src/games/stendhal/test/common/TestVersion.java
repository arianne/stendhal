package games.stendhal.test.common;

import games.stendhal.client.update.Version;

public class TestVersion extends TestCase {

	public void testExtractVersion() {
		assertEquals("extratVersion 0: ", "",       Version.cut("0.52.1", 0));
		assertEquals("extratVersion 1: ", "0",      Version.cut("0.52.1", 1));
		assertEquals("extratVersion 2: ", "0.52",   Version.cut("0.52.1", 2));
		assertEquals("extratVersion 3: ", "0.52.1", Version.cut("0.52.1", 3));
		assertEquals("extratVersion 4: ", "0.52.1", Version.cut("0.52.1", 4));
	}
	
	public void testCheckVersionCompatibility() {
		assertTrue("VersionCompatible 0.52 ~ 0.52", Version.checkCompatibility("0.52", "0.52"));
		assertTrue("VersionCompatible 0.52 ~ 0.52.0", Version.checkCompatibility("0.52", "0.52.0"));
		assertTrue("VersionCompatible 0.52 ~ 0.52.1", Version.checkCompatibility("0.52", "0.52.1"));
		assertTrue("VersionCompatible ! 0.53 ~ 0.52", !Version.checkCompatibility("0.53", "0.52"));
		assertTrue("VersionCompatible ! 0.52 ~ 0.53", !Version.checkCompatibility("0.52", "0.53"));
	}
	
	public void testCompare() {
		assertTrue("VersionCompare 0.52 ~ 0.52",   Version.compare("0.52", "0.52")   == 0);
		assertTrue("VersionCompare 0.52 ~ 0.52.0", Version.compare("0.52", "0.52.0") == 0);
		assertTrue("VersionCompare 0.52 ~ 0.52.1", Version.compare("0.52", "0.52.1") < 0);
		assertTrue("VersionCompare ! 0.53 ~ 0.52", Version.compare("0.53", "0.52")   > 0);
		assertTrue("VersionCompare ! 0.52 ~ 0.53", Version.compare("0.52", "0.53")   < 0);
	}

	public static void main(String[] args) {
		TestVersion testCase = new TestVersion();

		testCase.runTestCase(TestVersion.class);
	}
}
