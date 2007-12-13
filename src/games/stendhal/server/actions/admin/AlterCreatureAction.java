package games.stendhal.server.actions.admin;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class AlterCreatureAction extends AdministrationAction {
private static final String _TEXT = "text";
private static final String _TARGET = "target";
private static final String _ALTERCREATURE = "altercreature";
public static void register(){
	CommandCenter.register(_ALTERCREATURE, new AlterCreatureAction(), 900);

}
	@Override
	public void perform(Player player, RPAction action) {
	
		if (action.has(_TARGET) && action.has(_TEXT)) {
			Entity changed = getTarget(player, action);

			if (changed == null) {
				logger.debug("Entity not found");
				player.sendPrivateText("Entity not found");
				return;
			}

			/*
			 * It will contain a string like: name/atk/def/hp/xp
			 */
			String stat = action.get(_TEXT);

			String[] parts = stat.split("/");

			if (changed instanceof Creature && parts.length == 5) {
				Creature creature = (Creature) changed;
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						"alter", action.get(_TARGET), stat);
	
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
