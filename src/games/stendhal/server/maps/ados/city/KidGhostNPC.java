/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.city;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Builds a Ghost NPC.
 *
 * @author kymara
 */
public class KidGhostNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}
	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC ghost = new SpeakerNPC("Ben") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(34, 121));
				nodes.add(new Node(23, 121));
				nodes.add(new Node(23, 112));
				nodes.add(new Node(17, 112));
				nodes.add(new Node(17, 124));
				nodes.add(new Node(5, 124));
				nodes.add(new Node(5, 111));
				nodes.add(new Node(17, 111));
				nodes.add(new Node(17, 112));
				nodes.add(new Node(35, 112));
				nodes.add(new Node(35, 119));
				nodes.add(new Node(23, 119));
				setPath(new FixedPath(nodes, true));
			}

			@Override
		    protected void createDialog() {
			    add(ConversationStates.IDLE,
			    	ConversationPhrases.GREETING_MESSAGES,
			    	new GreetingMatchesNameCondition(getName()), true,
			    	ConversationStates.IDLE,
			    	null,
			    	new ChatAction() {
			    		@Override
						public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			    			if (!player.hasQuest("find_ghosts")) {
			    				player.setQuest("find_ghosts", "looking:said");
			    			}
			    			final String npcQuestText = player.getQuest("find_ghosts");
			    			final String[] npcDoneText = npcQuestText.split(":");
			    			final String lookStr;
							if (npcDoneText.length > 1) {
								lookStr = npcDoneText[0];
							} else {
								lookStr = "";
							}
			    			final String saidStr;
							if (npcDoneText.length > 1) {
								saidStr = npcDoneText[1];
							} else {
								saidStr = "";
							}
			    			final List<String> list = Arrays.asList(lookStr.split(";"));
						    if (list.contains(npc.getName()) || player.isQuestCompleted("find_ghosts")) {
							    npc.say("Hello again. I'm glad you remember me. I'll just keep walking here till I have someone to play with.");
							} else {
							    player.setQuest("find_ghosts", lookStr
									    + ";" + npc.getName()
									    + ":" + saidStr);
							    npc.say("Hello! Hardly anyone speaks to me. The other children pretend I don't exist. I hope you remember me.");
							    player.addXP(100);
							    player.addKarma(10);
							}
						}
					});
			}
		};

		ghost.setDescription("You see a ghostly figure of a small boy.");
		ghost.setResistance(0);
		ghost.setEntityClass("kid7npc");
		// He is a ghost so he is see through
		ghost.setVisibility(50);
		ghost.setPosition(34, 121);
		// He has low HP
		ghost.initHP(30);
		ghost.setBaseHP(100);
		ghost.put("no_shadow", "");
		zone.add(ghost);
	}
}
