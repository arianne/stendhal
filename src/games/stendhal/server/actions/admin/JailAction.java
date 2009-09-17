package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.JAIL;
import static games.stendhal.common.constants.Actions.MINUTES;
import static games.stendhal.common.constants.Actions.TARGET;

import java.sql.SQLException;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

public class JailAction extends AdministrationAction {

	private static final String USAGE_JAIL_NAME_MINUTES_REASON = "Usage: /jail <name> <minutes> <reason>";

	public static void register() {
		CommandCenter.register(JAIL, new JailAction(), 400);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (action.has(TARGET) && action.has(MINUTES)) {
			final String target = action.get(TARGET);

			try {
				if (!DAORegister.get().get(CharacterDAO.class).hasCharacter(null, target)) {
					player.sendPrivateText("No character with that name: " + target);
					return;
				}
			} catch (SQLException e) {
				logger.error(e, e);
			}
			
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			try {
				final int minutes = action.getInt(MINUTES);
				SingletonRepository.getJail().imprison(target, player, minutes, reason);
				new GameEvent(player.getName(), JAIL, target, Integer.toString(minutes), reason).raise();
				
			} catch (final NumberFormatException e) {
				player.sendPrivateText(USAGE_JAIL_NAME_MINUTES_REASON);
			}
		} else {
			player.sendPrivateText(USAGE_JAIL_NAME_MINUTES_REASON);
		}

	}

}
