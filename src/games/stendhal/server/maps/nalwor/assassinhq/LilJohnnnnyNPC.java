package games.stendhal.server.maps.nalwor.assassinhq;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * Inside assassin headquarters classroom area.
 */
public class LilJohnnnnyNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildLilJohnnnny(zone);
	}

	private void buildLilJohnnnny(StendhalRPZone zone) {
		SpeakerNPC liljohnnnny = new SpeakerNPC("lil johnnnny") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("I didn't mean to hurt him.");
				addJob("He was making fun of me.");
				addHelp("He said my mommy wears army boots.");
				addOffer("I'm gonna make him sorry he ever said that!");
				addGoodbye("well, if you really have to leave...");
			}
		};

		liljohnnnny.setDescription("You see lil johnnnny, plotting his next rebellion.");
		liljohnnnny.setEntityClass("liljohnnnnynpc");
		liljohnnnny.setPosition(23, 2);
		liljohnnnny.initHP(100);
		zone.add(liljohnnnny);
	}
}
