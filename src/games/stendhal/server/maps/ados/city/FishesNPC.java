package games.stendhal.server.maps.ados.city;

import games.stendhal.common.Direction;
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
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}
	
	private void buildNPC(final StendhalRPZone zone) {
		final PassiveNPC f1 = new PassiveNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(53, 108));
				nodes.add(new Node(58, 108));
				nodes.add(new Node(58, 111));
				nodes.add(new Node(53, 111));
				setPath(new FixedPath(nodes, true));
			}
		};
		
		f1.setPosition(53, 109);
		f1.setDescription("You see a fish.");
		f1.setDirection(Direction.DOWN);
		f1.setEntityClass("animal/fish_roach");
		f1.setVisibility(50); //underwater
		zone.add(f1);
	}
}
