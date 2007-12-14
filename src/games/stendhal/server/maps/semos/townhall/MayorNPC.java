package games.stendhal.server.maps.semos.townhall;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MayorNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosTownhallAreaMayor(zone);
	}

	/**
	 * Adding a a Mayor to the townhall who gives out daily quests
	 */
	private void buildSemosTownhallAreaMayor(StendhalRPZone zone) {
		// We create an NPC
		SpeakerNPC npc = new SpeakerNPC("Mayor Sakhs") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(13, 3));
				nodes.add(new Node(19, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome citizen! Do you need #help?");
				addJob("I'm the mayor of Semos village.");
				addHelp("You will find a lot of people in Semos that offer you help on different topics.");
				addGoodbye("Have a good day and enjoy your stay!");
			}
		};

		npc.setEntityClass("mayornpc");
		npc.setPosition(13, 3);
		npc.initHP(100);
		zone.add(npc);
	}
}
