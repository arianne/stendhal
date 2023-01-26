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


public class HasSpentTotalMoneyCondition extends AbstractChatCondition {

	private final int hashModifier = AbstractChatCondition.getNextUniqueHashModifier();
	private final int required;
	private List<String> npcs;


	/**
	 * Creates a condition that checks if player has spent a total
	 * amount of money with any NPCs.
	 *
	 * @param required
	 *     The minimum total amount required.
	 */
	public HasSpentTotalMoneyCondition(final int required) {
		this.required = required;
	}

	/**
	 * Creates a condition that checks if player has spent a total
	 * amount of money with certain NPCs.
	 *
	 * @param required
	 *     The minimum total amount required.
	 * @param npcs
	 *     NPC seller names.
	 */
	public HasSpentTotalMoneyCondition(final int required, final String... npcs) {
		this.required = required;
		this.npcs = Arrays.asList(npcs);
	}

	/**
	 * Creates a condition that checks if player has spent a total
	 * amount of money with certain NPCs.
	 *
	 * @param required
	 *     The minimum total amount required.
	 * @param npcs
	 *     NPC seller names.
	 */
	public HasSpentTotalMoneyCondition(final int required, final List<String> npcs) {
		this.required = required;
		this.npcs = npcs;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		if (!player.hasMap("npc_purchases")) {
			return false;
		}

		int total = 0;
		Set<String> npcnames;
		if (npcs != null) {
			npcnames = new HashSet<>();
			npcnames.addAll(npcs);
		} else {
			npcnames = player.getMap("npc_purchases").keySet();
		}
		for (final String npcname: npcnames) {
			total += player.getCommerceTransactionAmount(npcname, false);
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
		if (!(obj instanceof HasSpentTotalMoneyCondition)) {
			return false;
		}
		final HasSpentTotalMoneyCondition other = (HasSpentTotalMoneyCondition) obj;
		return npcs.equals(other.npcs) && required == other.required;
	}
}
