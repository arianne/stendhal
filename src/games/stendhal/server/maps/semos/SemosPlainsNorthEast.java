package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SemosPlainsNorthEast implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildSemosNorthEastPlainsArea(zone);
	}


	private void buildSemosNorthEastPlainsArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Jenny") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}
		
			@Override
			protected void createDialog() {
				addJob("I run this windmill, where I can #mill people's #grain into flour for them. I also supply the bakery in Semos.");
				addReply("grain", "There's a farm nearby; they usually let people harvest there. You'll need a scythe, of course.");
				addHelp("Do you know the bakery in Semos? I'm proud to say they use my flour. But the wolves ate my delivery boy again recently... they're probably running out.");
				addGoodbye();

				// Jenny mills flour if you bring her grain.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("grain", new Integer(5));

				ProducerBehaviour behaviour = new ProducerBehaviour(
						"jenny_mill_flour", "mill", "flour", requiredResources, 2 * 60);

				addProducer(behaviour,
						"Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.");
				
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "woman_003_npc");
		npc.setDirection(Direction.DOWN);
		npc.set(19, 38);
		npc.initHP(100);
		zone.add(npc);
	}
}
