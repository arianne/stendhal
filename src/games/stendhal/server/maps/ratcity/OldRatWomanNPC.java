package games.stendhal.server.maps.ratcity;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Rat Mother NPC.
 *
 * @author Norien
 */
public class OldRatWomanNPC implements ZoneConfigurator {

         /**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}
	private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC woman = new SpeakerNPC("Agnus") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 4));
				nodes.add(new Node(3, 13));
				nodes.add(new Node(12, 13));
				nodes.add(new Node(12, 4));
				
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello There");
				addJob("Leave it to my childern to not check in once in a while.");
				addHelp("I have no help to offer you.");
				addGoodbye("Bye");
				// remaining behaviour defined in games.stendhal.server.maps.quests.FindRatChildern
			}
		};
		woman.setDescription("You see an old ratwoman. She appears somehow worried.");
		

		woman.setEntityClass("oldratwomannpc");
		
		woman.setPosition(3, 4);
		// She has low HP
		woman.initHP(30);
		zone.add(woman);
	}
}
