package games.stendhal.server.maps.kalavan.castle;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds the king in Kalavan castle
 * 
 * @author kymara
 */
public class KingNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC kingNPC = new SpeakerNPC("King Cozart") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(40, 22));
				nodes.add(new Node(42, 22));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				// it's all in
				// games.stendhal.server.maps.quests.ImperialPrincess
			}
		};

		kingNPC.setEntityClass("kingcozartnpc");
		kingNPC.setPosition(40, 22);
		kingNPC.initHP(100);
		zone.add(kingNPC);
	}
}
