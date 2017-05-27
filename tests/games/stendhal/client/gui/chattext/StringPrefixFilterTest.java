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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringPrefixFilterTest {

	private Vector<String> names;

	@Before
	public void setUp() throws Exception {
		names = new Vector<String>();
		names.add("abel");
		names.add("unable");
		names.add("mam");
		names.add("mini");
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for filterCopy.
	 */
	@Test
	public void testFilterCopy() {
		StringPrefixFilter filter = new StringPrefixFilter("");

		Collection< ? extends String> filterednames = filter.filterCopy(names);
		assertTrue(filterednames.isEmpty());

		filter = new StringPrefixFilter("m");
		filterednames = filter.filterCopy(names);
		assertThat(filterednames.size(), is(2));

		filter = new StringPrefixFilter("ma");
		filterednames = filter.filterCopy(names);
		assertThat(filterednames.size(), is(1));

		filter = new StringPrefixFilter("a");
		filterednames = filter.filterCopy(names);
		assertThat(filterednames.size(), is(1));

	}

	/**
	 * Tests for filterCaseInsensitiveCopy.
	 */
	@Test
	public void testFilterCaseInsensitiveCopy() {
		StringPrefixFilter filter = new StringPrefixFilter("M");
		Collection< ? extends String> filterednames = filter.filterCopy(names);
		assertThat(filterednames.size(), is(2));
	}

}
