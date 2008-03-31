package games.stendhal.server.maps.nalwor.assassinhq;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * Outside entrance to assassin headquarters in 0_nalwor_caves_e.
 */
public class KaryoNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildKaryo(zone);
	}

	private void buildKaryo(StendhalRPZone zone) {
		SpeakerNPC karyo = new SpeakerNPC("Karyo") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			public void createDialog() {
				addGreeting("Welcome. May i help you?");
				addJob("My job is to see you don't enter unless you have the proper ID.");
				addGoodbye("Good bye.  If you wish to enter headquarters, obtain an assassin's license.");
				// all other behaviour is defined in the quest.
			}
		};

		karyo.setDescription("You see Karyo, guardian of assassin headquarters entrance. He'd much rather be doing other things.");
		karyo.setEntityClass("chiefassassinnpc");
		karyo.setPosition(24, 12);
		karyo.initHP(100);
		zone.add(karyo);
	}
}
