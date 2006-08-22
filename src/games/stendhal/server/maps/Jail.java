package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import marauroa.common.game.IRPZone;

import java.util.List;
import java.util.LinkedList;

/**
 * creates a jail keeper. 
 * Game masters can place players in -2_semos_jail for bad behaviour.
 */
public class Jail implements IContent {
	public Jail() {
		zoneSub1SemosJail();
		zoneSub2SemosJail();
	}

	private void zoneSub1SemosJail() {
		NPCList npcs = NPCList.get();
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
		"-1_semos_jail"));
		SpeakerNPC npc = new SpeakerNPC("Marcus") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 6));
				nodes.add(new Path.Node(21, 6));
				nodes.add(new Path.Node(21, 7));
				nodes.add(new Path.Node(9, 7));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addGreeting("Greetings! How may I #help you?");
				addJob("I am the jail keeper. You have been confined here because of your bad behaviour.");
				addHelp("Wait for an admin to come here and decide about you. There is meanwhile no exit from here.");
				addGoodbye();
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "youngsoldiernpc");
		npc.set(9, 6);
		npc.initHP(100);
		zone.addNPC(npc);

	}

	private void zoneSub2SemosJail() {
		NPCList npcs = NPCList.get();
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
		"-2_semos_jail"));
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
				addGreeting("Greetings! How may I #help you?");
				addJob("I am the jail keeper. You have been confined here because of your bad behaviour.");
				addHelp("Wait for an admin to come here and decide about you. There is meanwhile no exit from here.");
				addGoodbye();
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
