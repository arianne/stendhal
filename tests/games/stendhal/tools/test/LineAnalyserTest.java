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
package games.stendhal.tools.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LineAnalyserTest {

	/**
	 * Tests for empty.
	 */
	@Test
	public void testEmpty() {
		final LineAnalyser analyser = new LineAnalyser("");
		assertEquals("", analyser.getLine());
		assertEquals("", analyser.getStripped());
		assertNull(analyser.getProtagonist());
		assertEquals("", analyser.getText());
		assertFalse(analyser.isComment());
		assertTrue(analyser.isEmpty());
		assertFalse(analyser.isNPCSpeaking());
		assertFalse(analyser.isPlayerSpeaking());
		assertFalse(analyser.isStatus());
	}

	/**
	 * Tests for space.
	 */
	@Test
	public void testSpace() {
		final LineAnalyser analyser = new LineAnalyser(" ");
		assertEquals("", analyser.getLine());
		assertEquals("", analyser.getStripped());
		assertNull(analyser.getProtagonist());
		assertEquals("", analyser.getText());
		assertFalse(analyser.isComment());
		assertTrue(analyser.isEmpty());
		assertFalse(analyser.isNPCSpeaking());
		assertFalse(analyser.isPlayerSpeaking());
		assertFalse(analyser.isStatus());
	}

	/**
	 * Tests for player.
	 */
	@Test
	public void testPlayer() {
		final LineAnalyser analyser = new LineAnalyser("[21:24] <player> hi");
		assertEquals("[21:24] <player> hi", analyser.getLine());
		assertEquals("<player> hi", analyser.getStripped());
		assertEquals("player", analyser.getProtagonist());
		assertEquals("hi", analyser.getText());
		assertFalse(analyser.isComment());
		assertFalse(analyser.isEmpty());
		assertFalse(analyser.isNPCSpeaking());
		assertTrue(analyser.isPlayerSpeaking());
		assertFalse(analyser.isStatus());
	}

	/**
	 * Tests for nPC.
	 */
	@Test
	public void testNPC() {
		final LineAnalyser analyser = new LineAnalyser("[21:24] <Plink> *cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?");
		assertEquals("[21:24] <Plink> *cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?", analyser.getLine());
		assertEquals("<Plink> *cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?", analyser.getStripped());
		assertEquals("Plink", analyser.getProtagonist());
		assertEquals("*cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?", analyser.getText());
		assertFalse(analyser.isComment());
		assertFalse(analyser.isEmpty());
		assertTrue(analyser.isNPCSpeaking());
		assertFalse(analyser.isPlayerSpeaking());
		assertFalse(analyser.isStatus());
	}

	/**
	 * Tests for comment.
	 */
	@Test
	public void testComment() {
		final LineAnalyser analyser = new LineAnalyser("// he doesn't do anything.");
		assertEquals("// he doesn't do anything.", analyser.getLine());
		assertEquals("he doesn't do anything.", analyser.getStripped());
		assertNull(analyser.getProtagonist());
		assertEquals("he doesn't do anything.", analyser.getText());
		assertTrue(analyser.isComment());
		assertFalse(analyser.isEmpty());
		assertFalse(analyser.isNPCSpeaking());
		assertFalse(analyser.isPlayerSpeaking());
		assertTrue(analyser.isStatus());
	}

	/**
	 * Tests for status.
	 */
	@Test
	public void testStatus() {
		final LineAnalyser analyser = new LineAnalyser("[21:25] player earns 10 experience points.");
		assertEquals("[21:25] player earns 10 experience points.", analyser.getLine());
		assertEquals("player earns 10 experience points.", analyser.getStripped());
		assertNull(analyser.getProtagonist());
		assertEquals("player earns 10 experience points.", analyser.getText());
		assertFalse(analyser.isComment());
		assertFalse(analyser.isEmpty());
		assertFalse(analyser.isNPCSpeaking());
		assertFalse(analyser.isPlayerSpeaking());
		assertTrue(analyser.isStatus());
	}
}
