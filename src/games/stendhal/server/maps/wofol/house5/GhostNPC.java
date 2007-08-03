package games.stendhal.server.maps.wofol.house5;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

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
		SpeakerNPC ghost = new SpeakerNPC("Zak") {
			@Override
			protected void createPath() {
                                List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(3, 3));
				nodes.add(new Path.Node(10, 3));
				nodes.add(new Path.Node(10, 8));
				nodes.add(new Path.Node(8, 8));
				nodes.add(new Path.Node(8, 6));
				nodes.add(new Path.Node(6, 6));
				nodes.add(new Path.Node(6, 4));
				nodes.add(new Path.Node(3, 4));
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
									    + ";" + npc.getName() + ":" +
									    npcDoneText[1]);
							    npc.say("Remember my name ... " + npc.getName() +
							            " ... " + npc.getName() + " ...");
							    player.addXP(100);
							}
							else {
							    npc.say("Let the dead rest in peace");
							}
						}
					});
			}

		};
		ghost.setDescription("You see a ghostly figure of a man. You have no idea how he died.");
	//	ghost.setObstacle(false);

		npcs.add(ghost);
		zone.assignRPObjectID(ghost);
		ghost.put("class", "man_000_npc");
		// he is a ghost so he is see through
		ghost.setVisibility(50);
		ghost.set(3, 3);
		// he has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
