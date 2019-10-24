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
		assertEquals(Integer.valueOf(0), ou.getDetail());
		assertEquals(Integer.valueOf(0), ou.getHair());
		assertEquals(Integer.valueOf(0), ou.getHead());
		assertEquals(Integer.valueOf(0), ou.getDress());
		assertEquals(Integer.valueOf(0), ou.getBody());

		// new layers
		assertEquals(Integer.valueOf(0), ou.getHat());
		assertEquals(Integer.valueOf(0), ou.getMask());
		assertEquals(Integer.valueOf(0), ou.getEyes());
		assertEquals(Integer.valueOf(0), ou.getMouth());
	}

	/**
	 * Tests for outfitIntegerIntegerIntegerIntegerInteger.
	 */
	@Test
	public void testOutfitIntegerIntegerIntegerIntegerInteger() {
		final Outfit ou = new Outfit(4, 3, 2, 1, 1, 2, 3, 4, 5);
		assertEquals(Integer.valueOf(1), ou.getDetail());
		assertEquals(Integer.valueOf(2), ou.getHair());
		assertEquals(Integer.valueOf(3), ou.getHead());
		assertEquals(Integer.valueOf(4), ou.getDress());
		assertEquals(Integer.valueOf(5), ou.getBody());

		// new layers
		assertEquals(Integer.valueOf(4), ou.getHat());
		assertEquals(Integer.valueOf(3), ou.getMask());
		assertEquals(Integer.valueOf(2), ou.getEyes());
		assertEquals(Integer.valueOf(1), ou.getMouth());

		final Outfit outfit2 = new Outfit(-4, -3, -2, -1, -1, -2, -3, -4, -5);
		assertEquals(Integer.valueOf(-1), outfit2.getDetail());
		assertEquals(Integer.valueOf(-2), outfit2.getHair());
		assertEquals(Integer.valueOf(-3), outfit2.getHead());
		assertEquals(Integer.valueOf(-4), outfit2.getDress());
		assertEquals(Integer.valueOf(-5), outfit2.getBody());

		// new layers
		assertEquals(Integer.valueOf(-4), outfit2.getHat());
		assertEquals(Integer.valueOf(-3), outfit2.getMask());
		assertEquals(Integer.valueOf(-2), outfit2.getEyes());
		assertEquals(Integer.valueOf(-1), outfit2.getMouth());
	}

	/**
	 * Tests for outfitInt.
	 */
	@Test
	public void testOutfitInt() {
		Outfit ou = new Outfit(0, 0, 0, 0, 0);
		assertEquals(Integer.valueOf(0), ou.getDetail());
		assertEquals(Integer.valueOf(0), ou.getHair());
		assertEquals(Integer.valueOf(0), ou.getHead());
		assertEquals(Integer.valueOf(0), ou.getDress());
		assertEquals(Integer.valueOf(0), ou.getBody());

		// new layers
		assertEquals(Integer.valueOf(0), ou.getMouth());
		assertEquals(Integer.valueOf(0), ou.getEyes());
		assertEquals(Integer.valueOf(0), ou.getMask());
		assertEquals(Integer.valueOf(0), ou.getHat());

		ou = new Outfit(501020304, 4, 3, 2, 1);
		assertEquals(Integer.valueOf(5), ou.getDetail());
		assertEquals(Integer.valueOf(1), ou.getHair());
		assertEquals(Integer.valueOf(2), ou.getHead());
		assertEquals(Integer.valueOf(3), ou.getDress());
		assertEquals(Integer.valueOf(4), ou.getBody());

		// new layers
		assertEquals(Integer.valueOf(4), ou.getMouth());
		assertEquals(Integer.valueOf(3), ou.getEyes());
		assertEquals(Integer.valueOf(2), ou.getMask());
		assertEquals(Integer.valueOf(1), ou.getHat());

		final String outfitnumber = "0501020304";
		ou = new Outfit(Integer.parseInt(outfitnumber), 4, 3, 2, 1);
		assertEquals(Integer.valueOf(5), ou.getDetail());
		assertEquals(Integer.valueOf(1), ou.getHair());
		assertEquals(Integer.valueOf(2), ou.getHead());
		assertEquals(Integer.valueOf(3), ou.getDress());
		assertEquals(Integer.valueOf(4), ou.getBody());

		// new layers
		assertEquals(Integer.valueOf(4), ou.getMouth());
		assertEquals(Integer.valueOf(3), ou.getEyes());
		assertEquals(Integer.valueOf(2), ou.getMask());
		assertEquals(Integer.valueOf(1), ou.getHat());
	}

	/**
	 * Tests for getCode.
	 */
	@Test
	public void testGetCode() {
		assertEquals(12345678, new Outfit(12345678, 4, 3, 2, 1).getCode());
	}

	/**
	 * Tests for putOver.
	 */
	@Test
	public void testPutOver() {
		Outfit orig = new Outfit(12345678, 4, 3, 2, 1);
		final Outfit pullover = new Outfit();
		assertEquals(12345678, orig.getCode());

		Outfit result = orig.putOver(pullover);
		assertEquals(12345678, result.getCode());


		orig = new Outfit(99, 98, 97, 96, null, 12, 34, 56, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(12), result.getHair());
		assertEquals(Integer.valueOf(34), result.getHead());
		assertEquals(Integer.valueOf(56), result.getDress());
		assertEquals(Integer.valueOf(0), result.getBody());

		// new layers
		assertEquals(Integer.valueOf(99), result.getHat());
		assertEquals(Integer.valueOf(98), result.getMask());
		assertEquals(Integer.valueOf(97), result.getEyes());
		assertEquals(Integer.valueOf(96), result.getMouth());

		orig = new Outfit(null, 98, 97, 96, null, 12, 34, null, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(12), result.getHair());
		assertEquals(Integer.valueOf(34), result.getHead());
		assertEquals(Integer.valueOf(0), result.getDress());
		assertEquals(Integer.valueOf(0), result.getBody());

		// new layers
		assertEquals(Integer.valueOf(0), result.getHat());
		assertEquals(Integer.valueOf(98), result.getMask());
		assertEquals(Integer.valueOf(97), result.getEyes());
		assertEquals(Integer.valueOf(96), result.getMouth());

		orig = new Outfit(null, null, 97, null, null, 12, null, null, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(12), result.getHair());
		assertEquals(Integer.valueOf(0), result.getHead());
		assertEquals(Integer.valueOf(0), result.getDress());
		assertEquals(Integer.valueOf(0), result.getBody());

		// new layers
		assertEquals(Integer.valueOf(0), result.getHat());
		assertEquals(Integer.valueOf(0), result.getMask());
		assertEquals(Integer.valueOf(97), result.getEyes());
		assertEquals(Integer.valueOf(0), result.getMouth());

		orig = new Outfit(null, null, null, null, null, null, null, null, null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getHair());
		assertEquals(Integer.valueOf(0), result.getHead());
		assertEquals(Integer.valueOf(0), result.getDress());
		assertEquals(Integer.valueOf(0), result.getBody());

		// new layers
		assertEquals(Integer.valueOf(0), result.getHat());
		assertEquals(Integer.valueOf(0), result.getMask());
		assertEquals(Integer.valueOf(0), result.getEyes());
		assertEquals(Integer.valueOf(0), result.getMouth());
	}

	/**
	 * Tests for isPartOf.
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
		of = new Outfit(0, 0, 0, 0, null, Outfits.HAIR_OUTFITS, 0, 0, 0);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, 0, 0, null, 0, Outfits.HEAD_OUTFITS, 0, 0);

		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, 0, 0, null, 0, 0, Outfits.CLOTHES_OUTFITS, 0);

		assertFalse(of.isChoosableByPlayers());
		of = new Outfit(0, 0, 0, 0, null, 0, 0, 0, Outfits.BODY_OUTFITS);
		assertFalse(of.isChoosableByPlayers());
		// fail("wont work for any part of outfit == null");

		// TODO: new layers
	}

	/**
	 * Tests for isNaked.
	 */
	@Test
	public void testIsNaked() {
		Outfit of = new Outfit();
		assertTrue(of.isNaked());
		 of = new Outfit(null, null, null, null, null, 0, 0, null, 0);
		assertTrue(of.isNaked());
		 of = new Outfit(null, null, null, null, null, 0, 0, 1, 0);
		assertFalse(of.isNaked());
	}

	/**
	 * Tests for removeOutfit
	 */
	@Test
	public void testRemoveOutfit() {
		Outfit orig = new Outfit(12345678, 9, 10, 11, 12);
		Outfit result = orig.removeOutfit(new Outfit(null, null, null, null, 12, null, null, null, null));
		assertEquals(Integer.valueOf(0), result.getDetail());
		assertEquals(Integer.valueOf(12), result.getHair());
		assertEquals(Integer.valueOf(34), result.getHead());
		assertEquals(Integer.valueOf(56), result.getDress());
		assertEquals(Integer.valueOf(78), result.getBody());

		// new layers
		assertEquals(Integer.valueOf(9), result.getMouth());
		assertEquals(Integer.valueOf(10), result.getEyes());
		assertEquals(Integer.valueOf(11), result.getMask());
		assertEquals(Integer.valueOf(12), result.getHat());

		orig = new Outfit(null, null, null, null, null, 12, 34, 56, null);
		result = orig.removeOutfit(new Outfit(null, null, null, null, 1, null, null, null, null));
		assertEquals(12345600, result.getCode());

		orig = new Outfit(12345678, 9, 10, 11, 12);
		result = orig.removeOutfit(new Outfit(12345678, 9, 10, 11, 12));
		assertEquals(0, result.getCode());
	}
}
