package games.stendhal.server.maps.ados.wall;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Wall North population
 *
 * @author hendrik
 */
public class SoldierNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildAdosGreetingSoldier(zone);
	}

	/**
	 * Creatures a soldier telling people a story, why Ados is so empty.
	 *
	 * @param zone StendhalRPZone
	 */
	private void buildAdosGreetingSoldier(StendhalRPZone zone) {

		SpeakerNPC npc = new SpeakerNPC("Julius") {

			@Override
			protected void createPath() {
				List<Node> path = new LinkedList<Node>();
				path.add(new Node(84, 109));
				path.add(new Node(84, 116));
				setPath(new FixedPath(path, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, have you heard the latest news? A small girl called #Susi is missing and almost the whole population is out of town looking for her.");
				addReply("susi",
				        "This girl was last seen during the Semos Mine Town Revival Weeks. But she did not return after the end of the fair.");
				addJob("I protect Ados City against looters while most of the people are out of town searching for that girl.");
				addHelp("Just look around, some people are still in Ados doing their work.");
				addGoodbye("I hope you will enjoy your visit to Ados anyway.");
			}
		};

		npc.setEntityClass("youngsoldiernpc");
		npc.setPosition(84, 109);
		npc.initHP(100);
		zone.add(npc);
	}
}
