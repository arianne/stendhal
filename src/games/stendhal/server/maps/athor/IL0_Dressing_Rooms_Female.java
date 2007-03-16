package games.stendhal.server.maps.athor;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

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
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildFemaleDressingRoom(zone, attributes);
	}


	private void buildFemaleDressingRoom(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC pam = new SpeakerNPC("Pam") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// doesn't move
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hallo!");
				addJob("I'm one of the lifeguards at this beach. And as you can see, I also take care of the women's dressing room.");
				addHelp("Just tell me if you want to #borrow #a #swimsuit!");
				addGoodbye("Have fun!");

				Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("swimsuit", 5);
				OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(
						priceList);
				addOutfitChanger(behaviour, "borrow");
			}
		};
		NPCList.get().add(pam);
		zone.assignRPObjectID(pam);
		pam.put("class", "lifeguardfemalenpc");
		pam.setDirection(Direction.LEFT);
		pam.set(12, 10);
		pam.initHP(100);
		zone.addNPC(pam);
	}
}
