package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import marauroa.common.game.IRPZone;

import java.util.List;
import java.util.LinkedList;

public class Jail implements IContent {
	public Jail(StendhalRPWorld world) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_admin_jail"));
		NPCList npcs = NPCList.get();

		SpeakerNPC npc = new SpeakerNPC("Sten Tanquilos") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(4, 14));
				nodes.add(new Path.Node(27, 14));
				nodes.add(new Path.Node(27, 17));
				nodes.add(new Path.Node(4, 17));
				setPath(nodes, true);
			}

			protected void createDialog() {
				Behaviours.addGreeting(this);
				Behaviours.addJob(this,
						"I am the jail keeper. You have been confined here because of your bad behaviour.");
				Behaviours.addHelp(this,
						"Wait for an admin to come here and decide about you. There is meanwhile no exit from here.");
				Behaviours.addGoodbye(this);
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "youngsoldiernpc");
		npc.set(4, 14);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
