package games.stendhal.server.core.config.factory;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigurableFactoryContextTest {

	private Map<String, String> attributes;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		attributes = new HashMap<String, String>();
		attributes.put("positive", "true");
		attributes.put("string", "stringvalue");
		attributes.put("negative", "false");
		attributes.put("fiveInt", "5");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConfigurableFactoryContext() {
		new ConfigurableFactoryContext(attributes);
		assertTrue("noexecption", true);
	}

	@Test
	public void testGetBoolean() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertTrue(con.getBoolean("nonExistingkey", true));
		assertFalse(con.getBoolean("nonExistingkey", false));
		assertTrue(con.getBoolean("positive", false));
		assertFalse(con.getBoolean("negative", true));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNonExistingRequiredBoolean() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertTrue(con.getRequiredBoolean("key"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNonbooleanRequiredBoolean() {

		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertTrue(con.getRequiredBoolean("string"));
	}

	@Test
	public void testGetRequiredBoolean() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertTrue(con.getRequiredBoolean("positive"));
		assertFalse(con.getRequiredBoolean("negative"));
	}

	@Test
	public void testGetInt() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertThat(con.getInt("nonExistingkey", 1), is(1));
		assertThat(con.getInt("fiveInt", 1), is(5));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNonExitantRequiredInt() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		con.getRequiredInt("nonExistingkey");

	}

	public void testGetRequiredInt() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertThat(con.getRequiredInt("fiveInt"), is(5));

	}

	@Test
	public void testGetString() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertThat(con.getString("nonexistantstring", "default"), is("default"));
		assertThat(con.getString("string", "default"), is("stringvalue"));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRequiredString() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		con.getRequiredString("nonexistantstring");

	}

}
