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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * says the list of the npc names for unstarted quests in a specified region in the form npc1, npc2, and npc3 all need your help.
 */
public class SayNPCNamesForUnstartedQuestsAction implements ChatAction {

	private final String region;

	/**
	 * Creates a new SayNPCNamesForUnstartedQuestsAction.
	 * 
	 * @param region region of NPC
	 */
	public SayNPCNamesForUnstartedQuestsAction(String region) {
		this.region = region;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		List<String> npcs = SingletonRepository.getStendhalQuestSystem().getNPCNamesForUnstartedQuestsInRegionForLevel(player, region);
        String verb = "need";
        if (npcs.size()==1) { 
        	verb = "needs";  
        }
		if (npcs.size()>0) {
        	raiser.say(Grammar.enumerateCollectionWithHash(npcs) + " " + verb + " your help.");
        } else {
        	raiser.say("You have already embarked upon all quests which you can handle, near " + region + ".");
        }
        	
	}

	@Override
	public String toString() {
		return "SayNPCNamesForUnstartedQuestsAction in region <" + region +  ">";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SayNPCNamesForUnstartedQuestsAction.class);
	}
}
