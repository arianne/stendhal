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
package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.common.Outfits;

public class OutfitTest {

	/**
	 * Tests for outfit.
	 */
	@Test
	public void testOutfit() {
		final Outfit ou = new Outfit();
		assertEquals(Integer.valueOf(0), ou.getLayer("detail"));
		assertEquals(Integer.valueOf(0), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(0), ou.getLayer("dress"));
		assertEquals(Integer.valueOf(0), ou.getLayer("body"));

		// extended layers
		assertEquals(Integer.valueOf(0), ou.getLayer("hat"));
		assertEquals(Integer.valueOf(0), ou.getLayer("mask"));
		assertEquals(Integer.valueOf(0), ou.getLayer("eyes"));
		assertEquals(Integer.valueOf(0), ou.getLayer("mouth"));
	}

	/**
	 * Tests for outfitIntegerIntegerIntegerIntegerInteger.
	 */
	@Test
	public void testOutfitIntegerIntegerIntegerIntegerInteger() {
		final Outfit ou = new Outfit(9, 8, 7, 6, 5, 4, 3, 2, 1);
		assertEquals(Integer.valueOf(9), ou.getLayer("body"));
		assertEquals(Integer.valueOf(8), ou.getLayer("dress"));
		assertEquals(Integer.valueOf(7), ou.getLayer("head"));
		assertEquals(Integer.valueOf(6), ou.getLayer("mouth"));
		assertEquals(Integer.valueOf(5), ou.getLayer("eyes"));
		assertEquals(Integer.valueOf(4), ou.getLayer("mask"));
		assertEquals(Integer.valueOf(3), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(2), ou.getLayer("hat"));
		assertEquals(Integer.valueOf(1), ou.getLayer("detail"));

		final Outfit outfit2 = new Outfit(-9, -8, -7, -6, -5, -4, -3, -2, -1);
		assertEquals(Integer.valueOf(-9), outfit2.getLayer("body"));
		assertEquals(Integer.valueOf(-8), outfit2.getLayer("dress"));
		assertEquals(Integer.valueOf(-7), outfit2.getLayer("head"));
		assertEquals(Integer.valueOf(-6), outfit2.getLayer("mouth"));
		assertEquals(Integer.valueOf(-5), outfit2.getLayer("eyes"));
		assertEquals(Integer.valueOf(-4), outfit2.getLayer("mask"));
		assertEquals(Integer.valueOf(-3), outfit2.getLayer("hair"));
		assertEquals(Integer.valueOf(-2), outfit2.getLayer("hat"));
		assertEquals(Integer.valueOf(-1), outfit2.getLayer("detail"));
	}

	/**
	 * Tests for outfitInt.
	 *
	 * TODO: make tests more extensive for extended layers
	 */
	@Test
	public void testOutfitInt() {
		Outfit ou = new Outfit(0, 0, 0, 0, 0, 0, 0, 0, 0);
		assertEquals(Integer.valueOf(0), ou.getLayer("detail"));
		assertEquals(Integer.valueOf(0), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(0), ou.getLayer("dress"));
		assertEquals(Integer.valueOf(0), ou.getLayer("body"));

		// extended layers
		assertEquals(Integer.valueOf(0), ou.getLayer("mouth"));
		assertEquals(Integer.valueOf(0), ou.getLayer("eyes"));
		assertEquals(Integer.valueOf(0), ou.getLayer("mask"));
		assertEquals(Integer.valueOf(0), ou.getLayer("hat"));

		// Note: old outfit parts are mapped to new system. So some input values may not be the same as output.

		ou = new Outfit("501020304");
		assertEquals(Integer.valueOf(5), ou.getLayer("detail"));
		assertEquals(Integer.valueOf(1), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(1), ou.getLayer("head"));
		assertEquals(Integer.valueOf(5), ou.getLayer("dress")); // old dress 3 is now mapped to 5
		assertEquals(Integer.valueOf(0), ou.getLayer("body"));

		// extended layers
		assertEquals(Integer.valueOf(0), ou.getLayer("mouth"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		assertEquals(Integer.valueOf(0), ou.getLayer("mask"));
		assertEquals(Integer.valueOf(0), ou.getLayer("hat"));

		final String outfitnumber = "0501020304";
		ou = new Outfit(outfitnumber);
		assertEquals(Integer.valueOf(5), ou.getLayer("detail"));
		assertEquals(Integer.valueOf(1), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(1), ou.getLayer("head"));
		assertEquals(Integer.valueOf(5), ou.getLayer("dress")); // old dress 3 is now mapped to 5
		assertEquals(Integer.valueOf(0), ou.getLayer("body"));

		// extended layers
		assertEquals(Integer.valueOf(0), ou.getLayer("mouth"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		assertEquals(Integer.valueOf(0), ou.getLayer("mask"));
		assertEquals(Integer.valueOf(0), ou.getLayer("hat"));
	}

	/**
	 * Tests for getCode.
	 */
	@Test
	public void testGetCode() {
		assertEquals(12345678, new Outfit("12345678").getCode());
		assertEquals(907030201, new Outfit(1, 2, 3, 4, 5, 6, 7, 8, 9).getCode());
	}

	/**
	 * Tests for putOver.
	 */
	@Test
	public void testPutOver() {
		Outfit orig = new Outfit("12345678");
		final Outfit pullover = new Outfit();
		assertEquals(12345678, orig.getCode());

		Outfit result = orig.putOver(pullover);
		assertEquals(12345678, result.getCode());


		orig = new Outfit(99, 98, 97, 96, 95, 94, 93, 92, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(99), result.getLayer("body"));
		assertEquals(Integer.valueOf(98), result.getLayer("dress"));
		assertEquals(Integer.valueOf(97), result.getLayer("head"));
		assertEquals(Integer.valueOf(96), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(95), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(94), result.getLayer("mask"));
		assertEquals(Integer.valueOf(93), result.getLayer("hair"));
		assertEquals(Integer.valueOf(92), result.getLayer("hat"));

		orig = new Outfit(null, 98, 97, 96, 95, 94, 93, 92, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getLayer("body"));
		assertEquals(Integer.valueOf(98), result.getLayer("dress"));
		assertEquals(Integer.valueOf(97), result.getLayer("head"));
		assertEquals(Integer.valueOf(96), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(95), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(94), result.getLayer("mask"));
		assertEquals(Integer.valueOf(93), result.getLayer("hair"));
		assertEquals(Integer.valueOf(92), result.getLayer("hat"));

		orig = new Outfit(null, null, 97, 96, 95, 94, 93, 92, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getLayer("body"));
		assertEquals(Integer.valueOf(0), result.getLayer("dress"));
		assertEquals(Integer.valueOf(97), result.getLayer("head"));
		assertEquals(Integer.valueOf(96), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(95), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(94), result.getLayer("mask"));
		assertEquals(Integer.valueOf(93), result.getLayer("hair"));
		assertEquals(Integer.valueOf(92), result.getLayer("hat"));

		orig = new Outfit(null, null, null, 96, 95, 94, 93, 92, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getLayer("body"));
		assertEquals(Integer.valueOf(0), result.getLayer("dress"));
		assertEquals(Integer.valueOf(0), result.getLayer("head"));
		assertEquals(Integer.valueOf(96), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(95), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(94), result.getLayer("mask"));
		assertEquals(Integer.valueOf(93), result.getLayer("hair"));
		assertEquals(Integer.valueOf(92), result.getLayer("hat"));

		orig = new Outfit(null, null, null, null, 95, 94, 93, 92, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getLayer("body"));
		assertEquals(Integer.valueOf(0), result.getLayer("dress"));
		assertEquals(Integer.valueOf(0), result.getLayer("head"));
		assertEquals(Integer.valueOf(0), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(95), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(94), result.getLayer("mask"));
		assertEquals(Integer.valueOf(93), result.getLayer("hair"));
		assertEquals(Integer.valueOf(92), result.getLayer("hat"));

		orig = new Outfit(null, null, null, null, null, 94, 93, 92, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getLayer("body"));
		assertEquals(Integer.valueOf(0), result.getLayer("dress"));
		assertEquals(Integer.valueOf(0), result.getLayer("head"));
		assertEquals(Integer.valueOf(0), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(0), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(94), result.getLayer("mask"));
		assertEquals(Integer.valueOf(93), result.getLayer("hair"));
		assertEquals(Integer.valueOf(92), result.getLayer("hat"));

		orig = new Outfit(null, null, null, null, null, null, 93, 92, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getLayer("body"));
		assertEquals(Integer.valueOf(0), result.getLayer("dress"));
		assertEquals(Integer.valueOf(0), result.getLayer("head"));
		assertEquals(Integer.valueOf(0), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(0), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(0), result.getLayer("mask"));
		assertEquals(Integer.valueOf(93), result.getLayer("hair"));
		assertEquals(Integer.valueOf(92), result.getLayer("hat"));

		orig = new Outfit(null, null, null, null, null, null, null, 92, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getLayer("body"));
		assertEquals(Integer.valueOf(0), result.getLayer("dress"));
		assertEquals(Integer.valueOf(0), result.getLayer("head"));
		assertEquals(Integer.valueOf(0), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(0), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(0), result.getLayer("mask"));
		assertEquals(Integer.valueOf(0), result.getLayer("hair"));
		assertEquals(Integer.valueOf(92), result.getLayer("hat"));

		orig = new Outfit(null, null, null, null, null, null, null, null, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getLayer("body"));
		assertEquals(Integer.valueOf(0), result.getLayer("dress"));
		assertEquals(Integer.valueOf(0), result.getLayer("head"));
		assertEquals(Integer.valueOf(0), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(0), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(0), result.getLayer("mask"));
		assertEquals(Integer.valueOf(0), result.getLayer("hair"));
		assertEquals(Integer.valueOf(0), result.getLayer("hat"));
	}

	/**
	 * Tests for isPartOf.
	 *
	 * TODO: make tests more extensive for extended layers
	 */
	@Test
	public void testIsPartOf() {
		final Outfit of = new Outfit();
		assertTrue(of.isPartOf(of));
		Outfit part = new Outfit(null, null, null, null, null, null, null, null, null);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, null, null, null, null, 0, null, null, null);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, null, null, null, null, null, 0, null, null);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, null, null, null, null, null, null, 0, null);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, null, null, null, null, null, null, null, 0);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, null, null, null, null, 0, null, null, 0);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, null, null, null, null, 1, null, null, 0);
		assertFalse(part.isPartOf(of));
		part = new Outfit(null, null, null, null, null, 1, 5, 5, 0);
		assertFalse(part.isPartOf(of));
		part = new Outfit(1, null, null, null, null, null, null, null, null);
		assertFalse(part.isPartOf(of));
	}

	/**
	 * Tests for isChoosableByPlayers.
	 */
	@Test
	public void testIsChoosableByPlayers() {
		Outfit of = new Outfit();
		assertTrue(of.isChoosableByPlayers());
		of = new Outfit(Outfits.BODY_OUTFITS, 0, 0, 0, 0, 0, 0, 0, null);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, Outfits.CLOTHES_OUTFITS, 0, 0, 0, 0, 0, 0, null);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, Outfits.HEAD_OUTFITS, 0, 0, 0, 0, 0, null);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, 0, Outfits.MOUTH_OUTFITS, 0, 0, 0, 0, null);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, 0, 0, Outfits.EYES_OUTFITS, 0, 0, 0, null);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, 0, 0, 0, Outfits.MASK_OUTFITS, 0, 0, null);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, 0, 0, 0, 0, Outfits.HAIR_OUTFITS, 0, null);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, 0, 0, 0, 0, 0, Outfits.HAT_OUTFITS, null);
		assertFalse(of.isChoosableByPlayers());
		// fail("wont work for any part of outfit == null");
	}

	/**
	 * Tests for isNaked.
	 */
	@Test
	public void testIsNaked() {
		Outfit of = new Outfit();
		assertTrue(of.isNaked());
		of = new Outfit(0, null, 0, null, null, null, 0, null, null);
		assertTrue(of.isNaked());
		of = new Outfit(0, 1, 0, null, null, null, 0, null, null);
		assertFalse(of.isNaked());
	}

	/**
	 * Tests for removeOutfit
	 *
	 * TODO: make tests more extensive for extended layers
	 */
	@Test
	public void testRemoveOutfit() {
		Outfit orig = new Outfit("12345678");
		Outfit result = orig.removeOutfit(null, null, null, null, null, null, null, null, 12);
		assertEquals(Integer.valueOf(978), result.getLayer("body")); // old special body sprite indexes started at 78 & have been incremented by 900, so 78 + 900
		assertEquals(Integer.valueOf(56), result.getLayer("dress"));
		assertEquals(Integer.valueOf(34), result.getLayer("head"));
		assertEquals(Integer.valueOf(0), result.getLayer("mouth"));
		assertEquals(Integer.valueOf(0), result.getLayer("eyes"));
		assertEquals(Integer.valueOf(0), result.getLayer("mask"));
		assertEquals(Integer.valueOf(12), result.getLayer("hair"));
		assertEquals(Integer.valueOf(0), result.getLayer("hat"));
		assertEquals(Integer.valueOf(0), result.getLayer("detail"));

		orig = new Outfit(null, 56, 34, null, null, null, 12, null, null);
		result = orig.removeOutfit(null, null, null, null, null, null, null, null, 1);
		assertEquals(12345600, result.getCode());

		orig = new Outfit("12345678");
		result = orig.removeOutfit(new Outfit("12345678"));
		assertEquals(0, result.getCode());
	}

	/**
	 * Tests for mapping done from old outfit code to new extended outfit layers
	 */
	@Test
	public void testMapOldOutfit() {
		// male body
		Outfit ou = new Outfit("14");
		assertEquals(Integer.valueOf(0), ou.getLayer("body"));
		// female body 1
		ou = new Outfit("11");
		assertEquals(Integer.valueOf(1), ou.getLayer("body"));
		// female body 2
		ou = new Outfit("13");
		assertEquals(Integer.valueOf(2), ou.getLayer("body"));

		// head w/ large ears
		ou = new Outfit("0");
		assertEquals(Integer.valueOf(1), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		ou = new Outfit("10000");
		assertEquals(Integer.valueOf(1), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		ou = new Outfit("20000");
		assertEquals(Integer.valueOf(1), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		ou = new Outfit("30000");
		assertEquals(Integer.valueOf(1), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		ou = new Outfit("40000");
		assertEquals(Integer.valueOf(1), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		// head w/ small ears
		ou = new Outfit("50000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(0), ou.getLayer("eyes"));
		ou = new Outfit("60000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(0), ou.getLayer("eyes"));
		ou = new Outfit("70000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		// small eyes & long chin
		ou = new Outfit("80000");
		assertEquals(Integer.valueOf(2), ou.getLayer("head"));
		assertEquals(Integer.valueOf(18), ou.getLayer("eyes"));
		// glasses
		ou = new Outfit("90000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("mask"));
		// blue eyes
		ou = new Outfit("100000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(0), ou.getLayer("eyes"));
		// green eyes
		ou = new Outfit("110000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(19), ou.getLayer("eyes"));
		// more small eyes
		ou = new Outfit("120000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(23), ou.getLayer("eyes"));
		// bright blue eyes
		ou = new Outfit("130000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(21), ou.getLayer("eyes"));
		// red eyes
		ou = new Outfit("140000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		// blinking eyes
		ou = new Outfit("150000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("mouth"));
		assertEquals(Integer.valueOf(13), ou.getLayer("eyes"));
		// green eyebrows
		ou = new Outfit("160000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(14), ou.getLayer("eyes"));
		// pink eyes
		ou = new Outfit("170000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(20), ou.getLayer("eyes"));
		// bright blue eyes with lips
		ou = new Outfit("180000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(2), ou.getLayer("mouth"));
		assertEquals(Integer.valueOf(21), ou.getLayer("eyes"));
		// thick eyebrows
		ou = new Outfit("190000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(2), ou.getLayer("eyes"));
		// scarred eye
		ou = new Outfit("200000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(15), ou.getLayer("eyes"));
		// eyepatch
		ou = new Outfit("210000");
		assertEquals(Integer.valueOf(0), ou.getLayer("head"));
		assertEquals(Integer.valueOf(1), ou.getLayer("eyes"));
		assertEquals(Integer.valueOf(4), ou.getLayer("mask"));

		// shoulder length curly hair
		ou = new Outfit("3000000");
		assertEquals(Integer.valueOf(23), ou.getLayer("hair"));
		// long ponytail
		ou = new Outfit("13000000");
		assertEquals(Integer.valueOf(26), ou.getLayer("hair"));
		// shoulder length curly hair
		ou = new Outfit("15000000");
		assertEquals(Integer.valueOf(23), ou.getLayer("hair"));
		// short hair w/ baseball cap
		ou = new Outfit("33000000");
		assertEquals(Integer.valueOf(7), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(1), ou.getLayer("hat"));
		ou = new Outfit("34000000");
		assertEquals(Integer.valueOf(7), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(1), ou.getLayer("hat"));
		// hood
		ou = new Outfit("37000000");
		assertEquals(Integer.valueOf(0), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(13), ou.getLayer("hat"));
		// shoulder length hair
		ou = new Outfit("38000000");
		assertEquals(Integer.valueOf(20), ou.getLayer("hair"));
		// backward baseball cap
		ou = new Outfit("39000000");
		assertEquals(Integer.valueOf(0), ou.getLayer("hair"));
		assertEquals(Integer.valueOf(2), ou.getLayer("hat"));

		// casual jacket
		ou = new Outfit("200");
		assertEquals(Integer.valueOf(6), ou.getLayer("dress"));
		// casual clothes
		ou = new Outfit("300");
		assertEquals(Integer.valueOf(5), ou.getLayer("dress"));
		// denim jacket
		ou = new Outfit("500");
		assertEquals(Integer.valueOf(6), ou.getLayer("dress"));
		// robe
		ou = new Outfit("1800");
		assertEquals(Integer.valueOf(22), ou.getLayer("dress"));
		ou = new Outfit("2300");
		assertEquals(Integer.valueOf(22), ou.getLayer("dress"));
		// leather jacket
		ou = new Outfit("2400");
		assertEquals(Integer.valueOf(52), ou.getLayer("dress"));
		// sleeveless dress
		ou = new Outfit("2900");
		assertEquals(Integer.valueOf(27), ou.getLayer("dress"));
		// robe
		ou = new Outfit("3500");
		assertEquals(Integer.valueOf(22), ou.getLayer("dress"));
		// soldier uniform /w cape
		ou = new Outfit("4300");
		assertEquals(Integer.valueOf(50), ou.getLayer("dress"));
		// other robe
		ou = new Outfit("4600");
		assertEquals(Integer.valueOf(29), ou.getLayer("dress"));
		// soldier uniform
		ou = new Outfit("5000");
		assertEquals(Integer.valueOf(11), ou.getLayer("dress"));
		// uniform/armor w/ cape
		ou = new Outfit("6200");
		assertEquals(Integer.valueOf(23), ou.getLayer("dress"));
	}
}
