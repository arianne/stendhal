/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.awt.Color;

import org.junit.Test;

import marauroa.common.game.RPObject;
import utilities.PlayerTestHelper;

/**
 * Tests for OutfitColor.
 */
public class OutfitColorTest {
	/**
	 * Check situations when get() should return PLAIN.
	 */
	@Test
	public void testConstructPlain() {
		// no color
		RPObject player = PlayerTestHelper.createPlayer("Mannequin");
		OutfitColor color = OutfitColor.get(player);
		// Should be the same, not just equal
		assertSame(OutfitColor.PLAIN, color);
		// Ensure that the player has an empty color map
		player.put("outfit_colors", "dress", 0);
		player.remove("outfit_colors", "dress");
		// Should still be the same instance
		color = OutfitColor.get(player);
		assertSame(OutfitColor.PLAIN, color);
		// Should change now
		player.put("outfit_colors", "dress", 0);
		color = OutfitColor.get(player);
		assertNotSame(OutfitColor.PLAIN, color);
		// also should be unequal
		assertFalse(OutfitColor.PLAIN.equals(color));

		// try the plain constructor
		player.remove("outfit_colors", "dress");
		color = new OutfitColor(player);
		// must not be the same
		assertNotSame(OutfitColor.PLAIN, color);
		// but must be equal
		assertEquals(OutfitColor.PLAIN, color);
	}

	/**
	 * Test feeding invalid values to the color map. The server should not do
	 * that, but it should be handled gracefully.
	 */
	@Test
	public void testInvalidValue() {
		RPObject player = PlayerTestHelper.createPlayer("Mannequin");
		// Ensure that the player has an empty color map
		player.put("outfit_colors", "dress", "foo");
		// invalid value in "dress", but otherwise plain
		OutfitColor color = OutfitColor.get(player);
		// Same instance is not guaranteed
		assertEquals(OutfitColor.PLAIN, color);
		player.remove("outfit_colors", "dress");
		player.put("outfit_colors", "sky", "blue");
		color = OutfitColor.get(player);
		assertEquals(OutfitColor.PLAIN, color);

		player.put("outfit_colors", "dress", 1);
		color = OutfitColor.get(player);
		// Unrecognized value
		assertNotSame(OutfitColor.PLAIN, color);
		// also should be unequal
		assertFalse(OutfitColor.PLAIN.equals(color));
	}

	/**
	 * Test hashCode.
	 */
	@Test
	public void testHashCode() {
		RPObject player = PlayerTestHelper.createPlayer("Mannequin");
		OutfitColor color = OutfitColor.get(player);
		assertEquals(OutfitColor.PLAIN.hashCode(), color.hashCode());

		// two equal instances should have the same hashcode
		player.put("outfit_colors", "dress", 0);
		color = OutfitColor.get(player);
		assertEquals(color.hashCode(), OutfitColor.get(player).hashCode());
		// unrecognized part, should not affect
		player.put("outfit_colors", "sky", 0x0000ff);
		assertEquals(color.hashCode(), OutfitColor.get(player).hashCode());

		player.put("outfit_colors", "hair", 0xbad);
		color = OutfitColor.get(player);
		assertEquals(color.hashCode(), OutfitColor.get(player).hashCode());
	}

	/**
	 * Test equals.
	 */
	@Test
	public void testEquals() {
		// about everything else is tested many times elsewhere
		assertFalse(OutfitColor.PLAIN.equals(0));
		assertFalse(OutfitColor.PLAIN.equals(42));
		assertFalse(OutfitColor.PLAIN.equals(new Object()));
	}

	/**
	 * Test toString
	 */
	@Test
	public void testToString() {
		RPObject player = PlayerTestHelper.createPlayer("Mannequin");
		OutfitColor color = OutfitColor.get(player);
		assertEquals("", color.toString());
		player.put("outfit_colors", "dress", 0);
		color = OutfitColor.get(player);
		assertEquals("dress=" + 0xff000000 + ";", color.toString());
		player.put("outfit_colors", "sky", 0x0000ff);
		color = OutfitColor.get(player);
		assertEquals("dress=" + 0xff000000 + ";", color.toString());
		player.put("outfit_colors", "hair", 0xff00beeb);
		color = OutfitColor.get(player);
		assertEquals("dress=" + 0xff000000 + ";hair="
				+ 0xff00beeb + ";", color.toString());
		// Consistent ordering:
		player.put("outfit_colors", "detail", 0xfff00ba5);
		color = OutfitColor.get(player);
		assertEquals("Inconsistent ordering", "detail=" + 0xfff00ba5 +
				";dress=" + 0xff000000 + ";hair=" + 0xff00beeb + ";", color.toString());
	}

	/**
	 * Test getting and setting a color.
	 */
	@Test
	public void testGetSetColor() {
		RPObject player = PlayerTestHelper.createPlayer("Mannequin");
		OutfitColor color = new OutfitColor(player);
		color.setColor("hair", Color.BLUE);
		assertEquals(color.getColor("hair"), Color.BLUE);
		assertFalse(color.getColor("hair").equals(Color.BLACK));

		// set via constructor
		player.put("outfit_colors", "dress", 0xff00);
		color = OutfitColor.get(player);
		assertEquals(color.getColor("dress"), Color.GREEN);
	}
}
