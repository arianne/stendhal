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
 * Condition to check if a player has earned an amount of money by
 * selling to NPCs.
 */
public class HasEarnedMoneyCondition extends AbstractChatCondition {

	private final int hashModifier = AbstractChatCondition.getNextUniqueHashModifier();
	private final Map<String, Integer> amounts;


	/**
	 * Creates a condition that checks if player has earned an amount
	 * of money from any NPC.
	 *
	 * @param amount
	 *     The minimum amount required.
	 */
	public HasEarnedMoneyCondition(final int amount) {
		this.amounts = new HashMap<>();
		this.amounts.put("__any__", amount);
	}

	/**
	 * Creates a condition that checks if player has earned an amount
	 * of money from each NPC.
	 *
	 * @param amount
	 *     The minimum amount required.
	 * @param npcs
	 *     NPC buyer names.
	 */
	public HasEarnedMoneyCondition(final int amount, final String... npcs) {
		this.amounts = new HashMap<>();
		for (final String npcname: npcs) {
			this.amounts.put(npcname, amount);
		}
	}

	/**
	 * Creates a condition that checks if player has earned an amount
	 * of money from each NPC.
	 *
	 * @param amount
	 *     The minimum amount required.
	 * @param npcs
	 *     NPC buyer names.
	 */
	public HasEarnedMoneyCondition(final int amount, final List<String> npcs) {
		this.amounts = new HashMap<>();
		for (final String npcname: npcs) {
			this.amounts.put(npcname, amount);
		}
	}

	/**
	 * Creates a condition that checks if player has earned an amount
	 * of money from each NPC.
	 *
	 * @param amounts
	 *     NPC buyer names & amount required for each.
	 */
	public HasEarnedMoneyCondition(final Map<String, Integer> amounts) {
		this.amounts = amounts;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		if (!player.hasMap("npc_sales")) {
			return false;
		}

		if (amounts.containsKey("__any__")) {
			for (final String npcname: player.getMap("npc_sales").keySet()) {
				if (player.getCommerceTransactionAmount(npcname, true) >= amounts.get("__any__")) {
					return true;
				}
			}
			return false;
		}

		for (final Map.Entry<String, Integer> npc: amounts.entrySet()) {
			if (player.getCommerceTransactionAmount(npc.getKey(), true) < npc.getValue()) {
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
		if (!(obj instanceof HasEarnedMoneyCondition)) {
			return false;
		}
		final HasEarnedMoneyCondition other = (HasEarnedMoneyCondition) obj;
		return amounts.equals(other.amounts);
	}
}
