package games.stendhal.server.maps.semos.city;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RetireeNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosCityAreaDiogenes(zone);
	}

	private void buildSemosCityAreaDiogenes(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Diogenes") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(22, 42));
				nodes.add(new Path.Node(26, 42));
				nodes.add(new Path.Node(26, 44));
				nodes.add(new Path.Node(31, 44));
				nodes.add(new Path.Node(31, 42));
				nodes.add(new Path.Node(35, 42));
				nodes.add(new Path.Node(35, 28));
				nodes.add(new Path.Node(22, 28));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("Ha ha! Job? I retired decades ago! Ha ha!");
				addHelp("I can't help you, but you can help Stendhal; tell all your friends, and help out with development! Visit http://arianne.sourceforge.net and see how you can help!");
				addGoodbye();
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, String text, SpeakerNPC engine) {
						        if (Rand.rand(2) == 0) {
							        say("Ah, quests... just like the old days when I was young! I remember one quest that was about... Oh look, a bird! Hmm, what? Ah, quests... just like the old days when I was young!");
						        } else {
							        say("You know that Sato over there buys sheep? Well, rumour has it that there's a creature deep in the dungeons who also buys sheep... and it pays much better than Sato, too!");
						        }
					        }
				        });

				add(ConversationStates.ATTENDING, "cleanme!", null, ConversationStates.ATTENDING, "What?",
				        new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, String text, SpeakerNPC engine) {
						        if (AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "alter", false)) {
							        for (String quest : player.getQuests()) {
								        player.removeQuest(quest);
							        }
						        } else {
							        say("What? No; you clean me! Begin with my back, thanks.");
							        player.setHP(player.getHP() - 5);
							        player.notifyWorldAboutChanges();
						        }
					        }
				        });
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "beggarnpc");
		npc.set(24, 42);
		npc.initHP(100);
		zone.add(npc);

	}
}
