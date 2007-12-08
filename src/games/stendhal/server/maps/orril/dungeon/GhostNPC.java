package games.stendhal.server.maps.orril.dungeon;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Ghost NPC
 *
 * @author kymara
 */
public class GhostNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC ghost = new SpeakerNPC("Goran") {
			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(216, 127));
				nodes.add(new Node(200, 127));
				nodes.add(new Node(200, 120));
				nodes.add(new Node(216, 120));
				nodes.add(new Node(216, 122));
				nodes.add(new Node(200, 122));
				nodes.add(new Node(200, 124));
				nodes.add(new Node(216, 124));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES, null,
						ConversationStates.IDLE, null,
						new SpeakerNPC.ChatAction() {
							@Override
							public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
								if (!player.hasQuest("find_ghosts")) {
									player.setQuest("find_ghosts",
											"looking:said");
								}
								String npcQuestText = player.getQuest("find_ghosts");
								String[] npcDoneText = npcQuestText.split(":");
				    			String lookStr = npcDoneText.length>1? npcDoneText[0]: "";
				    			String saidStr = npcDoneText.length>1? npcDoneText[1]: "";
								List<String> list = Arrays.asList(lookStr.split(";"));
								if (!list.contains(npc.getName())) {
									player.setQuest("find_ghosts",
											lookStr + ";"
													+ npc.getName() + ":"
													+ saidStr);
									npc.say("Remember my name ... "
											+ npc.getName() + " ... "
											+ npc.getName() + " ...");
									player.addXP(100);
								} else {
									npc.say("Let the dead rest in peace");
								}
							}
						});
			}

		};

		ghost.setDescription("You see a ghostly figure of a man. He appears to have died in battle.");
		ghost.setResistance(0);
		ghost.setEntityClass("deadmannpc");
		// he is a ghost so he is see through
		ghost.setVisibility(70);
		ghost.setPosition(216, 127);
		// he has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
