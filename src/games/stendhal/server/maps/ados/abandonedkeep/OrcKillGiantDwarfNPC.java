package games.stendhal.server.maps.ados.abandonedkeep;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Inside Ados Abandoned Keep - level -1 .
 */
public class OrcKillGiantDwarfNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildZogfang(zone);
	}

	private void buildZogfang(StendhalRPZone zone) {
		SpeakerNPC zogfang = new SpeakerNPC("Zogfang") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(5, 106));
				nodes.add(new Node(15, 106));
				nodes.add(new Node(15, 109));
				nodes.add(new Node(12, 109));
				nodes.add(new Node(12, 112));
				nodes.add(new Node(12, 114));
				nodes.add(new Node(5, 114));
				setPath(new FixedPath(nodes, true));
				
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I stay and wait for able-bodied warriors to help defeat our enemies.");
				addHelp("We are the ones in need of help.");
				addOffer("I have nothing to offer except thanks for a job well done.");
				addGoodbye();
			}
		};

		zogfang.setEntityClass("orcbuyernpc");
		zogfang.setPosition(10, 107);
		zogfang.initHP(100);
		zone.add(zogfang);
	}
}
