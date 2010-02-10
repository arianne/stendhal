package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.StateTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * QUEST: The Sad Scientist.
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Boris Karlova, a scientist in Kalavan</li>
 * <li>Mayor Sakhs, the mayor of semos</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * 		<li>Talk to Boris Karlova, a lonely scientist.</li>
 * 		<li>Give him all stuff he needs for a present for his honey.</li>
 * 		<li>Talk to semos mayor.</li>
 * 		<li>Bring Karlova mayor's letter.</li>
 * 		<li>Kill the Imperial Scientist.</li>
 *		<li>Give him the flask with his brother's blood.</li> 
 * </ul>
 * 
 * REWARD:
 * <ul>
 * 		<li>a pair of black legs</li>
 * 		<li>20 Karma</li>
 * 		<li>10000 XP</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * 		<li>None</li>
 * </ul>
 */
public class SadScientist extends AbstractQuest {
	
	private static final String LETTER_DESCRIPTION = "You see a letter for Boris Karlova.";
	public static final String QUEST_SLOT = "sad_scientist";
	private static final int REQUIRED_MINUTES = 20;


	@Override
	public String getName() {
		return "TheSadScientist";
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	/* (non-Javadoc)
	 * @see games.stendhal.server.maps.quests.AbstractQuest#addToWorld()
	 */
	@Override
	public void addToWorld() {
		super.addToWorld();
		prepareQuestSteps();
	}

	private void prepareQuestSteps() {
		prepareScientist();
		prepareMayor();
	}

	private void prepareScientist() {
		final SpeakerNPC scientistNpc = npcs.get("Boris Karlova");
		final SpeakerNPC mayorNpc = npcs.get("Mayor Sakhs");
		startOfQuest(scientistNpc);
		playerReturnsAfterStartWithItems(scientistNpc);
		playerReturnsAfterStartWithoutItems(scientistNpc);
		playerReturnsAfterGivingTooEarly(scientistNpc);
		playerReturnsAfterGivingWhenFinished(scientistNpc);
		playerVisitsMayorSakhs(mayorNpc);
		playerReturnsWithLetter(scientistNpc);
		playerReturnsAfterKillingTheImperialScientist(scientistNpc);
	}

	private void playerReturnsAfterKillingTheImperialScientist(
			SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "kill_scientist"),
				new KilledCondition("imperial scientist"),
				new PlayerHasItemWithHimCondition("flask with blood"),
				new QuestNotActiveCondition("mithril_cloak")
			);
		ChatAction action = new MultipleActions(
										new SetQuestAction(QUEST_SLOT, "decorating;"+System.currentTimeMillis())
										);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition, ConversationStates.ATTENDING, 
				"Ha, ha, ha! I will cover legs with this blood and they will transform in my #symbol.", 
				null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("symbol", "Symbol"),
				condition, ConversationStates.IDLE, 
				"I am going to create a pair of black legs. Come back in 5 minutes.", 
				action);
	}

	private void playerReturnsWithLetter(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "find_mary"),
				new PlayerHasItemWithHimCondition("note"),
				new QuestNotActiveCondition("mithril_cloak")
			);
		final ChatAction action = new MultipleActions(
					new SetQuestAction(QUEST_SLOT, "kill_scientist")
				);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.INFORMATION_2,
				"Hello! Do you have anything for me?",
				null);
		npc.add(ConversationStates.INFORMATION_2, Arrays.asList("Letter","letter"),
				condition,
				ConversationStates.ATTENDING,
				"Oh no! I feel the pain. I do not need this item anymore. I want to transform it. I want to make it a symbol of pain. You! Go kill my brother, the Imperial Scientist. Give me his blood in this bottle.",
				action);
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.GOODBYE_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "kill_scientist"),
				ConversationStates.INFORMATION_2,
				"Do it!",
				null);
	}

	private void playerVisitsMayorSakhs(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "find_mary"),
				new QuestNotActiveCondition("mithril_cloak")
			);
		final ChatAction action = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				final Item item = SingletonRepository.getEntityManager().getItem("note");
				item.setInfoString(player.getName());
				item.setDescription(LETTER_DESCRIPTION);
				item.setBoundTo(player.getName());
				player.equipOrPutOnGround(item);
			}
		};
		npc.add(ConversationStates.ATTENDING, Arrays.asList("mary","Mary"),
				condition,
				ConversationStates.ATTENDING, 
				"What? How do you know her? Well it is a sad story." +
				" She was picking arandula for Ilisa (they were friends)" +
				" and she saw the catacombs entrance. 3 months later a" +
				" young hero saw her, and she was a vampirette. What a" +
				" sad story. I kept this for her husband. A letter. " +
				"I think he is in Kalavan." ,
				action);
	}

	private void playerReturnsAfterGivingWhenFinished(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "making;"),
				new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES, 1),
				new QuestNotActiveCondition("mithril_cloak")
			);
		final ChatAction action = new SetQuestAction(QUEST_SLOT,"mary");
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.INFORMATION_1, 
				"I finished the legs. But I cannot trust you. Before I give the" +
				" jewelled legs to you, I need a message from my darling. Ask Mayor" +
				" Sakhs for Mary. Can you do that for me?",
				null);
		npc.add(ConversationStates.INFORMATION_1, ConversationPhrases.YES_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Oh, thank you. I am waiting.",
				action);
		npc.add(ConversationStates.INFORMATION_1, ConversationPhrases.NO_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Pah! Bye!",
				action);
	}

	private void playerReturnsAfterGivingTooEarly(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
				new QuestStateStartsWithCondition(QUEST_SLOT, "making;"),
				new NotCondition(new TimePassedCondition(QUEST_SLOT, REQUIRED_MINUTES, 1)),
				new QuestNotActiveCondition("mithril_cloak")
			);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Hello. Please return when you have everything what I need for the jewelled legs.",
				new StateTimeRemainingAction(QUEST_SLOT, "Do you think I can work that fast? Go away. Come back in", REQUIRED_MINUTES, 1));
	}

	private void playerReturnsAfterStartWithoutItems(final SpeakerNPC npc) {
		final ChatCondition condition = new AndCondition(
										new QuestInStateCondition(QUEST_SLOT, "start"),
										new NotCondition( new PlayerHasItemWithHimCondition("emerald")), 
										new NotCondition( new PlayerHasItemWithHimCondition("obsidian")),
										new NotCondition( new PlayerHasItemWithHimCondition("sapphire")),
										new NotCondition( new PlayerHasItemWithHimCondition("carbuncle",2)),
										new NotCondition( new PlayerHasItemWithHimCondition("gold bar",20)),
										new NotCondition( new PlayerHasItemWithHimCondition("mithril bar")),
										new NotCondition( new PlayerHasItemWithHimCondition("shadow legs")),
										new QuestNotActiveCondition("mithril_cloak")
									);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Hello. Please return when you have everything what I need for the jewelled legs.",
				null);
	}

	private void playerReturnsAfterStartWithItems(final SpeakerNPC npc) {
		//player returns after start
		final AndCondition condition = new AndCondition(
									new QuestInStateCondition(QUEST_SLOT, "start"),
									new PlayerHasItemWithHimCondition("emerald"), 
									new PlayerHasItemWithHimCondition("obsidian"),
									new PlayerHasItemWithHimCondition("sapphire"),
									new PlayerHasItemWithHimCondition("carbuncle",2),
									new PlayerHasItemWithHimCondition("gold bar",20),
									new PlayerHasItemWithHimCondition("mithril bar"),
									new PlayerHasItemWithHimCondition("shadow legs"),
									new QuestNotActiveCondition("mithril_cloak")
									);
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				condition,
				ConversationStates.ATTENDING, 
				"Hello. Did you bring what I need?",
				null);
		final ChatAction action = new MultipleActions(
									new SetQuestAction(QUEST_SLOT,"making;"+System.currentTimeMillis()),
									new DropItemAction("emerald"),
									new DropItemAction("obsidian"),
									new DropItemAction("sapphire"),
									new DropItemAction("carbuncle",2),
									new DropItemAction("gold bar",20),
									new DropItemAction("mithril bar"),
									new DropItemAction("shadow legs"));
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.YES_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"Wonderful! I will start my work. I can do this at a very little time with the help of technology! Please come back in 20 minutes.",
				action);
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
				condition,
				ConversationStates.IDLE, 
				"What a wasteful child.",
				null);
	}

	private void startOfQuest(final SpeakerNPC npc) {
		//offer the quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new QuestNotStartedCondition(QUEST_SLOT),new QuestNotActiveCondition("mithril_cloak")),
				ConversationStates.QUEST_OFFERED,
				"So...looks like you want to help me?",null);
		//accept the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.QUEST_STARTED,
				"My wife is living in Semos City. She loves gems. Can you bring me some #gems that I need to make a pair of precious #legs?" ,
				null);
		// #gems
		npc.add(ConversationStates.QUEST_STARTED,
				Arrays.asList("gem","gems"),
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.QUEST_STARTED,
				"I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need a pair of shadow legs as the base to add the gems to. Can you do that for my wife? " ,
				null);
		// #legs
		npc.add(ConversationStates.QUEST_STARTED,
				Arrays.asList("leg","legs"),
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.QUEST_STARTED,
				"Jewelled legs. I need an emerald, an obsidian, a sapphire, 2 carbuncles, 20 gold bars, one mithril bar, and I need a pair of shadow legs as the base to add the gems to. Can you do that for my wife? Can you bring what I need? " ,
				null);
		//yes, no after start of quest
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.ATTENDING,
				"I am waiting, semos man." ,
				new SetQuestAction(QUEST_SLOT, "start"));
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.QUEST_STARTED,
				"Go away before I kill you!" ,
				null);
		//reject the quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotActiveCondition("mithril_cloak"),
				ConversationStates.ATTENDING,
				"If you change your mind please ask me again..." ,
				null);
	}

	private void prepareMayor() {
		// TODO Auto-generated method stub
		
	}
	
}
