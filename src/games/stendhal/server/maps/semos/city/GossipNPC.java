/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;

/**
 * A guy (original name: Nomyr Ahba) who looks into the windows of the bakery
 * and the house next to it.
 *
 * Basically all he does is sending players to the retired adventurer at
 * the dungeon entrance.
 */
public class GossipNPC implements ZoneConfigurator {

    @Override
	public void configureZone(StendhalRPZone zone,
            Map<String, String> attributes) {
        buildNPC(zone);
    }

    private void buildNPC(StendhalRPZone zone) {
        final SpeakerNPC npc = new SpeakerNPC("Nomyr Ahba") {

            @Override
            public void createDialog() {
                addGreeting(null,new SayTextAction("Hi again, [name]. How can I #help you this time?"));

                // A little trick to make NPC remember if it has met
                // player before and react accordingly
                // NPC_name quest doesn't exist anywhere else neither is
                // used for any other purpose
                add(ConversationStates.IDLE,
                    ConversationPhrases.GREETING_MESSAGES,
                    new AndCondition(new GreetingMatchesNameCondition(getName()),
                    		new QuestNotCompletedCondition("Nomyr")),
                    ConversationStates.INFORMATION_1,
                    "Heh heh... Oh, hello stranger! You look a bit disoriented... d'you want to hear the latest gossip?",
                    new SetQuestAction("Nomyr", "done"));

                add(ConversationStates.ATTENDING,
                    ConversationPhrases.YES_MESSAGES,
                    null,
                    ConversationStates.INFORMATION_1,
                    "The young people have joined the Imperial Deniran Army to fight in the south, so the city has been left almost unprotected against the hordes of monsters coming from the dungeons. Can you help us?",
                    null);

                add(ConversationStates.ATTENDING,
                    ConversationPhrases.NO_MESSAGES,
                    null,
                    ConversationStates.IDLE,
                    "Huh. Well, you could help me by taking a peek through that other window, if you're not busy... I'm trying to figure out what's going on inside.",
                    null);

                add(ConversationStates.ATTENDING,
                    "sack",
                    null,
                    ConversationStates.ATTENDING,
                    "Ah. You're not really curious about the contents of my sack. You can ask Karl for your own empty one to fill it with whatever you like. Even sugar!",
                    null);

                add(ConversationStates.ATTENDING,
                    "karl",
                    null,
                    ConversationStates.ATTENDING,
                    "Oh. He's an elderly friendly farmer that lives in a farm a bit east from here. Follow the road to Ados and I'm sure you'll find him.",
                    null);

                add(ConversationStates.INFORMATION_1,
                    ConversationPhrases.YES_MESSAGES,
                    null,
                    ConversationStates.IDLE,
                    "First of all, you should go talk to Hayunn Naratha. He's a great old hero, and he's also pretty much our only defender here... I'm sure he will gladly give you some advice! Good luck.",
                    null);

                add(ConversationStates.INFORMATION_1,
                    ConversationPhrases.NO_MESSAGES,
                    null,
                    ConversationStates.IDLE,
                    "Awww... so you're a coward, then? Huh.",
                    null);

                addHelp("I'm a... Let's call me an \"observer\". I can tell you about all the latest rumours. Do you want to hear?");
                addJob("I know every rumour that exists in Semos, and I invented most of them! The one about Hackim smuggling in weapons for wandering adventurers like you is true, though.");
                addQuest("Thanks for asking, but I don't need anything right now... Gee! I just wish this #sack wasn't so cumbersome.");
                addOffer("Tz although I carry this #sack with me, it doesn't mean that I have anything for you...But I heard, that the old guy Monogenes over there needs some help...");
                addGoodbye();
            }


            @Override
            protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(46, 20));
                nodes.add(new Node(46, 21));
                nodes.add(new Node(50, 21));
                nodes.add(new Node(50, 20));
                nodes.add(new Node(46, 21));
                setPath(new FixedPath(nodes, true));
            }

        };
        npc.setPosition(46, 20);
        npc.setEntityClass("thiefnpc");
        zone.add(npc);
        npc.setDescription("This guy here, Nomyr Ahba, seems to be curious. His huge bag camouflages him.");
    }

}
