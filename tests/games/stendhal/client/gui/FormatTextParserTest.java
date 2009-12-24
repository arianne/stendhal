package games.stendhal.client.gui;

import games.stendhal.client.FormatTextParserExtension;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FormatTextParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for format.
	 */
	@Test
	public void testFormat() throws Exception {
		StringBuilder builder = new StringBuilder();
		FormatTextParser parser = new FormatTextParserExtension(builder);
		parser.format("##text");
	}



}
