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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;


public class HasEarnedTotalMoneyCondition extends AbstractChatCondition {

	private static final int hashModifier = AbstractChatCondition.getNextUniqueHashModifier();
	private final int required;
	private List<String> npcs;


	/**
	 * Creates a condition that checks if player has earned a total
	 * amount of money from any NPCs.
	 *
	 * @param required
	 *     The minimum total amount required.
	 */
	public HasEarnedTotalMoneyCondition(final int required) {
		this.required = required;
	}

	/**
	 * Creates a condition that checks if player has earned a total
	 * amount of money from certain NPCs.
	 *
	 * @param required
	 *     The minimum total amount required.
	 * @param npcs
	 *     NPC buyer names.
	 */
	public HasEarnedTotalMoneyCondition(final int required, final String... npcs) {
		this.required = required;
		this.npcs = Arrays.asList(npcs);
	}

	/**
	 * Creates a condition that checks if player has earned a total
	 * amount of money from certain NPCs.
	 *
	 * @param required
	 *     The minimum total amount required.
	 * @param npcs
	 *     NPC buyer names.
	 */
	public HasEarnedTotalMoneyCondition(final int required, final List<String> npcs) {
		this.required = required;
		this.npcs = npcs;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		if (!player.hasMap("npc_sales")) {
			return false;
		}

		int total = 0;
		Set<String> npcnames;
		if (npcs != null) {
			npcnames = new HashSet<>();
			npcnames.addAll(npcs);
		} else {
			npcnames = player.getMap("npc_sales").keySet();
		}
		for (final String npcname: npcnames) {
			total += player.getCommerceTransactionAmount(npcname, true);
		}
		return total >= required;
	}

	@Override
	public String toString() {
		String st = getClass().getSimpleName() + ": " + required;
		if (npcs != null) {
			st += " " + String.join(",", npcs);
		}
		return st;
	}

	@Override
	public int hashCode() {
		return hashModifier * (npcs.hashCode() + required);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof HasEarnedTotalMoneyCondition)) {
			return false;
		}
		final HasEarnedTotalMoneyCondition other = (HasEarnedTotalMoneyCondition) obj;
		return npcs.equals(other.npcs) && required == other.required;
	}
}
