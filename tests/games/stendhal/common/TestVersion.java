package games.stendhal.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.client.update.Version;

import org.junit.Test;

public class TestVersion {

	/**
	 * Tests for extractVersion.
	 */
	@Test
	public void testExtractVersion() {
		assertEquals("extratVersion 0: ", "", Version.cut("0.52.1", 0));
		assertEquals("extratVersion 1: ", "0", Version.cut("0.52.1", 1));
		assertEquals("extratVersion 2: ", "0.52", Version.cut("0.52.1",
				2));
		assertEquals("extratVersion 3: ", "0.52.1", Version.cut(
				"0.52.1", 3));
		assertEquals("extratVersion 4: ", "0.52.1", Version.cut(
				"0.52.1", 4));
	}

	/**
	 * Tests for checkVersionCompatibility.
	 */
	@Test
	public void testCheckVersionCompatibility() {
		assertTrue("VersionCompatible 0.52 ~ 0.52", Version
				.checkCompatibility("0.52", "0.52"));
		assertTrue("VersionCompatible 0.52 ~ 0.52.0", Version
				.checkCompatibility("0.52", "0.52.0"));
		assertTrue("VersionCompatible 0.52 ~ 0.52.1", Version
				.checkCompatibility("0.52", "0.52.1"));
		assertFalse("VersionCompatible ! 0.53 ~ 0.52", Version
				.checkCompatibility("0.53", "0.52"));
		assertFalse("VersionCompatible ! 0.52 ~ 0.53", Version
				.checkCompatibility("0.52", "0.53"));
	}

	/**
	 * Tests for compare.
	 */
	@Test
	public void testCompare() {
		assertEquals("VersionCompare 0.52 = 0.52", 0, Version.compare("0.52", "0.52"));
		assertEquals("VersionCompare 0.52 = 0.52.0", 0, Version.compare("0.52", "0.52.0"));
		assertEquals("VersionCompare 0.52 < 0.52.1", -1, Version.compare("0.52", "0.52.1"));
		assertEquals("VersionCompare 0.53 > 0.52", 1, Version.compare("0.53", "0.52"));
		assertEquals("VersionCompare 0.52 < 0.53", -1, Version.compare("0.52", "0.53"));
	}

	/**
	 * Tests for compareInt.
	 */
	@Test
	public void testCompareInt() {
		assertEquals("VersionCompare 0.2 < 0.10", -8, Version.compare("0.2", "0.10"));
		assertEquals("VersionCompare 0.10 > 0.2", 8, Version.compare("0.10", "0.2"));
		assertEquals("VersionCompare 0.02 = 0.2", 0, Version.compare("0.02", "0.2"));
	}

}
