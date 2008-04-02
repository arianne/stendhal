package games.stendhal.server.maps.magic.theater;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configure Baldemar - mithril shield forger.
 *
 * @author kymara
 */
public class MithrilShieldForgerNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildbaldemar(zone);
	}

	private void buildbaldemar(StendhalRPZone zone) {
		SpeakerNPC baldemar = new SpeakerNPC("Baldemar") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hoi stuff goes here.");
				addJob("I am a wizard. I have studied long and hard to perfect the art of mithril forging.");
				addHelp("help goes here.");
				addOffer("offer goes here.");
				addReply("mithril", "mithril goes here.");
				addGoodbye();
			} //remaining behaviour defined in quest
		};

		baldemar.setDescription("You see Baldemar, a Mithrilbourgh Wizard well studied in the craft of forging mithril.");
		baldemar.setEntityClass("mithrilforgernpc");
		baldemar.setPosition(4, 6);
		baldemar.initHP(100);
		zone.add(baldemar);
	}
}
