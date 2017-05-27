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
package games.stendhal.client.sprite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Image;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * Tests for CompositeSprite
 */
public class CompositeSpriteTest {
	/**
	 * Check that multiple empty sprites get merged to one empty sprite.
	 */
	@Test
	public void testMergeEmpty() {
		Sprite empty0 = new EmptySprite(32, 32, 0xf00f);
		Sprite empty1 = new EmptySprite(32, 32, 0x0ff0);

		List<Sprite> slaves = new LinkedList<Sprite>();
		slaves.add(empty0);
		slaves.add(empty1);

		SpriteCache cache = new SpriteCache();
		Sprite result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue((result == empty0) || (result == empty1));

		Sprite empty2 = new EmptySprite(32, 32, 0xaaaa);
		slaves.clear();
		slaves.add(empty0);
		slaves.add(empty1);
		slaves.add(empty2);
		result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue((result == empty0) || (result == empty1) || (result == empty2));
	}

	/**
	 * Test merging empty sprites with an image sprite
	 */
	@Test
	public void testMergeEmptyAndImage() {
		Sprite empty0 = new EmptySprite(32, 32, 0xf00f);
		Sprite empty1 = new EmptySprite(32, 32, 0x0ff0);
		// null image will do fine as long as we don't draw them
		Image img = null;
		Sprite image = new ImageSprite(img, 0xaaa);

		// check adding empty first
		List<Sprite> slaves = new LinkedList<Sprite>();
		slaves.add(empty0);
		slaves.add(image);

		SpriteCache cache = new SpriteCache();
		Sprite result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result == image);

		// Then the other way around
		slaves.clear();
		slaves.add(image);
		slaves.add(empty0);
		result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result == image);

		// lastly an image between two empty sprites
		slaves.clear();
		slaves.add(empty0);
		slaves.add(image);
		slaves.add(empty1);
		result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result == image);
	}

	/**
	 * Test merging 2 same ImageSprites
	 */
	@Test
	public void testMergeIdentical() {
		Sprite empty0 = new EmptySprite(32, 32, 0xf00f);
		// null image will do fine as long as we don't draw them
		Image img = null;
		Sprite image = new ImageSprite(img, 0xaaa);

		// fist check the simple case
		List<Sprite> slaves = new LinkedList<Sprite>();
		slaves.add(image);
		slaves.add(image);

		SpriteCache cache = new SpriteCache();
		Sprite result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result == image);

		// The same with 3 copies
		slaves.clear();
		slaves.add(image);
		slaves.add(image);
		slaves.add(image);
		result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result == image);

		// complicated sample with a couple of empty images
		slaves.clear();
		slaves.add(empty0);
		slaves.add(image);
		slaves.add(empty0);
		slaves.add(empty0);
		slaves.add(image);
		slaves.add(empty0);
		slaves.add(image);
		slaves.add(empty0);
		slaves.add(empty0);
		result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result == image);
	}

	/**
	 * Test merging references gives sane hash codes
	 */
	@Test
	public void testMergeRefs() {
		// null image will do fine as long as we don't draw them
		Image img = null;
		Sprite image0 = new ImageSprite(img, 0xf00f);
		Sprite image1 = new ImageSprite(img, 0x0ff0);
		Sprite image2 = new ImageSprite(img, 0xaaaa);

		// fist check the simple case
		List<Sprite> slaves = new LinkedList<Sprite>();
		slaves.add(image0);
		slaves.add(image1);

		SpriteCache cache = new SpriteCache();
		Sprite result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result instanceof CompositeSprite);
		assertEquals(0xffff, result.getReference().hashCode());

		slaves.clear();
		slaves.add(image0);
		slaves.add(image1);
		slaves.add(image2);
		result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result instanceof CompositeSprite);
		assertEquals(0x5555, result.getReference().hashCode());
	}

	/**
	 * Check that previous one is got from a cache, should there be an
	 * equivalent composite
	 */
	public void testCacheBehaviour() {
		// null image will do fine as long as we don't draw them
		Image img = null;
		Sprite image0 = new ImageSprite(img, 0xf00f);
		Sprite image1 = new ImageSprite(img, 0x0ff0);

		// fist check the simple case
		List<Sprite> slaves = new LinkedList<Sprite>();
		slaves.add(image0);
		slaves.add(image1);

		SpriteCache cache = new SpriteCache();
		Sprite result = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result instanceof CompositeSprite);
		assertEquals(0xffff, result.getReference().hashCode());

		// different, but equivalent composite
		slaves.clear();
		slaves.add(image0);
		slaves.add(new EmptySprite(32, 32, "plugh"));
		slaves.add(image1);
		Sprite result2 = CompositeSprite.getComposite(cache, slaves, null, null);
		assertTrue(result2 instanceof CompositeSprite);
		assertTrue(result == result2);
	}
}
