package games.stendhal.server.maps.wofol.house5;

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
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}
	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC ghost = new SpeakerNPC("Zak") {
			@Override
			protected void createPath() {
                                List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 4));
				nodes.add(new Node(10, 4));
				nodes.add(new Node(10, 9));
				nodes.add(new Node(8, 9));
				nodes.add(new Node(8, 7));
				nodes.add(new Node(6, 7));
				nodes.add(new Node(6, 5));
				nodes.add(new Node(3, 5));
				setPath(new FixedPath(nodes, true));
			}

			@Override
		    protected void createDialog() {
			    add(ConversationStates.IDLE,
			    	ConversationPhrases.GREETING_MESSAGES,
			    	null,
			    	ConversationStates.IDLE,
			    	null,
			    	new SpeakerNPC.ChatAction() {
			    		@Override
			    		public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
			    			if (!player.hasQuest("find_ghosts")) {
			    				player.setQuest("find_ghosts", "looking:said");
			    			}
			    			String npcQuestText = player.getQuest("find_ghosts");
			    			String[] npcDoneText = npcQuestText.split(":");
			    			List<String> list = Arrays.asList(npcDoneText[0].split(";"));
						    if (!list.contains(npc.getName())) {
							    player.setQuest("find_ghosts", npcDoneText[0] + ";"
													+ npc.getName() + ":"
													+ npcDoneText[1]);
							    npc.say("Remember my name ... " + npc.getName()
							    		+ " ... " + npc.getName() + " ...");
							    player.addXP(100);
							} else {
							    npc.say("Let the dead rest in peace");
							}
						}
					});
			}

		};

		ghost.setDescription("You see a ghostly figure of a man. You have no idea how he died.");
		ghost.setResistance(0);
		ghost.setEntityClass("man_000_npc");
		// he is a ghost so he is see through
		ghost.setVisibility(50);
		ghost.setPosition(3, 4);
		// he has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
