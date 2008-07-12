package games.stendhal.server.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

public class CommandCenterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();

	}

	@Test
	public void testRegister() {
		final ActionListener listener = new ActionListener() {
			public void onAction(final Player player, final RPAction action) {
				player.put("success", "true");
			};
		};
		final RPAction action = new RPAction();
		action.put("type", "action");
		final PrivateTextMockingTestPlayer caster = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		CommandCenter.register("action", listener);
		assertFalse(caster.has("success"));
		CommandCenter.execute(caster, action);
		assertTrue(caster.has("success"));
	}

	@Test
	public void testRegisterTwice() {
		CommandCenter.register("this", new ActionListener() {

			public void onAction(final Player player, final RPAction action) {

			}
		});

		CommandCenter.register("this", new ActionListener() {
			public void onAction(final Player player, final RPAction action) {

			}
		});
	}

	@Test
	public void testExecuteNullNull() {
		CommandCenter.execute(null, null);

	}

	@Test
	public void testExecuteUnknown() {
		final RPAction action = new RPAction();

		action.put("type", "");
		final PrivateTextMockingTestPlayer caster = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		CommandCenter.execute(caster, action);
		assertEquals("Unknown command ", caster.getPrivateTextString());
	}

}
