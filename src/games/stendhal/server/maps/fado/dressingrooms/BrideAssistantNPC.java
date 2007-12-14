package games.stendhal.server.maps.fado.dressingrooms;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;

import java.util.HashMap;
import java.util.Map;

/**
 * Dressing rooms at fado hotel
 * 
 * @author kymara
 */
public class BrideAssistantNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildDressingRoom(zone, attributes);
	}

	private void buildDressingRoom(StendhalRPZone zone,
			Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Tamara") {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome! If you're a bride-to-be I can #help you get ready for your wedding");
				addJob("I assist brides with getting dressed for their wedding.");
				addHelp("Just tell me if you want to #wear #a #gown for your wedding.");
				addQuest("You don't want to be thinking about that kind of thing ahead of your big day!");
				addReply(
						"gown",
						"Every bride needs a beautiful wedding dress! It's a charge of 100 money if you want to #wear #a #gown.");
				addGoodbye("Have a lovely time!");

				Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("gown", 100);
				OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(
						priceList);
				new OutfitChangerAdder().addOutfitChanger(this, behaviour,
						"wear");
			}
		};

		npc.setEntityClass("woman_003_npc");
		npc.setDirection(Direction.RIGHT);
		npc.setPosition(3, 10);
		npc.initHP(100);
		zone.add(npc);
	}
}
