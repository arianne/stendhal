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
package games.stendhal.server.entity.npc.action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Says the list of the NPC names for unstarted quests in a specified region in the form npc1, npc2, and npc3 all need your help.
 */
@Dev(category=Category.IGNORE, label="\"...\"")
public class SayNPCNamesForUnstartedQuestsAction implements ChatAction {

	private final List<String> regions;

	/**
	 * Creates a new SayNPCNamesForUnstartedQuestsAction.
	 *
	 * @param region region of NPC
	 */
	public SayNPCNamesForUnstartedQuestsAction(String region) {
		this.regions = Arrays.asList(region);
	}

	/**
	 * Creates a new SayNPCNamesForUnstartedQuestsAction.
	 *
	 * @param regions regions of NPC
	 */
	public SayNPCNamesForUnstartedQuestsAction(List<String> regions) {
		this.regions = new LinkedList<String>(regions);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		// to build up the message the npc will say
		StringBuilder sb = new StringBuilder();
		// capture the regions with no quests remaining so that we can list them all at the end
		List<String> finishedregions = new LinkedList<String>();

		for (String region: regions) {
			// to hold the list of npcs for each region
			List<String> npcs = SingletonRepository.getStendhalQuestSystem().getNPCNamesForUnstartedQuestsInRegionForLevel(player, region);
			String verb = "need";
	        if (npcs.size()==1) {
	        	verb = "needs";
	        }
			if (npcs.size()>0) {
	        	sb.append("In " + region + " ");
	        	sb.append(Grammar.enumerateCollectionWithHash(npcs));
	        	sb.append(" " + verb + " your help. ");
	        } else {
	        	finishedregions.add(region);
	        }
		}
		if (finishedregions.size() > 0) {
			sb.append("There's no one in " + Grammar.enumerateCollection(finishedregions) + " who'd have a task you can handle, or that you haven't helped already.");
		}
		raiser.say(sb.toString().trim());


	}

	@Override
	public String toString() {
		return "SayNPCNamesForUnstartedQuestsAction in region <" + regions.toString() +  ">";
	}

	@Override
	public int hashCode() {
		return 5393 * regions.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayNPCNamesForUnstartedQuestsAction)) {
			return false;
		}
		SayNPCNamesForUnstartedQuestsAction other = (SayNPCNamesForUnstartedQuestsAction) obj;
		return regions.equals(other.regions);
	}
}
