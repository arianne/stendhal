package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestToYearAction;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestSmallerThanCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class FoundGirl {
	private SpeakerNPC npc;
	private ChatCondition noFriends;
	private ChatCondition anyFriends;
	private ChatCondition oldFriends;
	private ChatCondition currentFriends;

	private void buildConditions() {
		noFriends = new QuestNotStartedCondition("susi");
		anyFriends = new QuestStartedCondition("susi");
		oldFriends = new OrCondition(new QuestInStateCondition("susi", "friends"), new QuestSmallerThanCondition("susi", Calendar.getInstance().get(Calendar.YEAR)));
		currentFriends = new QuestInStateCondition("susi", Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
	}

	private StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");

	private void createGirlNPC() {

		npc = new SpeakerNPC("Susi") {

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
				// done outside
			}
		};

		//	npcs.add(npc);
		npc.setOutfit(new Outfit(04, 07, 32, 13));
		npc.setPosition(95, 120);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		// npc.setSpeed(1.0);
		zone.add(npc);
	}
	
	private void addDialog() {
		
		// greeting
		addGreetingDependingOnQuestState();

		npc.addJob("I am just a litte girl waiting for my father to take me out. We will have lots of fun here at the #Semos #Mine #Town #Revival #Weeks-");
		npc.addGoodbye("Have fun!");
		npc.addReply("debuggera", "Debuggera is my crazy twin sister.");
		npc.addHelp("Just have fun.");
		npc.addOffer("I can offer you my #friendship.");

		// Revival Weeks
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
			ConversationStates.ATTENDING,
			"During the Revival Weeks we #celebrate the old and now mostly dead Semos Mine Town. "
			+ "The party was cancelled last year because the people of Ados were searching for me after I got lost. "
			+ "Now that I am found we can party again!",
			null);
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("celebrate", "celebration", "party"),
			ConversationStates.ATTENDING,
			// TODO: add Liliana or adjust this sentence
			"You can get a costume from Liliana over there or you can try to solve a difficult puzzle in one of the houses.",
			null);


		// friends
		npc.add(
			ConversationStates.ATTENDING, Arrays.asList("friend", "friends", "friendship"),
			new QuestInStateCondition("susi", Integer.toString(Calendar.getInstance().get(Calendar.YEAR))), 
			ConversationStates.ATTENDING,
			"Thanks for being a friend.", null);

		addFirstQuest();
		addSecondQuest();
		
		// quest
		addQuest();
	}


	private void addGreetingDependingOnQuestState() {
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, 
				noFriends, ConversationStates.ATTENDING,
				"Guess what, we are having another #Semos #Mine #Town #Revival #Weeks.", null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, 
				anyFriends, ConversationStates.ATTENDING,
				null, new ChatAction(){

					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						npc.say("Hello " + player.getName() + ", nice to meet you again. "
						+ "Guess what, we are having another #Semos #Mine #Town #Revival #Weeks.");
					}
		});
		// TODO: Tell old friends about renewal
	}


	private void addQuest() {
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				noFriends,
				ConversationStates.ATTENDING, "I need a #friend.", null);
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				oldFriends,
				ConversationStates.ATTENDING, "We should renew our #friendship.", null);
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				currentFriends,
				ConversationStates.ATTENDING,
				"I have made a lot of friends during the #Semos #Mine #Town #Revival #Weeks.",
				null);
	}

	private void addFirstQuest() {
		// initial friends quest
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("friend", "friends", "friendship"),
			noFriends,
			ConversationStates.INFORMATION_1,
			"Please repeat:\r\n                        \"A circle is round,\"",
			null);
		npc.add(ConversationStates.INFORMATION_1, Arrays.asList(
			"A circle is round,", "A circle is round"), null,
			ConversationStates.INFORMATION_2, "\"it has no end.\"",
			null);
		npc.add(ConversationStates.INFORMATION_2, Arrays.asList(
			"it has no end.", "it has no end"), null,
			ConversationStates.INFORMATION_3,
			"\"That's how long,\"", null);
		npc.add(ConversationStates.INFORMATION_3, Arrays.asList(
			"That's how long,", "That's how long",
			"Thats how long,", "Thats how long"), null,
			ConversationStates.INFORMATION_4,
			"\"I will be your friend.\"", null);

		ChatAction reward = new MultipleActions(new IncreaseKarmaAction(10), new IncreaseXPAction(25), new SetQuestToYearAction("susi"));
		npc.add(ConversationStates.INFORMATION_4, Arrays.asList(
			"I will be your friend.", "I will be your friend"),
			null, ConversationStates.ATTENDING,
			"Yay! We are friends now.",
			reward);
	}

	private void addSecondQuest() {
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("friend", "friends", "friendship"),
				oldFriends,
				ConversationStates.INFORMATION_5,
				"Please repeat:\r\n                        \"Make new friends,\"",
				null);
		npc.add(ConversationStates.INFORMATION_5, Arrays.asList(
				"Make new friends,", "Make new friends"), null,
				ConversationStates.INFORMATION_6, "\"but keep the old.\"",
				null);
		// TODO: this does not work, only "but" is accepted
		npc.add(ConversationStates.INFORMATION_6, Arrays.asList(
				"but keep the old.", "but keep the old"), null,
				ConversationStates.INFORMATION_7, "\"One is silver,\"",
				null);
		npc.add(ConversationStates.INFORMATION_7, Arrays.asList(
				"One is silver,", "One is silver"), null,
				ConversationStates.INFORMATION_8, "\"And the other gold.\"",
				null);

		// TODO: lower case "and" is not accepted
		ChatAction reward = new MultipleActions(new IncreaseKarmaAction(15), new IncreaseXPAction(50), new SetQuestToYearAction("susi"));
		npc.add(ConversationStates.INFORMATION_8, Arrays.asList(
				"And the other gold.", "And the other gold"),
				null, ConversationStates.ATTENDING,
				"Yay! We are even better friends now.",
				reward);
	}

	public void addToWorld() {
		buildConditions();
		createGirlNPC();
		addDialog();
	}
}
