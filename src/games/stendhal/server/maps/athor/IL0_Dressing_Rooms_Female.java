package games.stendhal.server.maps.athor;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Dressing rooms at the Athor island beach (Inside / Level 0)
 *
 * @author daniel
 */
public class IL0_Dressing_Rooms_Female implements ZoneConfigurator {

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
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// doesn't move
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addJob("I'm one of the lifeguards at this beach. And as you can see, I also take care of the women's dressing room.");
				addHelp("Just tell me if you want to #borrow #a #swimsuit!");
				addGoodbye("Have fun!");

				Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("swimsuit", 5);
				OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(priceList);
				addOutfitChanger(behaviour, "borrow");

				// stuff needed for the SuntanCreamForZara quest
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("arandula", new Integer(1));
				requiredResources.put("kokuda", new Integer(1));
				requiredResources.put("minor_potion", new Integer(1));

				ProducerBehaviour behaviour_mix = new ProducerBehaviour("pamela_mix_cream", "mix", "suntan_cream",
				        requiredResources, 10 * 60);

				addProducer(behaviour_mix, "Hallo!");

				addReply(
				        Arrays.asList("suntan", "cream", "suntan_cream"),
				        "David's and mine suntan cream is famous all over the island. But the way to the labyrinth entrance is blocked, so we can't get all the ingredients we need. If you bring me the things we need, I can #mix our special suntan cream for you.");

				addReply("arandula", "Arandula is a herb which is growing around Semos.");

				addReply(
				        "kokuda",
				        "We can't find the Kokuda herb which is growing on this island, because the entrance of the labyrinth, where you can find this herb, is blocked.");

				addReply("minor_potion", "It's a small bottle full of potion. You can buy it at several places.");

			}
		};
		NPCList.get().add(pam);
		zone.assignRPObjectID(pam);
		pam.put("class", "lifeguardfemalenpc");
		pam.setDirection(Direction.LEFT);
		pam.set(12, 10);
		pam.initHP(100);
		zone.add(pam);
	}
}
