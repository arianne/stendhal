package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import marauroa.common.game.IRPZone;

import java.util.List;
import java.util.LinkedList;

public class Ados implements IContent {
	private StendhalRPWorld world;

	private NPCList npcs;

	public Ados(StendhalRPWorld world) {
		this.npcs = NPCList.get();
		this.world = world;

		buildRockArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_ados_rock")));
	}


	private void buildRockArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Balduin") {
			protected void createPath() {
				// NPC doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			protected void createDialog() {
				Behaviours.addHelp(this,
								   "There is a swamp east of this mountain where you might get some rare weapons.");
				Behaviours.addJob(this,
						          "I'm much too old for hard work. I'm just living here as a hermit.");

				Behaviours.addGoodbye(this, "It was nice to meet you.");
			}
			// remaining behaviour is defined in maps.quests.WeaponsCollector.
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldwizardnpc");
		npc.set(16, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
