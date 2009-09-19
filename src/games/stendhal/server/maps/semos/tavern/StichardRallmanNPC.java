package games.stendhal.server.maps.semos.tavern;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

public class StichardRallmanNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC stallman = new SpeakerNPC("Stichard Rallman") {

			@Override
			public void say(final String text) {
				setDirection(Direction.DOWN);
				super.say(text, false);
			}

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to Stendhal! True #free software!");
				addJob("I am the #free software evangelizer!");
				addHelp("Help #Stendhal to be even better. Donate your time, tell your friends to play, create maps.");
				addReply("free",
					"''Free software'' is a matter of liberty, not price. To understand the concept, you should think of ''free'' as in ''free speech,'' not as in ''free beer''.");
				addReply("stendhal",
					"Stendhal is completely #free software (client, server, graphics, everything) under #GNU #GPL. You can run, copy, distribute, study, change and improve this software.");
				addReply("gnu", "http://www.gnu.org/");
				addReply("gpl", "http://www.gnu.org/licenses/gpl.html");

				addGoodbye();
			}
		};

		stallman.setEntityClass("richardstallmannpc");
		stallman.setPosition(24, 19);
		stallman.setDirection(Direction.DOWN);
		stallman.initHP(100);
		zone.add(stallman);
	}
}
