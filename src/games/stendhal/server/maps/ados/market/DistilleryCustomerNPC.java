/***************************************************************************
 *                     (C) Copyright 2015 - Stendhal                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.market;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a npc in Ados (name: Hank) who is a customer of the distillery
 *
 * @author storyteller
 *
 */
public class DistilleryCustomerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Hank") {

			@Override
			protected void createPath() {
				// no path
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey there! How you're doin'?");
				addHelp("I don't think I can help you, I'm just having a little #drink here at #Uncle #Dag's pretty distillery. But maybe you would like to join me? We could #talk you about some interesting things, too.");
				addJob("I worked as a miner in the coal #mine north of #Semos #City. But then that digging stopped and I moved to Ados City with my #family, trying to get a new job here.");
				addOffer("I'd offer you a #drink but all my small change is already gone.");
				addGoodbye("Bye then! Always take care!");
				addReply(ConversationPhrases.QUEST_MESSAGES,
						"Currently nothing, thanks.");
				addReply("talk", "I don't know the latest #gossip, do you know some? Or maybe you are interested in my former #job?");
				addReply("gossip", "Always keep an ear on people's tellings and talk with everybody. You may learn interesting and useful things which could help you one day. Probably it's just good for having some conversation topics at least.");
				addReply(Arrays.asList("Uncle Dag", "Uncle", "Dag"),
						"Uncle Dag is the man! Cheers!");
				addReply("drink", "Fierywater... that's a strong one! Take care with that!");
		        addReply("family", "My lovely #wife and my #daughter. They really are my two angels!");
		        addReply("wife", "She is just an amazing person, I love her much! If I have got a new job soon, she will be more happy again, so tomorrow I try to get a job at the docks.");
		        addReply("daughter", "My daughter is happy we moved from #Semos #City to Ados. She is in that age where she likes to go out at the weekend. Here she has more friends and there's just some more action going on, you know.");
		        addReply(Arrays.asList("Semos City", "Semos", "City"),
		        		"It was a nice place to live, but nothing exciting happens there. Here in Ados it is different, it's a huge city.");
		        addReply("mine", "The Semos mine is quite old, there are places you better not go to. In the area near the entrance it's relatively safe but the #other #miners and I heard strange noises from the deeper tunnels... Many are blocked now, but nobody knows what is #haunting down there...");
		        addReply(Arrays.asList("other miners", "other", "miners"),
		        		"Oh, we were a lot of miners working in the mine, Barbarus, Pickins, Nathan... just to name a few. I don't know where all the others did go after leaving the mine, but I heard rumors about #Barbarus still collecting old tools and materials there to sell them.");
		        addReply("Barbarus", "He may still be in the mine today. If that is true, he probably stays in the area near the entrance, as he knows how dangerous it is in the deeper tunnels... Maybe if you are able to find him, he could draw you a map of the mine because he knows the area quite well. Just in case you get lost...");
		        addReply("haunting", "Some say there is a dragon living down in the mines, but others believe in some other strange appearances... I don't know what is true, but I'm not going to find it out!");
			}
		};

		npc.setDescription("You see Hank. He is just having a drink.");
		npc.setEntityClass("hanknpc");
		npc.setPosition(37, 31);
		npc.setDirection(Direction.LEFT);
		npc.initHP(100);
		zone.add(npc);
	}

}
