package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

public class SemosPlainsNorthEast {
	private NPCList npcs = NPCList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();
		buildSemosNorthEastPlainsArea((StendhalRPZone) world
				.getRPZone(new IRPZone.ID("0_semos_plains_ne")));
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
				addJob("I'm the local miller. People bring me grain so that I can make flour.");
				addReply("grain", "If you have a scythe, you can harvest grain at the nearby farm.");
				addHelp("Do you know the bakery in Semos? I am proud that they use the flour that I produce.");
				addGoodbye();

				// Jenny mills flour if you bring her grain.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("grain", new Integer(5));

				ProducerBehaviour behaviour = new ProducerBehaviour(
						"jenny_mill_flour", "mill", "flour", requiredResources, 2 * 60);

				addProducer(behaviour,
						"Greetings. I am Jenny, the local miller. If you bring me #grain, I can #mill flour for you.");
				
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "woman_003_npc");
		npc.setDirection(Direction.DOWN);
		npc.set(19, 38);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
