package games.stendhal.server.maps.kotoch;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SmithNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildKotochSmitherArea(zone);
	}

	private void buildKotochSmitherArea(StendhalRPZone zone) {
		SpeakerNPC smith = new SpeakerNPC("Vulcanus") {

			@Override
			// he doesn't move.
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Chairetismata! I am Vulcanus the smither.");
				addGoodbye("Farewell");
				addHelp("I may help you to get a very #special items for only a few ones.");
				addJob("I used to forge weapons for the King of Faumoni, but this was long ago, since now the bridge is blocked.");
				
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("special"),
				        null,
				        ConversationStates.ATTENDING,
				        "Who told you that!?! *cough* Anyway, yes, I can forge an very special item for you. But you will need to run the #quest",
				        null);
			}
		};

		smith.setDescription("You see vulcanus. You feel a strange sensation near him.");
		zone.assignRPObjectID(smith);
		smith.put("class", "smithnpc");
		smith.set(62, 114);
		smith.setDirection(Direction.DOWN);
		smith.initHP(100);
		zone.add(smith);
		npcs.add(smith);
	}
}
