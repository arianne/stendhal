package games.stendhal.server.maps.ados;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Entrance to Deathmatch
 */
public class AdosSwamp implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();;
	
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("0_ados_swamp")),
			java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildDeathmatchRecruiter(zone);
	}


	private void buildDeathmatchRecruiter(StendhalRPZone zone) {
		
		SpeakerNPC npc = new SpeakerNPC("Thonatus") {
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
				addJob("I'm recruiter for the Ados #deathmatch.");
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
						if (player.getLevel() >= 20) {
							StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_ados_wall_n");
							player.teleport(zone, 100, 86, Direction.DOWN, null);
						} else {
							engine.say("Sorry, you are too weak!");
						}
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
