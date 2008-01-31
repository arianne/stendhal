package games.stendhal.server.maps.ados.abandonedkeep;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

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
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I stand and wait for able-bodied warriors to help defeat our enemies.");
				addHelp("We are the ones in need of help.");
				addOffer("I have nothing to offer except thanks for a job well done.");
				addGoodbye();
			}
		};

		zogfang.setEntityClass("orc_npc");
		zogfang.setPosition(10, 107);
		zogfang.initHP(100);
		zone.add(zogfang);
	}
}
