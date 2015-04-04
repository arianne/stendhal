package games.stendhal.server.actions.move;

import static games.stendhal.common.constants.Actions.WALK;
import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class WalkAction implements ActionListener {
	
	/**
	 * Registers walk action.
	 */
	public static void register() {
		CommandCenter.register(WALK, new WalkAction());
	}
	
	/**
	 * Begin walking.
	 */
	@Override
	public void onAction(Player player, RPAction action) {
		/* The speed at which the player will walk. */
		final double newSpeed;
		final Direction walkDirection = player.getDirection();
		
		if (player.stopped()) {
			/* Check if the player's direction is defined. */
			if ((walkDirection == Direction.STOP) || (walkDirection == null)) {
				/* Set default direction to DOWN. */
				player.setDirection(Direction.DOWN);
			}
			
			/* Begin walking using the entity's previous speed. */
			if (((Double)player.getPreviousSpeed() == null
					|| player.getPreviousSpeed() <= 0.0)) {
				/* Use base speed if previous speed has not been set or is set
				 * to 0.
				 */
				newSpeed = player.getBaseSpeed();
			} else {
				newSpeed = player.getPreviousSpeed();
			}
			player.setSpeed(newSpeed);
		} else {
			/* Use the same command to stop walking. */
			player.stop();
		}
	}
}
