package games.stendhal.server.maps.ados.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SilentNPC;

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

		// Fish that swims around in the fountain
		final SilentNPC f1 = new SilentNPC() {
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

		// Fish that swims up and down in the coast
		final SilentNPC f2 = new SilentNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(84, 81));
				nodes.add(new Node(84, 78));
				nodes.add(new Node(90, 78));
				nodes.add(new Node(90, 28));
				nodes.add(new Node(85, 28));
				nodes.add(new Node(85, 17));
				nodes.add(new Node(80, 17));
				nodes.add(new Node(80, 20));
				nodes.add(new Node(85, 20));
				nodes.add(new Node(85, 28));
				nodes.add(new Node(90, 28));
				nodes.add(new Node(90, 78));
				nodes.add(new Node(84, 78));
				setPath(new FixedPath(nodes, true));
			}
		};

		f2.setPosition(84, 81);
		f2.setDescription("You see a fish.");
		f2.setDirection(Direction.UP);
		f2.setEntityClass("animal/fish_roach");
		f2.setVisibility(50); //underwater
		f2.setIgnoresCollision(true);
		zone.add(f2);
	}
}
