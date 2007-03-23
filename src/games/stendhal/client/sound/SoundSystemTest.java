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

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SoundSystemTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Ignore
	public final void testPlaySoundIntern() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testPlaySoundStringInt() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testPlaySoundStringIntInt() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testProbablePlaySound() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testPlayMapSound() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testPlayAmbientSound() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testStopAmbientSound() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testClearAmbientSounds() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testGetSoundClip() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testStartSoundCycle() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testStopSoundCycle() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testContains() {
		assertFalse(SoundSystem.get().contains(""));
		//TODO: create test for finding one

	}

	@Test
	public final void testSetandGetMute() {
		SoundSystem.get().setMute(true);
		assertTrue("muted sound should be mute", SoundSystem.get().isMute());
		SoundSystem.get().setMute(false);
		assertFalse("muted sound should be mute", SoundSystem.get().isMute());
	}

	@Test
	public final void testSetAndGetVolume() {
		SoundSystem.get().setVolume(0);
		assertEquals(0, SoundSystem.get().getVolume());
		SoundSystem.get().setVolume(100);
		assertEquals(100, SoundSystem.get().getVolume());
	}

	@Test
	public final void testSetVolumeOutOfBounds() {
		SoundSystem.get().setVolume(-1);
		assertEquals(0, SoundSystem.get().getVolume());
		SoundSystem.get().setVolume(101);
		assertEquals(100, SoundSystem.get().getVolume());
	}

	@Test
	public final void testIsOperative() {
		assertTrue(SoundSystem.get().isOperative());
	}

	@Test
	public final void testGet() {
		SoundSystem ss1 = SoundSystem.get();
		SoundSystem ss2 = SoundSystem.get();
		assertTrue("must receive identical instance", (ss1 == ss2));

	}

	@Ignore
	public final void testExit() {
		SoundSystem.get().exit();
		assertFalse(SoundSystem.get().isOperative());
	}

	@Test
	public final void testisValidEntry() {

		assertFalse("value has comma x", SoundSystem.get().isValidEntry(",x", ""));
		assertFalse("key does not start with sfx name has point", SoundSystem.get().isValidEntry("", "."));
		assertTrue("key does  start with sfx name has point", SoundSystem.get().isValidEntry("sfx.", "."));
		assertTrue("value has comma x and name has point", SoundSystem.get().isValidEntry("sfx.,x", "."));

	}

	@Ignore
	public final void testTransferData() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testZoneEntered() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testGetVolumeDelta() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testZoneLeft() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore
	public final void testPlayerMoved() {
		fail("Not yet implemented"); // TODO
	}

}
