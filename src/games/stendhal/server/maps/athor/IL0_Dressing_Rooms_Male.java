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
public class IL0_Dressing_Rooms_Male implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildMaleDressingRoom(zone, attributes);
		//buildFemaleDressingRoom(femaleZone, attributes);
	}


	private void buildMaleDressingRoom(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC david = new SpeakerNPC("David") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// doesn't move
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hallo!");
				addJob("I'm one of the lifeguards at this beach. And as you can see, I also take care of the men's dressing room.");
				addHelp("Just tell me if you want to #buy #1 #male_swimsuit!");
				addGoodbye("Have fun!");

				Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("male_swimsuit", 5);
				OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(
						priceList);
				addOutfitChanger(behaviour, true);
			}
		};
		NPCList.get().add(david);
		zone.assignRPObjectID(david);
		// TODO
		david.put("class", "naughtyteennpc");
		david.setDirection(Direction.RIGHT);
		david.set(3, 10);
		david.initHP(100);
		zone.addNPC(david);
	}
}
