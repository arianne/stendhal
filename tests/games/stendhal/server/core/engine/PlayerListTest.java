package games.stendhal.server.core.engine;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.entity.player.Player;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

public class PlayerListTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testPlayerList() {
		PlayerList list = new PlayerList();
		list.size();	// just to avoid the "is never read" warning
	}

	@Test
	public void testGetOnlinePlayer() {
		PlayerList list = new PlayerList();
		assertThat(list.size(), is(0));
		PrivateTextMockingTestPlayer jack = PlayerTestHelper.createPrivateTextMockingTestPlayer("jack");
		list.add(jack);
		assertThat(list.size(), is(1));
		assertSame(jack, list.getOnlinePlayer("jack"));
		PrivateTextMockingTestPlayer jack2 = PlayerTestHelper.createPrivateTextMockingTestPlayer("jack");
		list.add(jack2);
		assertThat(list.size(), is(1));
		assertThat(jack2, sameInstance(list.getOnlinePlayer("jack")));
		assertThat(jack, not(sameInstance(list.getOnlinePlayer("jack"))));
		assertTrue(list.remove(jack));
		assertThat(list.size(), is(0));
	}

	@Test
	public void testAllPlayersModify() {
		PrivateTextMockingTestPlayer jack = PlayerTestHelper.createPrivateTextMockingTestPlayer("jack");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		PrivateTextMockingTestPlayer ghost = PlayerTestHelper.createPrivateTextMockingTestPlayer("ghost");
		ghost.setGhost(true);
		PlayerList list = new PlayerList();
		list.add(jack);
		list.add(bob);
		list.add(ghost);
		final String message = "tellall test";
		list.tellAllOnlinePlayers(message);
		assertEquals(message, jack.getPrivateTextString());
		assertEquals(message, bob.getPrivateTextString());
		assertEquals(message, ghost.getPrivateTextString());
		list.forAllPlayersExecute(new Task<Player>() {
			public void execute(Player player) {
				//TODO Is there a possibility to avoid this type cast?
				((PrivateTextMockingTestPlayer)player).resetPrivateTextString();
			}
		});

		assertEquals("", jack.getPrivateTextString());
		assertEquals("", bob.getPrivateTextString());
		assertEquals("", ghost.getPrivateTextString());

		list.forFilteredPlayersExecute(new Task<Player>() {
			public void execute(Player player) {
				player.sendPrivateText(message);
			}
		}, new FilterCriteria<Player>() {
			public boolean passes(Player o) {
				return o.isGhost();
			}
		});
		assertEquals("", jack.getPrivateTextString());
		assertEquals("", bob.getPrivateTextString());
		assertEquals(message, ghost.getPrivateTextString());
	}

	@Test
	public void testAllPlayersRemove() {
		PrivateTextMockingTestPlayer jack = PlayerTestHelper.createPrivateTextMockingTestPlayer("jack");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		PrivateTextMockingTestPlayer ghost = PlayerTestHelper.createPrivateTextMockingTestPlayer("ghost");
		ghost.setGhost(true);
		final PlayerList list = new PlayerList();
		list.add(jack);
		list.add(bob);
		list.add(ghost);
		final String message = "tellall test";
		list.tellAllOnlinePlayers(message);
		assertEquals(message, jack.getPrivateTextString());
		assertEquals(message, bob.getPrivateTextString());
		assertEquals(message, ghost.getPrivateTextString());
		list.forAllPlayersExecute(new Task<Player>() {

			public void execute(Player player) {
				list.remove(player);
			}

		});
		assertThat(list.size(), is(0));
	}

}
