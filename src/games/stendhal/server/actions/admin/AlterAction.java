package games.stendhal.server.actions.admin;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

public class AlterAction extends AdministrationAction {
	public static void register(){
		CommandCenter.register("alter", new AlterAction(), 900);
		
	}
	@Override
	public void perform(Player player, RPAction action) {

		if (action.has("target") && action.has("stat") && action.has("mode")
				&& action.has("value")) {
			Entity changed = getTarget(player, action);

			if (changed == null) {
				logger.debug("Entity not found");
				player.sendPrivateText("Entity not found");
				return;
			}

			String stat = action.get("stat");

			if (stat.equals("name") && (changed instanceof Player)) {
				logger.error("DENIED: Admin " + player.getName()
						+ " trying to change player " + action.get("target")
						+ "'s name");
				player.sendPrivateText("Sorry, name cannot be changed.");
				return;
			}

			if (stat.equals("adminlevel")) {
				player
						.sendPrivateText("Use #/adminlevel #<playername> #[<newlevel>] to display or change adminlevel.");
				return;
			}

			if (stat.equals("title") && (changed instanceof Player)) {
				player
						.sendPrivateText("The title attribute may not be changed directly.");
				return;
			}

			RPClass clazz = changed.getRPClass();

			boolean isNumerical = false;

			Definition type = clazz.getDefinition(DefinitionClass.ATTRIBUTE,
					stat);
			if (type == null) {
				player
						.sendPrivateText("Attribute you are altering is not defined in RPClass("
								+ changed.getRPClass().getName() + ")");
				return;
			}

			if ((type.getType() == Type.BYTE) || (type.getType() == Type.SHORT)
					|| (type.getType() == Type.INT)) {
				isNumerical = true;
			}

			if (changed.getRPClass().hasDefinition(DefinitionClass.ATTRIBUTE,
					stat)) {
				String value = action.get("value");
				String mode = action.get("mode");

				if (isNumerical) {
					int numberValue = Integer.parseInt(value);
					if (mode.equals("add")) {
						numberValue = changed.getInt(stat) + numberValue;
					}

					if (mode.equals("sub")) {
						numberValue = changed.getInt(stat) - numberValue;
					}

					if (stat.equals("hp")
							&& (changed.getInt("base_hp") < numberValue)) {
						logger.error("DENIED: Admin " + player.getName()
								+ " trying to set player "
								+ Grammar.suffix_s(action.get("target"))
								+ " HP over its Base HP");
						return;
					}

					if (stat.equals("hp") && numberValue == 0) {
						logger.error("DENIED: Admin " + player.getName()
								+ " trying to set player "
								+ Grammar.suffix_s(action.get("target"))
								+ " HP to 0, making it so unkillable.");
						return;
					}

					switch (type.getType()) {
					case BYTE:
						if ((numberValue > Byte.MAX_VALUE)
								|| (numberValue < Byte.MIN_VALUE)) {
							return;
						}
						break;
					case SHORT:
						if ((numberValue > Short.MAX_VALUE)
								|| (numberValue < Short.MIN_VALUE)) {
							return;
						}
						break;
					case INT:
						/*
						 * as numberValue is currently of type integer, this is
						 * pointless: if ((numberValue > Integer.MAX_VALUE) ||
						 * (numberValue < Integer.MIN_VALUE)) { return; }
						 */
						break;
					}

					StendhalRPRuleProcessor.get().addGameEvent(
							player.getName(), "alter", action.get("target"),
							stat, Integer.toString(numberValue));
					changed.put(stat, numberValue);
				} else {
					// Can be only setif value is not a number
					if (mode.equals("set")) {
						StendhalRPRuleProcessor.get()
								.addGameEvent(player.getName(), "alter",
										action.get("target"), stat,
										action.get("value"));
						changed.put(stat, action.get("value"));
					}
				}

				changed.update();
				changed.notifyWorldAboutChanges();
			}
		}
	}

}
