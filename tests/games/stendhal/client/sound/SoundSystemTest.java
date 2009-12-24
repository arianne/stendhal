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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SoundSystemTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


	/**
	 * Tests for contains.
	 */
	@Test
	public final void testContains() {
		assertFalse(SoundSystem.get().contains(""));
	}

	/**
	 * Tests for setandGetMute.
	 */
	@Test
	public final void testSetandGetMute() {
		SoundSystem.get().setMute(true);
		assertTrue("muted sound should be mute", SoundSystem.get().isMute());
		SoundSystem.get().setMute(false);
		assertFalse("muted sound should be mute", SoundSystem.get().isMute());
	}

	/**
	 * Tests for setAndGetVolume.
	 */
	@Test
	public final void testSetAndGetVolume() {
		SoundSystem.get().setVolume(0);
		assertEquals(0, SoundSystem.get().getVolume());
		SoundSystem.get().setVolume(100);
		assertEquals(100, SoundSystem.get().getVolume());
	}

	/**
	 * Tests for setVolumeOutOfBounds.
	 */
	@Test
	public final void testSetVolumeOutOfBounds() {
		SoundSystem.get().setVolume(-1);
		assertEquals(0, SoundSystem.get().getVolume());
		SoundSystem.get().setVolume(101);
		assertEquals(100, SoundSystem.get().getVolume());
	}

	
	/**
	 * Tests for get.
	 */
	@Test
	public final void testGet() {
		final SoundSystem ss1 = SoundSystem.get();
		final SoundSystem ss2 = SoundSystem.get();
		assertTrue("must receive identical instance", (ss1 == ss2));
	}

	/**
	 * Tests for isValidEntry.
	 */
	@Test
	public final void testisValidEntry() {

		assertFalse("value has comma x", SoundSystem.get().isValidEntry(",x",
				""));
		assertFalse("key does not start with sfx name has point", SoundSystem
				.get().isValidEntry("", "."));
		assertTrue("key does  start with sfx name has point", SoundSystem.get()
				.isValidEntry("sfx.", "."));
		assertTrue("value has comma x and name has point", SoundSystem.get()
				.isValidEntry("sfx.,x", "."));
	}

}
