package games.stendhal.server.maps.athor.cave;

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
		SpeakerNPC ghost = new SpeakerNPC("Mary") {
			@Override
			protected void createPath() {
                                List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(65, 74));
				nodes.add(new Node(43, 74));
				nodes.add(new Node(43, 69));
				nodes.add(new Node(37, 69));
				nodes.add(new Node(37, 65));
				nodes.add(new Node(34, 65));
				nodes.add(new Node(34, 58));
				nodes.add(new Node(31, 58));
				nodes.add(new Node(31, 52));
				nodes.add(new Node(28, 52));
				nodes.add(new Node(28, 46));
				nodes.add(new Node(26, 46));
				nodes.add(new Node(26, 41));
				nodes.add(new Node(25, 41));
				nodes.add(new Node(25, 38));
				nodes.add(new Node(36, 38));
				nodes.add(new Node(36, 43));
				nodes.add(new Node(45, 43));
				nodes.add(new Node(45, 46));
				nodes.add(new Node(61, 46));
				nodes.add(new Node(61, 54));
				nodes.add(new Node(63, 54));
				nodes.add(new Node(63, 58));
				nodes.add(new Node(64, 58));
				nodes.add(new Node(64, 66));
				nodes.add(new Node(65, 66));
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
			    			String lookStr = npcDoneText.length>1? npcDoneText[0]: "";
			    			String saidStr = npcDoneText.length>1? npcDoneText[1]: "";
			    			List<String> list = Arrays.asList(lookStr.split(";"));
						    if (!list.contains(npc.getName())) {
							    player.setQuest("find_ghosts", lookStr
									    + ";" + npc.getName() + ":"
									    +  saidStr);
							    npc.say("Remember my name ... " + npc.getName()
							    		+ " ... " + npc.getName() + " ...");
							    player.addXP(100);
							} else {
							    npc.say("Please, let the dead rest in peace");
							}
						}
					});
			}
		};

		ghost.setDescription("You see a ghostly figure of a woman.");
		ghost.setResistance(0);
		ghost.setEntityClass("woman_005_npc");
		// she is a ghost so she is see through
		ghost.setVisibility(50);
		ghost.setPosition(65, 74);
		// she has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
