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
package games.stendhal.client.gui.chattext;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChatCacheTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for chatCache.
	 */
	@Test
	public void testChatCache() {
		new ChatCache(null);
	}

	/**
	 * Tests for getLines.
	 */
	@Test
	public void testGetLines() {
		ChatCache cache = new ChatCache(null);
		assertTrue(cache.getLines().isEmpty());
		cache.addlinetoCache("one");
		assertFalse(cache.getLines().isEmpty());
	}


	/**
	 * Tests for getAndSetCurrent.
	 */
	@Test
	public void testGetAndSetCurrent() {
		ChatCache cache = new ChatCache(null);
		cache.setCurrent(0);
		assertThat(cache.getCurrent(), is(0));
		cache.setCurrent(10);
		assertThat(cache.getCurrent(), is(10));

	}

	/**
	 * Tests for addlinetoCache.
	 */
	@Test
	public void testAddlinetoCache() {
		ChatCache cache = new ChatCache(null);

		cache.addlinetoCache("one");
		assertThat(cache.previous(), is("one"));
		cache.addlinetoCache("two");
		assertThat(cache.previous(), is("two"));
	}

	/**
	 * Tests for nextAndPrevious.
	 */
	@Test
	public void testNextAndPrevious() {
		ChatCache cache = new ChatCache(null);
		assertFalse(cache.hasNext());
		cache.addlinetoCache("one");
		assertFalse(cache.hasNext());
		assertFalse(cache.hasPrevious());
		cache.addlinetoCache("two");
		assertFalse(cache.hasNext());
		assertTrue(cache.hasPrevious());

		assertThat(cache.previous(), is("two"));
		assertThat(cache.current(), is("two"));

		assertThat(cache.previous(), is("one"));
		assertThat(cache.current(), is("one"));

		assertThat(cache.next(), is("two"));
		assertThat(cache.current(), is("two"));

	}

	/**
	 * Tests for nextOnEmptyCache.
	 */
	@Test
	public void testNextOnEmptyCache() {

		ChatCache cache = new ChatCache(null);
		cache.addlinetoCache("one");
		assertFalse(cache.hasNext());
		try {
			cache.next();
		} catch (NoSuchElementException e) {
			assertThat(cache.current(), is("one"));

		}
	}

	/**
	 * Tests for previousOnEmptyCache.
	 */
	public void testPreviousOnEmptyCache() {

		ChatCache cache = new ChatCache(null);
		assertFalse(cache.hasPrevious());
		cache.addlinetoCache("one");
		try {
			cache.previous();
		} catch (NoSuchElementException e) {
			assertThat(cache.current(), is("one"));

		}
	}
}
