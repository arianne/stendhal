package games.stendhal.server.maps.ados.swamp;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Entrance to Deathmatch
 */
public class DeathmatchRecruiterNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildDeathmatchRecruiter(zone);
	}

	private void buildDeathmatchRecruiter(StendhalRPZone zone) {

		SpeakerNPC npc = new SpeakerNPC("Thonatus") {

			@Override
			protected void createPath() {
				List<Node> path = new LinkedList<Node>();
				path.add(new Node(40, 36));
				path.add(new Node(40, 85));
				path.add(new Node(53, 85));
				path.add(new Node(53, 81));
				path.add(new Node(84, 81));
				path.add(new Node(84, 57));
				path.add(new Node(89, 57));
				path.add(new Node(89, 38));
				path.add(new Node(72, 38));
				path.add(new Node(72, 33));
				path.add(new Node(50, 33));
				path.add(new Node(50, 36));
				setPath(new FixedPath(path, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey there. You look like a reasonable fighter.");
				addJob("I'm recruiter for the Ados #deathmatch.");
				addHelp("Have you ever heard of the Ados #deathmatch.");
				add(ConversationStates.ATTENDING, "deathmatch", null, ConversationStates.ATTENDING,
				        "The deathmatch is the ultimate challenge for true #heroes.", null);
				add(ConversationStates.ATTENDING, "heroes", null, ConversationStates.ATTENDING,
				        "Are you such a hero? I can take you to the #challenge.", null);
				addGoodbye("I hope you will enjoy the Ados #Deathmatch!");

				add(ConversationStates.ATTENDING, "challenge", 
					new LevelGreaterThanCondition(19), 
					ConversationStates.ATTENDING, null,
					new TeleportAction("0_ados_wall_n", 100, 86, Direction.DOWN));

				add(ConversationStates.ATTENDING, "challenge",
					new LevelLessThanCondition(20), 
					ConversationStates.ATTENDING, "Sorry, you are too weak!",
					null);
			}
		};

		npc.setEntityClass("youngsoldiernpc");
		npc.setPosition(40, 36);
		npc.initHP(100);
		zone.add(npc);
	}
}
