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
package games.stendhal.server.maps.semos.city;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.StoreMessageAction;
import games.stendhal.server.entity.player.Player;

/**
 * A crazy old man (original name: Diogenes) who walks around the city.
 */
public class RetireeNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Diogenes") {

			@Override
			public void createDialog() {
				addGreeting();
				addGoodbye();

				addJob(
                    "Ha ha! Job? I retired from my job as the #postman decades ago! Ha ha!"
                );

				addHelp(
                    "I can't help you, but you can help Stendhal!" + " " +
                    "Tell all your friends and help out with development!" + " " +
                    "Visit https://stendhalgame.org and see how you can help!"
                );

				addReply(
                    "postman",
                    "I used to deliver messages. But now there's a new kid doing it." + " " +
                    "Tell you what, I'll send him a message now, to give you.",
                        new StoreMessageAction("Diogenes",
                            "Hello it was nice chatting to you earlier in Semos." + " " +
                            "If you want to use postman to send messages to others who aren't here right now, just /msg postman")
                );

				addOffer(
                    "Well... Well..." + " " +
                    "I could still carry your letters around, but I'm retired and someone else got my job..." + " " +
                    "You can visit that guy, new #postman is in Semos plains in the north of here."
                );

				add(
                    ConversationStates.ATTENDING,
                    ConversationPhrases.QUEST_MESSAGES,
                    null,
                    ConversationStates.ATTENDING,
                    null,
                    new ChatAction() {
                        @Override
                        public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
                            if (Rand.throwCoin() == 1) {
                                npc.say("Ah, quests... just like the old days when I was young! I remember one quest that was about... Oh look, a bird! Hmm, what? Ah, quests... just like the old days when I was young!");
                            } else {
                                npc.say("You know that Sato over there buys sheep? Well, rumour has it that there's a creature deep in the dungeons who also buys sheep... and it pays much better than Sato, too!");
                            }
                        }
                    }
				);

				// A convenience function to make it easier for admins to test quests.
				add(
                    ConversationStates.ATTENDING,
                    "cleanme!",
                    null,
                    ConversationStates.IDLE,
                    "What?!",
                    new ChatAction() {
                        @Override
                        public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
                            if (AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "alter", false)) {
                                for (final String quest : player.getQuests()) {
                                    player.removeQuest(quest);
                                }
                            } else {
                                npc.say("What?! No... You clean me! Begin with my back, thanks.");
                                player.damage(5, npc.getEntity());
                                player.notifyWorldAboutChanges();
                            }
                        }
                    }
				);
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(22, 43));
				nodes.add(new Node(25, 43));
				nodes.add(new Node(25, 45));
				nodes.add(new Node(31, 45));
				nodes.add(new Node(31, 43));
				nodes.add(new Node(35, 43));
				nodes.add(new Node(35, 29));
				nodes.add(new Node(22, 29));
				setPath(new FixedPath(nodes, true));
			}

		};
		npc.setPosition(24, 43);
		npc.setCollisionAction(CollisionAction.STOP);
		npc.setEntityClass("beggarnpc");
		npc.setDescription("Diogenes is an elderly man, but sprightly for his age. He looks friendly and helpful.");
		npc.setSounds(Arrays.asList("laugh-old-man-01", "laugh-old-man-02"));
		zone.add(npc);
	}
}
