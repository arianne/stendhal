package games.stendhal.common.filter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import games.stendhal.server.entity.player.Player;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

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

	class Adminfilter implements FilterCriteria<Player> {

		public boolean passes(final Player o) {
			return o.getAdminLevel() == 0;
		}

	}

	class NoAdminfilter implements FilterCriteria<Player> {

		public boolean passes(final Player o) {
			return o.getAdminLevel() > 0;
		}

	}

}
