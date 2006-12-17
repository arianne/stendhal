package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * Ados Wall North population
 *
 * @author hendrik
 */
public class AdosWallNorth {

private NPCList npcs = NPCList.get();;
	
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();
		buildAdosGreetingSoldier((StendhalRPZone) world.getRPZone(new IRPZone.ID(
			"0_ados_wall_n")));
	}

	/**
	 * Creatures a soldier telling people a story, why Ados is so empty.
	 *
	 * @param zone StendhalRPZone
	 */
	private void buildAdosGreetingSoldier(StendhalRPZone zone) {
		
		SpeakerNPC npc = new SpeakerNPC("Julius") {
			@Override
			protected void createPath() {
				List<Path.Node> path = new LinkedList<Path.Node>();
				path.add(new Path.Node(84, 108));
				path.add(new Path.Node(84, 115));
				setPath(path, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, have you heard the latest news? A small girl called #Susi is missing and almost the whole population is out of town looking for her.");
				addReply("susi", "This girl was last seen during the Semos Mine Town Revival Weeks. But she did not return after the end of the fair.");
				addJob("I protect Ados City against looters while most of the people are out of town searching for that girl.");
				addHelp("Just look around, some people are still in Ados doing their work.");
				addGoodbye("I hope you will enjoy your visit to Ados anyway.");
			}
		};

		npc.put("class", "youngsoldiernpc");
		npc.set(84, 108);
		npc.initHP(100);
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		zone.addNPC(npc);

	}
}
