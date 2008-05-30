package games.stendhal.server.maps.ados.city;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Ghost NPC.
 *
 * @author kymara
 */
public class KidGhostNPC implements ZoneConfigurator {
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
		SpeakerNPC ghost = new SpeakerNPC("Ben") {
			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(34, 121));
				nodes.add(new Node(24, 121));
				nodes.add(new Node(24, 112));
				nodes.add(new Node(13, 112));
				nodes.add(new Node(13, 121));
				nodes.add(new Node(6, 121));
				nodes.add(new Node(6, 112));
				nodes.add(new Node(13, 112));
				nodes.add(new Node(13, 121));
				nodes.add(new Node(24, 121));
				nodes.add(new Node(24, 112));
				nodes.add(new Node(34, 112));
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
			    			String lookStr = npcDoneText.length > 1 ? npcDoneText[0] : "";
			    			String saidStr = npcDoneText.length > 1 ? npcDoneText[1] : "";
			    			List<String> list = Arrays.asList(lookStr.split(";"));
						    if (list.contains(npc.getName()) || player.isQuestCompleted("find_ghosts")) {
							    npc.say("Hello again. I'm glad you remember me. I'll just keep walking here till I have someone to play with.");
							} else {
							    player.setQuest("find_ghosts", lookStr
									    + ";" + npc.getName()
									    + ":" + saidStr);
							    npc.say("Hello! Hardly anyone speaks to me. The other children pretend I don't exist. I hope you remember me.");
							    player.addXP(100);
							    player.addKarma(10);
							}
						}
					});
			}

		};
		ghost.setDescription("You see a ghostly figure of a small boy.");
		ghost.setResistance(0);
		ghost.setEntityClass("kid7npc");
		// He is a ghost so he is see through
		ghost.setVisibility(50);
		ghost.setPosition(34, 121);
		// He has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
