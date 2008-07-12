package games.stendhal.server.actions.equip;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.item.Present;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

public class WrapActionTest {

	@BeforeClass
	public static void setUpBeforeclass() throws Exception {
		MockStendlRPWorld.get();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOnActionnotAtplayer() {
		final WrapAction wrap = new WrapAction();
		final PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		final RPAction action = new RPAction();
		action.put("type", "wrap");
		action.put("args", "");
		wrap.onAction(player, action);
		assertThat(player.getPrivateTextString(), is("You don't have any null"));
		player.resetPrivateTextString();

		action.put("args", "blabla");
		wrap.onAction(player, action);
		assertThat(player.getPrivateTextString(), is("You don't have any null blabla"));

		player.resetPrivateTextString();

		action.put("target", "what");
		action.put("args", "blabla");
		wrap.onAction(player, action);
		assertThat(player.getPrivateTextString(), is("You don't have any what blabla"));
	}

	@Test
	public void testOnActionPotion() {
		final WrapAction wrap = new WrapAction();
		final PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");

		PlayerTestHelper.equipWithItem(player, "potion");

		final RPAction action = new RPAction();
		action.put("type", "wrap");
		action.put("target", "potion");
		wrap.onAction(player, action);
		assertTrue(player.isEquipped("present"));
		final Present present = (Present) player.getFirstEquipped("present");
		assertNotNull(present);
		assertThat(present.getInfoString(), is("potion"));
		present.onUsed(player);
		assertTrue(player.isEquipped("potion"));

	}

	@Test
	public void testOnActionGreaterPotion() {
		
		final WrapAction wrap = new WrapAction();
		final PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");

		PlayerTestHelper.equipWithItem(player, "greater potion");

		final RPAction action = new RPAction();
		action.put("type", "wrap");
		action.put("target", "greater");
		action.put("args", "potion");
		wrap.onAction(player, action);
		assertTrue(player.isEquipped("present"));
		final Present present = (Present) player.getFirstEquipped("present");
		assertNotNull(present);
		assertThat(present.getInfoString(), is("greater potion"));
		present.onUsed(player);
		assertTrue(player.isEquipped("greater potion"));
		
		
	}

	@Test
	public void testOnActionMithrilshield() {
		final WrapAction wrap = new WrapAction();
		final PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");

		PlayerTestHelper.equipWithItem(player, "mithril shield");

		final RPAction action = new RPAction();
		action.put("type", "wrap");
		action.put("target", "mithril");
		action.put("args", "shield");
		wrap.onAction(player, action);
		assertTrue(player.isEquipped("present"));
		final Present present = (Present) player.getFirstEquipped("present");
		assertNotNull(present);
		assertThat(present.getInfoString(), is("mithril shield"));
		present.onUsed(player);
		assertTrue(player.isEquipped("mithril shield"));

	}

}
