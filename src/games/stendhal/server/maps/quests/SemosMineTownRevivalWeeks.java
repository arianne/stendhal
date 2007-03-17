package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.player.Player;
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
				addGreeting("Hi, I like the #Semos #Mine #Town #Revival #Weeks. Its like a huge party.");
				addJob("I am just a litte girl having lots of fun here during the #Semos #Mine #Town #Revival #Weeks-");
				addGoodbye("Have fun!");
				add(ConversationStates.ATTENDING, "debuggera", null, ConversationStates.ATTENDING, "She is my crazy twin sister.", null);
				addQuest("Just have fun.");
				add(ConversationStates.ATTENDING, Arrays.asList("offer"), ConversationStates.ATTENDING, "I can offer you my #friendship.", null);

				// Revival Weeks
				add(ConversationStates.ATTENDING, Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
					ConversationStates.ATTENDING, "During the Revival Weeks we #celebrate the old and now mostly dead Semos Mine Town. Lots of people from Ados come for a visit.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("celebrate", "celebration", "party"),
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, String text, SpeakerNPC engine) {
							return !player.has("outfit_org");
						}
					},
					ConversationStates.ATTENDING, "You can get a costume from Fidorea over there or you can try to solve a difficult puzzle in one of the houses.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("celebrate", "celebration", "party"),
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, String text, SpeakerNPC engine) {
							return player.has("outfit_org");
						}
					},
					ConversationStates.ATTENDING, "I see, you already got a costume from Fidorea. But have you tried your luck in a difficult puzzle in one of the houses?", null);

			    // friends
			    add(ConversationStates.ATTENDING, Arrays.asList("friend", "friends"), new StandardInteraction.QuestInStateCondition("susi", "friends"), ConversationStates.ATTENDING, "We are friends.", null);
			    add(ConversationStates.ATTENDING, Arrays.asList("friend", "friends"), new StandardInteraction.QuestNotInStateCondition("susi", "friends"), ConversationStates.INFORMATION_1, "Please repeat:\r\n                        \"A circle is round,\"", null);
			    add(ConversationStates.INFORMATION_1, Arrays.asList("A circle is round,", "A circle is round"), null, ConversationStates.INFORMATION_2, "\"it has no end.\"", null);
			    add(ConversationStates.INFORMATION_2, Arrays.asList("it has no end.", "it has no end"), null, ConversationStates.INFORMATION_3, "\"That's how long,\"", null);
			    add(ConversationStates.INFORMATION_3, Arrays.asList("That's how long,", "That's how long", "Thats how long,", "Thats how long"), null, ConversationStates.INFORMATION_4, "\"I will be your friend.\"", null);
			    add(ConversationStates.INFORMATION_4, Arrays.asList("I will be your friend.", "I will be your friend"), null, ConversationStates.ATTENDING, "Cool. We are friends now.", new StandardInteraction.SetQuestAction("susi", "friends"));

			    // help
				add(ConversationStates.ATTENDING, ConversationPhrases.HELP_MESSAGES, 
					new StandardInteraction.QuestInStateCondition("susi", "friends"), 
					ConversationStates.ATTENDING, 
					"I have made a lot of friends during the #Semos #Mine #Town #Revival #Weeks.", null);
				add(ConversationStates.ATTENDING, ConversationPhrases.HELP_MESSAGES, 
								new StandardInteraction.QuestNotInStateCondition("susi", "friends"), 
								ConversationStates.ATTENDING, 
								"I need a #friend.", null);
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "girlnpc");
		npc.set(95, 119);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		//npc.setSpeed(1.0);
		zone.add(npc);
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
	
	@Override
	public void addToWorld() {
		super.addToWorld();
		createNPC();
		createSignToCloseTower();
	}
}
