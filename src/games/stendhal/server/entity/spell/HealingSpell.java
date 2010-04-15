/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

/*
 * HealingSpell.java
 *
 * Created on March 29, 2007, 5:37 PM
 */

package games.stendhal.server.entity.spell;

import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * @author timothyb89 A healing spell. It restores the user to full HP (for
 *         now).
 */
public class HealingSpell extends Spell implements UseListener {
	
	public HealingSpell(String name, int amount, int atk, int cooldown,
			int def, double lifesteal, int mana, int minimumlevel, int range,
			int rate, int regen) {
		super(name, amount, atk, cooldown, def, lifesteal, mana, minimumlevel, range,
				rate, regen);
	}

	private int healAmount;

	@Override
	public String describe() {
		return "You see a healing spell.";
	}

	//
	// HealingSpell
	//

	/**
	 * Gets the amount the healing spell will heal you.
	 * @param player 
	 * @return amount of health healed
	 * 
	 * 
	 */
	public int getHealingAmount(final Player player) {
		return player.getBaseHP() - healAmount;
	}

	//
	// UseListener
	//

	public boolean onUsed(final RPEntity user) {
		final Player player = (Player) user;

		if (player.getMana() >= 25) {
			player.heal(getHealingAmount(player), true);

			// takes away the mana
			final int mana = player.getMana();
			final int newmana = mana - 25;

			// sets the new mana amount
			player.setMana(newmana);

			// now that everything has been set, notify the player.
			player.sendPrivateText("You have been healed. You now have #"
					+ player.getMana() + " mana left.");

			// saves changes (last because the stats are refreshed by default on
			// zone change)
			player.update();
			player.notifyWorldAboutChanges();
			return true;
		} else {
			player.sendPrivateText("You do not have enough mana to cast this spell.");
		}
		return false;
	}
}
