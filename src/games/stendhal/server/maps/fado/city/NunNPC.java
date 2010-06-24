package games.stendhal.server.maps.fado.city;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

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
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNunNPC(zone, attributes);
	}

	//
	// A Nun NPC outside church
	//
	private void buildNunNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC nunnpc = new SpeakerNPC("Sister Benedicta") {

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

			/* (non-Javadoc)
			 * @see games.stendhal.server.entity.npc.SpeakerNPC#onGoodbye(games.stendhal.server.entity.player.Player)
			 */
			@Override
			protected void onGoodbye(Player player) {
				setDirection(Direction.RIGHT);
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
