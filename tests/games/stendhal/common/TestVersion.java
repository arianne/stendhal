package games.stendhal.common;

import games.stendhal.client.update.Version;
import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class TestVersion extends TestCase {

	@Test
	public void testExtractVersion() {
		Assert.assertEquals("extratVersion 0: ", "", Version.cut("0.52.1", 0));
		Assert.assertEquals("extratVersion 1: ", "0", Version.cut("0.52.1", 1));
		Assert.assertEquals("extratVersion 2: ", "0.52", Version.cut("0.52.1",
				2));
		Assert.assertEquals("extratVersion 3: ", "0.52.1", Version.cut(
				"0.52.1", 3));
		Assert.assertEquals("extratVersion 4: ", "0.52.1", Version.cut(
				"0.52.1", 4));
	}

	@Test
	public void testCheckVersionCompatibility() {
		Assert.assertTrue("VersionCompatible 0.52 ~ 0.52", Version
				.checkCompatibility("0.52", "0.52"));
		Assert.assertTrue("VersionCompatible 0.52 ~ 0.52.0", Version
				.checkCompatibility("0.52", "0.52.0"));
		Assert.assertTrue("VersionCompatible 0.52 ~ 0.52.1", Version
				.checkCompatibility("0.52", "0.52.1"));
		Assert.assertTrue("VersionCompatible ! 0.53 ~ 0.52", !Version
				.checkCompatibility("0.53", "0.52"));
		Assert.assertTrue("VersionCompatible ! 0.52 ~ 0.53", !Version
				.checkCompatibility("0.52", "0.53"));
	}

	@Test
	public void testCompare() {
		Assert.assertTrue("VersionCompare 0.52 = 0.52", Version.compare("0.52",
				"0.52") == 0);
		Assert.assertTrue("VersionCompare 0.52 = 0.52.0", Version.compare(
				"0.52", "0.52.0") == 0);
		Assert.assertTrue("VersionCompare 0.52 < 0.52.1", Version.compare(
				"0.52", "0.52.1") < 0);
		Assert.assertTrue("VersionCompare 0.53 > 0.52", Version.compare("0.53",
				"0.52") > 0);
		Assert.assertTrue("VersionCompare 0.52 < 0.53", Version.compare("0.52",
				"0.53") < 0);
	}

	@Test
	public void testCompareInt() {
		Assert.assertTrue("VersionCompare 0.2 < 0.10", Version.compare("0.2",
				"0.10") < 0);
		Assert.assertTrue("VersionCompare 0.10 > 0.2", Version.compare("0.10",
				"0.2") > 0);
		Assert.assertTrue("VersionCompare 0.02 = 0.2", Version.compare("0.02",
				"0.2") == 0);
	}

}
