/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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

	/**
	 * Tests for configurableFactoryContext.
	 */
	@Test
	public void testConfigurableFactoryContext() {
		new ConfigurableFactoryContext(attributes);
		assertTrue("noexecption", true);
	}

	/**
	 * Tests for getBoolean.
	 */
	@Test
	public void testGetBoolean() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertTrue(con.getBoolean("nonExistingkey", true));
		assertFalse(con.getBoolean("nonExistingkey", false));
		assertTrue(con.getBoolean("positive", false));
		assertFalse(con.getBoolean("negative", true));

	}

	/**
	 * Tests for getNonExistingRequiredBoolean.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetNonExistingRequiredBoolean() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertTrue(con.getRequiredBoolean("key"));
	}

	/**
	 * Tests for getNonbooleanRequiredBoolean.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetNonbooleanRequiredBoolean() {

		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertTrue(con.getRequiredBoolean("string"));
	}

	/**
	 * Tests for getRequiredBoolean.
	 */
	@Test
	public void testGetRequiredBoolean() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertTrue(con.getRequiredBoolean("positive"));
		assertFalse(con.getRequiredBoolean("negative"));
	}

	/**
	 * Tests for getInt.
	 */
	@Test
	public void testGetInt() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertThat(con.getInt("nonExistingkey", 1), is(1));
		assertThat(con.getInt("fiveInt", 1), is(5));

	}

	/**
	 * Tests for getNonExitantRequiredInt.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetNonExitantRequiredInt() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		con.getRequiredInt("nonExistingkey");

	}

	/**
	 * Tests for getRequiredInt.
	 */
	public void testGetRequiredInt() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertThat(con.getRequiredInt("fiveInt"), is(5));

	}

	/**
	 * Tests for getString.
	 */
	@Test
	public void testGetString() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		assertThat(con.getString("nonexistantstring", "default"), is("default"));
		assertThat(con.getString("string", "default"), is("stringvalue"));

	}

	/**
	 * Tests for getRequiredString.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetRequiredString() {
		ConfigurableFactoryContext con = new ConfigurableFactoryContext(
				attributes);
		con.getRequiredString("nonexistantstring");

	}

}
