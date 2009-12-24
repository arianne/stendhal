/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.client.sound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SoundEffectMapTest {

	private SoundEffectMap sem;

	private SoundEffectMap sem2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public final void setup() {
		sem = SoundEffectMap.getInstance();
		sem2 = SoundEffectMap.getInstance();

	}

	@After
	public final void tearDown() {
		sem = null;
		sem2 = null;
	}

	/**
	 * Tests for getInstance.
	 */
	@Test
	public final void testGetInstance() {

		assertEquals("Singleton instance is equal", sem, sem2);
	}

	/**
	 * Tests for getByName.
	 */
	@Test
	public final void testGetByName() {
		final String key1 = "testGetByNameStringvalue";
		final String value = "testGetByName";
		SoundEffectMap.getInstance().put(key1, value);
		assertEquals("stringValue", value, SoundEffectMap.getInstance()
				.getByName(key1));
		final String key = "testPutStringClipRunner";
		final ClipRunner cvalue = new ClipRunner("value");
		SoundEffectMap.getInstance().put(key, cvalue);
		assertEquals("ClipRunnerValue", cvalue, SoundEffectMap.getInstance()
				.getByName(key));
	}

	/**
	 * Tests for putStringString.
	 */
	@Test
	public final void testPutStringString() {
		final String key1 = "testPutStringString";
		final String value = "value";
		SoundEffectMap.getInstance().put(key1, value);
		assertTrue(SoundEffectMap.getInstance().containsKey(key1));

	}

	/**
	 * Tests for putStringClipRunner.
	 */
	@Test
	public final void testPutStringClipRunner() {
		final String key = "testPutStringClipRunner";
		final ClipRunner value = new ClipRunner("value");
		SoundEffectMap.getInstance().put(key, value);
		assertTrue(SoundEffectMap.getInstance().containsKey(key));
	}

	/**
	 * Tests for size.
	 */
	@Test
	public final void testSize() {
		final int size = SoundEffectMap.getInstance().size();
		SoundEffectMap.getInstance().put("empty", "");
		assertEquals("should be grown by own after adding a new one", size + 1,
				SoundEffectMap.getInstance().size());
	}

}
