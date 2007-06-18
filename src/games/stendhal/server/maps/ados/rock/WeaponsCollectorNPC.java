package games.stendhal.server.maps.ados.rock;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

public class WeaponsCollectorNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildRockArea(zone);
	}

	private void buildRockArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Balduin") {

			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addHelp("There is a swamp east of this mountain where you might get some rare weapons.");
				addJob("I'm much too old for hard work. I'm just living here as a hermit.");
				addGoodbye("It was nice to meet you.");
			}
			// remaining behaviour is defined in maps.quests.WeaponsCollector.
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldwizardnpc");
		npc.set(16, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}
