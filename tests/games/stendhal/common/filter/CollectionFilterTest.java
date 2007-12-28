package games.stendhal.common.filter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import games.stendhal.server.entity.player.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class CollectionFilterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public final void testFilter() {
		Collection<Player> list = new LinkedList<Player>();

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

		CollectionFilter<Player> cf1 = new CollectionFilter<Player>();

		cf1.addFilterCriteria(new Adminfilter());
		List<? extends Player> result = (List<? extends Player>) cf1.filterCopy(list);
		assertThat(result.size(), is(3));
		result.remove(1);
		assertThat(result.size(), is(2));
		assertThat(list.size(), is(5));

		CollectionFilter<Player> cf2 = new CollectionFilter<Player>();

		cf2.addFilterCriteria(new NoAdminfilter());
		result = (List<? extends Player>) cf2.filterCopy(list);
		assertThat(result.size(), is(2));
		result.remove(1);
		assertThat(result.size(), is(1));
		assertThat(list.size(), is(5));

	}

	class Adminfilter implements FilterCriteria<Player> {

		public boolean passes(Player o) {
			return o.getAdminLevel() == 0;

		}

	}

	class NoAdminfilter implements FilterCriteria<Player> {

		public boolean passes(Player o) {
			return o.getAdminLevel() > 0;

		}

	}

}
