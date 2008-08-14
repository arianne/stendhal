package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Puts the player into a funny constume.
 */
public class SemosMineTownRevivalWeeks extends AbstractQuest {

	private static final String QUEST_SLOT = "semos_mine_town_revival";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void createNPC() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(
				"0_semos_mountain_n2");
		final SpeakerNPC npc = new SpeakerNPC("Susi") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(95, 120));
				nodes.add(new Node(95, 113));
				nodes.add(new Node(101, 113));
				nodes.add(new Node(101, 108));
				nodes.add(new Node(95, 108));
				nodes.add(new Node(95, 104));
				nodes.add(new Node(90, 104));
				nodes.add(new Node(90, 107));
				nodes.add(new Node(89, 107));
				nodes.add(new Node(89, 113));
				nodes.add(new Node(77, 113));
				nodes.add(new Node(77, 110));
				nodes.add(new Node(87, 110));
				nodes.add(new Node(87, 113));
				nodes.add(new Node(92, 113));
				nodes.add(new Node(92, 120));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, I like the #Semos #Mine #Town #Revival #Weeks. Its like a huge party.");
				addJob("I am just a litte girl having lots of fun here during the #Semos #Mine #Town #Revival #Weeks-");
				addGoodbye("Have fun!");
				add(ConversationStates.ATTENDING, "debuggera", null,
						ConversationStates.ATTENDING,
						"She is my crazy twin sister.", null);
				addQuest("Just have fun.");
				addOffer("I can offer you my #friendship.");

				// Revival Weeks
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
					ConversationStates.ATTENDING,
					"During the Revival Weeks we #celebrate the old and now mostly dead Semos Mine Town. Lots of people from Ados come for a visit.",
					null);
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("celebrate", "celebration", "party"),
					new ChatCondition() {
						public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
							return !player.has("outfit_org");
						}
					},
					ConversationStates.ATTENDING,
					"You can get a costume from Fidorea over there or you can try to solve a difficult puzzle in one of the houses.",
					null);
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("celebrate", "celebration", "party"),
					new ChatCondition() {
						public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
							return player.has("outfit_org");
						}
					},
					ConversationStates.ATTENDING,
					"I see, you already got a costume from Fidorea. But have you tried your luck in a difficult puzzle in one of the houses?",
					null);

				// friends
				add(ConversationStates.ATTENDING, Arrays.asList("friend",
					"friends"),
					new QuestInStateCondition("susi", "friends"), 
					ConversationStates.ATTENDING,
					"We are friends.", null);

				add(ConversationStates.ATTENDING,
					Arrays.asList("friend", "friends"),
					new QuestNotInStateCondition("susi", "friends"),
					ConversationStates.INFORMATION_1,
					"Please repeat:\r\n                        \"A circle is round,\"",
					null);
				add(ConversationStates.INFORMATION_1, Arrays.asList(
					"A circle is round,", "A circle is round"), null,
					ConversationStates.INFORMATION_2, "\"it has no end.\"",
					null);
				add(ConversationStates.INFORMATION_2, Arrays.asList(
					"it has no end.", "it has no end"), null,
					ConversationStates.INFORMATION_3,
					"\"That's how long,\"", null);
				add(ConversationStates.INFORMATION_3, Arrays.asList(
					"That's how long,", "That's how long",
					"Thats how long,", "Thats how long"), null,
					ConversationStates.INFORMATION_4,
					"\"I will be your friend.\"", null);
				add(ConversationStates.INFORMATION_4, Arrays.asList(
					"I will be your friend.", "I will be your friend"),
					null, ConversationStates.ATTENDING,
					"Cool. We are friends now.",
					new SetQuestAction("susi", "friends"));

				// help
				add(
					ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					new QuestInStateCondition("susi", "friends"),
					ConversationStates.ATTENDING,
					"I have made a lot of friends during the #Semos #Mine #Town #Revival #Weeks.",
					null);
				add(ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					new QuestNotInStateCondition("susi", "friends"),
					ConversationStates.ATTENDING, "I need a #friend.", null);
			}
		};

		npcs.add(npc);
		npc.setEntityClass("girlnpc");
		npc.setPosition(95, 120);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		// npc.setSpeed(1.0);
		zone.add(npc);
	}

	private void createSignToCloseTower() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(
				"0_semos_mountain_n2");
		final Sign sign = new Sign();
		sign.setPosition(105, 114);
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
