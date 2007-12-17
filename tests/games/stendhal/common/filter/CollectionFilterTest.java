package games.stendhal.common.filter;

import java.util.Collection;
import java.util.List;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;

import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;
import marauroa.common.game.RPObject;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class CollectionFilterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public final void testFilter() {

		Collection<Player> list = StendhalRPRuleProcessor.get().getPlayers();
		MockStendhalRPRuleProcessor.get().addPlayer(PlayerTestHelper.createPlayer("nonAdmin1"));
		MockStendhalRPRuleProcessor.get().addPlayer(PlayerTestHelper.createPlayer("nonAdmin2"));
		MockStendhalRPRuleProcessor.get().addPlayer(PlayerTestHelper.createPlayer("nonAdmin3"));
		MockStendhalRPRuleProcessor.get().addPlayer(PlayerTestHelper.createPlayer("Admin1"));
		MockStendhalRPRuleProcessor.get().addPlayer(PlayerTestHelper.createPlayer("bob"));
		assertThat(list.size(), is(5));

		StendhalRPRuleProcessor.get().getPlayer("Admin1").setAdminLevel(1);
		StendhalRPRuleProcessor.get().getPlayer("bob").setAdminLevel(10);
		
		Adminfilter af = new Adminfilter();
		
		assertTrue(af.passes(StendhalRPRuleProcessor.get().getPlayer("nonAdmin1")));
		assertTrue(af.passes(StendhalRPRuleProcessor.get().getPlayer("nonAdmin2")));
		assertTrue(af.passes(StendhalRPRuleProcessor.get().getPlayer("nonAdmin3")));
		assertFalse(af.passes(StendhalRPRuleProcessor.get().getPlayer("Admin1")));
		assertFalse(af.passes(StendhalRPRuleProcessor.get().getPlayer("bob")));
		
		
		CollectionFilter<RPObject> cf1 = new CollectionFilter<RPObject>();
		
		
		cf1.addFilterCriteria(new Adminfilter());
		List<RPObject> result = (List<RPObject>) cf1.filterCopy(list);
		assertThat(result.size(), is(3));
		result.remove(1);
		assertThat(result.size(), is(2));
		assertThat(list.size(), is(5));
	
		
		CollectionFilter<RPObject> cf2 = new CollectionFilter<RPObject>();
		
		
		cf2.addFilterCriteria(new NoAdminfilter());
		 result = (List<RPObject>) cf2.filterCopy(list);
		assertThat(result.size(), is(2));
		result.remove(1);
		assertThat(result.size(), is(1));
		assertThat(list.size(), is(5));
		

	}

	class Adminfilter implements FilterCriteria {

		public boolean passes(Object o) {
			return ((Player) o).getAdminLevel() == 0;

		}

	}
	
	class NoAdminfilter implements FilterCriteria {

		public boolean passes(Object o) {
			return ((Player) o).getAdminLevel() > 0;

		}

	}

}
