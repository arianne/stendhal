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
package games.stendhal.common.filter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;

public class CollectionFilterTest {

	/**
	 * Tests for filter.
	 */
	@Test
	public final void testFilter() {
		final Collection<Player> list = new LinkedList<Player>();

		list.add(PlayerTestHelper.createPlayer("nonAdmin1"));
		list.add(PlayerTestHelper.createPlayer("nonAdmin2"));
		list.add(PlayerTestHelper.createPlayer("nonAdmin3"));
		Player player = PlayerTestHelper.createPlayer("Admin1");
		player.setAdminLevel(1);
		list.add(player);
		player = PlayerTestHelper.createPlayer("bob");
		player.setAdminLevel(10);
		list.add(player);
		assertThat(list.size(), is(5));

		final CollectionFilter<Player> cf1 = new CollectionFilter<Player>();

		cf1.addFilterCriteria(new Adminfilter());
		Collection<? extends Player> result = cf1.filterCopy(list);
		assertThat(result.size(), is(3));
		result.remove(result.iterator().next());
		assertThat(result.size(), is(2));
		assertThat(list.size(), is(5));

		final CollectionFilter<Player> cf2 = new CollectionFilter<Player>();

		cf2.addFilterCriteria(new NoAdminfilter());
		result = cf2.filterCopy(list);
		assertThat(result.size(), is(2));
		result.remove(result.iterator().next());
		assertThat(result.size(), is(1));
		assertThat(list.size(), is(5));
	}

	private static class Adminfilter implements FilterCriteria<Player> {

		@Override
		public boolean passes(final Player o) {
			return o.getAdminLevel() == 0;
		}

	}

	private static class NoAdminfilter implements FilterCriteria<Player> {

		@Override
		public boolean passes(final Player o) {
			return o.getAdminLevel() > 0;
		}

	}

}
