/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;


/**
 * Condition to check if a player has spent an amount of money by
 * buying from NPCs.
 */
public class HasSpentMoneyCondition extends AbstractChatCondition {

	private final int hashModifier = AbstractChatCondition.getNextUniqueHashModifier();
	private final Map<String, Integer> amounts;


	/**
	 * Creates a condition that checks if player has spent an amount
	 * of money with any NPC.
	 *
	 * @param amount
	 *     The minimum amount required.
	 */
	public HasSpentMoneyCondition(final int amount) {
		this.amounts = new HashMap<>();
		this.amounts.put("__any__", amount);
	}

	/**
	 * Creates a condition that checks if player has spent an amount
	 * of money with each NPC.
	 *
	 * @param amount
	 *     The minimum amount required.
	 * @param npcs
	 *     NPC seller names.
	 */
	public HasSpentMoneyCondition(final int amount, final String... npcs) {
		this.amounts = new HashMap<>();
		for (final String npcname: npcs) {
			this.amounts.put(npcname, amount);
		}
	}

	/**
	 * Creates a condition that checks if player has spent an amount
	 * of money with each NPC.
	 *
	 * @param amount
	 *     The minimum amount required.
	 * @param npcs
	 *     NPC seller names.
	 */
	public HasSpentMoneyCondition(final int amount, final List<String> npcs) {
		this.amounts = new HashMap<>();
		for (final String npcname: npcs) {
			this.amounts.put(npcname, amount);
		}
	}

	/**
	 * Creates a condition that checks if player has spent an amount
	 * of money with each NPC.
	 *
	 * @param amounts
	 *     NPC seller names & amount required for each.
	 */
	public HasSpentMoneyCondition(final Map<String, Integer> amounts) {
		this.amounts = amounts;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		if (!player.hasMap("npc_purchases")) {
			return false;
		}

		if (amounts.containsKey("__any__")) {
			for (final String npcname: player.getMap("npc_purchases").keySet()) {
				if (player.getCommerceTransactionAmount(npcname, false) >= amounts.get("__any__")) {
					return true;
				}
			}
			return false;
		}

		for (final Map.Entry<String, Integer> npc: amounts.entrySet()) {
			if (player.getCommerceTransactionAmount(npc.getKey(), false) < npc.getValue()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String st = getClass().getSimpleName() + ": ";
		if (amounts.containsKey("__any__")) {
			st += amounts.get("__any__");
		} else {
			String tmp = "";
			for (final Map.Entry<String, Integer> npc: amounts.entrySet()) {
				if (tmp.length() > 0) {
					tmp += ",";
				}
				tmp += npc.getKey() + "=" + npc.getValue();
			}
			st += tmp;
		}
		return st;
	}

	@Override
	public int hashCode() {
		return hashModifier * amounts.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof HasSpentMoneyCondition)) {
			return false;
		}
		final HasSpentMoneyCondition other = (HasSpentMoneyCondition) obj;
		return amounts.equals(other.amounts);
	}
}
