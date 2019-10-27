/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.ADD;
import static games.stendhal.common.constants.Actions.ADMINLEVEL;
import static games.stendhal.common.constants.Actions.ALTER;
import static games.stendhal.common.constants.Actions.ATTR_HP;
import static games.stendhal.common.constants.Actions.MODE;
import static games.stendhal.common.constants.Actions.SET;
import static games.stendhal.common.constants.Actions.STAT;
import static games.stendhal.common.constants.Actions.SUB;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TITLE;
import static games.stendhal.common.constants.Actions.UNSET;
import static games.stendhal.common.constants.Actions.VALUE;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;

public class AlterAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(ALTER, new AlterAction(), 900);
	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (hasNeededAttributes(action)) {
			final Entity changed = getTargetAnyZone(player, action);

			if (changed == null) {
				logger.debug("Entity not found");
				player.sendPrivateText("Entity not found");
				return;
			}

			final String stat = action.get(STAT);

			if ("name".equals(stat) && (changed instanceof Player)) {
				logger.error("DENIED: Admin " + player.getName() + " trying to change player " + action.get(TARGET)
						+ "'s name");
				player.sendPrivateText("Sorry, name cannot be changed.");
				return;
			}

			if (ADMINLEVEL.equals(stat)) {
				player.sendPrivateText("Use #/adminlevel #<playername> #[<newlevel>] to display or change adminlevel.");
				return;
			}

			if (TITLE.equals(stat) && (changed instanceof Player)) {
				player.sendPrivateText("The title attribute may not be changed directly.");
				return;
			}

			final RPClass clazz = changed.getRPClass();


			final Definition type = clazz.getDefinition(DefinitionClass.ATTRIBUTE, stat);
			if (type == null) {
				player.sendPrivateText("Attribute you are altering is not defined in RPClass("
						+ changed.getRPClass().getName() + ")");
				return;
			} else {
				final String value = action.get(VALUE);
				final String mode = action.get(MODE);

				if ((mode.length() > 0) && !mode.equalsIgnoreCase(ADD)
						&& !mode.equalsIgnoreCase(SUB) && !mode.equalsIgnoreCase(SET) && !mode.equalsIgnoreCase(UNSET)) {
					player.sendPrivateText("Please issue one of the modes 'add', 'sub', 'set' or 'unset'.");
					return;
				}

				if (stat.equals("features") && changed instanceof Player) {
					if (!mode.equalsIgnoreCase(ADD) && !mode.equalsIgnoreCase(SUB)) {
						player.sendPrivateText("Please issue mode 'add' or 'sub'.");
						return;
					}

					if (mode.equalsIgnoreCase(ADD)) {
						String f_key;
						String f_value;

						if (value.contains(" ")) {
							f_key = value.split(" ")[0];
							f_value = value.replace(f_key, "").trim();
						} else {
							f_key = value;
							f_value = "";
						}

						((Player) changed).setFeature(f_key, f_value);
					} else if (mode.equalsIgnoreCase(SUB)) {
						((Player) changed).unsetFeature(value);
					}
				} else if (isParsableByInteger(type)) {
					int numberValue;

					try {
						numberValue = Integer.parseInt(value);
					} catch (final NumberFormatException e) {
						player.sendPrivateText("Please issue a numeric value instead of '" + value + "'");
						return;
					}

					if (mode.equalsIgnoreCase(ADD)) {
						numberValue = changed.getInt(stat) + numberValue;
					}

					if (mode.equalsIgnoreCase(SUB)) {
						numberValue = changed.getInt(stat) - numberValue;
					}

					if (ATTR_HP.equals(stat) && (changed.getInt("base_hp") < numberValue)) {
						logger.info("Admin " + player.getName() + " trying to set entity "
								+ Grammar.suffix_s(action.get(TARGET)) + " HP over its Base HP, "
								+ "we instead restored entity " + action.get(TARGET) + " to full health.");
						numberValue = changed.getInt("base_hp");
					}

					if (ATTR_HP.equals(stat) && (numberValue <= 0)) {
						logger.error("DENIED: Admin " + player.getName() + " trying to set entity "
								+ Grammar.suffix_s(action.get(TARGET)) + " HP to 0, making it so unkillable.");
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

					new GameEvent(player.getName(), ALTER, action.get(TARGET), stat, Integer.toString(numberValue)).raise();
					changed.put(stat, numberValue);
				} else {
					// If value is not a number, only SET and UNSET can be used
					if (mode.equalsIgnoreCase(SET)) {
						new GameEvent(player.getName(), ALTER, action.get(TARGET), stat, action.get(VALUE)).raise();
						changed.put(stat, action.get(VALUE));

					} else if (mode.equalsIgnoreCase(UNSET)) {
						if (type.getType() != Type.FLAG) {
							player.sendPrivateText("Attribute to be unset is not of type 'flag'.");
							return;
						}
						new GameEvent(player.getName(), ALTER, action.get(TARGET), stat, "unset").raise();
						changed.remove(stat);
					}
				}

				changed.update();
				changed.notifyWorldAboutChanges();
			}
		}
	}

	protected boolean isParsableByInteger(final Definition type) {
		if ((type.getType() == Type.BYTE) || (type.getType() == Type.SHORT)
				|| (type.getType() == Type.INT)) {
			return true;
		}
		return false;
	}

	protected boolean hasNeededAttributes(final RPAction action) {
		return action.has(TARGET) && action.has(STAT) && action.has(MODE)
				&& action.has(VALUE);
	}

}
