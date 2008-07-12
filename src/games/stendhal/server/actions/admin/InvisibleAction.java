package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class InvisibleAction extends AdministrationAction {
	private static final String _INVISIBLE = "invisible";

	public static void register() {
		CommandCenter.register(_INVISIBLE, new InvisibleAction(), 500);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (player.isInvisibleToCreatures()) {
			player.setInvisible(false);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					_INVISIBLE, "off");
		} else {
			player.setInvisible(true);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					_INVISIBLE, "on");
		}
	}

}
