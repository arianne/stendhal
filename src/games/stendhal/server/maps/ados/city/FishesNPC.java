package games.stendhal.server.maps.ados.city;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.PassiveNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds some fish NPCs.
 *
 * @author AntumDeluge
 */
public class FishesNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}
	
	private void buildNPC(final StendhalRPZone zone) {
		final PassiveNPC f1 = new PassiveNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(76, 62));
				nodes.add(new Node(76, 66));
				nodes.add(new Node(73, 66));
				nodes.add(new Node(73, 62));
				/*nodes.add(new Node(85, 53));
				nodes.add(new Node(88, 53));
				nodes.add(new Node(88, 56));
				nodes.add(new Node(85, 56));*/
				setPath(new FixedPath(nodes, true));
			}
		};
		
		f1.setPosition(76, 62);
		f1.setDescription("You see a fish.");
		f1.setResistance(0);
		f1.setEntityClass("fish");
		f1.setVisibility(50); //underwater
		//f1.setRandomPathFrom(76, 62, 5);
		f1.setIgnoresCollision(true);
		zone.add(f1);
	}
}
