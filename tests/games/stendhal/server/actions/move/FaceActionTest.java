package games.stendhal.server.actions.move;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.Direction;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class FaceActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private boolean stopCalled;
	private Direction directionSet;
	private boolean notifyCalled;

	/**
	 * Tests for onAction.
	 */
	@Test
	public void testOnAction() {
		final FaceAction fa = new FaceAction();
		final RPAction action = new RPAction();
		PlayerTestHelper.generatePlayerRPClasses();
		final Player player = new Player(new RPObject()) {
			@Override
			public void stop() {
				stopCalled = true;
			}

			@Override
			public void setDirection(final Direction dir) {
				directionSet = dir;
			}

			@Override
			public void notifyWorldAboutChanges() {
				notifyCalled = true;
			}
		};

		fa.onAction(player, action);
		assertFalse(stopCalled);
		assertFalse(notifyCalled);
		assertNull(directionSet);

		action.put("dir", 1);
		fa.onAction(player, action);
		assertTrue(stopCalled);
		assertTrue(notifyCalled);
		assertNotNull(directionSet);

	}

}
