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

import static games.stendhal.common.constants.Actions.ALTERCREATURE;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;

import games.stendhal.common.MathHelper;
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
			 * It will contain a string like: name;atk;def;hp;xp
			 */
			final String stat = action.get(TEXT);

			final String[] parts = stat.split(";");
			if (!(changed instanceof Creature)) {
				logger.debug("Target " + changed.getTitle() + " was not a creature.");
				player.sendPrivateText("Target " + changed.getTitle() + " was not a creature.");
				return;
			}

			if (parts.length != 5) {
				logger.debug("Incorrect stats string for creature.");
				player.sendPrivateText("/altercreature <id> name;atk;def;hp;xp - Use a - as a placeholder to keep default value.");
				return;
			}


			final Creature creature = (Creature) changed;
			new GameEvent(player.getName(), "alter", action.get(TARGET), stat).raise();

			short newatk = parseShortError(parts[1], creature.getAtk(), "ATK", player);
			short newdef = parseShortError(parts[2], creature.getDef(), "DEF", player);
			short newHP = parseShortError(parts[3], creature.getBaseHP(), "HP", player);
			final int newXP = MathHelper.parseIntDefault(parts[4], creature.getXP());

			if(!"-".equals(parts[0])) {
				creature.setName(parts[0]);
			}
			creature.setAtk(newatk);
			creature.setDef(newdef);
			creature.initHP(newHP);
			creature.setXP(newXP);

			creature.update();
			creature.notifyWorldAboutChanges();
		}
	}

	/**
	 * A helper for parsing integer values used for stats that can actually only
	 * fit a short. Sends an error message to the player if needed.
	 *
	 * @param s string to be parsed
	 * @param def default value
	 * @param desc name of the stat
	 * @param player player to be messaged on error
	 * @return value of the string, or the default value on error
	 */
	private short parseShortError(String s, int def, String desc, Player player) {
		short val = (short) def;
		try {
			val = Short.parseShort(s);
		} catch (NumberFormatException e) {
			player.sendPrivateText("Invalid " + desc + " value '" + s + "'.");
		}
		return val;
	}
}
