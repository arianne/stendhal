package games.stendhal.server.core.engine;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.entity.player.Player;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PlayerListTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testPlayerList() {
		PlayerList list = new PlayerList();
		assertNotNull(list.getPlayers());

	}

	@Test
	public void testGetOnlinePlayer() {
		PlayerList list = new PlayerList();
		assertNotNull(list.getPlayers());
		assertThat(list.size(), is(0));
		Player jack = PlayerTestHelper.createPlayer("jack");
		list.add(jack);
		assertThat(list.size(), is(1));
		assertSame(jack, list.getOnlinePlayer("jack"));
		// TODO: it should not be possible to add the same player (name = name )
		// twice;
		Player jack2 = PlayerTestHelper.createPlayer("jack");
		list.add(jack2);
		assertThat(list.size(), is(2));
		assertThat(jack, sameInstance(list.getOnlinePlayer("jack")));
		assertThat(jack2, not(sameInstance(list.getOnlinePlayer("jack"))));
		assertTrue(list.remove(jack));
		assertThat(jack2, sameInstance(list.getOnlinePlayer("jack")));
		assertTrue(list.remove(jack2));
		assertNull(list.getOnlinePlayer("jack"));
		assertFalse(list.remove(jack2));

	}

	@Test
	public void testAllPlayersModify() {
		Player jack = PlayerTestHelper.createPlayer("jack");
		Player bob = PlayerTestHelper.createPlayer("bob");
		Player ghost = PlayerTestHelper.createPlayer("ghost");
		ghost.setGhost(true);
		PlayerList list = new PlayerList();
		list.add(jack);
		list.add(bob);
		list.add(ghost);
		final String message = "tellall test";
		list.tellAllOnlinePlayers(message);
		assertEquals(message, jack.getPrivateText());
		assertEquals(message, bob.getPrivateText());
		assertEquals(message, ghost.getPrivateText());
		list.forAllPlayersExecute(new Task() {

			public void execute(Player player) {
				player.clearEvents();

			}

		});

		assertEquals(null, jack.getPrivateText());
		assertEquals(null, bob.getPrivateText());
		assertEquals(null, ghost.getPrivateText());

		list.forFilteredPlayersExecute(new Task() {
			public void execute(Player player) {
				player.sendPrivateText(message);

			}
		}, new FilterCriteria<Player>() {
			public boolean passes(Player o) {
				return o.isGhost();

			}
		});
		assertEquals(null, jack.getPrivateText());
		assertEquals(null, bob.getPrivateText());
		assertEquals(message, ghost.getPrivateText());
	}

	
	

	

}
