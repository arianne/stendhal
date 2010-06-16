package games.stendhal.server.maps.semos.city;

import games.stendhal.common.Rand;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A crazy old man (original name: Diogenes) who walks around the city.
 */ 
public class RetireeNPC implements ZoneConfigurator {
	
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Diogenes") {
			
			@Override
			public void createDialog() {
				addGreeting();
				addJob("Ha ha! Job? I retired decades ago! Ha ha!");
				addHelp("I can't help you, but you can help Stendhal; tell all your friends, and help out with development! Visit http://stendhalgame.org and see how you can help!");
				addGoodbye();
				add(ConversationStates.ATTENDING,
						ConversationPhrases.QUEST_MESSAGES,
						null,
				        ConversationStates.ATTENDING,
				        null,
				        new ChatAction() {
					        public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						        if (Rand.throwCoin() == 1) {
							        npc.say("Ah, quests... just like the old days when I was young! I remember one quest that was about... Oh look, a bird! Hmm, what? Ah, quests... just like the old days when I was young!");
						        } else {
						        	npc.say("You know that Sato over there buys sheep? Well, rumour has it that there's a creature deep in the dungeons who also buys sheep... and it pays much better than Sato, too!");
						        }
					        }
				        });

				// A convenience function to make it easier for admins to test quests.
				add(ConversationStates.ATTENDING, "cleanme!", null, ConversationStates.ATTENDING, "What?",
				        new ChatAction() {
					        public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						        if (AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "alter", false)) {
							        for (final String quest : player.getQuests()) {
								        player.removeQuest(quest);
							        }
						        } else {
							        npc.say("What? No; you clean me! Begin with my back, thanks.");
							        player.damage(5, npc);
							        player.notifyWorldAboutChanges();
						        }
					        }
				        });
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(22, 43));
				nodes.add(new Node(26, 43));
				nodes.add(new Node(26, 45));
				nodes.add(new Node(31, 45));
				nodes.add(new Node(31, 43));
				nodes.add(new Node(35, 43));
				nodes.add(new Node(35, 29));
				nodes.add(new Node(22, 29));
				setPath(new FixedPath(nodes, true));
			}
			
		};
		npc.setPosition(24, 43);
		npc.setEntityClass("beggarnpc");
		zone.add(npc);
	}

}