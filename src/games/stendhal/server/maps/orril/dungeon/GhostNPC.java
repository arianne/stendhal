package games.stendhal.server.maps.orril.dungeon;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
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
		SpeakerNPC ghost = new SpeakerNPC("Goran") {

		      	//  he has no collisions
			@Override
			public boolean isObstacle(Entity entity) {
			        return false;
			}
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(216, 126));
				nodes.add(new Path.Node(200, 126));
				nodes.add(new Path.Node(200, 119));
				nodes.add(new Path.Node(216, 119));
				nodes.add(new Path.Node(216, 121));
				nodes.add(new Path.Node(200, 121));
				nodes.add(new Path.Node(200, 123));
				nodes.add(new Path.Node(216, 123));
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
		ghost.setDescription("You see a ghostly figure of a man. He appears to have died in battle.");
		npcs.add(ghost);
		zone.assignRPObjectID(ghost);
		ghost.put("class", "deadmannpc");
		// he is a ghost so he is see through
		ghost.put("visibility",70);
		ghost.set(216, 126);
		// he has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
