package games.stendhal.server.maps.kalavan.citygardens;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a little girl called Annie Jones.
 *
 * @author kymara
 */
public class LittleGirlNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createNPC(zone, attributes);
	}


	private void createNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Annie Jones") {
			
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(44, 90));
				nodes.add(new Node(44, 86));
				nodes.add(new Node(42, 86));
				nodes.add(new Node(42, 90));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {	
				// greeting and quest in maps/quests/IcecreamForAnnie.java
			addOffer("I'm a little girl, I haven't anything to offer.");
			addJob("I help my mummy.");
			addHelp("Ask my mummy.");
			addGoodbye("Ta ta.");
			}
		};

		npc.setDescription("You see a little girl, playing in the playground.");
		npc.setEntityClass("pinkgirlnpc");
		npc.setPosition(44, 90);
		npc.initHP(100);
		zone.add(npc);
	}
}
