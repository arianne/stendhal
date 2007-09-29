package games.stendhal.server.maps.fado.city;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * Builds the church Nun NPC.
 *
 * @author kymara
 */
public class NunNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNunNPC(zone, attributes);
	}

	//
	// A Nun NPC outside church
	//
	private void buildNunNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC nunnpc = new SpeakerNPC("Sister Benedicta") {

			@Override
			protected void createPath() {
				// does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to this place of worship.");
				addHelp("I don't know what you need, dear child.");
				addJob("I am a nun. But this is my life, not my work.");
				addGoodbye("Goodbye, may peace be with you.");
			}
		};

		nunnpc.setDescription("You see Sister Benedicta, a holy nun.");
		nunnpc.setEntityClass("nunnpc");
		nunnpc.setDirection(Direction.RIGHT);
		nunnpc.setPosition(53, 54);
		nunnpc.initHP(100);
		zone.add(nunnpc);
	}
}
