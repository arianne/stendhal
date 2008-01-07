package games.stendhal.server.actions.admin;

import games.stendhal.common.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

public class AlterAction extends AdministrationAction {
	private static final String _ATTR_HP = "hp";
	private static final String _SUB = "sub";
	private static final String _ADD = "add";
	private static final String _SET = "set";
	private static final String ATTR_TITLE = "title";
	private static final String ATTR_ADMINLEVEL = "adminlevel";
	private static final String _VALUE = "value";
	private static final String _MODE = "mode";
	private static final String _STAT = "stat";
	private static final String _TARGET = "target";
	private static final String _ALTER = "alter";

	public static void register() {
		CommandCenter.register(_ALTER, new AlterAction(), 900);
	}

	@Override
	public void perform(Player player, RPAction action) {
		if (action.has(_TARGET) && action.has(_STAT) && action.has(_MODE)
				&& action.has(_VALUE)) {
			Entity changed = getTarget(player, action);

			if (changed == null) {
				logger.debug("Entity not found");
				player.sendPrivateText("Entity not found");
				return;
			}

			String stat = action.get(_STAT);

			if (stat.equals("name") && (changed instanceof Player)) {
				logger.error("DENIED: Admin " + player.getName()
						+ " trying to change player " + action.get(_TARGET)
						+ "'s name");
				player.sendPrivateText("Sorry, name cannot be changed.");
				return;
			}

			if (stat.equals(ATTR_ADMINLEVEL)) {
				player.sendPrivateText("Use #/adminlevel #<playername> #[<newlevel>] to display or change adminlevel.");
				return;
			}

			if (stat.equals(ATTR_TITLE) && (changed instanceof Player)) {
				player.sendPrivateText("The title attribute may not be changed directly.");
				return;
			}

			RPClass clazz = changed.getRPClass();

			boolean isNumerical = false;

			Definition type = clazz.getDefinition(DefinitionClass.ATTRIBUTE, stat);
			if (type == null) {
				player.sendPrivateText("Attribute you are altering is not defined in RPClass("
						+ changed.getRPClass().getName() + ")");
				return;
			}

			if ((type.getType() == Type.BYTE) || (type.getType() == Type.SHORT)
					|| (type.getType() == Type.INT)) {
				isNumerical = true;
			}

			if (changed.getRPClass().hasDefinition(DefinitionClass.ATTRIBUTE, stat)) {
				String value = action.get(_VALUE);
				String mode = action.get(_MODE);

				if (mode.length()>0 && !mode.equalsIgnoreCase(_ADD) &&
						!mode.equalsIgnoreCase(_SUB) && !mode.equalsIgnoreCase(_SET)) {
					player.sendPrivateText("Please issue one of the modes 'add', 'sub' and 'set'.");
					return;
				}

				if (isNumerical) {
					int numberValue;

					try {
						numberValue = Integer.parseInt(value);
					} catch(NumberFormatException e) {
						player.sendPrivateText("Please issue a numeric value instead of '" + value + "'");
						return;
					}

					if (mode.equalsIgnoreCase(_ADD)) {
						numberValue = changed.getInt(stat) + numberValue;
					}

					if (mode.equalsIgnoreCase(_SUB)) {
						numberValue = changed.getInt(stat) - numberValue;
					}

					if (stat.equals(_ATTR_HP)
							&& (changed.getInt("base_hp") < numberValue)) {
						logger.error("DENIED: Admin " + player.getName()
								+ " trying to set player "
								+ Grammar.suffix_s(action.get(_TARGET))
								+ " HP over its Base HP");
						return;
					}

					if (stat.equals(_ATTR_HP) && numberValue == 0) {
						logger.error("DENIED: Admin " + player.getName()
								+ " trying to set player "
								+ Grammar.suffix_s(action.get(_TARGET))
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
					default:
							// we switch over an enum
							break;
					}

					StendhalRPRuleProcessor.get().addGameEvent(
							player.getName(), _ALTER, action.get(_TARGET),
							stat, Integer.toString(numberValue));
					changed.put(stat, numberValue);
				} else {
					// Can be only set if value is not a number
					if (mode.equalsIgnoreCase(_SET)) {
						StendhalRPRuleProcessor.get().addGameEvent(
								player.getName(), _ALTER, action.get(_TARGET),
								stat, action.get(_VALUE));
						changed.put(stat, action.get(_VALUE));
					}
				}

				changed.update();
				changed.notifyWorldAboutChanges();
			}
		}
	}

}
