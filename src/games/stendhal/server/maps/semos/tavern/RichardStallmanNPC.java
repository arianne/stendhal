package games.stendhal.server.maps.semos.tavern;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

public class RichardStallmanNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildStallman(zone);
	}

	private void buildStallman(StendhalRPZone zone) {
		SpeakerNPC stallman = new SpeakerNPC("Richard Stallman") {

			@Override
            public void say(String text) {
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
				addJob("I am the #free software evangelizer! I am the founder of #GNU.");
				addHelp("Help #Stendhal to be even better. Donate your time, tell your friends to play, create maps.");
				addReply("free",
					"''Free software'' is a matter of liberty, not price. To understand the concept, you should think of ''free'' as in ''free speech,'' not as in ''free beer''.");
				addReply("stendhal",
					"Stendhal is #free software under #GNU #GPL. You can run, copy, distribute, study, change and improve this software.");
				addReply("gnu", "http://www.gnu.org/");
				addReply("gpl", "http://www.gnu.org/licenses/gpl.html");
				
				
				addGoodbye();
			}
		};

		stallman.setEntityClass("richardstallmannpc");
		stallman.setPosition(26, 11);
		stallman.setDirection(Direction.DOWN);
		stallman.initHP(100);
		zone.add(stallman);
	}
}
