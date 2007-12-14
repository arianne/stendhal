package games.stendhal.server.maps.kalavan.citygardens;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds an ice cream seller npc
 *
 * @author kymara
 */
public class IceCreamSellerNPC implements ZoneConfigurator {
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
		buildNPC(zone, attributes);
	}

	//
	// MaidNPC
	//

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Sam") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi. Can I #offer you an icecream?");
				addJob("I sell delicious icecreams.");
				addHelp("I can #offer you a refreshing icecream.");
				addQuest("Mine's a simple life, I don't need a lot.");

				Map<String, Integer> offers = new HashMap<String, Integer>();
				offers.put("icecream", 30);
				new SellerAdder().addSeller(this, new SellerBehaviour(offers));
				addGoodbye("Bye, enjoy your day!");
			}
		};

		npc.setEntityClass("icecreamsellernpc");
		npc.setPosition(73, 54);
		npc.initHP(100);
		zone.add(npc);
	}
}
