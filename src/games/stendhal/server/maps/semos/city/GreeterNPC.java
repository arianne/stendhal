package games.stendhal.server.maps.semos.city;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GreeterNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosCityAreaMonogenes(zone);
	}

	private void buildSemosCityAreaMonogenes(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Monogenes") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addJob("Me? I give directions to newcomers to Semos and help them settle in. When I'm in a bad mood I sometimes give misleading directions to amuse myself... hee hee hee! Of course, sometimes I get my wrong directions wrong and they end up being right after all! Ha ha!");

				// All further behaviour is defined in MeetMonogenes.java.
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldmannpc");
		npc.set(26, 21);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);

	}
}
