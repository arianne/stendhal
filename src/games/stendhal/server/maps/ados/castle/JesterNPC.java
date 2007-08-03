package games.stendhal.server.maps.ados.castle;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Jester NPC to inform entrants to the castle
 *
 * @author kymara
 */
public class JesterNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Huckle Rohn") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(8, 56));
				nodes.add(new Path.Node(8, 44));
				nodes.add(new Path.Node(20, 44));
				nodes.add(new Path.Node(20, 34));
				nodes.add(new Path.Node(10, 34));
				nodes.add(new Path.Node(10, 9));
				nodes.add(new Path.Node(20, 9));
				nodes.add(new Path.Node(20, 44));
				nodes.add(new Path.Node(8, 44));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hail!");
				addJob("I'm the court jester, I can't stop for long! It's just not in my job description to stand and chat.");
				addHelp("Shhh...I could tell you about these shady outlaws...they've taken over the castle while the King is away. I just keep quiet, me. Shhh...");
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.IDLE,
				        "Nothing for me! Must keep juggling! Goodbye!", null);
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.IDLE,
				        "Nothing for me! Must keep juggling! Goodbye!", null);
 				addGoodbye("Bye!");
			}
		};
		npc.setDescription("You see Huckle Rohn, the court jester.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "magic_jesternpc");
		npc.set(8, 56);
		npc.initHP(100);
		zone.add(npc);

	}
}
