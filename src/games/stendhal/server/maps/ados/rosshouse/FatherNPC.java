package games.stendhal.server.maps.ados.rosshouse;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.Map;
/**
 * <p>Creates a normal version of mr ross in the ross house.
 */
public class FatherNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createDadNPC(zone);
	}

	private void createDadNPC(final StendhalRPZone zone) {

		if (System.getProperty("stendhal.minetown") != null) {
			return;
		}

		final SpeakerNPC npc = new SpeakerNPC("Mr Ross") {
			@Override
			protected void createPath() {
				setPath(null);

			}

			@Override
			protected void createDialog() {
			    addGreeting("Hi there.");
			    addJob("I'm looking after my daughter Susi.");
			    addHelp("If you need help finding any buildings in Ados, the guard Julius will give you a map. He is by the city entrance.");
			    addOffer("Sorry I do not have anything to offer you, but there are two places to eat in Ados - the tavern and a bar.");
			    addQuest("At the end of October we will be visiting the #Semos #Mine #Town #Revival #Weeks");
			    addGoodbye("Bye, nice to meet you.");

				// Revival Weeks
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
					ConversationStates.ATTENDING,
					"During the Revival Weeks at the end of October we celebrate the old and now mostly dead Semos Mine Town.",
					null);
			}
		};

		npc.setOutfit(new Outfit(27, 07, 34, 01));
		npc.setPosition(12, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}

}
