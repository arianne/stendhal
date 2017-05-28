/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Gives description for unstarted quest based on npc name
 */
@Dev(category=Category.IGNORE, label="\"...\"")
public class SayUnstartedQuestDescriptionFromNPCNameAction implements ChatAction {

	private final List<String> regions;

	/**
	 * Creates a new SayUnstartedQuestDescriptionFromNPCNameAction.
	 *
	 * @param region region of NPC
	 */
	public SayUnstartedQuestDescriptionFromNPCNameAction(String region) {
		this.regions = Arrays.asList(region);
	}

	/**
	 * Creates a new SayUnstartedQuestDescriptionFromNPCNameAction.
	 *
	 * @param regions regions of NPC
	 */
	public SayUnstartedQuestDescriptionFromNPCNameAction(List<String> regions) {
		this.regions = regions;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		for (String region: regions) {
			List<String> npcnames = SingletonRepository.getStendhalQuestSystem().getNPCNamesForUnstartedQuestsInRegionForLevel(player, region);
			for (String name :  npcnames) {
				if (name.equalsIgnoreCase(sentence.getTriggerExpression().toString())) {
					List<String> descs = SingletonRepository.getStendhalQuestSystem().getQuestDescriptionForUnstartedQuestInRegionFromNPCName(player,region,name);
					StringBuilder answer = new StringBuilder();
					for (String desc : descs) {
						answer.append(desc + " ");
					}
					raiser.say(answer.toString().trim());
				}
			}
		}
	}

	@Override
	public String toString() {
		return "SayUnstartedQuestDescriptionFromNPCNameAction in region <" + regions.toString() +  ">";
	}

	@Override
	public int hashCode() {
		return 5443 * regions.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SayUnstartedQuestDescriptionFromNPCNameAction)) {
			return false;
		}
		SayUnstartedQuestDescriptionFromNPCNameAction other = (SayUnstartedQuestDescriptionFromNPCNameAction) obj;
		return regions.equals(other.regions);
	}
}
