package games.stendhal.server.maps.ados;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * Entrance to Deathmatch
 */
public class AdosSwamp {
	private NPCList npcs = NPCList.get();;
	
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();
		buildDeathmatchRecruiter((StendhalRPZone) world.getRPZone(new IRPZone.ID(
			"0_ados_swamp")));
	}

	private void buildDeathmatchRecruiter(StendhalRPZone zone) {
		
		SpeakerNPC npc = new SpeakerNPC("Deathmatch Recruiter") {
			@Override
			protected void createPath() {
				List<Path.Node> path = new LinkedList<Path.Node>();
				path.add(new Path.Node(40, 35));
				path.add(new Path.Node(40, 84));
				path.add(new Path.Node(53, 84));
				path.add(new Path.Node(53, 80));
				path.add(new Path.Node(84, 80));
				path.add(new Path.Node(84, 56));
				path.add(new Path.Node(89, 56));
				path.add(new Path.Node(89, 37));
				path.add(new Path.Node(72, 37));
				path.add(new Path.Node(72, 32));
				path.add(new Path.Node(50, 32));
				path.add(new Path.Node(50, 35));
				setPath(path, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey there. You look like a reasonable fighter.");
				addJob("I'm recruiter for the Semos #deathmatch.");
				addHelp("Have you ever heard of the Semos #deathmatch.");
				add(ConversationStates.ATTENDING, "deathmatch", null, ConversationStates.ATTENDING, 
					"The deathmatch is the ultimate challenge for true #heroes.", null);
				add(ConversationStates.ATTENDING, "heroes", null, ConversationStates.ATTENDING,
					"Are you such a hero? I can take you to the #challenge.", null);
				addGoodbye("I hope you will enjoy the Semos #Deathmatch!");
				
				add(ConversationStates.ATTENDING, "challenge", null, ConversationStates.ATTENDING,
						null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("int_semos_deathmatch");
						player.teleport(zone, 17, 8, Direction.DOWN, null);			
					}
				});
			}
		};

		npc.put("class", "youngsoldiernpc");
		npc.set(40, 35);
		npc.initHP(100);
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		zone.addNPC(npc);

	}
}
