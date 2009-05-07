package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.ALTERCREATURE;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class AlterCreatureAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(ALTERCREATURE, new AlterCreatureAction(), 900);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (action.has(TARGET) && action.has(TEXT)) {
			final Entity changed = getTarget(player, action);

			if (changed == null) {
				logger.debug("Entity not found");
				player.sendPrivateText("Entity not found");
				return;
			}

			/*
			 * It will contain a string like: name/atk/def/hp/xp
			 */
			final String stat = action.get(TEXT);

			final String[] parts = stat.split("/");

			if ((changed instanceof Creature) && (parts.length == 5)) {
				final Creature creature = (Creature) changed;
				new GameEvent(player.getName(), "alter", action.get(TARGET), stat).raise();

				creature.setName(parts[0]);
				creature.setATK(Integer.parseInt(parts[1]));
				creature.setDEF(Integer.parseInt(parts[2]));
				creature.initHP(Integer.parseInt(parts[3]));
				creature.setXP(Integer.parseInt(parts[4]));

				creature.update();
				creature.notifyWorldAboutChanges();
			}
		}
	}

}
