package games.stendhal.server.maps.orril.dwarfmine;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configure Orril Dwarf Blacksmith (Underground/Level -3).
 * 
 * @author kymara
 */
public class BlacksmithNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildBlacksmith(zone);
	}

	private void buildBlacksmith(StendhalRPZone zone) {
		SpeakerNPC hogart = new SpeakerNPC("Hogart") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(20, 11));
				nodes.add(new Node(12, 11));
				nodes.add(new Node(12, 7));
				nodes.add(new Node(12, 10));
				nodes.add(new Node(20, 10));
				nodes.add(new Node(20, 8));
				nodes.add(new Node(20, 11));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am a master blacksmith. I used to forge weapons in secret for the dwarves in the mine, but they have forgotten me and my #stories.");
				addHelp("I could tell you a #story...");
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("story", "stories"),
				        ConversationStates.ATTENDING,
				        "I expect a scruff like you has never heard of Lady Tembells, huh? She was so beautiful. She died young and her distraught husband asked a powerful Lord to bring her back to life. The fool didn't get what he bargained for, she became a #vampire.",
				        null);
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("vampire"),
				        ConversationStates.ATTENDING,
				        "The husband had hired the help of a Vampire Lord! The Lady became his Vampire Bride and her maids became vampiresses. The Catacombs of North Semos are a deadly place now.",
				        null);
				addGoodbye("So long. I bet you won't sleep so well tonight.");
			} //remaining behaviour defined in maps.quests.VampireSword
		};

		hogart.setDescription("You see Hogart, a retired master dwarf smith.");
		hogart.setEntityClass("olddwarfnpc");
		hogart.setPosition(20, 11);
		hogart.initHP(100);
		zone.add(hogart);
	}
}
