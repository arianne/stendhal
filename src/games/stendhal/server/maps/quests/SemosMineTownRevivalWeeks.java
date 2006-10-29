package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * Puts the player into a funny constume
 */
public class SemosMineTownRevivalWeeks extends AbstractQuest {

	private static final String QUEST_SLOT = "semos_mine_town_revival";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	private void createNPC() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_mountain_n2"));
		SpeakerNPC npc = new SpeakerNPC("Susi") {
			@Override
			protected void createPath() {
				// npc does not move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(95,119));
				nodes.add(new Path.Node(95,112));
				nodes.add(new Path.Node(101,112));
				nodes.add(new Path.Node(101,107));
				nodes.add(new Path.Node(95,107));
				nodes.add(new Path.Node(95,103));
				nodes.add(new Path.Node(90,103));
				nodes.add(new Path.Node(90,106));
				nodes.add(new Path.Node(89,106));
				nodes.add(new Path.Node(89,112));
				nodes.add(new Path.Node(77,112));
				nodes.add(new Path.Node(77,109));
				nodes.add(new Path.Node(87,109));
				nodes.add(new Path.Node(87,112));
				nodes.add(new Path.Node(92,112));
				nodes.add(new Path.Node(92,119));
				setPath(nodes, true);

			}

			@Override
			protected void createDialog() {
				addGreeting("I like the #Semos #Mine #Town #Revival #Weeks. Its like a huge party.");
				addHelp("Just talk to Fidorea to get a nice costume.");
				addJob("I am just a litte girl having lots of fun here during the #Semos #Mine #Town #Revival #Weeks-");
				addGoodbye("Have fun!");
				add(ConversationStates.ATTENDING, Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
					ConversationStates.ATTENDING, "During the Revival Weeks we #celebrate the old and now mostly dead Semos Mine Town. Lots of people from Ados came for a visit.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("celebrate", "celebration", "party"),
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, SpeakerNPC engine) {
							return !player.has("outfit_org");
						}
					},
					ConversationStates.ATTENDING, "You can get a costume from Fidorea over there or you can try to solve a difficult puzzle in one of the houses.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("celebrate", "celebration", "party"),
								new SpeakerNPC.ChatCondition() {
									@Override
									public boolean fire(Player player, SpeakerNPC engine) {
										return player.has("outfit_org");
									}
								},
								ConversationStates.ATTENDING, "I see, you already got a costume from Fidorea. But have you tried your luck in a difficult puzzle in one of the houses?", null);
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "girlnpc");
		npc.set(95, 119);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npc.setSpeed(3.0);
		zone.addNPC(npc);
	}

	private void createSignToCloseTower() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_mountain_n2"));
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(105);
		sign.setY(114);
		sign.setText("Because of the missing guard rail it is too dangerous to enter the tower.");
		zone.add(sign);		
	}
	
	public void addToWorld() {
		super.addToWorld();
		createNPC();
		createSignToCloseTower();
	}
}
