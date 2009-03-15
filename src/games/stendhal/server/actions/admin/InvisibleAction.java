package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.INVISIBLE;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class InvisibleAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(INVISIBLE, new InvisibleAction(), 500);

	}

	@Override
	public void perform(final Player player, final RPAction action) {
		String state;
		if (player.isInvisibleToCreatures()) {
			player.setInvisible(false);
			
			state = "off";
		} else {
			player.setInvisible(true);
			state =  "on";
		}
		new GameEvent(player.getName(), INVISIBLE, state).raise();

	}

}
