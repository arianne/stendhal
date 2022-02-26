/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.Map;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;


/**
 * A ring that protects from XP loss.
 */
public class RingOfLife extends Item {
	public RingOfLife(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item copied item
	 */
	public RingOfLife(final RingOfLife item) {
		super(item);
	}

	/**
	 * Create a RingOfLife.
	 */
	public RingOfLife() {
		super("emerald ring", "ring", "emerald-ring", null);
		put("amount", 1);
	}

	/**
	 * Check if the ring is broken.
	 *
	 * @return <code>true</code> if the ring is broken, <code>true</code> if
	 *	it's intact
	 */
	public boolean isBroken() {
		return  getInt("amount") == 0;
	}

	/**
	 * Use up the ring's power.
	 */
	public void damage() {
		put("amount", 0);
	}

	@Override
	public void repair() {
		put("amount", 1);
	}

	/**
	 * Gets the description.
	 *
	 * The description of RingOfLife depends on the ring's state.
	 *
	 * @return The description text.
	 */
	@Override
	public String describe() {
		String text;
		if (isBroken()) {
			text = "You see an §'emerald ring', known as the ring of life. The gleam is lost from the stone and it has no powers.";
		} else {
			text = "You see an §'emerald ring', known as the ring of life. Wear it, and you risk less from death.";
		}

		if (isBound()) {
			text = text + " It is a special quest reward for " + getBoundTo()
					+ ", and cannot be used by others.";
		}
		return text;
	}

	/**
	 * Notify the player that it is not required to carry this ring
	 * in the finger slot to get its benefits.
	 */
	@Override
	public boolean onEquipped(final RPEntity entity, final String slot) {
		if (slot.equals("finger") && entity instanceof Player) {
			((Player) entity).sendPrivateText(
				"The aura of the ring is unchanged as you slide it on your finger."
				+ " You realize that even carrying it in your bag or on your keyring"
				+ " you will be under its influence.");
		}

		return super.onEquipped(entity, slot);
	}
}
