package games.stendhal.server.maps.ados.swamp;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Entrance to Deathmatch.
 */
public class DeathmatchRecruiterNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDeathmatchRecruiter(zone);
	}

	private void buildDeathmatchRecruiter(final StendhalRPZone zone) {

		final SpeakerNPC npc = new SpeakerNPC("Thonatus") {

			@Override
			protected void createPath() {
				final List<Node> path = new LinkedList<Node>();
				path.add(new Node(24, 9));
				path.add(new Node(38, 9));
				path.add(new Node(38, 15));
				path.add(new Node(35, 15));
				path.add(new Node(35, 21));
				path.add(new Node(47, 21));
				path.add(new Node(47, 10));
				path.add(new Node(43, 10));
				path.add(new Node(43, 6));
				path.add(new Node(55, 6));
				path.add(new Node(55, 16));
				path.add(new Node(62, 16));
				path.add(new Node(62, 14));
				path.add(new Node(64, 14));
				path.add(new Node(64, 15));
				path.add(new Node(70, 15));
				path.add(new Node(70, 18));
				path.add(new Node(80, 18));
				path.add(new Node(80, 9));
				path.add(new Node(69, 9));
				path.add(new Node(69, 14));
				path.add(new Node(56, 14));
				path.add(new Node(56, 8));
				path.add(new Node(47, 8));
				path.add(new Node(47, 21));
				path.add(new Node(35, 21));
				path.add(new Node(35, 15));
				path.add(new Node(24, 15));
				setPath(new FixedPath(path, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey there. You look like a reasonable fighter.");
				addJob("I'm recruiter for the Ados #deathmatch.");
				addHelp("I tell you about the Ados #deathmatch and send you there if you are strong enough.");
				addQuest("If you are brave, you can try the Ados #deathmatch.");
				addOffer("I'll tell you about the Ados #deathmatch.");
				add(ConversationStates.ATTENDING, "deathmatch", null, ConversationStates.ATTENDING,
				        "Many dangerous creatures will attack you in the deathmatch arena. It is only for strong #heroes.", null);
				// response to 'heroes' is defined in maps.quests.AdosDeathmatch 
				// because we need here to know about who is in the deathmatch. The teleport action is done there also.
				addGoodbye("I hope you will enjoy the Ados Deathmatch!");
			}
		};

		npc.setEntityClass("youngsoldiernpc");
		npc.setPosition(24, 9);
		npc.initHP(100);
		npc.setDescription("You see Thonatus, recruiter for the Ados Deathmatch. You are lucky that you find him, he is fast normally.");
		zone.add(npc);
	}
}
