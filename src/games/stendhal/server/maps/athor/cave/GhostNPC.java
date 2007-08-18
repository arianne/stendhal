package games.stendhal.server.maps.athor.cave;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
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

	private NPCList npcs = NPCList.get();

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
				nodes.add(new Node(65, 73));
				nodes.add(new Node(43, 73));
				nodes.add(new Node(43, 68));
				nodes.add(new Node(37, 68));
				nodes.add(new Node(37, 64));
				nodes.add(new Node(34, 64));
				nodes.add(new Node(34, 57));
				nodes.add(new Node(31, 57));
				nodes.add(new Node(31, 51));
				nodes.add(new Node(28, 51));
				nodes.add(new Node(28, 45));
				nodes.add(new Node(26, 45));
				nodes.add(new Node(26, 40));
				nodes.add(new Node(25, 40));
				nodes.add(new Node(25, 37));
				nodes.add(new Node(36, 37));
				nodes.add(new Node(36, 42));
				nodes.add(new Node(45, 42));
				nodes.add(new Node(45, 45));
				nodes.add(new Node(61, 45));
				nodes.add(new Node(61, 53));
				nodes.add(new Node(63, 53));
				nodes.add(new Node(63, 57));
				nodes.add(new Node(64, 57));
				nodes.add(new Node(64, 65));
				nodes.add(new Node(65, 65));
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
			    		public void fire(Player player, String text,
			    				SpeakerNPC npc) {
			    			if (!player.hasQuest("find_ghosts")) {
			    				player.setQuest("find_ghosts", "looking:said");
			    			}
			    			String npcQuestText = player.getQuest("find_ghosts");
			    			String[] npcDoneText = npcQuestText.split(":");
			    			List<String> list = Arrays.asList(npcDoneText[0].split(";"));
						    if (!list.contains(npc.getName())) {
							    player.setQuest("find_ghosts", npcDoneText[0]
									    + ";" + npc.getName() + ":"
									    +  npcDoneText[1]);
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
		ghost.setObstacle(false);

		npcs.add(ghost);
		zone.assignRPObjectID(ghost);
		ghost.put("class", "woman_005_npc");
		// she is a ghost so she is see through
		ghost.setVisibility(50);
		ghost.set(65, 73);
		// she has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
