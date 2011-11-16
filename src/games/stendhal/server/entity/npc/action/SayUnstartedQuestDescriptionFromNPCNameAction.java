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

import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Gives description for unstarted quest based on npc name
 */
public class SayUnstartedQuestDescriptionFromNPCNameAction implements ChatAction {
	private final String region;

	/**
	 * Creates a new SayUnstartedQuestDescriptionFromNPCNameAction.
	 * 
	 * @param region region of NPC
	 */
	public SayUnstartedQuestDescriptionFromNPCNameAction(String region) {
		this.region = region;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		List<String> npcnames = SingletonRepository.getStendhalQuestSystem().getNPCNamesForUnstartedQuestsInRegionForLevel(player, region);
		for (String name :  npcnames) {
			if (name.equalsIgnoreCase(sentence.getTriggerExpression().toString())) {
				List<String> descs = SingletonRepository.getStendhalQuestSystem().getQuestDescriptionForUnstartedQuestInRegionFromNPCName(player,region,name);
				StringBuffer answer = new StringBuffer();
				for (String desc : descs) {
					answer.append(desc + " ");
				}
				raiser.say(answer.toString().trim());
			}
		} 	
	}

	@Override
	public String toString() {
		return "SayUnstartedQuestDescriptionFromNPCNameAction in region <" + region +  ">";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SayUnstartedQuestDescriptionFromNPCNameAction.class);
	}
}
