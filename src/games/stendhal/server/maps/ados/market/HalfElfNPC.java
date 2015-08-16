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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a npc in Ados (name: Aerianna) who is a half elf
 *
 * @author storyteller
 *
 */
public class HalfElfNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Aerianna") {

			@Override
			protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(55, 47));
                nodes.add(new Node(55, 35));
                nodes.add(new Node(73, 35));
                nodes.add(new Node(73, 23));
                nodes.add(new Node(76, 23));
                nodes.add(new Node(76, 11));
                nodes.add(new Node(71, 11));
                nodes.add(new Node(71, 7));
                nodes.add(new Node(57, 7));
                nodes.add(new Node(57, 23));
                nodes.add(new Node(28, 23));
                nodes.add(new Node(28, 28));
                nodes.add(new Node(21, 28));
                nodes.add(new Node(21, 44));
                nodes.add(new Node(31, 44));
                nodes.add(new Node(31, 47));
                setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings, #friend. I am happy to see you.");
				addHelp("You need help? I can really advise you that gaining #experience and #knowledge might be useful for you.");
				addJob("I am a half-elf, my #father was a #human adventurer and my #mother is an #elf from #Nalwor. As I want to understand both cultures I travel through #Faiumoni and learn as much as I can about #cultures and habits.");
				addOffer("I have nothing to give, except for my #time talking to you.");
				addGoodbye("Thank you for talking with me, friend!");

		        addReply("quest", "No, #friend, I have no quest for you. I just explore this market here in Ados City at the moment.");
		        addReply("friend", "I like to make friends, also with people from other #cultures. We can learn so much from each other!");
		        addReply("father", "My father was a very brave #human adventurer who dared to enter the Nalwor forest one day. He was clever and managed to reach #Nalwor City. There he met my #mother and immediately fell in love with her...");
		        addReply("mother", "My mother is an #elf living in #Nalwor City. She fell in love with my #father some time after he came to Nalwor many #years ago, so I was born as an half-elf.");
		        addReply("time", "Time is a precious thing, even for me. As an half-elf I live much longer than a #human, but not as long as an #elf.");
		        addReply("Faiumoni", "Faiumoni is such a huge continent! I have been to the forests in the south, climbed the mountains in the north and even spend some time in the great desert in the south east. There is just so much to explore!");
		        addReply("experience", "You gain experience while doing things, taking care for everything that surrounds you and with learning from others.");
		        addReply("knowledge", "Knowledge is important to understand the way everything works. I like to learn a lot about our world, though I know I will never understand every part of it.");
		        addReply("Nalwor", "Haven't you heard of Nalwor City? Well, probably not, it is hidden deep in the forest, but not too far from here... I was born there many #years ago...");
		        addReply("years", "You should know that I'm older than I look... Elves can reach a very high age, so as my #mother is an #elf I also don't age so fast. But I'll keep my age as a little secret from you! *hihi*");
		        addReply("cultures", "There are many different cultures in Faiumoni, which #differ from region and race but often are really #similar to each other.");
		        addReply("differ", "Elvish cultures are somehow different to most human cultures and differ completely to dwarvish culture. A #dwarf would never choose to live in the forest but will always prefer to live either on a mountain or deep in the ground!");
		        addReply("similar", "Did you know that some #orc tribes also celebrate a ritual similar to a human marriage? But usually that orcish marriage does not last longer than their kids are grown up and can hunt on their own. Another example for cultural similarity is that some human tribes use to travel around as nomads, like albino elves do! Sometimes cultures are so similar, just have a closer look at them!");
		        addReply("human", "Well, because I was born and raised in #Nalwor you probably know better about humans than me, even if I am a half-elf with a human father. But what I have learned about humans in general is that they are very clever with inventing things and solving problems. Thought some of them use to fight quite often, others really show sympathy for other humans and creatures.");
		        addReply("elf", "Elves are very proud to be beautiful, intelligent and magical. I don't say that because I am a half-elf! *hihi* They live seperated from other races in the forest or at other places where magic is strong. Elves get very old, even if you don't see that from their appearance. If you ever can, make friends with some elves as you might learn a lot from them!");
		        addReply("dwarf", "Dwarves are small, but very brave! They live in the ground or on mountains because they love to dig for precious materials. All dwarven cultures have many old traditions, such as the forming of clans. I visited a dwarven clan a while ago and after a week of mistrust the dwarves turned out to be very generous and friendly!");
		        addReply("orc", "Orcs live a quite simple life, which deals mainly about hunting and fighting. I was lucky to save an orc chief who tried to kill a green dragon alone but nearly got killed because of bad luck, so he invited me to visit his tribe. I learned a lot about orcish culture during the time I was there. They are not evil, even if many people think so.");
			}
		};

		npc.setDescription("You see Aerianna, a really beautiful young woman with elvish ears.");
		npc.setEntityClass("halfelfnpc");
		npc.setPosition(55, 47);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}

}
