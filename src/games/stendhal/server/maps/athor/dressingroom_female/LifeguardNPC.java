package games.stendhal.server.maps.athor.dressingroom_female;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Dressing rooms at the Athor island beach (Inside / Level 0).
 *
 * @author daniel
 */
public class LifeguardNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildFemaleDressingRoom(zone, attributes);
	}

	private void buildFemaleDressingRoom(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC pam = new SpeakerNPC("Pam") {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addJob("I'm one of the lifeguards at this beach. And as you can see, I also take care of the women's dressing room.");
				addHelp("Just tell me if you want to #borrow #a #swimsuit!");
				addGoodbye("Have fun!");

				Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("swimsuit", 5);
				OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(priceList);
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, "borrow");

				// stuff needed for the SuntanCreamForZara quest
				Map<String, Integer> requiredResources = new TreeMap<String, Integer>();	// use sorted TreeMap instead of HashMap
				requiredResources.put("arandula", 1);
				requiredResources.put("kokuda", 1);
				requiredResources.put("minor potion", 1);

				ProducerBehaviour mixerBehaviour = new ProducerBehaviour("pamela_mix_cream",
						"mix", "suntan cream", requiredResources, 10 * 60);

				new ProducerAdder().addProducer(this, mixerBehaviour, "Hallo!");

				addReply(
				        Arrays.asList("suntan", "cream", "suntan cream"),
				        "David's and mine suntan cream is famous all over the island. But the way to the labyrinth entrance is blocked, so we can't get all the ingredients we need. If you bring me the things we need, I can #mix our special suntan cream for you.");

				addReply("arandula", "Arandula is a herb which is growing around Semos.");

				addReply(
				        "kokuda",
				        "We can't find the Kokuda herb which is growing on this island, because the entrance of the labyrinth, where you can find this herb, is blocked.");

				addReply("minor potion", "It's a small bottle full of potion. You can buy it at several places.");
			}
		};

		pam.setEntityClass("lifeguardfemalenpc");
		pam.setDirection(Direction.LEFT);
		pam.setPosition(12, 11);
		pam.initHP(100);
		zone.add(pam);
	}
}
